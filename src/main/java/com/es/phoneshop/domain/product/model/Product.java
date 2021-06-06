package com.es.phoneshop.domain.product.model;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

public class Product {
    private Long id;
    private String code;
    private String description;
    private int stock;
    /** can be null if no image is provided */
    private String imageUrl;

    private List<ProductPrice> pricesHistory;

    public Product() {
    }

    public Product(Long id, String code, String description, int stock, String imageUrl, List<ProductPrice> pricesHistory) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.pricesHistory = pricesHistory;
    }

    public List<ProductPrice> getPricesHistory() {
        return pricesHistory;
    }

    public void setPricesHistory(List<ProductPrice> pricesHistory) {
        this.pricesHistory = pricesHistory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ProductPrice getActualPrice() {return pricesHistory.stream().max(Comparator.comparing(ProductPrice::getFrom)).get();}
}