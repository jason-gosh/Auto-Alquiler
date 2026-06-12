package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.models.Vehicle;
import com.json.AutoAlquiler.repositories.VehicleRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final VehicleRepository vehicleRepository;

    public List<Vehicle> getAvailableVehicles(LocalDate startDate, LocalDate endDate, Long locationId, String type, Long maxPrice) {
        if (startDate == null || endDate == null) {
            return List.of();
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("ERROR: No se permiten búsquedas con fechas anteriores al día de hoy.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("ERROR: La fecha de devolución no puede ser menor a la fecha de recogida.");
        }
        return vehicleRepository.findAvailableVehicles(startDate, endDate, locationId, type, maxPrice);
    }
}
