package com.graey.Balgs.model;

import com.graey.Balgs.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "cart_item_addon")
public class CartItemAddOn extends BaseEntity  {
    @ManyToOne
    private CartItem cartItem;

    @ManyToOne
    private AddOnProduct product;
}
