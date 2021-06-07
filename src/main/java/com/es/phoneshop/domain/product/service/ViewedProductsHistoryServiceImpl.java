package com.es.phoneshop.domain.product.service;

import com.es.phoneshop.domain.cart.service.CartServiceImpl;
import com.es.phoneshop.domain.product.model.Product;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ViewedProductsHistoryServiceImpl implements ViewedProductsHistoryService {

    public static final String VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE =
            ViewedProductsHistoryServiceImpl.class.getName() + ".viewedProductsHistory";

    private int historySize;

    public ViewedProductsHistoryServiceImpl(int historySize) {
        this.historySize = historySize;
    }

    @Override
    public void setHistorySize(int size) {
        historySize = size;
    }

    @Override
    public void add(List<Long> viewedProductsIds, Long productId) {
        viewedProductsIds.stream()
                .filter(productId::equals)
                .findFirst()
                .ifPresent(viewedProductsIds::remove);

        readjustHistorySize(viewedProductsIds);

        viewedProductsIds.add(0, productId);
    }

    private void readjustHistorySize(List<Long> viewedProducts){
        int from = historySize - 1;
        int to = viewedProducts.size();

        IntStream.range(from, to)
                .map(i -> to - i + from - 1)
                .forEach(viewedProducts::remove);
    }

    @Override
    public List<Long> getProductIds(HttpSession session) {
        List<Long> products = (List<Long>) session.getAttribute(VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE);
        if (products == null){
            session.setAttribute(VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE, products = new ArrayList<>());
        }
        return products;
    }
}
