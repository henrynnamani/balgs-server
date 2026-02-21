package com.graey.Balgs.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "order_item_addon")
public class OrderItemAddOn {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private AddOnProduct product;

    private BigDecimal priceAtPurchase;
}
