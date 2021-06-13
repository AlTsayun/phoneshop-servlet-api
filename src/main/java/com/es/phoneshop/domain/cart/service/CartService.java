package com.es.phoneshop.domain.cart.service;

import com.es.phoneshop.domain.cart.model.Cart;

import javax.servlet.http.HttpSession;

public interface CartService {
    void add(Cart cart, Long productId, int quantity);

    Cart getCart(HttpSession session);
}
