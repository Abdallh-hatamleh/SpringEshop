package com.orange.eshop.cart_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CartResponse {
    private List<CartItemResponse> items;
    private Double total;
}
