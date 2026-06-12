package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {
    void deleteByVehicleId(Long vehicleId);
}