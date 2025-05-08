package com.orange.eshop.cart_service.dto.request;

import lombok.Data;

@Data
public class AddCartItemRequest {
    private Long productId;
    private int quantity;
}
