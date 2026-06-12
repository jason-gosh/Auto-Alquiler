package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.config.MissingRequiredElementException;
import com.json.AutoAlquiler.models.Country;
import com.json.AutoAlquiler.models.Identification;
import com.json.AutoAlquiler.models.Location;
import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.models.UserDetail;
import com.json.AutoAlquiler.repositories.CountryRepository;
import com.json.AutoAlquiler.repositories.IdentificationRepository;
import com.json.AutoAlquiler.repositories.LocationRepository;
import com.json.AutoAlquiler.repositories.UserDetailRepository;
import com.json.AutoAlquiler.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailService {

    private final UserDetailRepository userDetailRepository;
    private final UserRepository userRepository;
    private final IdentificationRepository identificationRepository;
    private final CountryRepository countryRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public UserDetail findDetailByUsernameOrNewEmpty(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new MissingRequiredElementException("El usuario que intenta búscar actualmente no existe.");
        }

        if (user.getDetail() == null) {
            UserDetail newDetail = new UserDetail();
            newDetail.setUser(user);
            user.setDetail(newDetail);
        }

        UserDetail detail = user.getDetail();

        if (detail.getTypeIdentification() == null) detail.setTypeIdentification(new Identification());
        if (detail.getCountry() == null) detail.setCountry(new Country());
        if (detail.getLocation() == null) detail.setLocation(new Location());

        return detail;
    }

    @Transactional(rollbackFor = Exception.class)
    public void upsertUserDetail(String username, UserDetail incomingDetail) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("El usuario de la sesión no existe.");
        }

        UserDetail userDetailToSave = user.getDetail();
        if (userDetailToSave == null) {
            userDetailToSave = new UserDetail();
            userDetailToSave.setUser(user);
            user.setDetail(userDetailToSave);
        }

        userDetailToSave.setIdentification(incomingDetail.getIdentification());
        userDetailToSave.setName(incomingDetail.getName());
        userDetailToSave.setAddress(incomingDetail.getAddress());
        userDetailToSave.setPhone(incomingDetail.getPhone());

        if (incomingDetail.getTypeIdentification() != null && incomingDetail.getTypeIdentification().getId() != null) {
            Identification typeId = identificationRepository
                .findById(incomingDetail.getTypeIdentification().getId())
                .orElseThrow(() -> new MissingRequiredElementException("El tipo de documento es necesario"));
            userDetailToSave.setTypeIdentification(typeId);
        }

        if (incomingDetail.getCountry() != null && incomingDetail.getCountry().getId() != null) {
            Country country = countryRepository
                .findById(incomingDetail.getCountry().getId())
                .orElseThrow(() -> new IllegalArgumentException("El país seleccionado no existe"));
            userDetailToSave.setCountry(country);
        }


        if (incomingDetail.getLocation() != null && incomingDetail.getLocation().getId() != null) {
            Location realLocation = locationRepository
                .findById(incomingDetail.getLocation().getId())
                .orElseThrow(() -> new IllegalArgumentException("La ubicación seleccionada no existe"));
            userDetailToSave.setLocation(realLocation);
        }

        
        if (incomingDetail.getPhone().isEmpty() || !incomingDetail.getPhone().matches("\\d+")) {
            throw new IllegalArgumentException("El número de teléfono no es válido o está vacío.");
        }

        // Guardamos los detalles con las llaves foráneas totalmente mapeadas
        userDetailRepository.save(userDetailToSave);
    }
}
