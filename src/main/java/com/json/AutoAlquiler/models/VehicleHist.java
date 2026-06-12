package com.json.AutoAlquiler.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles_hist")
@Getter
@NoArgsConstructor
public class VehicleHist {

    @Id
    @Column(name = "hist_id")
    private Long histId;

    @Column(name = "id")
    private Long vehicleId;

    private String plate;
    private String brand;
    private String model;
    private Long year;
    private String type;

    @Column(name = "daily_rate")
    private Long DailyRate;

    @Column(name = "location_details")
    private String locationDetails;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "operation_date")
    private String operationDate;
}
