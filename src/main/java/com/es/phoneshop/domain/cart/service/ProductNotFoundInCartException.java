package com.es.phoneshop.domain.cart.service;

public class ProductNotFoundInCartException extends RuntimeException {
    private String id;

    public ProductNotFoundInCartException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
