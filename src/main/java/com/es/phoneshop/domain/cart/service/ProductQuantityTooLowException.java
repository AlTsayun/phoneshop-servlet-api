package com.es.phoneshop.domain.cart.service;

public class ProductQuantityTooLowException extends RuntimeException {
    private int quantity;

    public ProductQuantityTooLowException(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
