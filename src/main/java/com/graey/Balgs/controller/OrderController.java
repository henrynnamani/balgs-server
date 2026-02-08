package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.order.OrderRequest;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@RequestBody OrderRequest order) {
        return service.checkout(UUID.fromString(order.userId()));
    }

    @PutMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable("id") String id) {
        return "";
    }

    @GetMapping
    public String getOrders() {
        return "";
    }
}
