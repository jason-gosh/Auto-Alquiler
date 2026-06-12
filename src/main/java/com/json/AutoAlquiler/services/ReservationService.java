package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.config.MissingRequiredElementException;
import com.json.AutoAlquiler.dtos.ReservationDTO;
import com.json.AutoAlquiler.dtos.UserDetailDTO;
import com.json.AutoAlquiler.models.Country;
import com.json.AutoAlquiler.models.Identification;
import com.json.AutoAlquiler.models.Location;
import com.json.AutoAlquiler.models.Reservation;
import com.json.AutoAlquiler.models.ReservationStatus;
import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.models.UserDetail;
import com.json.AutoAlquiler.models.Vehicle;
import com.json.AutoAlquiler.repositories.CountryRepository;
import com.json.AutoAlquiler.repositories.IdentificationRepository;
import com.json.AutoAlquiler.repositories.ReservationRepository;
import com.json.AutoAlquiler.repositories.VehicleRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;
    private final UserService userService;
    private final UserDetailService userDetailService;
    private final IdentificationRepository identificationRepository;
    private final CountryRepository countryRepository;
    private final LocationService locationService;

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public Map<String, Object> prepareReservationReview(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Vehicle vehicle = vehicleRepository
            .findById(vehicleId)
            .orElseThrow(() -> new MissingRequiredElementException("Vehículo no encontrado"));

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) throw new IllegalArgumentException("Los dias de la reserva deben ser mayores o iguales a un dia.");

        long totalPrice = totalDays * vehicle.getDailyRate();
        UserDetail userDetail = userDetailService.findDetailByUsernameOrNewEmpty(authentication.getName());
        String identification = (userDetail.getIdentification() != null) ? userDetail.getIdentification() : "";
        Long identificationTypeId = (userDetail.getTypeIdentification().getId() != null) ? userDetail.getTypeIdentification().getId() : 0L;
        Long locationId = (userDetail.getLocation() != null && userDetail.getLocation().getId() != null)
            ? userDetail.getLocation().getId()
            : 0L;

        return Map.of(
            "vehicle",
            vehicle,
            "identification",
            identification,
            "identificationTypeId",
            identificationTypeId,
            "locationId",
            locationId,
            "vehicleId",
            vehicleId,
            "startDate",
            startDate,
            "endDate",
            endDate,
            "totalDays",
            totalDays,
            "totalPrice",
            totalPrice
        );
    }

    public List<Reservation> getReservationsForCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        if (currentUser == null) throw new MissingRequiredElementException("No hay un Usuario autenticado.");

        boolean isSuperAdmin = currentUser.getRole().getRoleName().equalsIgnoreCase("ROLE_SUPER_ADMIN");
        boolean isAdmin = currentUser.getRole().getRoleName().equalsIgnoreCase("ROLE_ADMIN");

        if (isSuperAdmin) {
            return reservationRepository.findAllByOrderByVehicleIdAsc();
        }

        if (isAdmin) {
            return reservationRepository.findByVehicleAdminIdOrderByVehicleIdAsc(currentUser.getId());
        }

        return reservationRepository.findByClientIdOrderByVehicleIdAsc(currentUser.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteReservation(Long reservationId) {
        System.out.println("[ALERTA] Se ordeno borrar la reserva con Id: " + reservationId);
        Reservation res = findById(reservationId).orElseThrow(() ->
            new MissingRequiredElementException("No se encontro la reservacion con ID: " + reservationId)
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(auth -> {
            if (auth.getAuthority().contains("CLIENT")) throw new AccessDeniedException(
                "Los clientes no tienen permiso de borrar las reservaciones."
            );
        });
        res.setStatus(ReservationStatus.VOIDED);
        res.setUpdatedBy(authentication.getName());
        res.setTerminationReason("MANUAL REMOVE");
        reservationRepository.saveAndFlush(res);
        reservationRepository.delete(res);
    }

    @Transactional(rollbackFor = Exception.class)
    public Reservation processAndSaveReservation(
        ReservationDTO incomingReservation,
        UserDetailDTO incomingUserDetail,
        Authentication authentication
    ) {
        Long reservationId = incomingReservation.reservationId();
        Long vehicleId = incomingReservation.vehicleId();
        LocalDate startDate = incomingReservation.startDate();
        LocalDate endDate = incomingReservation.endDate();

        Long typeId = incomingUserDetail.identificationType();
        Long countryId = incomingUserDetail.countryId();
        Long LocationId = incomingUserDetail.locationId();
        String identification = incomingUserDetail.identification();
        String phone = incomingUserDetail.phone();
        String department = incomingUserDetail.department();
        String municipality = incomingUserDetail.municipality();

        long calculatedTotal;
        boolean isAnyAdmin;
        Vehicle oldVehicle;
        Vehicle targetVehicle;
        Country targetCountry;
        Location targetLocation;
        User currentUser;
        UserDetail targetUserDetail;
        Reservation reservation;

        /*System.out.println("########################################## ALERTA ############################################");
        System.out.println("RESERVA: "+incomingReservation);
        System.out.println("USUAIRODETALLE: "+incomingUserDetail);
        System.out.println("########################################## ALERTA ############################################");
        */

        validateFieldsDTOs(incomingUserDetail, incomingReservation);
        validateVehicleAvailability(vehicleId, startDate, endDate, reservationId);
        validateReservationConstraints(reservationId, startDate, endDate, phone);

        currentUser = userService.getAuthenticatedUser(authentication);
        reservation = resolveReservationEntity(reservationId, currentUser, vehicleId, authentication);
        oldVehicle = (reservationId != null) ? reservation.getVehicle() : null;

        targetVehicle = vehicleRepository
            .findById(vehicleId)
            .orElseThrow(() -> new IllegalArgumentException("El vehículo seleccionado ya no existe en el sistema."));
        targetCountry = countryRepository
            .findById(countryId)
            .orElseThrow(() -> new IllegalArgumentException("El país seleccionado ya no existe en el sistema."));

        targetLocation = locationService.validateAndGetLocationByUniqueKeyOrElseThrow(countryId, department, municipality);
        targetUserDetail = userService.findDetailByDocAndType(typeId, identification).orElse(new UserDetail());
        isAnyAdmin = authentication.getAuthorities().toString().toUpperCase().contains("ADMIN");
        calculatedTotal = calculateTotalAmount(startDate, endDate, targetVehicle.getDailyRate());
        processUserDetail(currentUser, targetUserDetail, incomingUserDetail, targetCountry, targetLocation, LocationId, isAnyAdmin);

        reservation.setVehicle(targetVehicle);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setTotalAmount(calculatedTotal);
        reservation.setStatus(ReservationStatus.PENDING);
        resolveVehicleStatusTransitions(oldVehicle, targetVehicle);

        log.info(
            "📌 Reserva [#{}] preparada por el sistema. Estado: {}. Total del alquiler: {}. Vehículo asociado: #{}. Cliente asociado: #{}",
            reservation.getId(),
            reservation.getStatus(),
            reservation.getTotalAmount(),
            reservation.getVehicle().getId(),
            reservation.getClient()
        );

        return reservationRepository.save(reservation);
    }

    // --- SEGMENTED ATOMIC HELPER METHODS (REGLAS DE NEGOCIO) ---

    private void validateFieldsDTOs(UserDetailDTO incomingUserDetail, ReservationDTO incomingReservation) {
        if (!incomingUserDetail.allFieldsAreSafe()) {
            throw new MissingRequiredElementException("Falta información obligatoria del Cliente.");
        }
        if (!incomingReservation.allFieldsAreSafe()) {
            throw new MissingRequiredElementException("Falta información obligatoria de la Reserva.");
        }
    }

    private void validateReservationConstraints(Long reservationId, LocalDate startDate, LocalDate endDate, String phone) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de entrega no puede ser posterior a la fecha de recogida.");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de entrega no puede estar en el pasado.");
        }
        if (!phone.matches("[0-9]+")) {
            throw new IllegalArgumentException("El número de teléfono debe contener únicamente dígitos del 0 al 9, sin puntos ni comas.");
        }
    }

    private void validateVehicleAvailability(Long vehicleId, LocalDate startDate, LocalDate endDate, Long reservationId) {
        boolean isOverlapped = (reservationId != null)
            ? reservationRepository.existsOverlappingReservationForUpdate(vehicleId, startDate, endDate, reservationId)
            : reservationRepository.existsOverlappingReservation(vehicleId, startDate, endDate);

        if (isOverlapped) {
            throw new IllegalArgumentException("El vehículo no se encuentra disponible en el rango de fechas seleccionado.");
        }
    }

    private Reservation resolveReservationEntity(Long reservationId, User user, Long targetVehicleId, Authentication auth) {
        if (reservationId == null) {
            Reservation newReservation = new Reservation();
            newReservation.setClient(user);
            return newReservation;
        }

        Reservation existingReservation = reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new MissingRequiredElementException("La reserva que intenta modificar ya no existe."));

        boolean isSuperAdmin = auth
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(r -> r.equals("ROLE_SUPER_ADMIN"));
        boolean isAdmin = auth
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(r -> r.equals("ROLE_ADMIN"));

        if (!isSuperAdmin && !isAdmin && !existingReservation.getVehicle().getId().equals(targetVehicleId)) {
            throw new IllegalArgumentException("Los clientes no tienen permitido cambiar el vehículo de una reserva existente.");
        }
        if (!isSuperAdmin && !isAdmin && !existingReservation.getClient().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permisos para modificar esta reserva.");
        }

        return existingReservation;
    }

    private void processUserDetail(
        User currentUser,
        UserDetail userDetailTarget,
        UserDetailDTO incomingUserDetail,
        Country country,
        Location location,
        Long locationId,
        boolean isAnyAdmin
    ) {
        String targetUsername;
        boolean isEqualsEntity;
        Identification typeIdentification;

        isEqualsEntity = incomingUserDetail.equalsAsEntity(userDetailTarget);

        if (!isAnyAdmin && !isEqualsEntity && userDetailTarget.getUser() != null) throw new AccessDeniedException(
            "Los clientes no pueden modificar la información personal en plena reservación."
        );

        if (incomingUserDetail.identificationType() == null) new MissingRequiredElementException(
            "El tipo de documento es obligatorio para continuar con la reserva."
        );

        if (userDetailTarget.getUser() == null) {
            userDetailTarget.setUser(currentUser);
        }

        targetUsername = userDetailTarget.getUser().getUsername();
        typeIdentification = identificationRepository.findById(incomingUserDetail.identificationType()).get();

        userDetailTarget.setName(incomingUserDetail.name());
        userDetailTarget.setPhone(incomingUserDetail.phone());
        userDetailTarget.setAddress(incomingUserDetail.address());
        userDetailTarget.setCountry(country);
        userDetailTarget.setLocation(location);
        userDetailTarget.setTypeIdentification(typeIdentification);
        userDetailTarget.setIdentification(incomingUserDetail.identification());

        if (!isEqualsEntity) {
            userDetailService.upsertUserDetail(targetUsername, userDetailTarget);
            String currentLocation = (userDetailTarget.getLocation().getId() != locationId)
                ? "OLD -> " + locationId + " NEW -> " + location.getId()
                : location.getId().toString();
            log.info(
                "📌 Detalles del Usuario: [#{}] procesados por el sistema. Identificación: {}. Tipo de Identificacion asociada: #{}. Localización asociada: #{}. La localización actual: {}",
                userDetailTarget.getId(),
                userDetailTarget.getIdentification(),
                userDetailTarget.getTypeIdentification(),
                userDetailTarget.getLocation().getId(),
                currentLocation
            );
        }
    }

    private long calculateTotalAmount(LocalDate startDate, LocalDate endDate, Long dailyRate) {
        long totalAmount;
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalDays <= 0) {
            throw new IllegalArgumentException("Los dias totales de la reservación deben ser igual o superiores a uno.");
        }
        totalAmount = totalDays * dailyRate;
        if (totalAmount <= 0) {
            throw new IllegalArgumentException("El precio total de la reservación deben ser igual o superiores a uno.");
        }
        return totalAmount;
    }

    private void resolveVehicleStatusTransitions(Vehicle oldVehicle, Vehicle targetVehicle) {
        if (oldVehicle != null && !oldVehicle.getId().equals(targetVehicle.getId())) {
            oldVehicle.setStatus("Disponible");
            vehicleRepository.save(oldVehicle);
        }
        targetVehicle.setStatus("Rentado");
        vehicleRepository.save(targetVehicle);
    }
}
