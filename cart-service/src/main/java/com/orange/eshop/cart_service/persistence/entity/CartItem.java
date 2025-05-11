package com.orange.eshop.cart_service.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class CartItem {

    @Id @GeneratedValue
    private Long id;
    private Long productId;
    private Integer quantity;
    private Long userId;
}
