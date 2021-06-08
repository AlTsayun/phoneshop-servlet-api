package com.es.phoneshop.domain.product.service;

import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ViewedProductsHistoryServiceImplTest {

    private ViewedProductsHistoryServiceImpl service;

    @Before
    public void setup(){
        service = new ViewedProductsHistoryServiceImpl(setupSessionLockWrapper(),3);
    }


    private SessionLockWrapper setupSessionLockWrapper(){
        SessionLockWrapper sessionLockWrapper = mock(SessionLockWrapper.class);
        SessionLockProvider sessionLockProvider = mock(SessionLockProvider.class);
        Lock lock = mock(Lock.class);
        when(sessionLockProvider.getLock(any())).thenReturn(lock);
        when(sessionLockWrapper.getSessionLockProvider(any())).thenReturn(sessionLockProvider);
        return sessionLockWrapper;
    }

    @Test
    public void testAdd() {

        List<Long> productsIds = new ArrayList<>();
        service.add(productsIds, 0L);
        assertEquals(productsIds.get(0), Long.valueOf(0L));

        service.add(productsIds, 1L);
        assertEquals(productsIds.get(0), Long.valueOf(1L));
        assertEquals(productsIds.get(1), Long.valueOf(0L));


        service.add(productsIds, 0L);
        assertEquals(productsIds.get(0), Long.valueOf(0L));
        assertEquals(productsIds.get(1), Long.valueOf(1L));

        service.add(productsIds, 0L);
        service.add(productsIds, 1L);
        service.add(productsIds, 2L);
        service.add(productsIds, 3L);
        assertEquals(productsIds.size(), 3);
        assertEquals(productsIds.get(0), Long.valueOf(3L));
        assertEquals(productsIds.get(1), Long.valueOf(2L));
        assertEquals(productsIds.get(2), Long.valueOf(1L));

    }

    @Test
    public void testGetProductIds() {
        HttpSession session = mock(HttpSession.class);
        List<Long> productsIds = List.of(0L);
        when(session.getAttribute(ViewedProductsHistoryServiceImpl.VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE))
                .thenReturn(productsIds);
        service.getProductIds(session);

        verify(session).getAttribute(eq(ViewedProductsHistoryServiceImpl.VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE));


        when(session.getAttribute(ViewedProductsHistoryServiceImpl.VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE))
                .thenReturn(null);
        service.getProductIds(session);

        verify(session).setAttribute(
                eq(ViewedProductsHistoryServiceImpl.VIEWED_PRODUCTS_HISTORY_SESSION_ATTRIBUTE),
                eq(List.of()));
    }
}