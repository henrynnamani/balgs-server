package com.graey.Balgs.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressResponse {
    private String phoneNumber;
    private String state;
    private String city;
    private String streetAddress;
}
