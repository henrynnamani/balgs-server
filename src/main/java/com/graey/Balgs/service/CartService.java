package com.graey.Balgs.service;

import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.CartItemMessages;
import com.graey.Balgs.common.messages.CartMessages;
import com.graey.Balgs.common.messages.ProductMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.addon.AddOnProductResponse;
import com.graey.Balgs.dto.cart.*;
import com.graey.Balgs.model.*;
import com.graey.Balgs.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public CartResponse cartList(UUID userId) {
        Cart cart = repo.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
        );

        return getCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(CartDto cartDto, UUID userId) {
        Cart cartExist = repo.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userId));

        Product product = productRepo.findById(UUID.fromString(cartDto.getProductId())).orElseThrow(
                () -> new ResourceNotFoundException(ProductMessages.PRODUCT_NOTFOUND)
        );

        boolean productAlreadyInCart = cartExist.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(UUID.fromString(cartDto.getProductId())));

        if(productAlreadyInCart) {
            throw new IllegalStateException(CartMessages.PRODUCT_ALREADY_IN_CART);
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cartExist);
        cartItem.setProduct(product);
        cartItem.setPriceAtAdd(product.getPrice());

        cartExist.getItems().add(cartItem);

        cartExist.setLastUpdatedAt(LocalDateTime.now());

        cartExist.setTotalPrice(
                cartExist.getItems().stream()
                        .map(CartItem::getPriceAtAdd)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        Cart cart = repo.save(cartExist);

        return getCartResponse(cart);
    }

    public CartResponse clearCart(UUID userId) {
        Cart cart = repo.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
        );

        cart.getItems().clear();

        cart.setTotalPrice(BigDecimal.ZERO);

        Cart clearedCart = repo.save(cart);

        return getCartResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(String productId, UUID userId) {

        Cart cart = repo.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
                );

        cart.getItems().forEach(item -> {
            UUID itemProductId = item.getProduct().getId();
            UUID incomingId = UUID.fromString(productId);
        });

        boolean removed = cart.getItems().removeIf(
                item -> item.getId().equals(UUID.fromString(productId))
        );

        cart.setTotalPrice(
                cart.getItems().stream()
                        .map(CartItem::getPriceAtAdd)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        Cart savedCart = repo.save(cart);

        return getCartResponse(savedCart);
    }

    private static CartResponse getCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .totalPrice(cart.getTotalPrice())
                .lastUpdatedAt(cart.getLastUpdatedAt())
                .items(cart.getItems() == null ? new ArrayList<>() :
                        cart.getItems().stream().map(item -> {
                            List<AddOnProductResponse> addons = (item.getAddons() == null || item.getAddons().isEmpty())
                                    ? new ArrayList<>()
                                    : item.getAddons().stream().map(addon ->
                                    AddOnProductResponse.builder()
                                            .id(addon.getId())
                                            .name(addon.getProduct().getName())
                                            .price(addon.getProduct().getPrice())
                                            .build()
                            ).toList();

                            return CartItemDetailResponse.builder()
                                    .id(item.getId())
                                    .name(item.getProduct().getModel())
                                    .romSize(item.getProduct().getRomSize())
                                    .color(item.getProduct().getColor())
                                    .condition(item.getProduct().getCondition())
                                    .price(item.getProduct().getPrice())
                                    .imageUrl(item.getProduct().getImageUrls().getFirst())
                                    .addons(addons)
                                    .build();
                        }).toList()
                )
                .build();
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

    public String removeAddOnProduct(UUID id) {
       cartItemAddOnRepo.deleteById(id);

       return CartItemMessages.CART_ITEM_REMOVED;
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
