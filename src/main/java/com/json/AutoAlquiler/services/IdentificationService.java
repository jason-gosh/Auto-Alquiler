package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.models.Identification;
import com.json.AutoAlquiler.repositories.IdentificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentificationService {

    private final IdentificationRepository identificationRepository;

    public List<Identification> findAll() {
        return identificationRepository.findAll();
    }
}
