package com.graey.Balgs.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "cart_item_addon")
public class CartItemAddOn {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private CartItem cartItem;

    @ManyToOne
    private AddOnProduct product;
}
