package com.json.AutoAlquiler.controllers;

import com.json.AutoAlquiler.models.Payment;
import com.json.AutoAlquiler.models.PaymentStatus;
import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.services.ManagementService;
import com.json.AutoAlquiler.services.PaymentService;
import com.json.AutoAlquiler.services.ReservationHistService;
import com.json.AutoAlquiler.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final ManagementService mgmtService;
    private final ReservationHistService reservationHistService;
    private final PaymentService paymentService;

    @GetMapping("/dashboard")
    public String showControlPanel(
        @RequestParam(value = "statusFilter", defaultValue = "ALL") String statusFilter,
        Authentication auth,
        Model model
    ) {
        model.addAttribute("clients", mgmtService.getAllClients());
        model.addAttribute("reservations", mgmtService.getActiveReservationsFiltered(statusFilter, auth));
        model.addAttribute("currentFilter", statusFilter);
        return "admin/dashboard";
    }

    @GetMapping("/clients/{id}/history")
    public String showClientHistoryAdmin(@PathVariable Long id, Authentication auth, RedirectAttributes flash, Model model) {
        try {
            User currentUser = userService.findByUsername(auth.getName());
            mgmtService.validateAdminAccessToClient(currentUser, id);
            model.addAttribute("reservationsHist", reservationHistService.getReservationHistByOwner(userService.findById(id)));
            return "admin/client-reservation-history";
        } catch (IllegalArgumentException | AccessDeniedException e) {
            flash.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al intentar encontrar el historial del cliente causado por: " + e.getMessage());
            flash.addFlashAttribute("errorMessage", "No se pudo cargar el historial de reservas para el cliente");
        }
        return "redirect:/admin/dashboard?error";
    }

    @GetMapping("/payment/manage/{reservationId}")
    public String managePaymentForm(@PathVariable Long reservationId, RedirectAttributes redirectAttributes, Model model) {
        try {
            Payment payment = paymentService.preparePaymentForReservation(reservationId);

            model.addAttribute("payment", payment);
            model.addAttribute("reservation", payment.getReservation());
            model.addAttribute("statuses", PaymentStatus.values());
            model.addAttribute("paymentMethods", paymentService.getAllPaymentMethods());

            return "admin/manage-payment";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.out.println("🚨Error crítico al cargar el pago causado por: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar el pago para la reservación");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/payment/save")
    public String savePayment(
        @RequestParam("reservationId") Long reservationId,
        @RequestParam("amount") Long amount,
        @RequestParam("status") PaymentStatus status,
        @RequestParam("paymentMethodId") Long paymentMethodId,
        RedirectAttributes redirectAttributes
    ) {
        try {
            paymentService.processReservationPayment(reservationId, amount, status, paymentMethodId);
            redirectAttributes.addFlashAttribute("successMessage", "El pago de la reserva se ha procesado con éxito.");
            return "redirect:/admin/dashboard?success";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.out.println("🚨 Error crítico al procesar el pago causado por: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar el pago.");
        }
        return "redirect:/admin/payment/manage/" + reservationId;
    }
}
