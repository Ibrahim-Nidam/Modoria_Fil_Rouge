package com.modoria.cart.application.service;

import com.modoria.cart.application.dto.CartItemRequestDTO;
import com.modoria.cart.application.dto.CartResponseDTO;
import com.modoria.cart.application.mapper.CartMapper;
import com.modoria.cart.domain.model.Cart;
import com.modoria.cart.domain.model.CartItem;
import com.modoria.cart.domain.repository.CartItemRepository;
import com.modoria.cart.domain.repository.CartRepository;
import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.repository.ProductRepository;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.infrastructure.security.CustomUserDetails;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponseDTO addItemToCart(CartItemRequestDTO request) {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createNewCart(currentUser));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(product.getPrice()) // Snapshot price
                    .build();
            cart.addItem(newItem);
        }

        cart.calculateTotalPrice();
        return cartMapper.toCartResponseDTO(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponseDTO updateItemQuantity(Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Ensure the item belongs to the current user's cart
        Long currentUserId = getCurrentUser().getId();
        if (!item.getCart().getUser().getId().equals(currentUserId)) {
            throw new ResourceNotFoundException("Cart item not found in your cart");
        }

        item.setQuantity(quantity);
        Cart cart = item.getCart();
        cart.calculateTotalPrice();
        return cartMapper.toCartResponseDTO(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        Long currentUserId = getCurrentUser().getId();
        if (!item.getCart().getUser().getId().equals(currentUserId)) {
            throw new ResourceNotFoundException("Cart item not found in your cart");
        }

        Cart cart = item.getCart();
        cart.removeItem(item);
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCartForCurrentUser() {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createNewCart(currentUser));
        return cartMapper.toCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        User currentUser = getCurrentUser();
        cartRepository.findByUserId(currentUser.getId()).ifPresent(cart -> {
            cart.getItems().clear();
            cart.setTotalPrice(java.math.BigDecimal.ZERO);
            cartRepository.save(cart);
        });
    }

    private User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    private Cart createNewCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .totalPrice(java.math.BigDecimal.ZERO)
                .build();
        return cartRepository.save(cart);
    }
}
