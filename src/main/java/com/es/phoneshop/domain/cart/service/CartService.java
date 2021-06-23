package com.es.phoneshop.domain.cart.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.MiniCart;

import javax.servlet.http.HttpSession;

public interface CartService {
    void addCartItem(HttpSession session, Long productId, int quantity);

    void updateCartItem(HttpSession session, Long productId, int quantity);

    void deleteCartItemById(HttpSession session, Long productId);

    MiniCart getMiniCart(HttpSession session);

    Cart get(HttpSession session);

    void clear(HttpSession session);
}
