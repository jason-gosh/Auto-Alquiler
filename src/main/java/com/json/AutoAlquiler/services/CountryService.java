package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.models.Country;
import com.json.AutoAlquiler.repositories.CountryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    public List<String> getCountriesNames() {
        return this.findAll().stream().map(Country::getCountry).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> getCountryNameById(Long countryId) {
        return List.of(this.countryRepository.findById(countryId).get().getCountry());
    }
}
