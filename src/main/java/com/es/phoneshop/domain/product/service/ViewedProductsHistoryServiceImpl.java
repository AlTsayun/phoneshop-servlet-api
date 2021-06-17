package com.es.phoneshop.domain.product.service;

import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

public class ViewedProductsHistoryServiceImpl implements ViewedProductsHistoryService {

    private final String viewedProductsSessionAttributeName;
    private final SessionLockProvider sessionLockProvider;
    private final ProductDao productDao;
    private int historySize;

    public ViewedProductsHistoryServiceImpl(ProductDao productDao,
                                            String viewedProductsSessionAttributeName,
                                            SessionLockProvider sessionLockProvider,
                                            int historySize) {
        this.productDao = productDao;
        this.viewedProductsSessionAttributeName = viewedProductsSessionAttributeName;
        this.sessionLockProvider = sessionLockProvider;
        this.historySize = historySize;
    }

    @Override
    public void add(HttpSession session, Long productId) {
        Lock lock = sessionLockProvider.getLock(session).writeLock();
        lock.lock();
        try {

            List<Long> productsIds = (List<Long>) session.getAttribute(viewedProductsSessionAttributeName);

            if (productsIds == null) {
                session.setAttribute(viewedProductsSessionAttributeName, productsIds = new ArrayList<>());
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
            lock.unlock();
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
        Lock lock = sessionLockProvider.getLock(session).writeLock();
        lock.lock();
        try {
            List<Long> productsIds = (List<Long>) session.getAttribute(viewedProductsSessionAttributeName);
            if (productsIds == null) {
                session.setAttribute(viewedProductsSessionAttributeName, productsIds = new ArrayList<>());
            }
            return productsIds;
        } finally {
            lock.unlock();
        }
    }
}
