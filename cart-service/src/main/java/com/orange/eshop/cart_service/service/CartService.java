package com.orange.eshop.cart_service.service;

import com.orange.eshop.cart_service.client.ProductClient;
import com.orange.eshop.cart_service.dto.request.AddCartItemRequest;
import com.orange.eshop.cart_service.dto.request.RemoveCartItemRequest;
import com.orange.eshop.cart_service.dto.request.UpdateCartItemRequest;
import com.orange.eshop.cart_service.dto.response.CartItemResponse;
import com.orange.eshop.cart_service.dto.response.CartResponse;
import com.orange.eshop.cart_service.dto.response.ProductResponse;
import com.orange.eshop.cart_service.entity.CartItem;
import com.orange.eshop.cart_service.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CartService {

    private final ProductClient productClient;
    private final CartItemRepository cartItemRepository;
    public CartItemResponse enrichItem(CartItem item) {

        ProductResponse product = productClient.getProductById(item.getId());

        CartItemResponse response = new CartItemResponse();
        response.setPrice(product.getPrice());
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setQuantity(item.getQuantity());
        return response;
    }

    public void addItemToCart(AddCartItemRequest request,Long userId)
    {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId,request.getProductId())
                .orElseGet( () -> {
                    CartItem newItem = new CartItem();
                    newItem.setUserId(userId);
                    newItem.setQuantity(0);
                    newItem.setProductId(request.getProductId());
                    return newItem;
                });
        item.setQuantity(item.getQuantity() + request.getQuantity());
        cartItemRepository.save(item);
    }

    public void updateItemQuantity(UpdateCartItemRequest request,Long userId){
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId,request.getProductId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
    }

    public void removeItem(RemoveCartItemRequest request,Long userId){
        cartItemRepository.deleteByUserIdAndProductId(userId,request.getProductId());
    }

    public CartResponse getCart(Long userId){

        List<CartItem> items = cartItemRepository.findAllByUserId(userId);
        List<CartItemResponse> itemResponses = items.stream().map( item ->
        {
            return enrichItem(item);
        }).toList();
        Double total = itemResponses.stream().
                mapToDouble(resp -> resp.getPrice() * resp.getQuantity())
                .sum();

        return new CartResponse(itemResponses,total);
    }

    public void clearCart(Long userId){
        cartItemRepository.deleteAllByUserId(userId);
    }
}
