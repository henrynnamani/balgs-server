package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.rating.RateVendor;
import com.graey.Balgs.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("ratings")
@Tag(name = "rating")
public class RatingController {

    @Autowired
    private RatingService service;

    @PutMapping("")
    @Operation(summary = "rate vendor")
    public ResponseEntity<ApiResponse<String>> rateVendor(@RequestBody RateVendor rateVendor) {
        return service.rateVendor(rateVendor);
    }
}
