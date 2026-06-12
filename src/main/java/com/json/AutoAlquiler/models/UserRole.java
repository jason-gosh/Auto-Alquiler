package com.json.AutoAlquiler.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserRole {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_name")
    private String roleName;

    @OneToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @MapsId
    @ToString.Exclude
    private User user;
}
