package com.graey.Balgs.service;

import com.graey.Balgs.common.enums.BroadcastPaymentStatus;
import com.graey.Balgs.common.enums.TradeInStatus;
import com.graey.Balgs.common.exception.AccessDeniedException;
import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.TradeInMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.dto.tradein.TradeInRequest;
import com.graey.Balgs.dto.tradein.TradeInResponse;
import com.graey.Balgs.dto.tradein.TradeInReview;
import com.graey.Balgs.model.TradeIn;
import com.graey.Balgs.model.User;
import com.graey.Balgs.repo.TradeInRepo;
import com.graey.Balgs.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeInService {

    private final TradeInRepo tradeInRepository;
    private final UserRepo userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public TradeInResponse submit(TradeInRequest dto,
                                     MultipartFile phoneVideo,
                                     MultipartFile receiptImage,
                                     UUID userId) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // IMEI check
//        ImeiStatus imeiStatus = imeiService.check(dto.getImei());
//        if (imeiStatus == ImeiStatus.BLACKLISTED) {
//            throw new IllegalStateException(
//                    "This device has been reported stolen or blacklisted and cannot be traded in"
//            );
//        }

        String phoneVideoUrl = null;
        String receiptImageUrl = null;

        if (phoneVideo != null && !phoneVideo.isEmpty()) {
            System.out.println("In video upload");
            phoneVideoUrl = cloudinaryService.uploadFile(phoneVideo, "video");
        }

        if (receiptImage != null && !receiptImage.isEmpty()) {
            receiptImageUrl = cloudinaryService.uploadFile(receiptImage, "image");
        }

        TradeIn tradeIn = mapToEntity(dto);
        tradeIn.setUser(user);
//        tradeIn.setImeiStatus(imeiStatus);
        tradeIn.setStatus(TradeInStatus.PENDING);
        tradeIn.setPhoneVideo(phoneVideoUrl);
        tradeIn.setReceiptImage(receiptImageUrl);

        TradeIn savedTradeIn = tradeInRepository.save(tradeIn);

        return mapToResponse(savedTradeIn);
    }

    public TradeInResponse review(UUID id, TradeInReview dto) {
        TradeIn tradeIn = findById(id);

        if (tradeIn.getStatus() != TradeInStatus.PENDING) {
            throw new IllegalStateException("Only PENDING trade-ins can be reviewed");
        }

        tradeIn.setVendorValuation(dto.getVendorValuation());
        tradeIn.setStatus(TradeInStatus.REVIEWED);

        return mapToResponse(tradeInRepository.save(tradeIn));
    }

    public TradeIn getTradeInById(UUID id) {
        return tradeInRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(TradeInMessages.TRADE_IN_NOT_FOUND)
        );
    }

    public TradeInResponse accept(UUID id, UUID userId) {
        TradeIn tradeIn = findById(id);
        validateOwner(tradeIn, userId);

        if (tradeIn.getStatus() != TradeInStatus.REVIEWED) {
            throw new IllegalStateException("Trade-in must be REVIEWED before accepting");
        }

        tradeIn.setStatus(TradeInStatus.ACCEPTED);
        return mapToResponse(tradeInRepository.save(tradeIn));
    }

    public TradeInResponse markBroadcastPaid(UUID id, UUID userId) {
        TradeIn tradeIn = findById(id);
        validateOwner(tradeIn, userId);

        if (tradeIn.getBroadcastPaymentStatus() != BroadcastPaymentStatus.UNPAID) {
            throw new IllegalStateException("Broadcast Payment alread made");
        }

        tradeIn.setBroadcastPaymentStatus(BroadcastPaymentStatus.PAID);
        return mapToResponse(tradeInRepository.save(tradeIn));
    }

    // Customer rejects the valuation
    public TradeInResponse reject(UUID id, UUID userId) {
        TradeIn tradeIn = findById(id);
        validateOwner(tradeIn, userId);

        if (tradeIn.getStatus() != TradeInStatus.REVIEWED) {
            throw new IllegalStateException("Trade-in must be REVIEWED before rejecting");
        }

        tradeIn.setStatus(TradeInStatus.REJECTED);
        return mapToResponse(tradeInRepository.save(tradeIn));
    }

    public List<TradeInResponse> getMyTradeIns(UUID userId) {
        return tradeInRepository.findByUserId(userId)
                .stream().map(this::mapToResponse).toList();
    }

    public List<TradeInResponse> getAll(TradeInStatus status) {
        List<TradeIn> results = (status != null)
                ? tradeInRepository.findByStatus(status)
                : tradeInRepository.findAll();
        return results.stream().map(this::mapToResponse).toList();
    }

    // --- Helpers ---

    private TradeIn findById(UUID id) {
        return tradeInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trade-in not found"));
    }

    private void validateOwner(TradeIn tradeIn, UUID userId) {
        if (!tradeIn.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not own this trade-in");
        }
    }

    private TradeIn mapToEntity(TradeInRequest dto) {
        TradeIn t = new TradeIn();
        t.setBrand(dto.getBrand());
        t.setModel(dto.getModel());
        t.setStorageSize(dto.getStorageSize());
        t.setBatteryHealth(dto.getBatteryHealth());
        t.setFaceIdPresent(dto.isFaceIdPresent());
        t.setTrueTonePresent(dto.isTrueTonePresent());
        t.setFaults(dto.getFaults());
        t.setRecentRepairs(dto.getRecentRepairs());
        t.setModelTradedFor(dto.getModelTradedFor());
        t.setModelTradedForStorageSize(dto.getModelTradedForStorageSize());
        return t;
    }

    private TradeInResponse mapToResponse(TradeIn t) {
        TradeInResponse dto = new TradeInResponse();
        dto.setId(t.getId());
        dto.setBrand(t.getBrand());
        dto.setModel(t.getModel());
        dto.setStorageSize(t.getStorageSize());
        dto.setBatteryHealth(t.getBatteryHealth());
        dto.setFaceIdPresent(t.isFaceIdPresent());
        dto.setTrueTonePresent(t.isTrueTonePresent());
        dto.setFaults(t.getFaults());
        dto.setRecentRepairs(t.getRecentRepairs());
        dto.setModelTradedFor(t.getModelTradedFor());
        dto.setModelTradedForStorageSize(t.getModelTradedForStorageSize());
        dto.setPhoneVideo(t.getPhoneVideo());
        dto.setReceiptImage(t.getReceiptImage());
        dto.setStatus(t.getStatus());
        dto.setVendorValuation(t.getVendorValuation());
        dto.setUserId(t.getUser().getId());
        return dto;
    }
}