//package com.orange.eshop.cart_service.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Data
//public class Cart {
//    @Id @GeneratedValue
//    private Long id;
//
//
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CartItem> items = new ArrayList<>();
//}
