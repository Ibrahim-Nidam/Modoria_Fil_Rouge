package com.modoria.review.domain.repository;

import com.modoria.catalog.domain.model.Category;
import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.repository.CategoryRepository;
import com.modoria.catalog.domain.repository.ProductRepository;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.review.domain.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

        @Autowired
        private ReviewRepository reviewRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        private Product product;
        private User user1;
        private User user2;
        private User user3;

        @BeforeEach
        void setUp() {
                Category category = Category.builder()
                                .name("Electronics")
                                .description("Test Category")
                                .build();
                category = categoryRepository.save(category);

                product = Product.builder()
                                .name("Test Product")
                                .description("Test Description")
                                .price(BigDecimal.valueOf(100.00))
                                .stock(10)
                                .category(category)
                                .build();
                product = productRepository.save(product);

                user1 = User.builder()
                                .email("user1@test.com")
                                .password("password")
                                .fullName("User One")
                                .build();
                user1 = userRepository.save(user1);

                user2 = User.builder()
                                .email("user2@test.com")
                                .password("password")
                                .fullName("User Two")
                                .build();
                user2 = userRepository.save(user2);

                user3 = User.builder()
                                .email("user3@test.com")
                                .password("password")
                                .fullName("User Three")
                                .build();
                user3 = userRepository.save(user3);
        }

        @Test
        void shouldCalculateAverageRatingCorrectlyOnlyForApproved() {
                // Given
                Review review1 = Review.builder()
                                .product(product)
                                .user(user1)
                                .rating(4)
                                .comment("Great!")
                                .status(com.modoria.review.domain.enums.ReviewStatus.APPROVED)
                                .build();
                reviewRepository.save(review1);

                Review review2 = Review.builder()
                                .product(product)
                                .user(user2)
                                .rating(5)
                                .comment("Excellent!")
                                .status(com.modoria.review.domain.enums.ReviewStatus.APPROVED)
                                .build();
                reviewRepository.save(review2);

                Review pendingReview = Review.builder()
                                .product(product)
                                .user(user3)
                                .rating(1)
                                .comment("Bad, but pending")
                                .status(com.modoria.review.domain.enums.ReviewStatus.PENDING)
                                .build();
                reviewRepository.save(pendingReview);

                // When
                Double avgRating = reviewRepository.getAverageRatingForProduct(product.getId());
                Long count = reviewRepository.countReviewsForProduct(product.getId());

                // Then
                assertThat(avgRating).isEqualTo(4.5);
                assertThat(count).isEqualTo(2L);
        }

        @Test
        void shouldReturnZeroForNoReviews() {
                // When
                Double avgRating = reviewRepository.getAverageRatingForProduct(product.getId());
                Long count = reviewRepository.countReviewsForProduct(product.getId());

                // Then
                assertThat(avgRating).isEqualTo(0.0);
                assertThat(count).isEqualTo(0L);
        }
}
