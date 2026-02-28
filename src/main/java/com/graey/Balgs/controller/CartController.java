package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.cart.CartAddOnDto;
import com.graey.Balgs.dto.cart.CartDto;
import com.graey.Balgs.dto.cart.CartResponse;
import com.graey.Balgs.model.Cart;
import com.graey.Balgs.model.User;
import com.graey.Balgs.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "cart", description = "endpoints necessary for cart management")
@RestController
@RequiredArgsConstructor
@RequestMapping("carts")
public class CartController {
    @Autowired
    private CartService service;

    @Operation(summary = "add to cart")
    @PostMapping("add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@RequestBody CartDto cartDto, @AuthenticationPrincipal User user) {
        return service.addToCart(cartDto, user.getId());
    }

    @Operation(summary = "remove product from cart")
    @PostMapping("remove")
    public ResponseEntity<ApiResponse<Cart>> removeFromCart(@RequestBody CartDto cartDto, @AuthenticationPrincipal User user) {
        return service.removeFromCart(cartDto.getProductId(), user.getId());
    }

    @PostMapping("addon")
    public ResponseEntity<ApiResponse<String>> attachAddOnProduct(@RequestBody CartAddOnDto cartAddOn) {
        return service.attachAddOnProduct(cartAddOn);
    }

    @Operation(summary = "clear cart")
    @PostMapping("clear")
    public ResponseEntity<ApiResponse<Cart>> clearCart(@AuthenticationPrincipal User user) {
        return service.clearCart(user.getId());
    }
}
