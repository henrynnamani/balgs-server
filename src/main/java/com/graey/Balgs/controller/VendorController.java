package com.graey.Balgs.controller;

import com.graey.Balgs.common.enums.OrderStatus;
import com.graey.Balgs.common.enums.VendorStatus;
import com.graey.Balgs.common.messages.VendorMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.order.UpdateOrderStatusRequest;
import com.graey.Balgs.dto.vendor.*;
import com.graey.Balgs.model.User;
import com.graey.Balgs.service.OrderService;
import com.graey.Balgs.service.OrderSseService;
import com.graey.Balgs.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "vendors", description = "vendor management")
@RestController
@RequiredArgsConstructor
@RequestMapping("vendors")
public class VendorController {

    @Autowired
    private VendorService service;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderSseService sseService;

    @Operation(summary = "get dashboard data")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardData>> getDashboardData(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(VendorMessages.VENDOR_DASHBOARD_DATA, service.getDashboardData(user.getId())));
    }

    @Operation(summary = "get vendor orders")
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<VendorOrdersResponse>>> getVendorOrders(@AuthenticationPrincipal User user, @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int limit,
                                                                                   @RequestParam(defaultValue = "id") String sortBy,
                                                                                   @RequestParam(defaultValue = "true") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sort);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(VendorMessages.VENDOR_ORDER_LIST, service.getVendorOrders(pageable, user)));
    }

    @Operation(summary = "setup vendor profile")
    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponse>> setupAccount(@RequestBody VendorDto vendorDto, @AuthenticationPrincipal User user) {
        return service.setupAccount(vendorDto, user.getId());
    }

    @Operation(summary = "update vendor profile")
    @PutMapping("/{vendorId}")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendorProfile(@PathVariable("vendorId") String id, @RequestBody UpdateVendor vendor) {
        return service.updateVendorProfile(UUID.fromString(id), vendor);
    }

    @Operation(summary = "activate vendor")
    @PutMapping("/{vendorId}/activate")
    public ResponseEntity<ApiResponse<VendorResponse>> activateVendor(@PathVariable("vendorId") String id) {
        return service.modifyVendorStatus(UUID.fromString(id), VendorStatus.APPROVED);
    }

    @Operation(summary = "suspend vendor")
    @PutMapping("/{vendorId}/suspend")
    public ResponseEntity<ApiResponse<VendorResponse>> suspendVendor(@PathVariable("vendorId") String id) {
        return service.modifyVendorStatus(UUID.fromString(id), VendorStatus.SUSPENDED);
    }

    @Operation(summary = "toggle vendor verification")
    @PutMapping("/{vendorId}/toggle-verification")
    public ResponseEntity<ApiResponse<VendorResponse>> toggleVerification(@PathVariable("vendorId") String id) {
        return service.toggleVerification(UUID.fromString(id));
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "update order status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(@PathVariable("id") String orderId, @RequestBody UpdateOrderStatusRequest request) {
        if (request.status() == OrderStatus.DELIVERED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only the buyer can confirm delivery"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(orderService.updateStatus(UUID.fromString(orderId), request.status())));
    }
}
