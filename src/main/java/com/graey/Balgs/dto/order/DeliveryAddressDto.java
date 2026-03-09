package com.graey.Balgs.dto.order;

import lombok.Data;

@Data
public class DeliveryAddressDto {
    private String state;
    private String city;
    private String streetAddress;
    private String nearestLandmark;
}

