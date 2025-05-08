package com.orange.eshop.cart_service.controller;


import com.orange.eshop.cart_service.config.security.CustomPrincipal;
import com.orange.eshop.cart_service.dto.request.AddCartItemRequest;
import com.orange.eshop.cart_service.dto.request.RemoveCartItemRequest;
import com.orange.eshop.cart_service.dto.request.UpdateCartItemRequest;
import com.orange.eshop.cart_service.dto.response.CartResponse;
import com.orange.eshop.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal CustomPrincipal principal) {
        CartResponse response = cartService.getCart(principal.getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addItem(@AuthenticationPrincipal CustomPrincipal principal, @RequestBody AddCartItemRequest request)
    {
        cartService.addItemToCart(request, principal.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Item Added Successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateItem(@AuthenticationPrincipal CustomPrincipal principal, @RequestBody UpdateCartItemRequest request)
    {
        cartService.updateItemQuantity(request, principal.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal CustomPrincipal principal, @RequestBody RemoveCartItemRequest request)
    {
        cartService.removeItem(request, principal.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal CustomPrincipal principal){
        cartService.clearCart(principal.getUserId());
        return ResponseEntity.ok().build();
    }
}
