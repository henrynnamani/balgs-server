package com.graey.Balgs.service;

import com.graey.Balgs.common.enums.OrderStatus;
import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.CartMessages;
import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.order.OrderItemAddOnResponse;
import com.graey.Balgs.dto.order.OrderItemResponse;
import com.graey.Balgs.dto.order.OrderRequest;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.model.*;
import com.graey.Balgs.repo.CartRepo;
import com.graey.Balgs.repo.OrderRepo;
import com.graey.Balgs.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepo repo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CartRepo cartRepo;

    @Transactional
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(OrderRequest orderDto) {
        Cart cart = cartRepo.findByUserId(UUID.fromString(orderDto.userId()))
                .orElseThrow(
                        () -> new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
                );

        User user = userRepo.findById(UUID.fromString(orderDto.userId()))
                .orElseThrow(
                        () -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND)
                );

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setPriceAtPurchase(item.getPriceAtAdd());

            order.getItems().add(orderItem);

            totalPrice = totalPrice.add(orderItem.getPriceAtPurchase());

            if(item.getAddons() != null) {
                for (CartItemAddOn addon : item.getAddons()) {
                    OrderItemAddOn orderItemAddOn = new OrderItemAddOn();

                    orderItemAddOn.setOrderItem(orderItem);
                    orderItemAddOn.setPriceAtPurchase(addon.getProduct().getPrice());
                    orderItemAddOn.setProduct(addon.getProduct());

                    orderItem.getAddons().add(orderItemAddOn);
                    totalPrice = totalPrice.add(orderItemAddOn.getPriceAtPurchase());
                }
            }
        }

        order.setTotalPrice(totalPrice);

        Order savedOrder = repo.save(order);

        cartRepo.delete(cart);

        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId(savedOrder.getId());
        orderResponse.setUserId(user.getId());
        orderResponse.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(savedOrder.getCreatedAt());

        List<OrderItemResponse> orderItemResponses = savedOrder.getItems().stream()
                .map(item -> {
                    OrderItemResponse orderItemResponse = new OrderItemResponse();

                    orderItemResponse.setId(item.getId());
                    orderItemResponse.setProduct(item.getProduct());
                    orderItemResponse.setPriceAtPurchase(item.getPriceAtPurchase());
                    orderItemResponse.setOrderId(savedOrder.getId());

                    List<OrderItemAddOnResponse> orderItemAddOnResponses = item.getAddons().stream()
                            .map(addon -> {
                                OrderItemAddOnResponse addOnResponse = new OrderItemAddOnResponse();

                                addOnResponse.setId(addon.getId());
                                addOnResponse.setProduct(addon.getProduct());
                                addOnResponse.setOrderItemId(orderItemResponse.getId());
                                addOnResponse.setPriceAtPurchase(addon.getPriceAtPurchase());

                                return addOnResponse;
                            }).toList();

                    orderItemResponse.setAddons(orderItemAddOnResponses);

                    return orderItemResponse;
                }).toList();

        orderResponse.setItems(orderItemResponses);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(OrderMessages.ORDER_PLACED_SUCCESSFULLY, orderResponse));
    }
}
