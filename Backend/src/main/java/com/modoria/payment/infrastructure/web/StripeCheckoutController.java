package com.modoria.payment.infrastructure.web;

import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.repository.ProductRepository;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.infrastructure.security.CustomUserDetails;
import com.modoria.order.application.dto.OrderResponseDTO;
import com.modoria.order.application.mapper.OrderMapper;
import com.modoria.order.domain.model.Order;
import com.modoria.order.domain.model.OrderItem;
import com.modoria.order.domain.model.OrderStatus;
import com.modoria.order.domain.repository.OrderRepository;
import com.modoria.payment.application.dto.CheckoutLineItemDTO;
import com.modoria.payment.application.dto.CheckoutSessionResponseDTO;
import com.modoria.payment.application.dto.CreateCheckoutSessionRequestDTO;
import com.modoria.payment.application.service.PaymentService;
import com.modoria.shared.email.EmailService;
import com.modoria.shared.exception.BadRequestException;
import com.modoria.shared.exception.PaymentProcessingException;
import com.modoria.shared.exception.ResourceNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class StripeCheckoutController {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    private final EmailService emailService;

    @PostMapping("/checkout-session")
    @Transactional
    public ResponseEntity<CheckoutSessionResponseDTO> createCheckoutSession(
            @Valid @RequestBody CreateCheckoutSessionRequestDTO request) {
        User currentUser = getCurrentUser();

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Cannot checkout an empty cart");
        }

        Order order = Order.builder()
                .user(currentUser)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CheckoutLineItemDTO item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));

            int quantity = item.getQuantity();
            if (quantity <= 0) {
                throw new BadRequestException("Quantity must be greater than zero");
            }

            if (product.getStock() < quantity) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            order.addItem(OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build());

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        String successUrl = frontendUrl + "/checkout-success?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = frontendUrl + "/checkout-cancel";

        CheckoutSessionResponseDTO response = paymentService.createCheckoutSession(savedOrder, successUrl, cancelUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/confirm-session")
    @Transactional
    public ResponseEntity<OrderResponseDTO> confirmCheckoutSession(@RequestParam("sid") String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new BadRequestException("Stripe session id is required");
        }

        Session session;
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new PaymentProcessingException("Unable to verify Stripe checkout session");
        }

        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            throw new PaymentProcessingException("Checkout session is not paid");
        }

        Map<String, String> metadata = session.getMetadata();
        String orderIdValue = metadata == null ? null : metadata.get("orderId");
        if (orderIdValue == null || orderIdValue.isBlank()) {
            throw new PaymentProcessingException("Stripe checkout session missing order reference");
        }

        Long orderId;
        try {
            orderId = Long.valueOf(orderIdValue);
        } catch (NumberFormatException ex) {
            throw new PaymentProcessingException("Invalid order reference in Stripe session");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User currentUser = getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Order not found or access denied");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            try {
                emailService.sendOrderConfirmationEmail(currentUser.getEmail(), order);
            } catch (Exception ex) {
                org.slf4j.LoggerFactory.getLogger(StripeCheckoutController.class)
                        .error("Failed to send confirmation email for order {}", order.getId(), ex);
            }
        }

        return ResponseEntity.ok(orderMapper.toOrderResponseDTO(order));
    }

    private User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUser();
    }
}
