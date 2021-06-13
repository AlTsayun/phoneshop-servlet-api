package com.es.phoneshop.infra.config;

import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryService;
import com.es.phoneshop.utils.LongIdGenerator;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;

public interface Configuration {
    ProductDao getProductDao();

    LongIdGenerator getLongIdGenerator();

    CartService getCartService();

    ViewedProductsHistoryService getViewedProductsHistoryService();

    SessionLockWrapper getSessionLockWrapper();

}
