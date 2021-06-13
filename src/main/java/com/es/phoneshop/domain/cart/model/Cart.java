package com.es.phoneshop.domain.cart.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;

    public Cart(List<CartItem> items) {
        this.items = items;
    }

    public List<CartItem> getItems() {
        return items;
    }
}
