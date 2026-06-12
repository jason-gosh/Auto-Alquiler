package com.json.AutoAlquiler.dtos;

import com.json.AutoAlquiler.models.UserDetail;
import java.util.Objects;
import org.springframework.web.bind.annotation.RequestParam;

public record UserDetailDTO(
    @RequestParam String name,
    @RequestParam String phone,
    @RequestParam String address,
    @RequestParam Long identificationType,
    @RequestParam String identification,
    @RequestParam Long countryId,
    @RequestParam Long locationId,
    @RequestParam String department,
    @RequestParam String municipality
) {
    public boolean equalsAsEntity(UserDetail incomingDetail) {
        if (incomingDetail == null) return false;
        if (incomingDetail.getTypeIdentification() == null) return false;
        if (incomingDetail.getCountry() == null) return false;
        if (incomingDetail.getLocation() == null) return false;

        return (
            Objects.equals(this.name, incomingDetail.getName()) &&
            Objects.equals(this.phone, incomingDetail.getPhone()) &&
            Objects.equals(this.address, incomingDetail.getAddress()) &&
            Objects.equals(this.identificationType, incomingDetail.getTypeIdentification().getId()) &&
            Objects.equals(this.identification, incomingDetail.getIdentification()) &&
            Objects.equals(this.countryId, incomingDetail.getCountry().getId()) &&
            Objects.equals(this.locationId, incomingDetail.getLocation().getId())
        );
    }

    public boolean allFieldsAreSafe() {
        return (
            this.name != null &&
            this.phone != null &&
            this.address != null &&
            this.locationId != null &&
            this.countryId != null &&
            this.identification != null &&
            this.identificationType != null &&
            this.department != null &&
            this.municipality != null
        );
    }
}
