package com.graey.Balgs.controller;

import com.graey.Balgs.common.messages.TradeInMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.tradein.TradeInRequest;
import com.graey.Balgs.dto.tradein.TradeInResponse;
import com.graey.Balgs.model.User;
import com.graey.Balgs.service.TradeInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/tradeins")
@Tag(name = "Tradeins")
public class TradeInController {
    @Autowired
    private TradeInService service;

    @Operation(summary = "Submit a trade-in")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<TradeInResponse>> submit(
            @RequestPart @Valid TradeInRequest tradeIn,
            @RequestParam(required = false) MultipartFile phoneVideo,
            @RequestParam(required = false) MultipartFile receiptImage,
            @AuthenticationPrincipal User user) throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(TradeInMessages.TRADE_IN_CREATED,
                        service.submit(tradeIn, phoneVideo, receiptImage, user.getId())));
    }
}
