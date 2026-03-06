package com.modoria.review.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modoria.review.application.dto.ReviewResponseDTO;
import com.modoria.review.application.dto.ReviewStatusUpdateDTO;
import com.modoria.review.application.service.ReviewService;
import com.modoria.review.domain.enums.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.modoria.identity.infrastructure.security.JwtAuthenticationFilter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ReviewResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = ReviewResponseDTO.builder()
                .id(1L)
                .productId(100L)
                .userId(1L)
                .rating(3)
                .comment("Pending review")
                .status(ReviewStatus.PENDING)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingReviews_Success() throws Exception {
        Page<ReviewResponseDTO> page = new PageImpl<>(List.of(responseDTO));
        when(reviewService.getPendingReviews(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/reviews/pending")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateReviewStatus_Success() throws Exception {
        ReviewStatusUpdateDTO updateDTO = new ReviewStatusUpdateDTO();
        updateDTO.setStatus(ReviewStatus.APPROVED);

        mockMvc.perform(patch("/api/v1/admin/reviews/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNoContent());

        verify(reviewService).updateReviewStatus(eq(1L), eq(ReviewStatus.APPROVED));
    }
}
