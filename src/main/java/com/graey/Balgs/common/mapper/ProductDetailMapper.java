package com.graey.Balgs.common.mapper;

import com.graey.Balgs.dto.product.ProductDetailResponse;
import com.graey.Balgs.dto.rating.RatingResponse;
import com.graey.Balgs.dto.rating.RatingSummaryResponse;
import com.graey.Balgs.dto.user.UserResponse;
import com.graey.Balgs.dto.vendor.VendorDetailResponse;
import com.graey.Balgs.dto.vendor.VendorResponse;
import com.graey.Balgs.model.Product;
import com.graey.Balgs.model.Rating;
import com.graey.Balgs.model.RatingSummary;
import com.graey.Balgs.model.Vendor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDetailMapper {

    public ProductDetailResponse toResponse(Product product) {
        ProductDetailResponse response = new ProductDetailResponse();
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

    private VendorDetailResponse toVendorResponse(Vendor vendor) {
        List<RatingResponse> ratings = vendor.getRatings() == null
                ? new ArrayList<>()
                : vendor.getRatings().stream()
                .map(this::toRatingResponse)
                .collect(Collectors.toList());

        RatingSummaryResponse ratingSummary = toRatingSummaryResponse(vendor.getRatingSummary());

        VendorDetailResponse response = new VendorDetailResponse();
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
        response.setRatings(ratings);
        response.setRatingSummary(ratingSummary);
        return response;
    }

    private RatingResponse toRatingResponse(Rating rating) {
        UserResponse userResponse = UserResponse.builder()
                .id(rating.getUser().getId())
                .build();

        return RatingResponse.builder()
                .id(rating.getId())
                .vendorId(rating.getVendor().getId())
                .user(userResponse)
                .rating(rating.getRating())
                .review(rating.getReview())
                .createdAt(rating.getCreatedAt())
                .build();
    }

    private RatingSummaryResponse toRatingSummaryResponse(RatingSummary ratingSummary) {
        return RatingSummaryResponse.builder()
                .id(ratingSummary.getId())
                .totalRatings(ratingSummary.getTotalRatings())
                .averageRating(ratingSummary.getAverageRating())
                .build();
    }
}