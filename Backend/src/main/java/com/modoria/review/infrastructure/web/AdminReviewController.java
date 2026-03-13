package com.modoria.review.infrastructure.web;

import com.modoria.review.application.dto.ReviewResponseDTO;
import com.modoria.review.application.dto.ReviewStatusUpdateDTO;
import com.modoria.review.application.service.ReviewService;
import com.modoria.review.domain.enums.ReviewStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Page<ReviewResponseDTO>> getPendingReviews(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getPendingReviews(pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Void> updateReviewStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReviewStatusUpdateDTO updateDTO) {
        reviewService.updateReviewStatus(id, updateDTO.getStatus());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Void> approveReview(@PathVariable Long id) {
        reviewService.updateReviewStatus(id, ReviewStatus.APPROVED);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
