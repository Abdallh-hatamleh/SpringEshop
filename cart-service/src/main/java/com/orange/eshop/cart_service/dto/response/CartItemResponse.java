package com.orange.eshop.cart_service.dto.response;

import lombok.Data;

@Data
public class CartItemResponse {
    private Long productId;
    private String productName;
    private Double price;
    private int quantity;
}
