package com.modoria.review.application.service;

import com.modoria.review.application.dto.ReviewCreateDTO;
import com.modoria.review.application.dto.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.modoria.review.domain.enums.ReviewStatus;

public interface ReviewService {
    ReviewResponseDTO addReview(Long productId, ReviewCreateDTO dto);

    Page<ReviewResponseDTO> getReviewsForProduct(Long productId, Pageable pageable);

    Page<ReviewResponseDTO> getPendingReviews(Pageable pageable);

    void updateReviewStatus(Long reviewId, ReviewStatus status);
}
