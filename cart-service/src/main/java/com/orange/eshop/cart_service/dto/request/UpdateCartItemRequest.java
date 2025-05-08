package com.orange.eshop.cart_service.dto.request;

import lombok.Data;

@Data
public class UpdateCartItemRequest {
    private Long productId;
    private int quantity;
}
