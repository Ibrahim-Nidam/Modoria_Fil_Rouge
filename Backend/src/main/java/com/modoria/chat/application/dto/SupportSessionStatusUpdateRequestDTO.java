package com.modoria.chat.application.dto;

import com.modoria.chat.domain.enums.SupportSessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportSessionStatusUpdateRequestDTO {

    @NotNull(message = "Status is required")
    private SupportSessionStatus status;
}
