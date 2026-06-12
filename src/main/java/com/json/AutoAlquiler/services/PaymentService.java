package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.config.MissingRequiredElementException;
import com.json.AutoAlquiler.models.Payment;
import com.json.AutoAlquiler.models.PaymentMethod;
import com.json.AutoAlquiler.models.PaymentStatus;
import com.json.AutoAlquiler.models.Reservation;
import com.json.AutoAlquiler.repositories.PaymentMethodRepository;
import com.json.AutoAlquiler.repositories.PaymentRepository;
import com.json.AutoAlquiler.repositories.ReservationRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public Payment preparePaymentForReservation(Long reservationId) {
        Reservation reservation = reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("La reserva solicitada no existe en el sistema."));

        Payment payment = paymentRepository.findByReservationId(reservationId).orElse(new Payment());

        if (payment.getId() == null) {
            payment.setReservation(reservation);
            payment.setAmount(reservation.getTotalAmount());
            payment.setStatus(PaymentStatus.PENDING.toString());
        }

        return payment;
    }

    @Transactional
    public void processReservationPayment(Long reservationId, Long amount, PaymentStatus status, Long paymentMethodId) {
        Reservation reservation = reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("La reserva especificada no existe."));

        if (amount > reservation.getTotalAmount()) {
            throw new IllegalArgumentException(
                "El monto digitado ($" + amount + ") excede el valor total de la reserva ($" + reservation.getTotalAmount() + ")."
            );
        }

        PaymentMethod method = paymentMethodRepository
            .findById(paymentMethodId)
            .orElseThrow(() -> new MissingRequiredElementException("El método de pago seleccionado no existe."));
        Payment payment = paymentRepository.findByReservationId(reservationId).orElse(new Payment());

        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setStatus(status.toString());
        payment.setPaymentMethod(method);
        payment.setPaymentDate(LocalDate.now());
        paymentRepository.save(payment);
    }
}
