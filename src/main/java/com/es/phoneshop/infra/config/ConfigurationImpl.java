package com.es.phoneshop.infra.config;

import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.CartServiceImpl;
import com.es.phoneshop.domain.order.persistence.ArrayListOrderDao;
import com.es.phoneshop.domain.order.persistence.OrderDao;
import com.es.phoneshop.domain.order.service.OrderService;
import com.es.phoneshop.domain.order.service.OrderServiceImpl;
import com.es.phoneshop.domain.product.persistence.ArrayListProductDao;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryService;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryServiceImpl;
import com.es.phoneshop.security.dosProtection.service.DosProtectionService;
import com.es.phoneshop.security.dosProtection.service.DosProtectionServiceImpl;
import com.es.phoneshop.utils.LongIdGenerator;
import com.es.phoneshop.utils.LongIdGeneratorImpl;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapperImpl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigurationImpl implements Configuration {

    private final String CART_SESSION_ATTRIBUTE_NAME = ConfigurationImpl.class.getName() + ".cart";
    private final String CART_SESSION_LOCK_ATTRIBUTE_NAME = ConfigurationImpl.class.getName() + ".cart.lock";

    private final String VIEWED_PRODUCTS_SESSION_ATTRIBUTE_NAME = ConfigurationImpl.class.getName() + ".viewedProducts";
    private final String VIEWED_PRODUCTS_SESSION_LOCK_ATTRIBUTE_NAME = ConfigurationImpl.class.getName() + ".viewedProducts.lock";

    private static final Lock instanceLock = new ReentrantLock();
    private static ConfigurationImpl instance;
    private final Lock productDaoLock = new ReentrantLock();
    private final Lock longIdGeneratorLock = new ReentrantLock();
    private final Lock cartServiceLock = new ReentrantLock();
    private final Lock viewedProductsHistoryServiceLock = new ReentrantLock();
    private final Lock sessionLockWrapperLock = new ReentrantLock();
    private final Lock orderDaoLock = new ReentrantLock();
    private final Lock orderServiceLock = new ReentrantLock();
    private final Lock dosProtectionServiceLock = new ReentrantLock();

    private ProductDao productDao;
    private OrderDao orderDao;
    private LongIdGenerator longIdGenerator;
    private CartService cartService;
    private ViewedProductsHistoryService viewedProductsHistoryService;
    private SessionLockWrapper sessionLockWrapper;
    private OrderService orderService;
    private DosProtectionService dosProtectionService;

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
    public OrderDao getOrderDao() {
        if (orderDao == null) {
            orderDaoLock.lock();
            try {
                if (orderDao == null) {
                    orderDao = new ArrayListOrderDao(getLongIdGenerator());
                }
            } finally {
                orderDaoLock.unlock();
            }
        }
        return orderDao;
    }

    @Override
    public DosProtectionService getDosProtectionService() {
        if (dosProtectionService == null) {
            dosProtectionServiceLock.lock();
            try {
                if (dosProtectionService == null) {
                    dosProtectionService = new DosProtectionServiceImpl(20, 30 * 1000);
                }
            } finally {
                dosProtectionServiceLock.unlock();
            }
        }
        return dosProtectionService;
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
                    cartService = new CartServiceImpl(getProductDao(),
                            CART_SESSION_ATTRIBUTE_NAME,
                            getCartSessionLockProvider()
                    );
                }
            } finally {
                cartServiceLock.unlock();
            }
        }
        return cartService;
    }

    @Override
    public OrderService getOrderService() {
        if (orderService == null) {
            orderServiceLock.lock();
            try {
                if (orderService == null) {
                    orderService = new OrderServiceImpl(getProductDao(), getOrderDao(), getCartService());
                }
            } finally {
                orderServiceLock.unlock();
            }
        }
        return orderService;
    }

    @Override
    public ViewedProductsHistoryService getViewedProductsHistoryService() {
        if (viewedProductsHistoryService == null) {
            viewedProductsHistoryServiceLock.lock();
            try {
                if (viewedProductsHistoryService == null) {
                    viewedProductsHistoryService = new ViewedProductsHistoryServiceImpl(
                            getProductDao(),
                            getViewedProductsSessionAttributeName(),
                            getViewedProductsSessionLockProvider(),
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

    @Override
    public SessionLockProvider getCartSessionLockProvider() {
        return getSessionLockWrapper().getSessionLockProvider(CART_SESSION_LOCK_ATTRIBUTE_NAME);
    }

    @Override
    public String getCartSessionAttributeName() {
        return CART_SESSION_ATTRIBUTE_NAME;
    }

    @Override
    public SessionLockProvider getViewedProductsSessionLockProvider() {
        return getSessionLockWrapper().getSessionLockProvider(VIEWED_PRODUCTS_SESSION_LOCK_ATTRIBUTE_NAME);
    }

    @Override
    public String getViewedProductsSessionAttributeName() {
        return VIEWED_PRODUCTS_SESSION_ATTRIBUTE_NAME;
    }
}
