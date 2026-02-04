package com.graey.Balgs.service;

import com.graey.Balgs.common.messages.ProductMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.product.CreateProduct;
import com.graey.Balgs.dto.product.UpdateProduct;
import com.graey.Balgs.model.Product;
import com.graey.Balgs.repo.ProductRepo;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;

    @Autowired
    private CloudinaryService cloudinaryService;

    public ResponseEntity<ApiResponse<Product>> createProduct(CreateProduct product, List<MultipartFile> images, MultipartFile video) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        String videoUrl = null;

        for(MultipartFile image : images) {
            String url = cloudinaryService.uploadFile(image, "image");
            imageUrls.add(url);
        }

        Product newProduct = new Product();

        newProduct.setModel(product.getModel());
        newProduct.setPrice(product.getPrice());
        newProduct.setBatteryHealth(product.getBatteryHealth());
        newProduct.setColor(product.getColor());
        newProduct.setCondition(product.getCondition());
        newProduct.setRamSize(product.getRamSize());
        newProduct.setRomSize(product.getRomSize());
        newProduct.setFaceIdPresent(product.getFaceIdPresent());
        newProduct.setTrueTonePresent(product.getTrueTonePresent());
        newProduct.setImageUrls(imageUrls);


        if(video != null && !video.isEmpty()) {
            videoUrl = cloudinaryService.uploadFile(video, "video");
            newProduct.setVideoUrl(videoUrl);
        }

        Product savedProduct = repo.save(newProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ProductMessages.PRODUCT_CREATED, savedProduct));
    }

    public ResponseEntity<ApiResponse<List<Product>>> getProducts() {
        try {
            List<Product> products = repo.findAll();

            return ResponseEntity.ok(ApiResponse.success(ProductMessages.PRODUCT_LIST, products));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    public ResponseEntity<ApiResponse<String>> removeProduct(String productId) {
        try {
            Optional<Product> productExist = repo.findById(productId);

            if (productExist.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(ProductMessages.PRODUCT_NOTFOUND));
            }

            Product product = productExist.get();

            repo.delete(product);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(ProductMessages.PRODUCT_DELETED));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    public ResponseEntity<ApiResponse<Product>> updateProduct(String productId, UpdateProduct update, List<MultipartFile> images, MultipartFile video) {
        try {
            Optional<Product> productExist = repo.findById(productId);

            if(productExist.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ProductMessages.PRODUCT_NOTFOUND));
            }

            Product product = productExist.get();

            if (update.getModel() != null) {
                product.setModel(update.getModel());
            }

            if (update.getRamSize() != null) {
                product.setRamSize(update.getRamSize());
            }

            if (update.getRomSize() != null) {
                product.setRomSize(update.getRomSize());
            }

            if (update.getCondition() != null) {
                product.setCondition(update.getCondition());
            }

            if (update.getFaceIdPresent() != null) {
                product.setFaceIdPresent(update.getFaceIdPresent());
            }

            if (update.getTrueTonePresent() != null) {
                product.setTrueTonePresent(update.getTrueTonePresent());
            }

            if (update.getBatteryHealth() != null) {
                product.setBatteryHealth(update.getBatteryHealth());
            }

            if (update.getPrice() != null) {
                product.setPrice(update.getPrice());
            }

            if (update.getColor() != null) {
                product.setColor(update.getColor());
            }

            if (images != null && !images.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();

                for (MultipartFile image : images) {
                    imageUrls.add(cloudinaryService.uploadFile(image, "image"));
                }

                product.setImageUrls(imageUrls);
            }

            if (video != null && !video.isEmpty()) {
                String videoUrl = cloudinaryService.uploadFile(video, "video");
                product.setVideoUrl(videoUrl);
            }

            Product updatedProduct = repo.save(product);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            ProductMessages.PRODUCT_UPDATED,
                            updatedProduct
                    )
            );
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
