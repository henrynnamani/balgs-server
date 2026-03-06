package com.graey.Balgs.service;

import com.graey.Balgs.common.enums.OrderStatus;
import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.mapper.OrderMapper;
import com.graey.Balgs.common.messages.CartMessages;
import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.dto.order.*;
import com.graey.Balgs.model.*;
import com.graey.Balgs.repo.CartRepo;
import com.graey.Balgs.repo.OrderRepo;
import com.graey.Balgs.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {
    @Autowired
    private OrderRepo repo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public OrderResponse checkout(User user, PlaceOrderRequest request) {
        Cart cart = cartRepo.findByUserId(user.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
                );

        User existingUser = userRepo.findById(user.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND)
                );

        Order order = new Order();

        order.setUser(existingUser);
        order.setStatus(OrderStatus.PENDING);

        order.setDeliveryAddress(DeliveryAddress.builder()
                        .state(request.getDeliveryAddress().getState())
                        .user(existingUser)
                        .email(user.getEmail())
                        .city(request.getDeliveryAddress().getCity())
                        .streetAddress(request.getDeliveryAddress().getStreetAddress())
                        .phoneNumber(user.getPhoneNumber())
                .build());

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
                    orderItemResponse.setProduct(OrderProductSummary.builder()
                                    .id(item.getProduct().getId())
                                    .name(item.getProduct().getModel())
                                    .color(item.getProduct().getColor())
                                    .romSize(item.getProduct().getRomSize().name())
                                    .imageUrl(item.getProduct().getImageUrls().getFirst())
                                    .condition(item.getProduct().getCondition().name())
                            .build());

                    orderItemResponse.setPriceAtPurchase(item.getPriceAtPurchase());
                    orderItemResponse.setOrderId(savedOrder.getId());

                    List<OrderItemAddOnResponse> orderItemAddOnResponses = item.getAddons().stream()
                            .map(addon -> {
                                OrderItemAddOnResponse addOnResponse = new OrderItemAddOnResponse();

                                ProductResponse addOnProduct = new ProductResponse();

                                addOnProduct.setId(addon.getProduct().getId());
                                addOnProduct.setName(addon.getProduct().getName());
                                addOnProduct.setPrice(addon.getProduct().getPrice());

                                addOnResponse.setId(addon.getId());
                                addOnResponse.setProduct(addOnProduct);
                                addOnResponse.setOrderItemId(orderItemResponse.getId());
                                addOnResponse.setPriceAtPurchase(addon.getPriceAtPurchase());

                                return addOnResponse;
                            }).toList();

                    orderItemResponse.setAddons(orderItemAddOnResponses);

                    return orderItemResponse;
                }).toList();

        orderResponse.setItems(orderItemResponses);

        return orderResponse;
    }

    public OrderDetailResponse getOrder(UUID id) {
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();

        Order order = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        orderDetailResponse.setId(order.getId());
        orderDetailResponse.setStatus(order.getStatus().name());
        orderDetailResponse.setTotalPrice(order.getTotalPrice());
        orderDetailResponse.setReference(String.valueOf(order.getId()));
        orderDetailResponse.setDeliveryAddress(order.getDeliveryAddress());

        List<OrderItemResponse> orderItemResponses = order.getItems().stream().map(orderItem -> {
            OrderItemResponse orderItemResponse = new OrderItemResponse();

            orderItemResponse.setId(orderItem.getId());
            orderItemResponse.setOrderId(order.getId());
            orderItemResponse.setProduct(
                    OrderProductSummary.builder()
                            .id(orderItem.getProduct().getId())
                            .name(orderItem.getProduct().getModel())
                            .color(orderItem.getProduct().getColor())
                            .condition(orderItem.getProduct().getCondition().name())
                            .imageUrl(orderItem.getProduct().getImageUrls().getFirst())
                            .romSize(orderItem.getProduct().getRomSize().name())
                            .build()
            );
            orderItemResponse.setPriceAtPurchase(orderItem.getPriceAtPurchase());
            orderItemResponse.setVendorName(orderItem.getProduct().getVendor().getBusinessName());
            orderItemResponse.setPurchaseTime(orderItem.getPurchaseDate());
            orderItemResponse.setAddons(
                    orderItem.getAddons().stream().map(addon ->
                            OrderItemAddOnResponse.builder()
                                    .id(addon.getId())
                                    .orderItemId(orderItem.getId())
                                    .product(
                                            ProductResponse.builder()
                                                    .id(addon.getProduct().getId())
                                                    .name(addon.getProduct().getName())
                                                    .price(addon.getProduct().getPrice())
                                                    .build()
                                    )
                                    .priceAtPurchase(addon.getPriceAtPurchase())
                                    .build()
                    ).toList()
            );

            return orderItemResponse; // ← was missing
        }).toList();

        orderDetailResponse.setItems(orderItemResponses);

        return orderDetailResponse;
    }

    public void completePayment(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        order.setStatus(OrderStatus.CONFIRMED);

        repo.save(order);
    }

    public boolean isOrderPaymentCompleted(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        return order.getStatus() == OrderStatus.CONFIRMED;
    }

    public BigDecimal getOrderTotalAmount(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        return order.getTotalPrice();
    }

    public Page<OrderResponse> getAllOrder(UUID userId, Pageable pageable) {
        return repo.findByUserId(userId, pageable).map(orderMapper::toResponse);
    }
}
