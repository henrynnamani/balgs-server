package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.addon.AddOnProductDto;
import com.graey.Balgs.dto.addon.AddOnProductResponse;
import com.graey.Balgs.service.AddonProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("addons")
@RequiredArgsConstructor
public class AddOnProductController {
    @Autowired
    private AddonProductService service;

    @PostMapping
    public ResponseEntity<ApiResponse<AddOnProductResponse>> createAddOnProduct(@RequestPart AddOnProductDto addonProduct, @RequestPart(required = true) MultipartFile image) throws IOException {
        return service.createAddOnProduct(addonProduct, image);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddOnProductResponse>> updateAddOnProduct(@PathVariable("id") String id, @RequestPart AddOnProductDto updateProduct, @RequestPart(required = true) MultipartFile image) throws IOException {
        return service.updateAddOnProduct(UUID.fromString(id), updateProduct, image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddOnProduct(@PathVariable("id") String id) {
        return service.deleteAddOnProduct(UUID.fromString(id));
    }
}
