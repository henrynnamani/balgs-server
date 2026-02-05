package com.graey.Balgs.controller;

import com.graey.Balgs.common.enums.VendorStatus;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.vendor.UpdateVendor;
import com.graey.Balgs.dto.vendor.VendorDto;
import com.graey.Balgs.dto.vendor.VendorResponse;
import com.graey.Balgs.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "vendors", description = "vendor management")
@RestController
@RequiredArgsConstructor
@RequestMapping("vendors")
public class VendorController {

    @Autowired
    private VendorService service;

    @Operation(summary = "setup vendor profile")
    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponse>> setupAccount(@RequestBody VendorDto vendorDto) {
        return service.setupAccount(vendorDto);
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
}
