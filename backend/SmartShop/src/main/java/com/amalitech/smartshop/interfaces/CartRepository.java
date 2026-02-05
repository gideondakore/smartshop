package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.Cart;

import java.util.Optional;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findById(Long id);
    Optional<Cart> findByUserId(Long userId);
    void deleteById(Long id);
    void deleteByUserId(Long userId);
    boolean existsById(Long id);
}
