package com.graey.Balgs.service;

import com.graey.Balgs.common.enums.OrderStatus;
import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.CartMessages;
import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.order.OrderItemAddOnResponse;
import com.graey.Balgs.dto.order.OrderItemResponse;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.model.*;
import com.graey.Balgs.repo.CartRepo;
import com.graey.Balgs.repo.OrderRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepo repo;

    @Autowired
    private CartRepo cartRepo;

    @Transactional
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(UUID userId) {
        Cart cart = cartRepo.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
        );

        Order order = new Order();

        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for(CartItem item : cart.getItems()) {
            Product product = item.getProduct();

            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(product.getId());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItem.setProductName(product.getModel());
            orderItem.setOrder(order);

            total = total.add(
                    orderItem.getPriceAtPurchase()
            );

            if(item.getAddons() != null) {
                for(CartItemAddOn addon : item.getAddons()) {
                    OrderItemAddOn orderItemAddOn = new OrderItemAddOn();

                    AddOnProduct addOnProduct = addon.getProduct();

                    orderItemAddOn.setItem(orderItem);
                    orderItemAddOn.setProductId(addOnProduct.getId());
                    orderItemAddOn.setPriceAtPurchase(addOnProduct.getPrice());
                    orderItemAddOn.setProductName(addOnProduct.getName());

                    orderItem.getAddons().add(orderItemAddOn);

                    total = total.add(
                            addOnProduct.getPrice()
                    );
                }
            }
            order.getItems().add(orderItem);
        }
            order.setTotalAmount(total);
            cartRepo.delete(cart);

        repo.save(order);

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setUserId(userId);
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponse> orderItemResponses = order.getItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();

                    itemResponse.setId(item.getId());
                    itemResponse.setOrderId(order.getId());
                    itemResponse.setProductId(item.getProductId());
                    itemResponse.setProductName(item.getProductName());
                    itemResponse.setPriceAtPurchase(item.getPriceAtPurchase());

                    List<OrderItemAddOnResponse> addons =
                            item.getAddons().stream()
                                    .map(addon -> {
                                        OrderItemAddOnResponse a = new OrderItemAddOnResponse();
                                        a.setId(addon.getId());
                                        a.setOrderItemId(item.getId());
                                        a.setProductId(addon.getProductId());
                                        a.setProductName(addon.getProductName());
                                        a.setPriceAtPurchase(addon.getPriceAtPurchase());
                                        return a;
                                    }).toList();

                    itemResponse.setAddons(addons);
                    return itemResponse;
                }).toList();

        response.setItems(orderItemResponses);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(OrderMessages.CHECKOUT_COMPLETED, response));
    }
}
