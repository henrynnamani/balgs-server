package com.graey.Balgs.controller;

import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.rating.RateVendor;
import com.graey.Balgs.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("ratings")
public class RatingController {

    @Autowired
    private RatingService service;

    @PutMapping("")
    public ResponseEntity<ApiResponse<String>> rateVendor(@RequestBody RateVendor rateVendor) {
        return service.rateVendor(rateVendor);
    }
}
