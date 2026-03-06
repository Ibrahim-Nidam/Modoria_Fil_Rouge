package com.modoria.review.infrastructure.web;

import com.modoria.review.application.dto.ReviewCreateDTO;
import com.modoria.review.application.dto.ReviewResponseDTO;
import com.modoria.review.application.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDTO> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewCreateDTO reviewCreateDTO) {
        ReviewResponseDTO response = reviewService.addReview(productId, reviewCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsForProduct(
            @PathVariable Long productId,
            Pageable pageable) {
        Page<ReviewResponseDTO> response = reviewService.getReviewsForProduct(productId, pageable);
        return ResponseEntity.ok(response);
    }
}
