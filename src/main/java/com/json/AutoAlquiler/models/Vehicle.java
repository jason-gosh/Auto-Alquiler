package com.json.AutoAlquiler.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String plate;

    private String brand;
    private String model;
    private String status;

    @Min(value = 1500, message = "Año no válido")
    private Integer year;

    private String type;

    @Min(value = 0, message = "La tarifa no puede ser negativa")
    private Long dailyRate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    @ToString.Exclude
    private Location location;

    private String locationDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    @ToString.Exclude
    private User admin;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.EAGER) //se mapea la tabla completa vehicles_images
    @ToString.Exclude
    private List<VehicleImage> images = new ArrayList<>();

    // Este método se ejecuta automáticamente antes de guardar en la BD
    @PrePersist
    @PreUpdate
    private void prepareData() {
        this.plate = (plate != null) ? plate.toUpperCase().trim() : null;
    }

    protected static int getCurrentYear() {
        return java.time.LocalDateTime.now().getYear();
    }
}
