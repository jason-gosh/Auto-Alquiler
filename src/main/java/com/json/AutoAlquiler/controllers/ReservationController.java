package com.json.AutoAlquiler.controllers;

import com.json.AutoAlquiler.dtos.ReservationCheckoutForm;
import com.json.AutoAlquiler.dtos.ReservationDTO;
import com.json.AutoAlquiler.dtos.UserDetailDTO;
import com.json.AutoAlquiler.models.Reservation;
import com.json.AutoAlquiler.services.CountryService;
import com.json.AutoAlquiler.services.IdentificationService;
import com.json.AutoAlquiler.services.LocationService;
import com.json.AutoAlquiler.services.ReservationService;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final IdentificationService identificationService;
    private final CountryService countryService;
    private final LocationService locationService;

    @GetMapping("/initProcess/{vehicleId}")
    public String initProcess(
        @PathVariable("vehicleId") Long vehicleId,
        @RequestParam(value = "reservationId", required = false) Long reservationId,
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            Map<String, Object> reviewData = reservationService.prepareReservationReview(vehicleId, startDate, endDate);
            model.addAllAttributes(reviewData);
            model.addAttribute("identifications", identificationService.findAll());
            model.addAttribute("countries", countryService.findAll());
            model.addAttribute("locations", locationService.findAll());
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("reservationForm", ReservationCheckoutForm.buildTemplate());
            return "reservations/confirm-form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar la reserva: " + e.getMessage());
            return "redirect:/catalog?startDate=" + startDate + "&endDate=" + endDate;
        } catch (Exception e) {
            System.out.println("Solicitud Erronea debido a la siguiente excepción: " + e.getMessage() + e.getStackTrace());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar la reserva. Intentelo más tarde.");
            return "redirect:/catalog?startDate=" + startDate + "&endDate=" + endDate;
        }
    }

    @PostMapping("/save")
    public String saveReservation(
        @ModelAttribute ReservationCheckoutForm reservationCheckoutForm,
        Authentication authentication,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        ReservationDTO reservationDTO = reservationCheckoutForm.toReservationDTO();
        UserDetailDTO userDetailDTO = reservationCheckoutForm.toUserDetailDTO();
        LocalDate startDate = reservationDTO.startDate();
        LocalDate endDate = reservationDTO.endDate();
        try {
            Reservation currentReservation = reservationService.processAndSaveReservation(reservationDTO, userDetailDTO, authentication);
            String successMessage = (reservationDTO.reservationId() != null)
                ? "¡Reserva modificada con éxito! Número de contrato: " + currentReservation.getContractNumber()
                : "¡Reserva confirmada con éxito! Número de contrato: " + currentReservation.getContractNumber();
            redirectAttributes.addFlashAttribute("success", successMessage);
            System.out.println("[DEBUG] Reserva guardada: " + currentReservation.toString());
            return "redirect:/catalog/reservedVehicles";
        } catch (IllegalArgumentException | AccessDeniedException | DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar la reserva: " + e.getMessage());
            return "redirect:/catalog?startDate=" + startDate + "&endDate=" + endDate;
        } catch (Exception e) {
            System.out.println("🚨 Error crítico al procesar la reserva causado por: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar la reserva");
            redirectAttributes.addAttribute("startDate", startDate);
            redirectAttributes.addAttribute("endDate", endDate);
            return "redirect:/catalog?startDate=" + startDate + "&endDate=" + endDate;
        }
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public String deleteReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.deleteReservation(id);
            redirectAttributes.addFlashAttribute("success", "La reserva ha sido eliminada permanentemente del sistema.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la reserva: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("🚨 Error crítico al borrar la reserva causado por: " + e);
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la reserva");
        }
        return "redirect:/catalog/reservedVehicles";
    }
}
