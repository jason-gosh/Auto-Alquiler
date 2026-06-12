package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.config.MissingRequiredElementException;
import com.json.AutoAlquiler.models.Location;
import com.json.AutoAlquiler.repositories.LocationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<Location> findAllByOrderByDepartmentAscMunicipalityAsc() {
        return this.locationRepository.findAllByOrderByDepartmentAscMunicipalityAsc();
    }

    public List<Location> findAll() {
        return this.locationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> getDepartmentsByCountry(Long countryId) {
        return locationRepository.findDepartmentsByCountryId(countryId);
    }

    @Transactional(readOnly = true)
    public List<String> getMunicipalitiesByCountryAndDepartment(Long countryId, String department) {
        return locationRepository.findMunicipalitiesByCountryAndDepartment(countryId, department);
    }

    @Transactional(readOnly = true)
    public Location validateAndGetLocationByUniqueKeyOrElseThrow(Long countryId, String department, String municipality) {
        if (countryId == null || department == null || department.isBlank() || municipality == null || municipality.isBlank()) {
            throw new MissingRequiredElementException("Los datos de la ubicación geográfica estan incompleta.");
        }
        return locationRepository
            .findByCountryIdAndDepartmentAndMunicipality(countryId, department, municipality)
            .orElseThrow(() -> new IllegalArgumentException("La solicitud contiene una ubicación geográfica incorrecta."));
    }
}
