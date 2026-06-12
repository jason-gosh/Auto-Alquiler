package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(
        "SELECT COUNT(r) > 0 FROM Reservation r WHERE r.vehicle.id = :vehicleId " +
            "AND (:startDate <= r.endDate AND :endDate > r.startDate)"
    )
    boolean existsOverlappingReservation(
        @Param("vehicleId") Long vehicleId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query(
        "SELECT COUNT(r) > 0 FROM Reservation r WHERE r.vehicle.id = :vehicleId " +
            "AND (:startDate <= r.endDate AND :endDate > r.startDate) " +
            "AND r.id != :currentReservationId"
    )
    boolean existsOverlappingReservationForUpdate(
        @Param("vehicleId") Long vehicleId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("currentReservationId") Long currentReservationId
    );

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.vehicle.admin.id = :adminId AND r.client.id = :clientId")
    Long countByAdminIdAndClientId(@Param("adminId") Long adminId, @Param("clientId") Long clientId);

    @Query("SELECT r FROM Reservation r JOIN Payment p ON p.reservation.id = r.id WHERE p.status = :status")
    List<Reservation> findByPaymentStatus(@Param("status") String status);

    @Query("SELECT r FROM Reservation r WHERE r.vehicle.id IN " + "(SELECT v.id FROM Vehicle v WHERE v.admin.id = :adminId)")
    List<Reservation> findAllByAdminId(@Param("adminId") Long adminId);

    @Query(
        "SELECT r FROM Reservation r JOIN Payment p ON p.reservation.id = r.id " +
            "WHERE r.vehicle.id IN (SELECT v.id FROM Vehicle v WHERE v.admin.id = :adminId) " +
            "AND p.status = :status"
    )
    List<Reservation> findAllByAdminIdAndPaymentStatus(@Param("adminId") Long adminId, @Param("status") String status);

    @Query("SELECT r FROM Reservation r WHERE r.endDate < :date AND (r.status = 'PENDING' OR r.status = 'ACTIVE')")
    List<Reservation> findExpiredReservations(@Param("date") LocalDate date);

    List<Reservation> findByClientIdOrderByVehicleIdAsc(Long clientId);
    List<Reservation> findByVehicleAdminIdOrderByVehicleIdAsc(Long adminId);
    List<Reservation> findAllByOrderByVehicleIdAsc();
    Optional<Reservation> findById(Long id);
}
