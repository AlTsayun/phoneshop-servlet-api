package com.es.phoneshop.domain.order.model;

import com.es.phoneshop.domain.common.model.Price;

public class OrderItem {
    private Long ProductId;
    private int quantity;
    private Price price;

    public OrderItem(Long productId, int quantity, Price price) {
        ProductId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getProductId() {
        return ProductId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Price getPrice() {
        return price;
    }
}
