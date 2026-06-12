package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.models.Reservation;
import com.json.AutoAlquiler.models.ReservationStatus;
import com.json.AutoAlquiler.repositories.PaymentRepository;
import com.json.AutoAlquiler.repositories.ReservationRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationAutomationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationAutomationService.class);
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(rollbackFor = Exception.class)
    public int closeAndCleanExpiredReservations() {
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(LocalDate.now());

        if (expiredReservations.isEmpty()) {
            return 0;
        }

        for (Reservation reservation : expiredReservations) {
            try {
                boolean hasPayment = paymentRepository.existsByReservationId(reservation.getId());

                if (hasPayment) {
                    reservation.setStatus(ReservationStatus.COMPLETED);
                } else {
                    reservation.setStatus(ReservationStatus.EXPIRED);
                }

                reservation.setTerminationReason("SYSTEM_CLOSURE");
                reservation.setUpdatedBy("system_daemon");
                reservationRepository.saveAndFlush(reservation);
                reservationRepository.delete(reservation);

                log.info(
                    "📌 Reserva [{}] procesada por el sistema. Estado: {} | Traspaso a _hist exitoso.",
                    reservation.getId(),
                    reservation.getStatus()
                );
            } catch (Exception ex) {
                log.error("❌ Error al procesar la reserva con ID: " + reservation.getId() + ". Abortando lote.", ex);
                // Lanzamos RuntimeException para obligar a Spring a hacer ROLLBACK completo de la transacción
                throw new RuntimeException("Error en lote de automatización. Datos protegidos.", ex);
            }
        }

        return expiredReservations.size();
    }
}
