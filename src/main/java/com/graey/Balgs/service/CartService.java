package com.graey.Balgs.service;

import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.CartMessages;
import com.graey.Balgs.common.messages.ProductMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.cart.CartDto;
import com.graey.Balgs.dto.cart.CartItemResponse;
import com.graey.Balgs.dto.cart.CartResponse;
import com.graey.Balgs.model.Cart;
import com.graey.Balgs.model.CartItem;
import com.graey.Balgs.model.Product;
import com.graey.Balgs.model.User;
import com.graey.Balgs.repo.CartRepo;
import com.graey.Balgs.repo.ProductRepo;
import com.graey.Balgs.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CartService {
    @Autowired
    private CartRepo repo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

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


    public Cart createCartForUser(UUID userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.ZERO);

        return repo.save(cart);
    }
}
