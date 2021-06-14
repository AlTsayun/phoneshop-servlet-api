package com.es.phoneshop.domain.product.service;

import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ViewedProductsHistoryServiceImplTest {

    @Before
    public void setup(){
        testProducts = setupTestProducts();
        productDao = setupProductDao(testProducts);
        service = new ViewedProductsHistoryServiceImpl(
                productDao,
                viewedProductsSessionAttributeName,
                setupSessionLockProvider(),
                3);
    }

    @Test
    public void testAdd() {

        List<Long> productIds = new ArrayList<>();

        HttpSession session = setupSession(viewedProductsSessionAttributeName, productIds);

        List<Long> expectedIds = new ArrayList<>();
        expectedIds.add(0L);
        service.add(session, 0L);
        assertEquals(expectedIds, productIds);

        productIds.clear();
        expectedIds = new ArrayList<>();
        expectedIds.add(1L);
        expectedIds.add(0L);
        service.add(session, 0L);
        service.add(session, 1L);
        assertEquals(expectedIds, productIds);

        productIds.clear();
        expectedIds = new ArrayList<>();
        expectedIds.add(0L);
        expectedIds.add(1L);
        service.add(session, 0L);
        service.add(session, 1L);
        service.add(session, 0L);
        assertEquals(expectedIds, productIds);

        productIds.clear();
        expectedIds = new ArrayList<>();
        expectedIds.add(3L);
        expectedIds.add(2L);
        expectedIds.add(1L);
        service.add(session, 0L);
        service.add(session, 1L);
        service.add(session, 2L);
        service.add(session, 3L);
        assertEquals(expectedIds, productIds);

    }


    @Test(expected = ProductNotFoundException.class)
    public void testAddWrongProductId() {
        List<Long> productIds = new ArrayList<>();
        HttpSession session = setupSession(viewedProductsSessionAttributeName, productIds);
        service.add(session, 10L);
    }

    @Test
    public void testGetProductIds() {
        List<Long> productsIds = new ArrayList<>();
        productsIds.add(0L);
        HttpSession session = setupSession(viewedProductsSessionAttributeName, productsIds);
        assertEquals(productsIds, service.getProductIds(session));
    }


    private HttpSession setupSession(String viewedProductsSessionAttributeName, List<Long> productIds){
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(viewedProductsSessionAttributeName)).thenReturn(productIds);
        return session;
    }

    private List<Product> setupTestProducts() {
        return List.of(new Product(0L, "code0", "description0", 20, null,
                        List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                                Currency.getInstance("USD")))),
                new Product(1L, "code1", "description1", 1, null,
                        List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                                Currency.getInstance("USD")))),
                new Product(2L, "code1", "description1", 1, null,
                        List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                                Currency.getInstance("USD")))),
                new Product(3L, "code1", "description1", 1, null,
                        List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                                Currency.getInstance("USD")))));
    }

    private ProductDao setupProductDao(List<Product> products) {
        ProductDao productDao = mock(ProductDao.class);
        products.forEach(it -> when(productDao.getById(it.getId())).thenReturn(Optional.of(it)));
        return productDao;
    }


    private SessionLockProvider setupSessionLockProvider(){
        SessionLockProvider sessionLockProvider = mock(SessionLockProvider.class);
        ReadWriteLock readWriteLock = mock(ReadWriteLock.class);
        Lock readLock = mock(Lock.class);
        Lock writeLock = mock(Lock.class);
        when(sessionLockProvider.getLock(any())).thenReturn(readWriteLock);
        when(readWriteLock.readLock()).thenReturn(readLock);
        when(readWriteLock.writeLock()).thenReturn(writeLock);
        return sessionLockProvider;
    }

    private ViewedProductsHistoryServiceImpl service;

    private List<Product> testProducts;
    private ProductDao productDao;

    private final String viewedProductsSessionAttributeName = "session.viewedProducts";
}