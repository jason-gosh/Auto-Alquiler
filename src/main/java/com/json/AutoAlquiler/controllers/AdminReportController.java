package com.json.AutoAlquiler.controllers;

import com.json.AutoAlquiler.dtos.DashboardMetricsDTO;
import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.services.ManagementService;
import com.json.AutoAlquiler.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final UserService userService;
    private final ManagementService managementService;

    @GetMapping
    public String showReportPanel(Model model) {
        User user = getAuthenticatedUser();
        if (user == null) {
            model.addAttribute("error", "Error: No se pudo mapear el usuario autenticado en la base de datos.");
            return "error";
        }
        DashboardMetricsDTO metrics = managementService.getAdminDashboardMetrics(user);

        model.addAttribute("metrics", metrics);
        return "admin/dashboard-reports";
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser();
        byte[] csvData = managementService.generateCsvReport(user);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_estrategico.csv");
        response.setContentLength(csvData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(csvData);
            os.flush();
        }
    }

    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        User user = getAuthenticatedUser();
        byte[] pdfData = managementService.generatePdfReport(user);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_estrategico.pdf");
        response.setContentLength(pdfData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return userService.findByUsername(username);
    }
}
