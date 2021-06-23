package com.es.phoneshop.domain.common.model;

import java.math.BigDecimal;
import java.util.Currency;

public class Price {
    private BigDecimal value;
    private Currency currency;

    public Price(BigDecimal value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Currency getCurrency() {
        return currency;
    }
}
