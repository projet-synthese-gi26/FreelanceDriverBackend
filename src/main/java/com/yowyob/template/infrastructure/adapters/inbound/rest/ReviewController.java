package com.yowyob.template.infrastructure.adapters.inbound.rest;

import com.yowyob.template.application.service.ReviewService;
import com.yowyob.template.domain.model.Review;
import com.yowyob.template.infrastructure.mappers.ReviewMapper;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReviewRequest;
import com.yowyob.template.infrastructure.adapters.inbound.rest.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Review", description = "Review API")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new review")
    public Mono<ReviewResponse> createReview(@RequestBody ReviewRequest reviewRequest) {
        Review review = reviewMapper.toDomain(reviewRequest);
        return reviewService.createReview(review)
                .map(reviewMapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a review by ID")
    public Mono<ReviewResponse> getReviewById(@PathVariable UUID id) {
        return reviewService.getReviewById(id)
                .map(reviewMapper::toResponse);
    }

    @GetMapping
    @Operation(summary = "Get all reviews")
    public Flux<ReviewResponse> getAllReviews() {
        return reviewService.getAllReviews()
                .map(reviewMapper::toResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a review")
    public Mono<ReviewResponse> updateReview(@PathVariable UUID id, @RequestBody ReviewRequest reviewRequest) {
        Review review = reviewMapper.toDomain(reviewRequest);
        return reviewService.updateReview(id, review)
                .map(reviewMapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public Mono<Void> deleteReview(@PathVariable UUID id) {
        return reviewService.deleteReview(id);
    }
}
