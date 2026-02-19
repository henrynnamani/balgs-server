package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.order.OrderRequest;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
