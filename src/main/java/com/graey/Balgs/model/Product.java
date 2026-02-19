package com.graey.Balgs.model;

import com.graey.Balgs.common.enums.ProductCondition;
import com.graey.Balgs.common.enums.RamSize;
import com.graey.Balgs.common.enums.RomSize;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String model;

    @Enumerated(value = EnumType.STRING)
    private RamSize ramSize;

    @Enumerated(value = EnumType.STRING)
    private RomSize romSize;

    @Enumerated(value = EnumType.STRING)
    private ProductCondition condition;

    @Column(nullable = false)
    private Boolean faceIdPresent;

    @Column(nullable = false)
    private Boolean trueTonePresent;

    @Column(nullable = false)
    private Integer batteryHealth;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 30)
    private String color;

    @Column(nullable = true)
    private String videoUrl;

    @ManyToOne
    private Vendor vendor;

    @ElementCollection
    private List<String> imageUrls;
}
