package com.es.phoneshop.domain.cart.service;


import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    private CartServiceImpl cartService;
    private List<Product> testProducts;
    private ProductDao productDao;
    private Cart testCart;

    @Before
    public void setup(){
        testProducts = setupTestProducts();
        productDao = setupProductDao(testProducts);
        testCart = setupCart();
        cartService = new CartServiceImpl(productDao, setupSessionLockWrapper());
    }

    private SessionLockWrapper setupSessionLockWrapper(){
        SessionLockWrapper sessionLockWrapper = mock(SessionLockWrapper.class);
        SessionLockProvider sessionLockProvider = mock(SessionLockProvider.class);
        Lock lock = mock(Lock.class);
        when(sessionLockProvider.getLock(any())).thenReturn(lock);
        when(sessionLockWrapper.getSessionLockProvider(any())).thenReturn(sessionLockProvider);
        return sessionLockWrapper;
    }

    private ProductDao setupProductDao(List<Product> products) {
        ProductDao productDao = mock(ProductDao.class);
        products.forEach(it -> when(productDao.getById(it.getId())).thenReturn(Optional.of(it)));
        return productDao;
    }

    private List<Product> setupTestProducts() {
        return List.of(new Product(0L, "code0", "description0", 20, null,
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                        Currency.getInstance("USD")))),
                new Product(1L, "code1", "description1", 1, null,
                        List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                                Currency.getInstance("USD")))));
    }

    private Cart setupCart() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(0L, 10));
        return new Cart(cartItems);
    }


    @Test
    public void testAdd(){
        cartService.add(testCart, 0L, 1);
        assertEquals(1, testCart.getItems().size());
        assertEquals(11, testCart.getItems().get(0).getQuantity());
        assertEquals(Long.valueOf(0L), testCart.getItems().get(0).getProductId());

        cartService.add(testCart, 1L, 1);
        assertEquals(2, testCart.getItems().size());
        assertEquals(11, testCart.getItems().get(0).getQuantity());
        assertEquals(Long.valueOf(0L), testCart.getItems().get(0).getProductId());
        assertEquals(1, testCart.getItems().get(1).getQuantity());
        assertEquals(Long.valueOf(1L), testCart.getItems().get(1).getProductId());
    }

    @Test(expected = ProductQuantityTooLowException.class)
    public void testAddProductQuantityTooLow(){
        cartService.add(testCart, 0L, -1);
    }

    @Test(expected = ProductStockNotEnoughException.class)
    public void testAddProductStockNotEnoughException(){
        cartService.add(testCart, 0L, 100);
    }

    @Test
    public void testGetCart(){
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(CartServiceImpl.CART_SESSION_ATTRIBUTE)).thenReturn(null);
        Cart cart = cartService.getCart(session);
        assertEquals(0, cart.getItems().size());
        verify(session).setAttribute(eq(CartServiceImpl.CART_SESSION_ATTRIBUTE), any());


        when(session.getAttribute(eq(CartServiceImpl.CART_SESSION_ATTRIBUTE))).thenReturn(testCart);
        cart = cartService.getCart(session);
        assertEquals(testCart, cart);

    }

}