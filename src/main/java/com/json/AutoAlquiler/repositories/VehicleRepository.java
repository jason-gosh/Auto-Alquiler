package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.Vehicle;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query(value = "SELECT * FROM vehicles v WHERE lower(v.status) != 'mantenimiento' " +
           "AND (:locationId IS NULL OR v.location_id = :locationId) " +
           "AND (:type IS NULL OR :type = '' OR lower(v.type) = lower(:type)) " +
           "AND (:maxPrice IS NULL OR v.daily_rate <= :maxPrice) " +
           "AND (:startDate IS NULL OR :endDate IS NULL OR v.id NOT IN (" +
           "    SELECT r.vehicle_id FROM reservations r " +
           "    WHERE r.start_date <= :endDate AND r.end_date >= :startDate" +
           "))", 
           nativeQuery = true)
    List<Vehicle> findAvailableVehicles(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("locationId") Long locationId,
        @Param("type") String type,
        @Param("maxPrice") Long maxPrice
    );

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE (:isSuperAdmin = true OR v.admin.id = :adminId) AND lower(v.status) != 'mantenimiento'")
    long countTotalVehicles(@Param("adminId") Long adminId, @Param("isSuperAdmin") boolean isSuperAdmin);
    
    @Query(value = "SELECT COUNT(*) FROM vehicles v WHERE (:isSuperAdmin = true OR v.admin_id = :adminId) " +
        "AND v.id NOT IN (SELECT r.vehicle_id FROM reservations r WHERE :nowMillis BETWEEN r.start_date AND r.end_date)", nativeQuery = true)
    long countAvailableVehicles(@Param("adminId") Long adminId, @Param("isSuperAdmin") boolean isSuperAdmin, @Param("nowMillis") Long nowMillis);
    
    List<Vehicle> findByStatus(String status);
    List<Vehicle> findByAdminUsername(String username);
    List<Vehicle> findByAdminId(Long adminId);
}
