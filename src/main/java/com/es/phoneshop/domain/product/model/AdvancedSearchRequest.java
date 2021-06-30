package com.es.phoneshop.domain.product.model;

import java.math.BigDecimal;

public class AdvancedSearchRequest {
    private String query;
    private QueryType queryType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public AdvancedSearchRequest(String query, QueryType queryType, BigDecimal minPrice, BigDecimal maxPrice) {
        this.query = query;
        this.queryType = queryType;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getQuery() {
        return query;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
}
