package com.orange.eshop.cart_service.dto.response;

import lombok.Data;

@Data
public class ProductResponse {
    private long id;
    private String name;
    private Double price;
}
