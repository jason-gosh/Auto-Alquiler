package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query(value = "SELECT COALESCE(SUM(p.amount), 0) FROM payments p " +
         "INNER JOIN reservations r ON p.reservation_id = r.id " +
         "INNER JOIN vehicles v ON r.vehicle_id = v.id " +
         "WHERE (:isSuperAdmin = true OR v.admin_id = :adminId) AND p.status = 'CONFIRMED' " +
         "AND p.payment_date >= :startMillis AND p.payment_date < :endMillis", nativeQuery = true)
    BigDecimal getRevenueByPeriod(
        @Param("adminId") Long adminId, 
        @Param("isSuperAdmin") boolean isSuperAdmin, 
        @Param("startMillis") Long startMillis, 
        @Param("endMillis") Long endMillis
    ); 
    
    Optional<Payment> findByReservationId(Long reservationId);
    boolean existsByReservationId(Long reservationId);
}