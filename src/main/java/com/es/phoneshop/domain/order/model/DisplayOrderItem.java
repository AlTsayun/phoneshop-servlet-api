package com.es.phoneshop.domain.order.model;

import com.es.phoneshop.domain.common.model.Price;
import com.es.phoneshop.domain.product.model.Product;

public class DisplayOrderItem {

    private Product product;
    private int quantity;
    private Price price;

    public DisplayOrderItem(Product product, int quantity, Price price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Price getPrice() {
        return price;
    }
}
