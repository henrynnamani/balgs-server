package com.graey.Balgs.service;

import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.mapper.ProductDetailMapper;
import com.graey.Balgs.common.mapper.ProductMapper;
import com.graey.Balgs.common.messages.ProductMessages;
import com.graey.Balgs.common.messages.VendorMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.product.CreateProduct;
import com.graey.Balgs.dto.product.ProductDetailResponse;
import com.graey.Balgs.dto.product.ProductResponse;
import com.graey.Balgs.dto.product.UpdateProduct;
import com.graey.Balgs.dto.vendor.VendorResponse;
import com.graey.Balgs.model.Product;
import com.graey.Balgs.model.Vendor;
import com.graey.Balgs.repo.ProductRepo;
import com.graey.Balgs.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;

    @Autowired
    private VendorRepo vendorRepo;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductDetailMapper productDetailMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    public ProductResponse createProduct(CreateProduct product, List<MultipartFile> images, MultipartFile video) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        String videoUrl = null;

        Vendor vendor = vendorRepo.findById(UUID.fromString(product.getVendorId())).orElseThrow(
                () -> new ResourceNotFoundException(VendorMessages.VENDOR_NOTFOUND)
        );

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
        newProduct.setVendor(vendor);


        if(video != null && !video.isEmpty()) {
            videoUrl = cloudinaryService.uploadFile(video, "video");
            newProduct.setVideoUrl(videoUrl);
        }

        Product savedProduct = repo.save(newProduct);
        VendorResponse vendorResponse = new VendorResponse().builder()
                .id(vendor.getId())
                .userId(vendor.getUser().getId())
                .location(vendor.getLocation())
                .status(vendor.getStatus())
                .verified(vendor.getVerified())
                .phoneNumber(vendor.getPhoneNumber())
                .stocksAvailable(vendor.getStocksAvailable())
                .accountNumber(vendor.getAccountNumber())
                .bankName(vendor.getBankName())
                .build();

        ProductResponse productResponse = new ProductResponse().builder()
                .model(savedProduct.getModel())
                .price(savedProduct.getPrice())
                .batteryHealth(savedProduct.getBatteryHealth())
                .color(savedProduct.getColor())
                .condition(savedProduct.getCondition())
                .ramSize(savedProduct.getRamSize())
                .romSize(savedProduct.getRomSize())
                .faceIdPresent(savedProduct.getFaceIdPresent())
                .trueTonePresent(savedProduct.getTrueTonePresent())
                .imageUrls(savedProduct.getImageUrls())
                .vendor(vendorResponse)
                .build();

        return productResponse;
    }

    public Page<ProductResponse> getProducts(Pageable pageable) {
        return repo.findAll(pageable).map(productMapper::toResponse);
    }

    public ProductDetailResponse getProduct(UUID id) {
        return repo.findById(id).map(productDetailMapper::toResponse).orElseThrow(
                () -> new ResourceNotFoundException(ProductMessages.PRODUCT_NOTFOUND)
        );
    }

    public String removeProduct(String productId) {
        Product product = repo.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ResourceNotFoundException(ProductMessages.PRODUCT_NOTFOUND));

        repo.delete(product);

        return ProductMessages.PRODUCT_DELETED;
    }

    public ProductResponse updateProduct(String productId, UpdateProduct update, List<MultipartFile> images, MultipartFile video) {
        try {
            Optional<Product> productExist = repo.findById(UUID.fromString(productId));

            if(productExist.isEmpty()) {
                throw new ResourceNotFoundException(ProductMessages.PRODUCT_NOTFOUND);
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

            Vendor vendor = updatedProduct.getVendor();

            VendorResponse vendorResponse = new VendorResponse().builder()
                    .id(vendor.getId())
                    .userId(vendor.getUser().getId())
                    .location(vendor.getLocation())
                    .status(vendor.getStatus())
                    .verified(vendor.getVerified())
                    .phoneNumber(vendor.getPhoneNumber())
                    .stocksAvailable(vendor.getStocksAvailable())
                    .accountNumber(vendor.getAccountNumber())
                    .bankName(vendor.getBankName())
                    .build();

            ProductResponse productResponse = new ProductResponse().builder()
                    .model(updatedProduct.getModel())
                    .price(updatedProduct.getPrice())
                    .batteryHealth(updatedProduct.getBatteryHealth())
                    .color(updatedProduct.getColor())
                    .condition(updatedProduct.getCondition())
                    .ramSize(updatedProduct.getRamSize())
                    .romSize(updatedProduct.getRomSize())
                    .faceIdPresent(updatedProduct.getFaceIdPresent())
                    .trueTonePresent(updatedProduct.getTrueTonePresent())
                    .imageUrls(updatedProduct.getImageUrls())
                    .vendor(vendorResponse)
                    .build();

            return productResponse;
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
