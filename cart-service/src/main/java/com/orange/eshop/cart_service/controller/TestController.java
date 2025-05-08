package com.orange.eshop.cart_service.controller;

import com.orange.eshop.cart_service.config.security.CustomPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public ResponseEntity<String> getTest(@AuthenticationPrincipal CustomPrincipal principal) {

        return ResponseEntity.ok(String.format("This is %s, with email %s, and userID %d", principal.getName(), principal.getUsername(), principal.getUserId()));

    }
}
