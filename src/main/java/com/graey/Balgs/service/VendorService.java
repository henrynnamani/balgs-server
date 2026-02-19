package com.graey.Balgs.service;

import com.cloudinary.Api;
import com.graey.Balgs.common.constant.VendorConstant;
import com.graey.Balgs.common.enums.VendorStatus;
import com.graey.Balgs.common.exception.ResourceBadRequestException;
import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.common.messages.VendorMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.config.PaystackConfig;
import com.graey.Balgs.dto.vendor.UpdateVendor;
import com.graey.Balgs.dto.vendor.VendorDto;
import com.graey.Balgs.dto.vendor.VendorResponse;
import com.graey.Balgs.model.User;
import com.graey.Balgs.model.Vendor;
import com.graey.Balgs.repo.UserRepo;
import com.graey.Balgs.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class VendorService {
    @Autowired
    private VendorRepo repo;
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PaystackConfig paystackConfig;

    private final RestTemplate restTemplate = new RestTemplate();
    
    public ResponseEntity<ApiResponse<VendorResponse>> setupAccount(VendorDto vendorDto) {
        User userExist = userRepo.findById(UUID.fromString(vendorDto.getUserId())).orElseThrow(
                () -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND)
        );
        
        Vendor vendor = new Vendor();
        
        vendor.setAccountNumber(vendorDto.getAccountNumber());
        vendor.setBankName(vendorDto.getBankName());
        vendor.setLocation(vendorDto.getLocation());
        vendor.setPhoneNumber(vendorDto.getPhoneNumber());
        vendor.setUser(userExist);

        String subAccount = createSubaccount(vendor);

        vendor.setSubaccount(subAccount);
        
        Vendor savedVendor = repo.save(vendor);

        VendorResponse response = getResponse(savedVendor);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(VendorMessages.VENDOR_CREATED_SUCCESSFULLY, response)
        );
    }

    public String createSubaccount(Vendor vendor){
            String url = paystackConfig.getBaseUrl() + "/subaccount";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + paystackConfig.getSecretKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("business_name", vendor.getBankName());
            body.put("settlement_bank", "999992"); // change to dynamic
            body.put("account_number", vendor.getAccountNumber());
            body.put("percentage_charge", VendorConstant.PERCENTAGE_CHARGE);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            Map data = (Map) response.getBody().get("data");

            return (String) data.get("subaccount_code");
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
