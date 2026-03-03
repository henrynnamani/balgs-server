package com.graey.Balgs.controller;

import com.graey.Balgs.common.messages.AddOnProductMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.addon.AddOnProductDto;
import com.graey.Balgs.dto.addon.AddOnProductResponse;
import com.graey.Balgs.service.AddonProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("addons")
@RequiredArgsConstructor
@Tag(name = "Addon")
public class AddOnProductController {
    @Autowired
    private AddonProductService service;

    @PostMapping
    @Operation(summary = "create addon product")
    public ResponseEntity<ApiResponse<AddOnProductResponse>> createAddOnProduct(@RequestPart AddOnProductDto addonProduct, @RequestPart(required = true) MultipartFile image) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(AddOnProductMessages.ADDON_PRODUCT_ADDED_SUCCESSFULLY, service.createAddOnProduct(addonProduct, image)));
    }

    @GetMapping
    @Operation(summary = "get addon products")
    public ResponseEntity<ApiResponse<List<AddOnProductResponse>>> getAddonProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(AddOnProductMessages.ADDON_PRODUCT_LIST, service.getAddons()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "update addon product")
    public ResponseEntity<ApiResponse<AddOnProductResponse>> updateAddOnProduct(@PathVariable("id") String id, @RequestPart AddOnProductDto updateProduct, @RequestPart(required = true) MultipartFile image) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(AddOnProductMessages.ADDON_PRODUCT_UPDATED_SUCCESSFULLY, service.updateAddOnProduct(UUID.fromString(id), updateProduct, image)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete addon product")
    public ResponseEntity<ApiResponse<String>> deleteAddOnProduct(@PathVariable("id") String id) {
        service.deleteAddOnProduct(UUID.fromString(id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(AddOnProductMessages.ADDON_PRODUCT_DELETED_SUCCESSFULLY));
    }
}
