package com.modoria.payment.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCheckoutSessionRequestDTO {

    @Valid
    @NotEmpty(message = "Items are required")
    private List<CheckoutLineItemDTO> items;
}
