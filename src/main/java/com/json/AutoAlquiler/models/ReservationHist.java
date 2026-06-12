package com.json.AutoAlquiler.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.ToString;
import org.hibernate.annotations.JoinFormula;

@Entity
@Table(name = "reservations_hist")
@Getter
@NoArgsConstructor
@ToString
public class ReservationHist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hist_id")
    private Long histId;

    @Column(name = "id")
    private Long id;

    @Column(name = "vehicle_id", insertable = false, updatable = false)
    private Long vehicleId;

    @Column(name = "contract_number")
    private String contractNumber;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_amount")
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReservationStatus status;
    
    @Column(name = "termination_reason")
    private String terminationReason;
    
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "operation_type")
    private String operationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula(
        "(SELECT v.hist_id FROM vehicles_hist v WHERE v.id = vehicle_id AND v.operation_type != 'DELETE' ORDER BY v.hist_id DESC LIMIT 1)"
    )
    @ToString.Exclude
    private VehicleHist vehicleHist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula(
        "(SELECT p.hist_id FROM payments_hist p WHERE p.reservation_id = id AND p.operation_type != 'DELETE' ORDER BY p.hist_id DESC LIMIT 1)"
    )
    @ToString.Exclude
    private PaymentHist paymentHist;
}
