package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {
    CartItem save(CartItem cartItem);
    Optional<CartItem> findById(Long id);
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    void deleteById(Long id);
    void deleteByCartId(Long cartId);
    boolean existsById(Long id);
}
