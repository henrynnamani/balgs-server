package com.graey.Balgs.controller;

import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.order.OrderRequest;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
@Tag(name = "order management", description = "")
public class OrderController {

    @Autowired
    private OrderService service;

    @PostMapping
    @Operation(summary = "place order")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(@RequestBody OrderRequest order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(OrderMessages.ORDER_PLACED_SUCCESSFULLY, service.checkout(order)));
    }

    @GetMapping
    @Operation(summary = "get all order")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sort);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderMessages.ORDER_LIST,service
                        .getAllOrder(pageable)));
    }
}
