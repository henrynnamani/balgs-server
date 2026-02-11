package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.order.OrderRequest;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@RequestBody OrderRequest order) {
        return service.checkout(order);
    }
}
