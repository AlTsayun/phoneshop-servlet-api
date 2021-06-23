package com.es.phoneshop.infra.config;

import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.order.persistence.OrderDao;
import com.es.phoneshop.domain.order.service.OrderService;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryService;
import com.es.phoneshop.security.dosProtection.service.DosProtectionService;
import com.es.phoneshop.utils.LongIdGenerator;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;

public interface Configuration {
    ProductDao getProductDao();

    LongIdGenerator getLongIdGenerator();

    CartService getCartService();

    OrderService getOrderService();

    OrderDao getOrderDao();

    DosProtectionService getDosProtectionService();

    ViewedProductsHistoryService getViewedProductsHistoryService();

    SessionLockWrapper getSessionLockWrapper();

    SessionLockProvider getCartSessionLockProvider();

    String getCartSessionAttributeName();

    SessionLockProvider getViewedProductsSessionLockProvider();

    String getViewedProductsSessionAttributeName();

}
