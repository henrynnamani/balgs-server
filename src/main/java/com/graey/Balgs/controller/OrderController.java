package com.graey.Balgs.controller;

import com.graey.Balgs.common.enums.OrderStatus;
import com.graey.Balgs.common.messages.OrderMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.order.*;
import com.graey.Balgs.model.Order;
import com.graey.Balgs.model.User;
import com.graey.Balgs.service.OrderService;
import com.graey.Balgs.service.OrderSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
@Tag(name = "order management", description = "")
public class OrderController {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderSseService sseService;

    @PostMapping
    @Operation(summary = "place order")
    public ResponseEntity<ApiResponse<List<String>>> checkout(@AuthenticationPrincipal User user, @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(OrderMessages.ORDER_PLACED_SUCCESSFULLY, service.checkout(user, request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "get order detail")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(OrderMessages.ORDER_DETAIL, service.getOrder(UUID.fromString(id))));
    }

    @GetMapping
    @Operation(summary = "get all order")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrder(
            @AuthenticationPrincipal User userDetail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        UUID userId =  userDetail.getId();
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sort);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(OrderMessages.ORDER_LIST,service
                        .getAllOrder(userId, pageable)));
    }

    @PutMapping("{id}/confirm-delivery")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(
            @PathVariable("id") String orderId, @AuthenticationPrincipal User user) {

        String result = service.updateStatus(
                UUID.fromString(orderId),
                OrderStatus.DELIVERED
        );

        OrderDetailResponse order = service.getOrder(UUID.fromString(orderId));
        sseService.pushOrderUpdate(orderId, user.getId().toString(), OrderStatus.DELIVERED);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping(value = "/{orderId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to real-time order status updates")
    public SseEmitter streamOrderStatus(@PathVariable String orderId) {
        return sseService.subscribe(orderId);
    }

    @GetMapping(value = "/stream/user/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUserOrders(@PathVariable String userId) {
        return sseService.subscribeUser(userId);
    }


}
