package com.es.phoneshop.domain.product.service;

public class ProductNotFoundException extends RuntimeException{
    private String id;

    public ProductNotFoundException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
