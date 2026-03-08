package com.modoria.payment.infrastructure.stripe;

import com.modoria.payment.application.dto.PaymentRequestDTO;
import com.modoria.payment.application.dto.PaymentResponseDTO;
import com.modoria.payment.application.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StripePaymentService implements PaymentService {

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        try {
            // Converting BigDecimal to long (cents) for Stripe
            long amountInCents = request.getAmount().multiply(new java.math.BigDecimal(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setPaymentMethod(request.getStripePaymentMethodId())
                    .setConfirm(true) // Attempt to confirm immediately
                    .setReturnUrl("https://modoria.com/checkout/complete") // Placeholder return URL
                    .putMetadata("order_reference", request.getOrderReference())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(
                                            PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.ALWAYS)
                                    .build())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                return PaymentResponseDTO.success(
                        paymentIntent.getId(),
                        paymentIntent.getStatus(),
                        "Payment succeeded");
            } else {
                return PaymentResponseDTO.builder()
                        .success(false)
                        .transactionId(paymentIntent.getId())
                        .status(paymentIntent.getStatus())
                        .clientSecret(paymentIntent.getClientSecret())
                        .message("Payment requires further action: " + paymentIntent.getStatus())
                        .build();
            }

        } catch (StripeException e) {
            log.error("Stripe payment error for order reference: {}", request.getOrderReference(), e);
            return PaymentResponseDTO.failure(
                    e.getMessage(),
                    e.getCode());
        } catch (Exception e) {
            log.error("Unexpected payment error", e);
            return PaymentResponseDTO.failure("Internal payment error", "INTERNAL_ERROR");
        }
    }
}
