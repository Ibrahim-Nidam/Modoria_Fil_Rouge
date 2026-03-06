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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@example.com").build();
        testProduct = Product.builder().id(100L).build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addReview_Success() {
        // Arrange
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Great product!");
        Review mappedReview = Review.builder().rating(5).comment("Great product!").build();
        Review savedReview = Review.builder().id(1L).rating(5).comment("Great product!").build();
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(1L, 100L, 1L, "Test User", 5, "Great product!", null);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(productRepository.findById(100L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.findByProductIdAndUserId(100L, 1L)).thenReturn(Optional.empty());

        when(reviewMapper.toEntity(createDTO)).thenReturn(mappedReview);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);
        when(reviewMapper.toDto(savedReview)).thenReturn(responseDTO);

        // Act
        ReviewResponseDTO result = reviewService.addReview(100L, createDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRating()).isEqualTo(5);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void addReview_DuplicateReview_ThrowsException() {
        // Arrange
        ReviewCreateDTO createDTO = new ReviewCreateDTO(5, "Great product!");
        Review existingReview = new Review();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(productRepository.findById(100L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.findByProductIdAndUserId(100L, 1L)).thenReturn(Optional.of(existingReview));

        // Act & Assert
        assertThatThrownBy(() -> reviewService.addReview(100L, createDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already reviewed");
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getReviewsForProduct_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Review review = Review.builder().id(1L).rating(4).comment("Good").build();
        Page<Review> reviewPage = new PageImpl<>(List.of(review));
        ReviewResponseDTO responseDTO = new ReviewResponseDTO(1L, 100L, 1L, "Test", 4, "Good", null);

        when(productRepository.existsById(100L)).thenReturn(true);
        when(reviewRepository.findByProductId(100L, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toDto(review)).thenReturn(responseDTO);

        // Act
        Page<ReviewResponseDTO> result = reviewService.getReviewsForProduct(100L, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getRating()).isEqualTo(4);
    }
}
