package com.graey.Balgs.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductSummary {
        private UUID id;
        private String name;
        private String imageUrl;
        private String condition;
        private String romSize;
        private String color;
}
