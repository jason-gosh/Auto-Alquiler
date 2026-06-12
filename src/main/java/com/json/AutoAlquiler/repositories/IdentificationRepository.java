package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.Identification;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentificationRepository extends JpaRepository<Identification, Long> {
    Optional<Identification> findByCode(String code);
}