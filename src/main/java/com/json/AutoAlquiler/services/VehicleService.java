package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.models.Location;
import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.models.Vehicle;
import com.json.AutoAlquiler.models.VehicleImage;
import com.json.AutoAlquiler.repositories.UserRepository;
import com.json.AutoAlquiler.repositories.VehicleImageRepository;
import com.json.AutoAlquiler.repositories.VehicleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final UserRepository userRepository;
    private final LocationService locationService;

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public List<Vehicle> findByStatus(String status) {
        return vehicleRepository.findByStatus(status);
    }

    public List<Vehicle> findByAdminUsername(String username) {
        return vehicleRepository.findByAdminUsername(username);
    }

    public List<Vehicle> findByAdminId(Long adminId) {
        return vehicleRepository.findByAdminId(adminId);
    }

    public List<Vehicle> findByOwner(Authentication authentication) {
        List<Vehicle> vehicles;
        boolean isSuperAdmin = authentication
            .getAuthorities()
            .stream()
            .anyMatch(role -> role.getAuthority().contains("SUPER_ADMIN"));
        String currentUsername = authentication.getName();
        if (isSuperAdmin) {
            vehicles = findAll();
            System.out.println("[ALERTA] Super administrador ha solicitado todos los vehiculos: " + currentUsername);
        } else {
            vehicles = findByAdminUsername(currentUsername);
        }
        return vehicles;
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        if ("Reservado".equalsIgnoreCase(vehicle.getStatus()) || "Rentado".equalsIgnoreCase(vehicle.getStatus())) {
            throw new IllegalStateException(
                "OPERACIÓN DENEGADA: El vehículo con placa " + vehicle.getPlate() + " tiene una reserva activa."
            );
        }

        if (vehicle.getImages() != null && !vehicle.getImages().isEmpty()) {
            for (VehicleImage image : vehicle.getImages()) {
                image.setVehicle(null);
                vehicleImageRepository.delete(image);
            }
            vehicle.getImages().clear();
        }

        vehicleRepository.delete(vehicle);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteImageByVehicleId(Long vehicleImageId) {
        this.vehicleImageRepository.deleteById(vehicleImageId);
    }

    @Transactional(rollbackFor = Exception.class)
    public VehicleImage save(VehicleImage vehicleImage) {
        return this.vehicleImageRepository.save(vehicleImage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void checkVehicleImageAndSave(Long vehicleId, String imageUrl) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);

        // Control de límite máximo (HU:5 imagenes por vehiculo)
        if (vehicle.getImages() != null && vehicle.getImages().size() >= 5) {
            throw new IllegalArgumentException("Límite alcanzado: Máximo 5 imágenes.");
        }

        //  Validar que el campo no llegue vacío o lleno de espacios en blanco
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("El enlace no puede estar vacío.");
        }

        String cleanUrl = imageUrl.trim();

        // Solo permite enlaces que inicien con http/https y terminen en extensiones de imagen estándar (.jpg, .png, .webp)
        String imageRegex = "^(https?://).*\\.(jpg|jpeg|png|webp|gif|svg)(\\?.*)?$";
        if (!cleanUrl.matches(imageRegex)) {
            throw new IllegalArgumentException(
                "Error de formato: Debe proporcionar un enlace URL directo de imagen válido (http/https y extensión .jpg, .jpe, .png, .webp, .svg, .gif)."
            );
        }

        VehicleImage img = new VehicleImage();
        img.setImagePath(cleanUrl);
        img.setVehicle(vehicle);
        this.save(img);
    }

    public List<Vehicle> findVehicles(String status, Authentication authentication) {
        if (status != null && !status.isEmpty()) {
            String formattedStatus = Character.toUpperCase(status.charAt(0)) + status.substring(1).toLowerCase();
            return this.findByStatus(formattedStatus);
        }
        return this.findByOwner(authentication);
    }

    @Transactional(rollbackFor = Exception.class)
    public void processAndSave(Vehicle vehicle) {
        // 1. Validación de reglas de negocio financieras
        if (vehicle.getDailyRate() != null && vehicle.getDailyRate() > 99999999999L) {
            throw new IllegalArgumentException("La tarifa diaria ingresada supera el límite financiero permitido por el sistema.");
        }

        if (vehicle.getLocation() != null) {
            Location managedLocation = locationService.validateAndGetLocationByUniqueKeyOrElseThrow(
                vehicle.getLocation().getCountry().getId(),
                vehicle.getLocation().getDepartment(),
                vehicle.getLocation().getMunicipality()
            );
            
            // Le seteamos la entidad pesistida con su respectivo ID al vehículo
            vehicle.setLocation(managedLocation);
        }
        

        // 3. Orquestación del estado de la entidad (Creación vs Edición)
        if (vehicle.getId() == null) {
            // Lógica de auditoría de seguridad para nuevas inserciones
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentAdmin = userRepository.findByUsername(currentUsername);

            if (currentAdmin == null) {
                throw new IllegalArgumentException("El usuario no es un administrador, esta acción no está autorizada.");
            }
            vehicle.setAdmin(currentAdmin);
        } else {
            // Preservación de relaciones persistidas al editar
            Vehicle existingVehicle = findById(vehicle.getId());
            if (existingVehicle != null) {
                vehicle.setAdmin(existingVehicle.getAdmin());
                vehicle.setImages(existingVehicle.getImages());
            }
        }

        save(vehicle);
    }
}
