package com.json.AutoAlquiler.dtos;

import com.json.AutoAlquiler.models.Reservation;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

public record ReservationDTO(
    @RequestParam(value = "reservationId", required = false) Long reservationId,
    @RequestParam Long vehicleId,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
) {
    public boolean equalsAsEntity(Reservation incomingReservation) {
        if (incomingReservation == null) return false;
        if (incomingReservation.getClient() == null) return false;
        if (incomingReservation.getVehicle() == null) return false;
        if (incomingReservation.getStatus() == null) return false;

        return (
            Objects.equals(this.reservationId, incomingReservation.getId()) &&
            Objects.equals(this.vehicleId, incomingReservation.getVehicle().getId()) &&
            Objects.equals(this.startDate, incomingReservation.getStartDate()) &&
            Objects.equals(this.endDate, incomingReservation.getEndDate())
        );
    }

    public boolean allFieldsAreSafe() {
        return (this.vehicleId != null) & (this.startDate != null) && this.endDate != null;
    }
}
