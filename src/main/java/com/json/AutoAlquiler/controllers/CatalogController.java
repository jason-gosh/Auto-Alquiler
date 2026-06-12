package com.json.AutoAlquiler.controllers;

import com.json.AutoAlquiler.models.Reservation;
import com.json.AutoAlquiler.services.CatalogService;
import com.json.AutoAlquiler.services.LocationService;
import com.json.AutoAlquiler.services.ReservationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/catalog")
public class CatalogController {

    private final CatalogService catalogService;
    private final LocationService locationService;
    private final ReservationService reservationService;

    @ModelAttribute("todayDate") // Se invoca en cualquier petición a este controlador
    public LocalDate prependTodayDate() {
        return LocalDate.now();
    }

    @GetMapping()
    public String showCatalogAvailableVehicles(
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "locationId", required = false) Long locationId,
        @RequestParam(value = "maxPrice", required = false) Long maxPrice,
        @RequestParam(value = "reservationId", required = false) Long reservationId,
        Authentication authentication,
        Model model
    ) {
        if (startDate == null && endDate == null) {
            model.addAttribute("vehicles", List.of());
            return "catalog/catalog-available-vehicles";
        }

        try {
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("locations", locationService.findAll());
            model.addAttribute("vehicles", catalogService.getAvailableVehicles(startDate, endDate, locationId, type, maxPrice));
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA -> "+catalogService.getAvailableVehicles(startDate, endDate, locationId, type, maxPrice));
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehicles", List.of());
        } catch (Exception e) {
            System.out.println("[ALERTA] Excepción en showCatalog debido a: " + e.getMessage());
            model.addAttribute("error", "No se pudo cargar los vehículos disponibles.");
        }
        return "catalog/catalog-available-vehicles";
    }

    @GetMapping("/reservedVehicles")
    public String showCatalogReservedVehicles(Model model, Authentication authentication) {
        try {
            List<Reservation> reservations = reservationService.getReservationsForCurrentUser(authentication);
            model.addAttribute("reservations", reservations);
            model.addAttribute("todayDate", LocalDate.now());
            return "catalog/catalog-reserved-vehicles";
        } catch (Exception e) {
            System.out.println("[ALERTA] No se puedo mostrar los vehiculos reservados debido a: " + e.getMessage());
            return "errors/error";
        }
    }
}
