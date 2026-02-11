package com.graey.Balgs.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference
    private Cart cart;

    @OneToMany(mappedBy = "cartItem", orphanRemoval = true)
    private List<CartItemAddOn> addons;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    private BigDecimal priceAtAdd;
}
