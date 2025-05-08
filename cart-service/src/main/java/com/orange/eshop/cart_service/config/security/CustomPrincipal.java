package com.orange.eshop.cart_service.config.security;

import lombok.Data;


@Data
public class CustomPrincipal {
    private final Long userId;
    private final String name;
    private final String username;
}
