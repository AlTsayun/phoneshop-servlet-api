package com.es.phoneshop.domain.product.service;

import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ViewedProductsHistoryServiceImpl implements ViewedProductsHistoryService {

    public static final String VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE =
            ViewedProductsHistoryServiceImpl.class.getName() + ".viewedProductsHistory";

    public static final String VIEWED_PRODUCTS_HISTORY_SESSION_LOCK_ATTRIBUTE =
            ViewedProductsHistoryServiceImpl.class.getName() + ".viewedProductsHistory.lock";
    private final SessionLockProvider sessionLockProvider;
    private final ProductDao productDao;
    private int historySize;

    public ViewedProductsHistoryServiceImpl(SessionLockWrapper sessionLockWrapper, ProductDao productDao, int historySize) {
        this.sessionLockProvider = sessionLockWrapper.getSessionLockProvider(VIEWED_PRODUCTS_HISTORY_SESSION_LOCK_ATTRIBUTE);
        this.historySize = historySize;
        this.productDao = productDao;
    }

    @Override
    public void add(HttpSession session, Long productId) {
        sessionLockProvider.getLock(session).readLock().lock();
        try {

            List<Long> productsIds = (List<Long>) session.getAttribute(VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE);
            if (productsIds == null) {
                session.setAttribute(VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE, productsIds = new ArrayList<>());
            }

            if (productDao.getById(productId).isPresent()) {
                productsIds.stream()
                        .filter(productId::equals)
                        .findFirst()
                        .ifPresent(productsIds::remove);

                readjustHistorySize(productsIds);

                productsIds.add(0, productId);
            } else {
                throw new ProductNotFoundException(productId.toString());
            }

        } finally {
            sessionLockProvider.getLock(session).readLock().unlock();
        }
    }

    private void readjustHistorySize(List<Long> viewedProducts) {
        int from = historySize - 1;
        int to = viewedProducts.size();

        IntStream.range(from, to)
                .map(i -> to - i + from - 1)
                .forEach(viewedProducts::remove);
    }

    @Override
    public List<Long> getProductIds(HttpSession session) {
        sessionLockProvider.getLock(session).readLock().lock();
        try {
            List<Long> products = (List<Long>) session.getAttribute(VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE);
            if (products == null) {
                session.setAttribute(VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE, products = new ArrayList<>());
            }
            return products;
        } finally {
            sessionLockProvider.getLock(session).readLock().unlock();
        }
    }
}
