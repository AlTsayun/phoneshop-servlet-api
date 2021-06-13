package com.es.phoneshop.domain.cart.service;

import com.es.phoneshop.domain.cart.model.Cart;

import javax.servlet.http.HttpSession;

public interface CartService {
    void add(HttpSession session, Long productId, int quantity);
    void update(HttpSession session, Long productId, int quantity);
    void deleteById(HttpSession session, Long productId);

    Cart getCart(HttpSession session);
}
