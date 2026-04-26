package com.graey.Balgs.service;

import com.graey.Balgs.common.enums.OrderStatus;
import com.graey.Balgs.common.enums.Role;
import com.graey.Balgs.common.enums.VendorStatus;
import com.graey.Balgs.common.exception.ResourceBadRequestException;
import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.mapper.OrderMapper;
import com.graey.Balgs.common.messages.RatingMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.common.messages.VendorMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.config.PaystackConfig;
import com.graey.Balgs.dto.vendor.*;
import com.graey.Balgs.model.RatingSummary;
import com.graey.Balgs.model.User;
import com.graey.Balgs.model.Vendor;
import com.graey.Balgs.repo.*;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VendorService {
    @Autowired
    private VendorRepo repo;
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private VendorOrdersMapper vendorOrdersMapper;

    @Autowired
    private RatingSummaryRepo ratingSummaryRepo;

    @Autowired
    private PaystackConfig paystackConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    public DashboardData getDashboardData(UUID userId) {
        Vendor vendor = repo.findByUserId(userId).orElseThrow(
                () -> new ResourceNotFoundException(VendorMessages.VENDOR_NOTFOUND)
        );

        RatingSummary vendorRating = ratingSummaryRepo.findByVendorId(vendor.getId()).orElseThrow(
                () -> new ResourceNotFoundException(RatingMessages.VENDOR_RATING_SUMMARY_NOT_FOUND)
        );

        LocalDateTime mtdStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now      = LocalDateTime.now();
        BigDecimal currentRevenue = orderRepo.sumRevenueByVendorAndDateRange(vendor.getId(), mtdStart, now);

        LocalDateTime lastStart = mtdStart.minusMonths(1);
        LocalDateTime lastEnd   = mtdStart.minusSeconds(1);
        BigDecimal lastRevenue  = orderRepo.sumRevenueByVendorAndDateRange(vendor.getId(), lastStart, lastEnd);

        BigDecimal totalRevenue = orderRepo.getTotalRevenueByVendor(vendor.getId());
        Long totalOrders = orderRepo.countTotalOrdersByVendor(vendor.getId());
        Long deliveredOrders = orderRepo.countOrdersByVendorAndStatus(vendor.getId(), OrderStatus.DELIVERED);
        BigDecimal averageOrderPerTransaction = orderRepo.getAverageOrderValueByVendor(vendor.getId());
        double rating = vendorRating.getAverageRating();

        double percentageChange = 0.0;
        if (lastRevenue.compareTo(BigDecimal.ZERO) != 0) {
            percentageChange = currentRevenue
                    .subtract(lastRevenue)
                    .divide(lastRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        percentageChange = Math.round(percentageChange * 10.0) / 10.0;

        List<VendorOrdersResponse> pendingOrders = orderRepo.findAllPendingOrderByVendorId(vendor.getId())
                .stream()
                .map(vendorOrdersMapper::toResponse)
                .toList();

        return DashboardData.builder()
                .vendorName(vendor.getBusinessName())
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .deliveredOrders(deliveredOrders)
                .percentageChange(percentageChange)
                .averageOrderPerTransaction(averageOrderPerTransaction)
                .rating(rating)
                .pendingOrders(pendingOrders)
                .build();
    }

    public Page<VendorOrdersResponse> getVendorOrders(Pageable pageable, User user) {
        Vendor vendor = repo.findByUserId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException(VendorMessages.VENDOR_NOTFOUND)
        );

        return orderRepo.findAllVendorOrder(vendor.getId(), pageable).map(vendorOrdersMapper::toResponse);
    }

    public Page<VendorResponse> getAllVendor(Pageable pageable) {
        return repo.findAll(pageable).map(VendorResponse::from);
    }

    public ResponseEntity<ApiResponse<VendorResponse>> setupAccount(VendorDto vendorDto, UUID userId) {
        User userExist = userRepo.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND)
        );

        userExist.setRole(Role.VENDOR);

        Vendor vendor = getVendor(vendorDto, userExist);

        Vendor savedVendor = repo.save(vendor);

        VendorResponse response = getResponse(savedVendor);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(VendorMessages.VENDOR_CREATED_SUCCESSFULLY, response)
        );
    }

    private static @NonNull Vendor getVendor(VendorDto vendorDto, User userExist) {
        RatingSummary ratingSummary = new RatingSummary();

        Vendor vendor = new Vendor();

        vendor.setBusinessName(vendorDto.getBusinessName());
        vendor.setAccountNumber(vendorDto.getAccountNumber());
        vendor.setBankName(vendorDto.getBankName());
        vendor.setLocation(vendorDto.getLocation());
        vendor.setPhoneNumber(vendorDto.getPhoneNumber());
        vendor.setUser(userExist);
        vendor.setRatingSummary(ratingSummary);

        ratingSummary.setVendor(vendor);
        return vendor;
    }

    public ResponseEntity<ApiResponse<VendorResponse>> modifyVendorStatus(UUID vendorId, VendorStatus status) {
        Vendor vendorExist = repo.findById(vendorId).orElseThrow(
                () -> new ResourceNotFoundException(VendorMessages.VENDOR_NOTFOUND)
        );

        if(vendorExist.getStatus().equals(status)) {
            throw new ResourceBadRequestException(VendorMessages.VENDOR_HAS_CURRENT_STATUS(status));
        }

        vendorExist.setStatus(status);

        Vendor savedVendor = repo.save(vendorExist);

        VendorResponse response = getResponse(savedVendor);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(VendorMessages.VENDOR_STATUS_UPDATED, response)
        );
    }

    public ResponseEntity<ApiResponse<VendorResponse>> toggleVerification(UUID vendorId) {
        Vendor vendorExist = repo.findById(vendorId).orElseThrow(
                () -> new ResourceNotFoundException(VendorMessages.VENDOR_NOTFOUND)
        );

        vendorExist.setVerified(!vendorExist.getVerified());

        Vendor savedVendor = repo.save(vendorExist);

        VendorResponse response = getResponse(savedVendor);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(savedVendor.getVerified() ? VendorMessages.VENDOR_VERIFIED : VendorMessages.VERIFICATION_REVOKED, response)
        );
    }

    public ResponseEntity<ApiResponse<VendorResponse>> updateVendorProfile(UUID vendorId, UpdateVendor vendor) {
        Vendor vendorExist = repo.findById(vendorId).orElseThrow(
                () -> new ResourceNotFoundException(VendorMessages.VENDOR_NOTFOUND)
        );
        
        if(vendor.getLocation() != null) {
            vendorExist.setLocation(vendor.getLocation());
        }

        if(vendor.getAccountNumber() != null) {
            vendorExist.setAccountNumber(vendor.getAccountNumber());
        }

        if(vendor.getBankName() != null) {
            vendorExist.setBankName(vendor.getBankName());
        }

        if(vendor.getPhoneNumber() != null) {
            vendorExist.setPhoneNumber(vendor.getPhoneNumber());
        }

        Vendor updatedVendor = repo.save(vendorExist);

        VendorResponse response = getResponse(updatedVendor);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(VendorMessages.VENDOR_PROFILE_UPDATED, response));
    }

    private static VendorResponse getResponse(Vendor savedVendor) {
        VendorResponse response = new VendorResponse();

        response.setAccountNumber(savedVendor.getAccountNumber());
        response.setBankName(savedVendor.getBankName());
        response.setId(savedVendor.getId());
        response.setLocation(savedVendor.getLocation());
        response.setStatus(savedVendor.getStatus());
        response.setPhoneNumber(savedVendor.getPhoneNumber());
        response.setVerified(savedVendor.getVerified());
        response.setUserId(savedVendor.getUser().getId());
        response.setStocksAvailable(savedVendor.getStocksAvailable());
        return response;
    }
}
