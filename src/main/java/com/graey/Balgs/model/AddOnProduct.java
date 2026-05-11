package com.graey.Balgs.model;

import com.graey.Balgs.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "addon_products")
public class AddOnProduct extends BaseEntity  {
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private int stock = 0;
}
