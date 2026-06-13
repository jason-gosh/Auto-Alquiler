package com.json.AutoAlquiler.controllers;

import com.json.AutoAlquiler.models.Vehicle;
import com.json.AutoAlquiler.services.CountryService;
import com.json.AutoAlquiler.services.LocationService;
import com.json.AutoAlquiler.services.VehicleService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final LocationService locationService;
    private final CountryService countryService;
    
    @GetMapping("")
    public String defaultRedirect() {
        return "redirect:/vehicles/list";
    }

    @GetMapping("/list")
    public String listVehicles(@RequestParam(required = false) String status, Model model, Authentication authentication) {
        List<Vehicle> vehicles = vehicleService.findVehicles(status, authentication);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("currentStatus", status); // Para mantener el filtro seleccionado
        return "vehicles/vehicle-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("locations", locationService.findAllByOrderByDepartmentAscMunicipalityAsc());
        model.addAttribute("countries", countryService.findAll());
        model.addAttribute("title", "Registrar Nuevo Vehículo");
        return "vehicles/vehicle-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.findById(id);
        if (vehicle == null) {
            return "redirect:/vehicles/list?error=notfound";
        }

        boolean hasLocation = vehicle.getLocation().getId() != null;
        List<String> preloadedDeps = hasLocation
            ? locationService.getDepartmentsByCountry(vehicle.getLocation().getCountry().getId())
            : List.of();

        List<String> preloadedMunis = hasLocation
            ? locationService.getMunicipalitiesByCountryAndDepartment(
                  vehicle.getLocation().getCountry().getId(),
                  vehicle.getLocation().getDepartment()
              )
            : List.of();

        model.addAttribute("title", "Editar Vehículo");
        model.addAttribute("locations", locationService.findAllByOrderByDepartmentAscMunicipalityAsc());
        model.addAttribute("countries", countryService.findAll());  
        model.addAttribute("preloadedDepartments", preloadedDeps);
        model.addAttribute("preloadedMunicipalities", preloadedMunis);
        model.addAttribute("vehicle", vehicle);
        return "vehicles/vehicle-form";
    }

    @PostMapping("/save")
    public String saveVehicle(
        @Valid @ModelAttribute("vehicle") Vehicle vehicle,
        BindingResult result,
        Model model,
        RedirectAttributes flash
    ) {
        try {
            if (result.hasErrors()) {
                String errorMsg = result.hasFieldErrors("dailyRate")
                    ? "Error: El valor de la tarifa es demasiado alto o inválido para el sistema."
                    : "Error: Verifique que los campos tengan el formato correcto.";

                model.addAttribute("error", errorMsg);
                model.addAttribute("locations", locationService.findAll());
                model.addAttribute("vehicle", vehicle);
                return "vehicles/vehicle-form";
            }

            vehicleService.processAndSave(vehicle);
            flash.addFlashAttribute("success", "Operación realizada con éxito");
            return "redirect:/vehicles/list";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            model.addAttribute(
                "error",
                "Error: La placa [" + vehicle.getPlate().toUpperCase() + "] ya se encuentra registrada o viola restricciones."
            );
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "No se pudo procesar la solicitud debido a un problema técnico.");
        }

        model.addAttribute("locations", locationService.findAll());
        model.addAttribute("countries", countryService.findAll());  
        model.addAttribute("vehicle", vehicle);
        return "vehicles/vehicle-form";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes flash) {
        System.out.println("[ALERTA] Se ordeno vehiculo con Id: " + id);
        try {
            vehicleService.delete(id);
            flash.addFlashAttribute("success", "El vehículo ha sido eliminado de la flota correctamente.");
        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.out.println("Ha ocurrido una excepción en el método deleteVehicle causado por: " + e.getMessage());
            flash.addFlashAttribute("error", "Error en el Proceso: No se pudo eliminar el vehículo.");
        }
        return "redirect:/vehicles/list";
    }

    @GetMapping("/details/{id}")
    public String viewDetails(@PathVariable Long id, Model model, @RequestParam(value = "source", required = false) String source) {
        try {
            boolean fromCatalog = "catalog".equalsIgnoreCase(source);
            Vehicle vehicle = vehicleService.findById(id);
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("images", vehicle.getImages());
            model.addAttribute("fromCatalog", fromCatalog);
            return "vehicles/vehicle-details";
        } catch (NullPointerException | NoSuchElementException e) {
            System.out.println("Ha ocurrido una excepción en el método viewDetails causado por: " + e.getClass() + " " + e.getMessage());
            return "/errors/400";
        }
    }

    @PostMapping("/details/{id}/add-image")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String addImage(
        @PathVariable(value = "id") Long vehicleId,
        @RequestParam(value = "imageUrl", required = false) String imageUrl,
        RedirectAttributes flash
    ) {
        try {
            vehicleService.checkVehicleImageAndSave(vehicleId, imageUrl);
            flash.addFlashAttribute("success", "Imagen añadida a la ficha correctamente.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.out.println("🚨 Error general al agregar la imagen al vehículo: " + vehicleId + " causado por: " + e.getMessage());
            flash.addFlashAttribute("error", "No se pudo agregar la imagen al vehículo.");
        }
        return "redirect:/vehicles/details/" + vehicleId;
    }

    @PostMapping("/details/{vehicleId}/delete-image/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String deleteImage(@PathVariable Long vehicleId, @PathVariable Long imageId, RedirectAttributes flash) {
        try {
            vehicleService.deleteImageByVehicleId(imageId);
            flash.addFlashAttribute("success", "Imagen eliminada de la ficha correctamente.");
        } catch (Exception e) {
            System.out.println("🚨 Error general al eliminar la imagen al vehículo: " + vehicleId + " causado por: " + e.getMessage());
            flash.addFlashAttribute("error", "No se pudo eliminar la imagen del vehículo.");
        }
        return "redirect:/vehicles/details/" + vehicleId;
    }
}
