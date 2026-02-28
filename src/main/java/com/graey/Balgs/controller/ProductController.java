package com.graey.Balgs.controller;

import com.graey.Balgs.common.messages.ProductMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.product.CreateProduct;
import com.graey.Balgs.dto.product.ProductDetailResponse;
import com.graey.Balgs.dto.product.ProductResponse;
import com.graey.Balgs.dto.product.UpdateProduct;
import com.graey.Balgs.model.Product;
import com.graey.Balgs.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "products", description = "product endpoints")
@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private ProductService service;

    @Operation(summary = "create new product")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestPart CreateProduct product, @RequestParam(required = false) List<MultipartFile> images, @RequestParam(required = false) MultipartFile video) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ProductMessages.PRODUCT_CREATED, service.createProduct(product, images, video)));
    }

    @Operation(summary = "get a product")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(ProductMessages.PRODUCT_DETAIL, service.getProduct(UUID.fromString(id))));
    }

    @Operation(summary = "get all products")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, limit, sort);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(ProductMessages.PRODUCT_LIST, service.getProducts(pageable)));
    }

    @Operation(summary = "update product")
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @RequestPart UpdateProduct product, @RequestParam List<MultipartFile> images, @RequestParam MultipartFile video) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(ProductMessages.PRODUCT_UPDATED, service.updateProduct(productId, product, images, video)));
    }

    @Operation(summary = "delete product")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> removeProduct(@PathVariable String productId){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(service.removeProduct(productId)));
    }

}
