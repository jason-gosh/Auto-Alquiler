package com.json.AutoAlquiler.repositories;

import com.json.AutoAlquiler.models.PaymentHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentHistRepository extends JpaRepository<PaymentHist, Long> {
}