package com.graey.Balgs.service;

import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.AddOnProductMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.addon.AddOnProductDto;
import com.graey.Balgs.dto.addon.AddOnProductResponse;
import com.graey.Balgs.model.AddOnProduct;
import com.graey.Balgs.repo.AddOnProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class AddonProductService {
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private AddOnProductRepo repo;

    public List<AddOnProductResponse> getAddons() {
        return repo.findAll().stream().map(addon -> new AddOnProductResponse().builder()
                    .id(addon.getId())
                    .price(addon.getPrice())
                    .name(addon.getName())
                    .imageUrl(addon.getImageUrl())
                    .build()
        ).toList();
    }

    public AddOnProductResponse createAddOnProduct(AddOnProductDto addOnProduct, MultipartFile image) throws IOException {
        String imageUrl = null;

        AddOnProduct product = new AddOnProduct();

        if(image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(image, "image");
            product.setImageUrl(imageUrl);
        }

        product.setName(addOnProduct.getName());
        product.setPrice(addOnProduct.getPrice());

        AddOnProduct savedProduct = repo.save(product);
        AddOnProductResponse response = getResponse(savedProduct);

        return response;
    }

    public void deleteAddOnProduct(UUID id) {
        AddOnProduct product = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(AddOnProductMessages.ADDON_PRODUCT_NOT_FOUND)
        );

        repo.delete(product);
    }

    public AddOnProductResponse updateAddOnProduct(UUID id, AddOnProductDto addOnProduct, MultipartFile image) throws IOException {
        AddOnProduct product = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(AddOnProductMessages.ADDON_PRODUCT_UPDATED_SUCCESSFULLY)
        );
        String imageUrl = null;

        if(image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadFile(image, "image");
            product.setImageUrl(imageUrl);
        }

        if(product.getPrice().equals(addOnProduct.getPrice())) {
            product.setPrice(addOnProduct.getPrice());
        }

        if(product.getName().equals(addOnProduct.getName())) {
            product.setName(addOnProduct.getName());
        }

        AddOnProduct savedProduct = repo.save(product);

        AddOnProductResponse response = getResponse(savedProduct);

        return response;
    }

    private AddOnProductResponse getResponse(AddOnProduct product) {
        AddOnProductResponse response = new AddOnProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setImageUrl(product.getImageUrl());

        return response;
    }
}
