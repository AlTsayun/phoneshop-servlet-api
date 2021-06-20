package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.order.service.OrderService;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private CheckoutPageServlet servlet;

    @Mock
    private MessagesHandler messagesHandler;

    private ProductDao productDao;
    private CartService cartService;
    private OrderService orderService;
    private List<Product> testProducts;

    @Mock
    private RequestDispatcher requestDispatcher;
    private HttpSession session;

    @Before
    public void setup() {
        testProducts = setupTestProducts();
        productDao = setupProductDao(testProducts);
        session = setupSession();
        cartService = setupCartService(session, setupCart());
        orderService = setupOrderService();
        servlet = new CheckoutPageServlet(setupConfiguration(productDao, cartService, orderService), messagesHandler);
        when(request.getRequestDispatcher(any())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
    }


    private Cart setupCart() {
        return new Cart(List.of(new CartItem(0L, 10)));
    }

    private HttpSession setupSession() {
        return mock(HttpSession.class);
    }


    private OrderService setupOrderService() {
        OrderService service = mock(OrderService.class);
        return service;
    }

    private CartService setupCartService(HttpSession session, Cart cart) {
        CartService cartService = mock(CartService.class);
        when(cartService.get(session)).thenReturn(cart);
        return cartService;
    }

    private ProductDao setupProductDao(List<Product> products) {
        ProductDao productDao = mock(ProductDao.class);
        products.forEach(it -> when(productDao.getById(it.getId())).thenReturn(Optional.of(it)));
        return productDao;
    }

    private List<Product> setupTestProducts() {
        return List.of(new Product(0L, "code", "descrition", 1, null,
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                        Currency.getInstance("USD")))));
    }

    private Configuration setupConfiguration(ProductDao productDao, CartService cartService, OrderService orderService) {
        Configuration config = mock(Configuration.class);
        when(config.getProductDao()).thenReturn(productDao);
        when(config.getOrderService()).thenReturn(orderService);
        when(config.getCartService()).thenReturn(cartService);
        return config;
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("productsInCart"), any());
        verify(requestDispatcher).forward(request, response);
    }
}