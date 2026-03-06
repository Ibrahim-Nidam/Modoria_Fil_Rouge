package com.modoria.review.application.dto;

import com.modoria.review.domain.enums.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewStatusUpdateDTO {
    @NotNull(message = "Status cannot be null")
    private ReviewStatus status;
}
