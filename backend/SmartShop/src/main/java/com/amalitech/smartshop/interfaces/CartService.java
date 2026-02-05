package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.dtos.requests.AddCartItemDTO;
import com.amalitech.smartshop.dtos.requests.UpdateCartItemDTO;
import com.amalitech.smartshop.dtos.responses.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCartByUserId(Long userId);
    CartResponseDTO addItemToCart(AddCartItemDTO request, Long userId);
    CartResponseDTO updateCartItem(Long itemId, UpdateCartItemDTO request, Long userId);
    CartResponseDTO removeItemFromCart(Long itemId, Long userId);
    void clearCart(Long userId);
    CartResponseDTO checkoutCart(Long userId);
}
