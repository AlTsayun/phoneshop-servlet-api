package com.es.phoneshop.domain.cart.service;


import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
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
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    @Before
    public void setup(){
        testProducts = setupTestProducts();
        productDao = setupProductDao(testProducts);
        testCart = setupCart();
        session = setupSession(testCart, cartSessionAttributeName);
        cartService = new CartServiceImpl(productDao, cartSessionAttributeName,setupSessionLockProvider());
    }

    @Test
    public void testAdd(){
        cartService.addCartItem(session, 0L, 1);
        assertEquals(1, testCart.getItems().size());
        assertEquals(11, testCart.getItems().get(0).getQuantity());
        assertEquals(Long.valueOf(0L), testCart.getItems().get(0).getProductId());

        cartService.addCartItem(session, 1L, 1);
        assertEquals(2, testCart.getItems().size());
        assertEquals(11, testCart.getItems().get(0).getQuantity());
        assertEquals(Long.valueOf(0L), testCart.getItems().get(0).getProductId());
        assertEquals(1, testCart.getItems().get(1).getQuantity());
        assertEquals(Long.valueOf(1L), testCart.getItems().get(1).getProductId());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testAddWrongProductId(){
        cartService.addCartItem(session, 10L, 1);
    }

    @Test(expected = ProductQuantityTooLowException.class)
    public void testAddProductQuantityTooLow(){
        cartService.addCartItem(session, 0L, -1);
    }

    @Test(expected = ProductStockNotEnoughException.class)
    public void testAddProductStockNotEnoughException(){
        cartService.addCartItem(session, 0L, 100);
    }

    @Test
    public void testUpdate(){
        cartService.updateCartItem(session, 0L, 1);
        assertEquals(1, testCart.getItems().size());
        assertEquals(1, testCart.getItems().get(0).getQuantity());
        assertEquals(Long.valueOf(0L), testCart.getItems().get(0).getProductId());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testUpdateWrongProductId(){
        cartService.updateCartItem(session, 10L, 1);
    }

    @Test(expected = ProductQuantityTooLowException.class)
    public void testUpdateProductQuantityTooLow(){
        cartService.updateCartItem(session, 0L, -1);
    }

    @Test(expected = ProductStockNotEnoughException.class)
    public void testUpdateProductStockNotEnoughException(){
        cartService.updateCartItem(session, 0L, 100);
    }

    @Test
    public void testDeleteById(){
        cartService.deleteCartItemById(session, 0L);
        assertEquals(0, testCart.getItems().size());
    }

    @Test(expected = ProductNotFoundInCartException.class)
    public void testDeleteByIdWrongProductId(){
        cartService.deleteCartItemById(session, 1L);
    }


    @Test
    public void testGetCart(){
        Cart resultCart = cartService.get(session);
        assertEquals(testCart, resultCart);
        verify(session).getAttribute(eq(cartSessionAttributeName));
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

    private HttpSession setupSession(Cart cart, String cartSessionAttributeName){
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(cartSessionAttributeName)).thenReturn(cart);
        return session;
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


    private CartServiceImpl cartService;
    private List<Product> testProducts;
    private ProductDao productDao;
    private HttpSession session;
    private Cart testCart;
    private final String cartSessionAttributeName = "session.cart";

}