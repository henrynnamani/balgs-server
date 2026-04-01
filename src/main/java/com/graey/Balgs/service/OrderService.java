package com.graey.Balgs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.graey.Balgs.common.enums.OrderStatus;
import com.graey.Balgs.common.exception.ProductUnavailableException;
import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.mapper.OrderMapper;
import com.graey.Balgs.common.messages.CartMessages;
import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.dto.order.*;
import com.graey.Balgs.model.*;
import com.graey.Balgs.repo.CartRepo;
import com.graey.Balgs.repo.OrderRepo;
import com.graey.Balgs.repo.ProductRepo;
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
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private OrderMapper orderMapper;

    @Transactional
    public List<String> checkout(User user, PlaceOrderRequest request) {
        Cart cart = cartRepo.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CartMessages.CART_NOTFOUND));

        User existingUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND));

        // Validate all items are still available before processing any
        for (CartItem item : cart.getItems()) {
            if (!item.getProduct().isAvailable()) {
                throw new ProductUnavailableException(
                        "Product no longer available: " + item.getProduct().getModel()
                );
            }
        }

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .city(request.getDeliveryAddress().getCity())
                .state(request.getDeliveryAddress().getState())
                .email(existingUser.getEmail())
                .phoneNumber(existingUser.getPhoneNumber())
                .streetAddress(request.getDeliveryAddress().getStreetAddress())
                .user(existingUser)
                .build();

        List<String> orderIds = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            Order order = new Order();
            order.setUser(existingUser);
            order.setStatus(OrderStatus.PENDING);
            order.setVendor(item.getProduct().getVendor());
            order.setDeliveryAddress(deliveryAddress);

            item.getProduct().setAvailable(false);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(item.getProduct())
                    .priceAtPurchase(item.getPriceAtAdd())
                    .build();

            List<OrderItemAddOn> addons = item.getAddons().stream().map(addon -> {
                OrderItemAddOn orderItemAddOn = new OrderItemAddOn();
                orderItemAddOn.setOrderItem(orderItem);
                orderItemAddOn.setProduct(addon.getProduct());
                orderItemAddOn.setPriceAtPurchase(addon.getProduct().getPrice());
                return orderItemAddOn;
            }).toList();

            orderItem.setAddons(addons);

            BigDecimal addonTotal = addons.stream()
                    .map(OrderItemAddOn::getPriceAtPurchase)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setItem(orderItem);
            order.setTotalPrice(item.getPriceAtAdd().add(addonTotal));

            Order saved = repo.save(order);
            orderIds.add(saved.getId().toString());
        }

        cartRepo.delete(cart);
        return orderIds;
    }

//    @Transactional
//    public List<String> checkout(User user, PlaceOrderRequest request) {
//        Cart cart = cartRepo.findByUserId(user.getId())
//                .orElseThrow(
//                        () -> new ResourceNotFoundException(CartMessages.CART_NOTFOUND)
//                );
//
//        User existingUser = userRepo.findById(user.getId())
//                .orElseThrow(
//                        () -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND)
//                );
//
//        List<String> orderIds = new ArrayList<>();
//
//            DeliveryAddress deliveryAddress = DeliveryAddress.builder()
//                            .city(request.getDeliveryAddress().getCity())
//                            .state(request.getDeliveryAddress().getState())
//                            .email(user.getEmail())
//                            .phoneNumber(user.getPhoneNumber())
//                            .streetAddress(request.getDeliveryAddress().getStreetAddress())
//                            .user(existingUser)
//                    .build();
//
//        for (CartItem item : cart.getItems()) {
//            Order order = new Order();
//            order.setUser(existingUser);
//            order.setStatus(OrderStatus.PENDING);
//            order.setVendor(item.getProduct().getVendor());
//
//            order.setDeliveryAddress(deliveryAddress);
//
//            item.getProduct().setAvailable(false);
//
//            OrderItem orderItem = OrderItem.builder()
//                    .order(order)
//                    .product(item.getProduct())
//                    .priceAtPurchase(item.getPriceAtAdd())
//                    .build();
//
//                List<OrderItemAddOn> addons = item.getAddons().stream().map(addon -> {
//                    OrderItemAddOn orderItemAddOn = new OrderItemAddOn();
//                    orderItemAddOn.setOrderItem(orderItem);
//                    orderItemAddOn.setProduct(addon.getProduct());
//                    orderItemAddOn.setPriceAtPurchase(addon.getProduct().getPrice());
//                    return orderItemAddOn;
//                }).toList();
//
//                orderItem.setAddons(addons);
//
//            BigDecimal addonTotal = addons.stream()
//                    .map(OrderItemAddOn::getPriceAtPurchase)
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            BigDecimal totalPrice = item.getPriceAtAdd().add(addonTotal);
//
//            order.setItem(orderItem);
//            order.setTotalPrice(totalPrice);
//
//            Order newOrder = repo.save(order);
//
//            System.out.println(newOrder);
//            orderIds.add(String.valueOf(newOrder.getId()));
//        }
//
//        cartRepo.delete(cart);
//
//        return orderIds;
//    }

    public OrderDetailResponse getOrder(UUID id) {
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();

        Order order = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        orderDetailResponse.setId(order.getId());
        orderDetailResponse.setStatus(order.getStatus().name());
        orderDetailResponse.setTotalPrice(order.getTotalPrice());
        orderDetailResponse.setReference(String.valueOf(order.getId()));

        orderDetailResponse.setDeliveryAddress(DeliveryAddressResponse.builder()
                        .phoneNumber(order.getDeliveryAddress().getPhoneNumber())
                        .state(order.getDeliveryAddress().getState())
                        .city(order.getDeliveryAddress().getCity())
                        .streetAddress(order.getDeliveryAddress().getStreetAddress())
                .build());

        OrderItemResponse orderItemResponse = new OrderItemResponse();

            orderItemResponse.setId(order.getItem().getId());
            orderItemResponse.setOrderId(order.getId());
            orderItemResponse.setProduct(
                    OrderProductSummary.builder()
                            .id(order.getItem().getProduct().getId())
                            .name(order.getItem().getProduct().getModel())
                            .color(order.getItem().getProduct().getColor())
                            .condition(order.getItem().getProduct().getCondition().name())
                            .imageUrl(order.getItem().getProduct().getImageUrls().getFirst())
                            .romSize(order.getItem().getProduct().getRomSize().name())
                            .build()
            );
            orderItemResponse.setPriceAtPurchase(order.getItem().getPriceAtPurchase());
            orderItemResponse.setVendorName(order.getItem().getProduct().getVendor().getBusinessName());
            orderItemResponse.setVendorId(order.getItem().getProduct().getVendor().getId());
            orderItemResponse.setPurchaseTime(order.getItem().getPurchaseDate());
            orderItemResponse.setAddons(
                    order.getItem().getAddons().stream().map(addon ->
                            OrderItemAddOnResponse.builder()
                                    .id(addon.getId())
                                    .orderItemId(order.getItem().getId())
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

        orderDetailResponse.setItem(orderItemResponse);

        return orderDetailResponse;
    }

    public String updateStatus(UUID id, OrderStatus orderStatus) {
        Order order = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        order.setStatus(orderStatus);

        repo.save(order);

        return OrderMessages.ORDER_STATUS_UPDATED;
    }

    public void completePayment(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        order.setStatus(OrderStatus.PROCESSING);

        repo.save(order);
    }

    public boolean isOrderPaymentCompleted(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        return order.getStatus() == OrderStatus.PROCESSING;
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
