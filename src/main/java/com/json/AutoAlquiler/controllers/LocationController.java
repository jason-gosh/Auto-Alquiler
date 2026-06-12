package com.json.AutoAlquiler.controllers;

import com.json.AutoAlquiler.services.CountryService;
import com.json.AutoAlquiler.services.LocationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("locations")
public class LocationController {

    private final LocationService locationService;
    private final CountryService countryService;

    @GetMapping("/countries")
    @ResponseBody
    public List<String> getCountries(@RequestParam(required = false) Long countryId) {
        if(countryId != null &&  countryId > 0){
            return countryService.getCountryNameById(countryId);
        }
        return countryService.getCountriesNames();
    }

    @GetMapping("/departments")
    @ResponseBody
    public List<String> getDepartments(@RequestParam Long countryId) {
        return locationService.getDepartmentsByCountry(countryId);
    }

    @GetMapping("/municipalities")
    @ResponseBody
    public List<String> getMunicipalities(@RequestParam Long countryId, @RequestParam String department) {
        return locationService.getMunicipalitiesByCountryAndDepartment(countryId, department);
    }
}
