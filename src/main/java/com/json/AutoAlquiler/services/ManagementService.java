package com.json.AutoAlquiler.services;

import com.json.AutoAlquiler.dtos.DashboardMetricsDTO;
import com.json.AutoAlquiler.models.Reservation;
import com.json.AutoAlquiler.models.User;
import com.json.AutoAlquiler.repositories.PaymentRepository;
import com.json.AutoAlquiler.repositories.ReservationHistRepository;
import com.json.AutoAlquiler.repositories.ReservationRepository;
import com.json.AutoAlquiler.repositories.UserRepository;
import com.json.AutoAlquiler.repositories.VehicleRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagementService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationHistRepository reservationHistRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<User> getAllClients() {
        return userRepository
            .findAll()
            .stream()
            .filter(user -> user.getRole().getRoleName().contains("CLIENT"))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Reservation> getActiveReservationsFiltered(String status, Authentication auth) {
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);

        boolean isSuperAdmin =
            "ROLE_SUPER_ADMIN".equalsIgnoreCase(currentUser.getRole().getRoleName()) ||
            "SUPER_ADMIN".equalsIgnoreCase(currentUser.getRole().getRoleName());

        if (status == null || status.trim().isEmpty() || status.equalsIgnoreCase("ALL")) {
            return isSuperAdmin ? reservationRepository.findAll() : reservationRepository.findAllByAdminId(currentUser.getId());
        }

        String dbStatus = mapToDbStatus(status);

        if (dbStatus == null) {
            return Collections.emptyList();
        }

        if (isSuperAdmin) {
            return reservationRepository.findByPaymentStatus(dbStatus);
        } else {
            return reservationRepository.findAllByAdminIdAndPaymentStatus(currentUser.getId(), dbStatus);
        }
    }

    @Transactional(readOnly = true)
    public List<Reservation> getActiveReservationsByClient(Long clientId) throws AccessDeniedException {
        return reservationRepository.findByClientIdOrderByVehicleIdAsc(clientId);
    }

    public void validateAdminAccessToClient(User admin, Long clientId) {
        if (admin.getRole().getRoleName().equalsIgnoreCase("ROLE_SUPER_ADMIN")) {
            return;
        }
        System.out.println("VALIDANDO RESERVAS ACTUALES");
        Long activeCount = reservationRepository.countByAdminIdAndClientId(admin.getId(), clientId);
        boolean hasActiveReservations = activeCount != null && activeCount > 0;
        System.out.println("VALIDANDO RESERVAS HISTORICAS");
        Long historyCount = reservationHistRepository.countByAdminIdAndClientIdInHistory(admin.getId(), clientId);
        boolean hasHistoricalReservations = historyCount != null && historyCount > 0;

        if (!hasActiveReservations && !hasHistoricalReservations) {
            throw new AccessDeniedException("No tienes autorización para ver la información historica o actual de este cliente.");
        }
    }

    @Transactional(readOnly = true)
    public DashboardMetricsDTO getAdminDashboardMetrics(User user) {
        boolean isSuperAdmin = user.getRole().getRoleName().equalsIgnoreCase("ROLE_SUPER_ADMIN");
        Long adminId = user.getId();

        // Calculo de marcas de tiempo del Día (Hoy)
        long startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfDay = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // Cálculo del Mes Corriente
        long startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfMonth = LocalDate.now().withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // Cálculo del Año Corriente
        long startOfYear = LocalDate.now().withDayOfYear(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endOfYear = LocalDate.now().withDayOfYear(1).plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();


        // Consultas con parámetros numéricos directos
        long totalVehicles = vehicleRepository.countTotalVehicles(adminId, isSuperAdmin);
        long availableVehicles = vehicleRepository.countAvailableVehicles(adminId, isSuperAdmin, startOfDay);
        long reservationsToday = reservationHistRepository.countReservationsToday(adminId, isSuperAdmin, LocalDate.now().toString());

        BigDecimal dailyRevenue = paymentRepository.getRevenueByPeriod(adminId, isSuperAdmin, startOfDay, endOfDay);
        BigDecimal monthlyRevenue = paymentRepository.getRevenueByPeriod(adminId, isSuperAdmin, startOfMonth, endOfMonth);
        BigDecimal yearlyRevenue = paymentRepository.getRevenueByPeriod(adminId, isSuperAdmin, startOfYear, endOfYear);

        double occupancyRate = 0.0;
        if (totalVehicles > 0) {
            long rentedVehicles = totalVehicles - availableVehicles;
            occupancyRate = ((double) rentedVehicles / totalVehicles) * 100.0;
        }

        return new DashboardMetricsDTO(
            totalVehicles,
            reservationsToday,
            monthlyRevenue,
            Math.round(occupancyRate * 100.0) / 100.0,
            dailyRevenue,
            yearlyRevenue
        );
    }

    @Transactional(readOnly = true)
    public byte[] generateCsvReport(User user) {
        DashboardMetricsDTO metrics = getAdminDashboardMetrics(user);
        StringBuilder csv = new StringBuilder();

        // El carácter '\ufeff' (BOM) obliga a Excel a abrir el archivo reconociendo acentos y símbolos de moneda en UTF-8
        csv.append("\ufeff");
        csv.append("INDICADOR,VALOR\n");
        csv.append("Flota Activa Total,").append(metrics.activeVehicles()).append("\n");
        csv.append("Reservas del Dia,").append(metrics.reservationsToday()).append("\n");
        csv.append("Tasa de Ocupacion (%),").append(metrics.occupancyRate()).append("%\n");
        csv.append("Ingresos Diarios,$").append(metrics.dailyRevenue()).append("\n");
        csv.append("Ingresos Mensuales,$").append(metrics.monthlyRevenue()).append("\n");
        csv.append("Ingresos Anuales,$").append(metrics.yearlyRevenue()).append("\n");

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] generatePdfReport(User user) {
        DashboardMetricsDTO metrics = getAdminDashboardMetrics(user);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título del PDF
            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("REPORTE ESTRATÉGICO DE OPERACIONES", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(25);
            document.add(title);

            // Tabla de datos (2 columnas)
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // Encabezados
            table.addCell("Indicador Operacional / Financiero");
            table.addCell("Valor Actual");

            // Filas de datos
            table.addCell("Flota Activa Total");
            table.addCell(String.valueOf(metrics.activeVehicles()));

            table.addCell("Reservas del Dia");
            table.addCell(String.valueOf(metrics.reservationsToday()));

            table.addCell("Tasa de Ocupacion");
            table.addCell(metrics.occupancyRate() + "%");

            table.addCell("Ingresos Diarios");
            table.addCell("$" + metrics.dailyRevenue());

            table.addCell("Ingresos Mensuales");
            table.addCell("$" + metrics.monthlyRevenue());

            table.addCell("Ingresos Anuales");
            table.addCell("$" + metrics.yearlyRevenue());

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error crítico al construir la estructura del PDF", e);
        }

        return out.toByteArray();
    }

    private String mapToDbStatus(String statusFilter) {
        String clean = statusFilter.trim().toUpperCase();
        switch (clean) {
            case "PENDIENTE":
            case "PENDING":
                return "PENDING";
            case "CONFIRMADA":
            case "CONFIRMED":
                return "CONFIRMED";
            case "RECHAZADA":
            case "RECHAZADO":
            case "REJECTED":
                return "REJECTED";
            default:
                return null;
        }
    }
}
