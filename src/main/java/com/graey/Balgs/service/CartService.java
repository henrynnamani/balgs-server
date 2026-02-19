package com.graey.Balgs.service;

import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.CartItemMessages;
import com.graey.Balgs.common.messages.CartMessages;
import com.graey.Balgs.common.messages.ProductMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.cart.CartAddOnDto;
import com.graey.Balgs.dto.cart.CartDto;
import com.graey.Balgs.dto.cart.CartItemResponse;
import com.graey.Balgs.dto.cart.CartResponse;
import com.graey.Balgs.model.*;
import com.graey.Balgs.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CartService {
    @Autowired
    private CartRepo repo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private AddOnProductRepo addOnProductRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private CartItemAddOnRepo cartItemAddOnRepo;

    @Transactional
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(CartDto cartDto) {
        Cart cartExist = repo.findByUserId(UUID.fromString(cartDto.getUserId()))
                .orElseGet(() -> createCartForUser(UUID.fromString(cartDto.getUserId())));

        Product product = productRepo.findById(UUID.fromString(cartDto.getProductId())).orElseThrow(
                () -> new ResourceNotFoundException(ProductMessages.PRODUCT_NOTFOUND)
        );

        boolean productAlreadyInCart = cartExist.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(UUID.fromString(cartDto.getProductId())));

        if(productAlreadyInCart) {
            throw new IllegalStateException(CartMessages.PRODUCT_ALREADY_IN_CART);
        }

        CartItem item = new CartItem();
        item.setCart(cartExist);
        item.setProduct(product);
        item.setPriceAtAdd(product.getPrice());

        cartExist.getItems().add(item);

        cartExist.setLastUpdatedAt(LocalDateTime.now());

        cartExist.setTotalPrice(
                cartExist.getItems().stream()
                        .map(CartItem::getPriceAtAdd)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        Cart savedCart = repo.save(cartExist);

        CartResponse response = new CartResponse(
                savedCart.getId(),
                savedCart.getUser().getId(),
                savedCart.getItems().stream()
                        .map(i -> new CartItemResponse(
                                i.getId(),
                                i.getProduct().getId(),
                                i.getPriceAtAdd()
                        )).toList(),
                cartExist.getTotalPrice()
        );

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(CartMessages.PRODUCT_ADDED_SUCCESSFULLY, response));
    }

    public ResponseEntity<ApiResponse<Cart>> clearCart(UUID userId) {
        Cart cart = repo.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
        );

        cart.getItems().clear();

        cart.setTotalPrice(BigDecimal.ZERO);

        Cart clearedCart = repo.save(cart);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(CartMessages.CART_CLEARED_SUCCESSFULLY, clearedCart));
    }

    @Transactional
    public ResponseEntity<ApiResponse<Cart>> removeFromCart(String userId, String productId) {

        Cart cart = repo.findByUserId(UUID.fromString(userId))
                .orElseThrow(() ->
                        new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
                );

        cart.getItems().removeIf(
                item -> item.getProduct().getId().equals(UUID.fromString(productId))
        );

        cart.setTotalPrice(
                cart.getItems().stream()
                        .map(CartItem::getPriceAtAdd)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        Cart savedCart = repo.save(cart);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(CartMessages.PRODUCT_REMOVED_SUCCESSFULLY, savedCart));
    }

    public ResponseEntity<ApiResponse<String>> attachAddOnProduct(CartAddOnDto addonDto) {
        CartItem cartItem = cartItemRepo.findById(addonDto.getCartItemId()).orElseThrow(
                () -> new ResourceNotFoundException(CartItemMessages.CART_ITEM_NOTFOUND)
        );

        AddOnProduct addOnProduct = addOnProductRepo.findById(addonDto.getAddOnProductId())
                .orElseThrow(() -> new ResourceNotFoundException(ProductMessages.PRODUCT_NOTFOUND));

        CartItemAddOn addon = new CartItemAddOn();

        addon.setCartItem(cartItem);
        addon.setProduct(addOnProduct);

        cartItemAddOnRepo.save(addon);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(CartItemMessages.ADDON_ADDED_TO_CART_ITEM)
        );
    }

    public Cart createCartForUser(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.ZERO);

        return repo.save(cart);
    }
}
