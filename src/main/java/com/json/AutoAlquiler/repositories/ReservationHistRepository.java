package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.ReservationHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public interface ReservationHistRepository extends JpaRepository<ReservationHist, Long> {

    @Query("SELECT r FROM ReservationHist r WHERE r.histId IN :ids ORDER BY r.histId DESC")
    List<ReservationHist> findAllByHistIdInOrderByHistIdDesc(@Param("ids") List<Long> ids);

    @Query("SELECT r FROM ReservationHist r WHERE r.histId IN :ids ORDER BY r.vehicleId ASC")
    List<ReservationHist> findAllByHistIdInOrderByVehicleIdAsc(@Param("ids") List<Long> ids);


    @Query(value = "SELECT r.hist_id FROM reservations_hist r " +
           "LEFT JOIN vehicles_hist v ON r.vehicle_id = v.id " +
           "LEFT JOIN payments_hist p ON p.reservation_id = r.id " +
           "WHERE r.hist_id = (SELECT MAX(sub_r.hist_id) FROM reservations_hist sub_r WHERE sub_r.id = r.id) " +
           "AND r.operation_type != 'DELETE' " +
           "AND (v.hist_id IS NULL OR (v.hist_id = (SELECT MAX(sub_v.hist_id) FROM vehicles_hist sub_v WHERE sub_v.id = v.id) AND v.operation_type != 'DELETE')) " +
           "AND (p.hist_id IS NULL OR (p.hist_id = (SELECT MAX(sub_p.hist_id) FROM payments_hist sub_p WHERE sub_p.id = p.id) AND p.operation_type != 'DELETE')) " +
           "ORDER BY r.hist_id DESC", nativeQuery = true)
    List<Long> findIdsAllHistorical();

    @Query(value = "SELECT r.hist_id FROM reservations_hist r " +
           "INNER JOIN vehicles_hist v ON r.vehicle_id = v.id " +
           "LEFT JOIN payments_hist p ON p.reservation_id = r.id " +
           "WHERE v.admin_id = :adminId " + 
           "AND r.hist_id = (SELECT MAX(sub_r.hist_id) FROM reservations_hist sub_r WHERE sub_r.id = r.id) " +
           "AND r.operation_type != 'DELETE' " +
           "AND v.hist_id = (SELECT MAX(sub_v.hist_id) FROM vehicles_hist sub_v WHERE sub_v.id = v.id) " +
           "AND v.operation_type != 'DELETE' " +
           "AND (p.hist_id IS NULL OR (p.hist_id = (SELECT MAX(sub_p.hist_id) FROM payments_hist sub_p WHERE sub_p.id = p.id) AND p.operation_type != 'DELETE')) " +
           "ORDER BY r.hist_id DESC", nativeQuery = true)
    List<Long> findIdsHistoricalByAdminId(@Param("adminId") Long adminId);

    @Query(value = "SELECT r.hist_id FROM reservations_hist r " +
           "LEFT JOIN vehicles_hist v ON r.vehicle_id = v.id " +
           "LEFT JOIN payments_hist p ON p.reservation_id = r.id " +
           "WHERE r.client_id = :clientId " + 
           "AND r.hist_id = (SELECT MAX(sub_r.hist_id) FROM reservations_hist sub_r WHERE sub_r.id = r.id) " +
           "AND r.operation_type != 'DELETE' " +
           "AND (v.hist_id IS NULL OR (v.hist_id = (SELECT MAX(sub_v.hist_id) FROM vehicles_hist sub_v WHERE sub_v.id = v.id) AND v.operation_type != 'DELETE')) " +
           "AND (p.hist_id IS NULL OR (p.hist_id = (SELECT MAX(sub_p.hist_id) FROM payments_hist sub_p WHERE sub_p.id = p.id) AND p.operation_type != 'DELETE')) " +
           "ORDER BY r.vehicle_id ASC", nativeQuery = true)
    List<Long> findIdsByClientIdOrderByVehicleIdAsc(@Param("clientId") Long clientId);

    @Query(value = "SELECT COUNT(r.hist_id) FROM reservations_hist r " +
           "INNER JOIN vehicles_hist v ON r.vehicle_id = v.id " +
           "WHERE v.admin_id = :adminId " +
           "AND r.client_id = :clientId " + 
           "AND r.hist_id = (SELECT MAX(sub_r.hist_id) FROM reservations_hist sub_r WHERE sub_r.id = r.id) " +
           "AND r.operation_type != 'DELETE' " +
           "AND v.hist_id = (SELECT MAX(sub_v.hist_id) FROM vehicles_hist sub_v WHERE sub_v.id = v.id) " +
           "AND v.operation_type != 'DELETE'", nativeQuery = true)
    Long countByAdminIdAndClientIdInHistory(@Param("adminId") Long adminId, @Param("clientId") Long clientId);

    @Query(
        value = "SELECT COUNT(*) FROM reservations_hist rh " +
                "INNER JOIN vehicles v ON rh.vehicle_id = v.id " +
                "WHERE (:isSuperAdmin = true OR v.admin_id = :adminId) " +
                "AND rh.operation_type = 'INSERT' " +
                "AND DATE(rh.operation_date, 'localtime') = :today",
        nativeQuery = true
    )
    long countReservationsToday(
        @Param("adminId") Long adminId,
        @Param("isSuperAdmin") boolean isSuperAdmin,
        @Param("today") String today
    );

    default List<ReservationHist> findAllWithGraph() {
        List<Long> ids = findIdsAllHistorical();
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return findAllByHistIdInOrderByHistIdDesc(ids);
    }

    default List<ReservationHist> findHistoricalByAdminId(Long adminId) {
        List<Long> ids = findIdsHistoricalByAdminId(adminId);
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return findAllByHistIdInOrderByHistIdDesc(ids);
    }

    default List<ReservationHist> findByClientIdOrderByVehicleIdAsc(Long clientId) {
        List<Long> ids = findIdsByClientIdOrderByVehicleIdAsc(clientId);
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return findAllByHistIdInOrderByVehicleIdAsc(ids);
    }
}