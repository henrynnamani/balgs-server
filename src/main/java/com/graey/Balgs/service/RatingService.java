package com.graey.Balgs.service;

import com.graey.Balgs.common.exception.ResourceNotFoundException;
import com.graey.Balgs.common.messages.RatingMessages;
import com.graey.Balgs.common.messages.UserMessages;
import com.graey.Balgs.common.messages.VendorMessages;
import com.graey.Balgs.common.utils.ApiResponse;
import com.graey.Balgs.dto.rating.RateVendor;
import com.graey.Balgs.model.Rating;
import com.graey.Balgs.model.RatingSummary;
import com.graey.Balgs.model.User;
import com.graey.Balgs.model.Vendor;
import com.graey.Balgs.repo.RatingRepo;
import com.graey.Balgs.repo.RatingSummaryRepo;
import com.graey.Balgs.repo.UserRepo;
import com.graey.Balgs.repo.VendorRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService {

    @Autowired
    private VendorRepo vendorRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RatingRepo repo;

    @Autowired
    private RatingSummaryRepo ratingSummaryRepo;

    @Transactional
    public ResponseEntity<ApiResponse<String>> rateVendor(RateVendor rateVendor) {
        UUID userId = UUID.fromString(rateVendor.getUserId());
        UUID vendorId = UUID.fromString(rateVendor.getVendorId());

        if(rateVendor.getRating() < 1 || rateVendor.getRating() > 5)
            throw new IllegalArgumentException();

        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException(VendorMessages.VENDOR_NOTFOUND));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOTFOUND));

        RatingSummary summary = ratingSummaryRepo.findById(vendorId)
                .orElseGet(() -> createEmptySummary(UUID.fromString(rateVendor.getVendorId())));

        repo.findByVendor_IdAndUser_Id(userId, vendorId)
                .ifPresentOrElse(
                        existing -> updateRating(existing, summary, rateVendor.getRating()),
                        () -> createRating(vendor, user, rateVendor.getRating(), rateVendor.getReview(), summary)
                );

        recalculateAverage(summary);
        ratingSummaryRepo.save(summary);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(RatingMessages.VENDOR_RATED_SUCCESSFULLY));
    }

    private void recalculateAverage(RatingSummary summary) {
        long totalScore =
                summary.getFiveStar() * 5 +
                        summary.getFourStar() * 4 +
                        summary.getThreeStar() * 3 +
                        summary.getTwoStar() * 2 +
                        summary.getOneStar();

        summary.setAverageRating(
                summary.getTotalRatings() == 0
                        ? 0.0
                        : (double) totalScore / summary.getTotalRatings()
        );
    }

    private void updateRating(Rating existing, RatingSummary summary, int rating) {
        decrementBucket(summary, existing.getRating());
        incrementBucket(summary, rating);

        existing.setRating(rating);

        repo.save(existing);
    }

    private void incrementBucket(RatingSummary summary, int rating) {
        switch (rating) {
            case 5 -> summary.setFiveStar(summary.getFiveStar() + 1);
            case 4 -> summary.setFourStar(summary.getFourStar() + 1);
            case 3 -> summary.setThreeStar(summary.getThreeStar() + 1);
            case 2 -> summary.setTwoStar(summary.getTwoStar() + 1);
            case 1 -> summary.setOneStar(summary.getOneStar() + 1);
        }
    }

    private void decrementBucket(RatingSummary summary, @Max(5) @Min(0) int rating) {
        switch (rating) {
            case 5 -> summary.setFiveStar(summary.getFiveStar() - 1);
            case 4 -> summary.setFourStar(summary.getFourStar() - 1);
            case 3 -> summary.setThreeStar(summary.getThreeStar() - 1);
            case 2 -> summary.setTwoStar(summary.getTwoStar() - 1);
            case 1 -> summary.setOneStar(summary.getOneStar() - 1);
        }
    }

    private void createRating(Vendor vendor, User user, int rating, String review, RatingSummary summary) {
        Rating vendorRating = new Rating();

        vendorRating.setVendor(vendor);
        vendorRating.setRating(rating);
        vendorRating.setReview(review);
        vendorRating.setUser(user);

        repo.save(vendorRating);

        summary.setTotalRatings(summary.getTotalRatings() + 1);
        incrementBucket(summary, rating);
    }


    @Transactional
    private RatingSummary createEmptySummary(UUID vendorId) {
        Optional<RatingSummary> existing =
                ratingSummaryRepo.findByVendorId(vendorId);

        if (existing.isPresent()) {
            return existing.get();
        }

        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(VendorMessages.VENDOR_NOTFOUND)
                );

        RatingSummary summary = new RatingSummary();

        summary.setVendorId(vendor.getId());
        summary.setTotalRatings(0L);
        summary.setAverageRating(0.0);

        try {
            return ratingSummaryRepo.save(summary);
        } catch (DataIntegrityViolationException ex) {
            return ratingSummaryRepo.findByVendorId(vendorId)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    RatingMessages.VENDOR_RATING_SUMMARY_FAILED
                            )
                    );
        }
    }
}
