package com.json.AutoAlquiler.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "user_detail",
    uniqueConstraints = { @UniqueConstraint(name = "uk_type_and_identification", columnNames = { "id_type", "identification" }) }
)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDetail {

    @Id
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    private String phone;
    private String address;

    @Column(name = "identification", nullable = false)
    private String identification;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_type", nullable = false)
    private Identification typeIdentification;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
}
