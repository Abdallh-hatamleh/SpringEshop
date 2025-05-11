package com.orange.eshop.cart_service.persistence.repository;

import com.orange.eshop.cart_service.persistence.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    List<CartItem> findAllByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    void deleteAllByUserId(Long userId);

}
