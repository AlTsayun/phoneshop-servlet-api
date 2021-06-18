package com.es.phoneshop.domain.product.service;

import com.es.phoneshop.domain.product.model.Product;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface ViewedProductsHistoryService {
    void add(HttpSession session, Long productId);
    List<Long> getProductIds(HttpSession session);
}
