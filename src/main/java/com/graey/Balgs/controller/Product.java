package com.graey.Balgs.controller;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
public class Product {
    private UUID id;
    private String name;
    private Integer ram;
    private Integer rom;
    private Integer batteryHealth;

    @Column(columnDefinition = "jsonb")
    private String metadata;
    private String sku;
}
