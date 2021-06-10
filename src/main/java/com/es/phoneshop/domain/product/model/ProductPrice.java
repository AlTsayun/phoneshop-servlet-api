package com.es.phoneshop.domain.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;

public class ProductPrice {
    private LocalDateTime from;

    /** null means there is no price because the product is outdated or new */
    private BigDecimal value;
    /** can be null if the price is null */
    private Currency currency;

    public ProductPrice(LocalDateTime from, BigDecimal value, Currency currency) {
        this.from = from;
        this.value = value;
        this.currency = currency;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPrice that = (ProductPrice) o;
        return from.equals(that.from) && value.equals(that.value) && currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, value, currency);
    }
}
