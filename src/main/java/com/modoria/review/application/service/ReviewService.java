package com.modoria.review.application.service;

import com.modoria.review.application.dto.ReviewCreateDTO;
import com.modoria.review.application.dto.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponseDTO addReview(Long productId, ReviewCreateDTO dto);

    Page<ReviewResponseDTO> getReviewsForProduct(Long productId, Pageable pageable);
}
