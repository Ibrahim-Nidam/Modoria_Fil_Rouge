package com.modoria.order.application.service;

import com.modoria.cart.application.dto.CartItemResponseDTO;
import com.modoria.cart.application.dto.CartResponseDTO;
import com.modoria.cart.application.service.CartService;
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
import com.modoria.shared.exception.ResourceNotFoundException;
import com.modoria.shared.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductRepository productRepository; // Needed to fetch the actual Product entity
    private final OrderMapper orderMapper;
    private final EmailService emailService;
    private final PdfInvoiceService pdfInvoiceService;

    @Override
    @Transactional
    public OrderResponseDTO checkoutCart() {
        User currentUser = getCurrentUser();
        CartResponseDTO cart = cartService.getCartForCurrentUser();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        // Create new Order
        Order order = Order.builder()
                .user(currentUser)
                .status(OrderStatus.PENDING)
                .totalAmount(cart.getTotalPrice())
                .build();

        // Convert CartItems to OrderItems
        for (CartItemResponseDTO cartItemDto : cart.getItems()) {
            Product product = productRepository.findById(cartItemDto.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStock() < cartItemDto.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - cartItemDto.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItemDto.getQuantity())
                    .price(cartItemDto.getPrice())
                    .build();

            order.addItem(orderItem);
        }

        // Save order and clear cart
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart();

        // Generate PDF and Send Email (asynchronously in a real app, keeping synchronous here for simplicity/Mailtrap)
        try {
            byte[] pdfInvoice = pdfInvoiceService.generateInvoice(savedOrder);
            emailService.sendOrderConfirmationEmail(currentUser.getEmail(), savedOrder, pdfInvoice);
        } catch (Exception e) {
            // Log but do not fail the checkout process
            org.slf4j.LoggerFactory.getLogger(OrderServiceImpl.class)
                    .error("Failed to generate/send invoice for order {}", savedOrder.getId(), e);
        }

        return orderMapper.toOrderResponseDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders() {
        User currentUser = getCurrentUser();
        return orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(orderMapper::toOrderResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Long currentUserId = getCurrentUser().getId();
        if (!order.getUser().getId().equals(currentUserId)) {
            throw new ResourceNotFoundException("Order not found or access denied");
        }

        return orderMapper.toOrderResponseDTO(order);
    }

    private User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUser();
    }
}
