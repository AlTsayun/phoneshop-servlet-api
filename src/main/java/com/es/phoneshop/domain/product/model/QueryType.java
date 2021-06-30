package com.es.phoneshop.domain.product.model;

public enum QueryType {
    ALL_WORDS, ANY_WORD;

    public static QueryType fromString(String str) {
        for (QueryType value :
                QueryType.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        return null;
    }
}
