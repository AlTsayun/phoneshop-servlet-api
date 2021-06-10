package com.es.phoneshop.infra.config;

import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.CartServiceImpl;
import com.es.phoneshop.domain.product.persistence.ArrayListProductDao;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryService;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryServiceImpl;
import com.es.phoneshop.utils.LongIdGenerator;
import com.es.phoneshop.utils.LongIdGeneratorImpl;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapperImpl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigurationImpl implements Configuration {

    private static final Lock instanceLock = new ReentrantLock();
    private static ConfigurationImpl instance;
    private final Lock productDaoLock = new ReentrantLock();
    private final Lock longIdGeneratorLock = new ReentrantLock();
    private final Lock cartServiceLock = new ReentrantLock();
    private final Lock viewedProductsHistoryServiceLock = new ReentrantLock();
    private final Lock sessionLockWrapperLock = new ReentrantLock();
    private ProductDao productDao;
    private LongIdGenerator longIdGenerator;
    private CartService cartService;
    private ViewedProductsHistoryService viewedProductsHistoryService;
    private SessionLockWrapper sessionLockWrapper;

    private ConfigurationImpl() {
    }

    public static ConfigurationImpl getInstance() {
        instanceLock.lock();
        try {
            if (instance == null) {
                instance = new ConfigurationImpl();
            }
            return instance;
        } finally {
            instanceLock.unlock();
        }
    }

    @Override
    public ProductDao getProductDao() {
        if (productDao == null) {
            productDaoLock.lock();
            try {
                if (productDao == null) {
                    productDao = new ArrayListProductDao(getLongIdGenerator());
                }
            } finally {
                productDaoLock.unlock();
            }
        }
        return productDao;
    }

    @Override
    public LongIdGenerator getLongIdGenerator() {
        if (longIdGenerator == null) {
            longIdGeneratorLock.lock();
            try {
                if (longIdGenerator == null) {
                    longIdGenerator = new LongIdGeneratorImpl(0L);
                }
            } finally {
                longIdGeneratorLock.unlock();
            }
        }
        return longIdGenerator;
    }

    @Override
    public CartService getCartService() {
        if (cartService == null) {
            cartServiceLock.lock();
            try {
                if (cartService == null) {
                    cartService = new CartServiceImpl(getProductDao(), getSessionLockWrapper());
                }
            } finally {
                cartServiceLock.unlock();
            }
        }
        return cartService;
    }

    @Override
    public ViewedProductsHistoryService getViewedProductsHistoryService() {
        if (viewedProductsHistoryService == null) {
            viewedProductsHistoryServiceLock.lock();
            try {
                if (viewedProductsHistoryService == null) {
                    viewedProductsHistoryService = new ViewedProductsHistoryServiceImpl(
                            getSessionLockWrapper(),
                            getProductDao(),
                            3);
                }
            } finally {
                viewedProductsHistoryServiceLock.unlock();
            }
        }
        return viewedProductsHistoryService;
    }

    @Override
    public SessionLockWrapper getSessionLockWrapper() {
        if (sessionLockWrapper == null) {
            sessionLockWrapperLock.lock();
            try {
                if (sessionLockWrapper == null) {
                    sessionLockWrapper = new SessionLockWrapperImpl();
                }
            } finally {
                sessionLockWrapperLock.unlock();
            }
        }
        return sessionLockWrapper;
    }
}
