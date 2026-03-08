package com.modoria.cart.application.service;

import com.modoria.cart.application.dto.CartItemRequestDTO;
import com.modoria.cart.application.dto.CartResponseDTO;

public interface CartService {
    CartResponseDTO addItemToCart(CartItemRequestDTO request);

    CartResponseDTO updateItemQuantity(Long itemId, Integer quantity);

    void removeItemFromCart(Long itemId);

    CartResponseDTO getCartForCurrentUser();

    void clearCart();
}
