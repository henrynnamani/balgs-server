package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.product.CreateProduct;
import com.graey.Balgs.dto.product.UpdateProduct;
import com.graey.Balgs.model.Product;
import com.graey.Balgs.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "products", description = "product endpoints")
@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private ProductService service;

    @Operation(summary = "create new product")
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestPart CreateProduct product, @RequestParam(required = false) List<MultipartFile> images, @RequestParam(required = false) MultipartFile video) throws IOException {
        return service.createProduct(product, images, video);
    }

    @Operation(summary = "get all products")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getProducts() {
        return service.getProducts();
    }

    @Operation(summary = "update product")
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String productId, @RequestPart UpdateProduct product, @RequestParam List<MultipartFile> images, @RequestParam MultipartFile video) {
        return service.updateProduct(productId, product, images, video);
    }

    @Operation(summary = "delete product")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> removeProduct(@PathVariable String productId){
        return service.removeProduct(productId);
    }

}
