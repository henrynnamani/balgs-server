package com.graey.Balgs.common.mapper;

import com.graey.Balgs.dto.product.ProductResponse;
import com.graey.Balgs.dto.vendor.VendorResponse;
import com.graey.Balgs.model.Product;
import com.graey.Balgs.model.Vendor;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setModel(product.getModel());
        response.setPrice(product.getPrice());
        response.setBatteryHealth(product.getBatteryHealth());
        response.setColor(product.getColor());
        response.setCondition(product.getCondition());
        response.setRamSize(product.getRamSize());
        response.setRomSize(product.getRomSize());
        response.setFaceIdPresent(product.getFaceIdPresent());
        response.setTrueTonePresent(product.getTrueTonePresent());
        response.setImageUrls(product.getImageUrls());
        response.setVendor(toVendorResponse(product.getVendor()));
        return response;
    }

    private VendorResponse toVendorResponse(Vendor vendor) {
        VendorResponse response = new VendorResponse();
        response.setId(vendor.getId());
        response.setBusinessName(vendor.getBusinessName());
        response.setUserId(vendor.getUser().getId());
        response.setLocation(vendor.getLocation());
        response.setStatus(vendor.getStatus());
        response.setVerified(vendor.getVerified());
        response.setPhoneNumber(vendor.getPhoneNumber());
        response.setStocksAvailable(vendor.getStocksAvailable());
        response.setAccountNumber(vendor.getAccountNumber());
        response.setBankName(vendor.getBankName());
        return response;
    }
}