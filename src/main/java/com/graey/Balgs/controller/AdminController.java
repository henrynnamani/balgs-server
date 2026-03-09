package com.graey.Balgs.controller;

import com.graey.Balgs.common.messages.AdminMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.admin.dashboard.DashboardResponse;
import com.graey.Balgs.dto.order.OrderResponse;
import com.graey.Balgs.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
@Tag(name = "admin")
public class AdminController {

    @Autowired
    private AdminService service;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard data")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(AdminMessages.ADMIN_DASHBOARD_DATA, service.getDashboardData()));
    }

    @GetMapping("/orders")
    @Operation(summary = "get all orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sort);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(AdminMessages.ADMIN_FETCH_ORDERS, service.getAllOrder(pageable)));
    }
}
