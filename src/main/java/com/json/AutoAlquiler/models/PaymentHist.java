package com.json.AutoAlquiler.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments_hist")
@Getter
@NoArgsConstructor
public class PaymentHist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_id")
    private Long histId;

    private Long id;

    private String status;

    private Long amount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "operation_type")
    private String operationType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_method_hist_id", referencedColumnName = "hist_id", nullable = false)
    private PaymentMethodHist paymentMethod;

    @Column(name = "reservation_id")
    private Long reservationId;
}
