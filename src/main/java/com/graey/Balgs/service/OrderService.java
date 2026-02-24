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
    public OrderResponse checkout(OrderRequest orderDto) {
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

                    ProductResponse product = new ProductResponse();

                    product.setId(item.getProduct().getId());
                    product.setName(item.getProduct().getModel());
                    product.setPrice(item.getProduct().getPrice());

                    orderItemResponse.setId(item.getId());
                    orderItemResponse.setProduct(product);
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

    public void completePayment(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        order.setStatus(OrderStatus.PAID);

        repo.save(order);
    }

    public boolean isOrderPaymentCompleted(UUID orderId) {
        Order order = repo.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(OrderMessages.ORDER_NOT_FOUND)
        );

        return order.getStatus() == OrderStatus.PAID;
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
