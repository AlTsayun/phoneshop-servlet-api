package com.es.phoneshop.domain.cart.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public class MiniCart {
    private int totalProductsQuantity;
    private BigDecimal totalPriceValue;
    private Currency currency;

    public MiniCart(int totalProductsQuantity, BigDecimal totalPriceValue, Currency currency) {
        this.totalProductsQuantity = totalProductsQuantity;
        this.totalPriceValue = totalPriceValue;
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiniCart miniCart = (MiniCart) o;
        return totalProductsQuantity == miniCart.totalProductsQuantity && totalPriceValue.equals(miniCart.totalPriceValue) && currency.equals(miniCart.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalProductsQuantity, totalPriceValue, currency);
    }

    public int getTotalProductsQuantity() {
        return totalProductsQuantity;
    }

    public void setTotalProductsQuantity(int totalProductsQuantity) {
        this.totalProductsQuantity = totalProductsQuantity;
    }

    public BigDecimal getTotalPriceValue() {
        return totalPriceValue;
    }

    public void setTotalPriceValue(BigDecimal totalPriceValue) {
        this.totalPriceValue = totalPriceValue;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
