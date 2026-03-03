package com.graey.Balgs.controller;

import com.graey.Balgs.common.messages.CartItemMessages;
import com.graey.Balgs.common.messages.CartMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.cart.CartAddOnDto;
import com.graey.Balgs.dto.cart.CartDto;
import com.graey.Balgs.dto.cart.CartListResponse;
import com.graey.Balgs.dto.cart.CartResponse;
import com.graey.Balgs.model.Cart;
import com.graey.Balgs.model.User;
import com.graey.Balgs.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "cart", description = "endpoints necessary for cart management")
@RestController
@RequiredArgsConstructor
@RequestMapping("carts")
public class CartController {
    @Autowired
    private CartService service;

    @Operation(summary = "get user cart")
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getUserCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(CartMessages.CART_lIST, service.cartList(user.getId())));
    }

    @Operation(summary = "add to cart")
    @PostMapping("add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@RequestBody CartDto cartDto, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(CartMessages.PRODUCT_ADDED_SUCCESSFULLY, service.addToCart(cartDto, user.getId())));
    }

    @Operation(summary = "remove product from cart")
    @PostMapping("remove")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(@RequestBody CartDto cartDto, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(CartMessages.PRODUCT_REMOVED_SUCCESSFULLY, service.removeFromCart(cartDto.getProductId(), user.getId())));

    }

    @PostMapping("addon")
    public ResponseEntity<ApiResponse<String>> attachAddOnProduct(@RequestBody CartAddOnDto cartAddOn) {
        return service.attachAddOnProduct(cartAddOn);
    }

    @DeleteMapping("addon/{id}")
    public ResponseEntity<ApiResponse<String>> removeAddOnProduct(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(CartItemMessages.ADDON_ADDED_TO_CART_ITEM, service.removeAddOnProduct(UUID.fromString(id)))
        );
    }

    @Operation(summary = "clear cart")
    @PostMapping("clear")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(CartMessages.CART_CLEARED_SUCCESSFULLY, service.clearCart(user.getId())));
    }
}
