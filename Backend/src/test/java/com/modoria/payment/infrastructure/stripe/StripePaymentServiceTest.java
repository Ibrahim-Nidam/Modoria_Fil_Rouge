package com.modoria.payment.infrastructure.stripe;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StripePaymentServiceTest {

    private final StripePaymentService service = new StripePaymentService();

    @Test
    void createCheckoutSession_withNullOrder_throwsNullPointerException() {
        assertThatThrownBy(() -> service.createCheckoutSession(null, "https://ok", "https://cancel"))
                .isInstanceOf(NullPointerException.class);
    }
}
