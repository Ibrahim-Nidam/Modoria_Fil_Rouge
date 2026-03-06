package com.modoria.review.application.service;

import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.repository.ProductRepository;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.review.application.dto.ReviewCreateDTO;
import com.modoria.review.application.dto.ReviewResponseDTO;
import com.modoria.review.application.mapper.ReviewMapper;
import com.modoria.review.domain.model.Review;
import com.modoria.review.domain.repository.ReviewRepository;
import com.modoria.shared.exception.DuplicateResourceException;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modoria.order.domain.repository.OrderRepository;
import com.modoria.review.domain.enums.ReviewStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional
    public ReviewResponseDTO addReview(Long productId, ReviewCreateDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} is adding a review for Product ID {}", email, productId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (reviewRepository.findByProductIdAndUserId(productId, user.getId()).isPresent()) {
            throw new DuplicateResourceException("You have already reviewed this product.");
        }

        boolean hasPurchased = orderRepository.hasUserPurchasedProduct(user.getId(), productId);

        Review review = reviewMapper.toEntity(dto);
        review.setProduct(product);
        review.setUser(user);
        review.setVerifiedPurchase(hasPurchased);
        review.setStatus(hasPurchased ? ReviewStatus.APPROVED : ReviewStatus.PENDING);

        review = reviewRepository.save(review);
        log.info("Successfully added review {} for product {}", review.getId(), productId);

        return reviewMapper.toDto(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getReviewsForProduct(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        return reviewRepository
                .findByProductIdAndStatus(productId, com.modoria.review.domain.enums.ReviewStatus.APPROVED, pageable)
                .map(reviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByStatus(ReviewStatus.PENDING, pageable)
                .map(reviewMapper::toDto);
    }

    @Override
    @Transactional
    public void updateReviewStatus(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setStatus(status);
        reviewRepository.save(review);
        log.info("Admin updated review {} status to {}", reviewId, status);
    }
}
