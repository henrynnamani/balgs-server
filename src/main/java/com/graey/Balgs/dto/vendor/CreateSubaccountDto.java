package com.graey.Balgs.dto.vendor;

import lombok.Data;

@Data
public class CreateSubaccountDto {
    private String business_name;
    private String settlement_bank;
    private String account_number;
    private Double percentage_charge;
}
