package com.graey.Balgs.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "order_item_addons")
public class OrderItemAddOn {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem item;

    private UUID productId;

    private String productName;

    private BigDecimal priceAtPurchase;
}
