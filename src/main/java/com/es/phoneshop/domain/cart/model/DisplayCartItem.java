package com.es.phoneshop.domain.cart.model;

import com.es.phoneshop.domain.product.model.Product;

import java.util.Objects;

public class DisplayCartItem {
    private Product product;
    private int quantity;

    public DisplayCartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisplayCartItem that = (DisplayCartItem) o;
        return quantity == that.quantity && product.equals(that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity);
    }
}
