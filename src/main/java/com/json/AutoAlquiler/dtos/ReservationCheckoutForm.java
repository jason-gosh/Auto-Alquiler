package com.json.AutoAlquiler.dtos;

import java.time.LocalDate;

public record ReservationCheckoutForm(
    Long reservationId,
    Long vehicleId,
    LocalDate startDate,
    LocalDate endDate,
    
    String name,
    String phone,
    String address,
    String identification,
    String department,
    String municipality,
    Long countryId,
    Long locationId,
    Long identificationType
) {
    public ReservationCheckoutForm {
    }


    public ReservationDTO toReservationDTO() {
        return new ReservationDTO(this.reservationId, this.vehicleId, this.startDate, this.endDate);
    }

    public UserDetailDTO toUserDetailDTO() {
        return new UserDetailDTO(this.name, this.phone, this.address, this.identificationType, this.identification, this.countryId, this.locationId, this.department, this.municipality);
    }


    public static ReservationCheckoutForm buildTemplate(){
        return new ReservationCheckoutForm(
            0L,
            0L,
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            "",
            "",
            "",
            "",
            "",
            "",
            0L,
            0L,
            0L
        );
    }
}
