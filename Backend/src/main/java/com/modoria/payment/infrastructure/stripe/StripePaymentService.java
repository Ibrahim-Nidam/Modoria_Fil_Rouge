package com.modoria.payment.infrastructure.stripe;

import com.modoria.order.domain.model.Order;
import com.modoria.order.domain.model.OrderItem;
import com.modoria.payment.application.dto.CheckoutSessionResponseDTO;
import com.modoria.payment.application.service.PaymentService;
import com.modoria.shared.exception.PaymentProcessingException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class StripePaymentService implements PaymentService {

    @Override
    public CheckoutSessionResponseDTO createCheckoutSession(Order order, String successUrl, String cancelUrl) {
        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .putMetadata("orderId", String.valueOf(order.getId()))
                    .putMetadata("userId", String.valueOf(order.getUser().getId()));

            for (OrderItem item : order.getItems()) {
                long unitAmountInCents = item.getPrice().multiply(BigDecimal.valueOf(100)).longValue();

                builder.addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(item.getQuantity().longValue())
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("mad")
                                .setUnitAmount(unitAmountInCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(item.getProduct().getName())
                                        .build())
                                .build())
                        .build());
            }

            Session session = Session.create(builder.build());

            return CheckoutSessionResponseDTO.builder()
                    .url(session.getUrl())
                    .sessionId(session.getId())
                    .orderId(order.getId())
                    .build();
        } catch (StripeException e) {
            log.error("Failed to create Stripe checkout session for order {}", order.getId(), e);
            throw new PaymentProcessingException("Unable to initialize Stripe checkout session");
        }
    }
}
