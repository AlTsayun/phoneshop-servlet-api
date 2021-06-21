package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.order.service.CartEmptyException;
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
import java.util.Map;
import java.util.Optional;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;
import static com.es.phoneshop.web.MessagesHandler.MessageType.SUCCESS;
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

    private String contextPath = "/contextPath";

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

    private Cart setupIllegalCart() {
        return new Cart(List.of(new CartItem(100L, 10)));
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

    private HttpServletRequest setupPostRequest(
            HttpSession session,
            String firstName,
            String lastName,
            String phoneNumber,
            String deliveryDate,
            String deliveryAddress,
            String paymentMethod) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getParameter(eq("firstName"))).thenReturn(firstName);
        when(req.getParameter(eq("lastName"))).thenReturn(lastName);
        when(req.getParameter(eq("phoneNumber"))).thenReturn(phoneNumber);
        when(req.getParameter(eq("deliveryDate"))).thenReturn(deliveryDate);
        when(req.getParameter(eq("deliveryAddress"))).thenReturn(deliveryAddress);
        when(req.getParameter(eq("paymentMethod"))).thenReturn(paymentMethod);
        when(req.getContextPath()).thenReturn(contextPath);
        when(req.getSession()).thenReturn(session);
        return req;
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("productsInCart"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetProductNotPresentInDao() throws ServletException, IOException {
        when(cartService.get(session)).thenReturn(setupIllegalCart());
        servlet.doGet(request, response);
        verify(messagesHandler).add(any(), any(), eq(ERROR), any());
        verify(request).setAttribute(eq("productsInCart"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        request = setupPostRequest(session,
                "firstName",
                "lastName",
                "123-12-12",
                "4000-01-01",
                "deliveryAddress",
                "cash");
        servlet.doPost(request, response);
        verify(messagesHandler).add(any(), any(), eq(SUCCESS), any());
        verify(cartService).clear(session);
        verify(response).sendRedirect(any());
    }

    @Test
    public void testDoPostIllegalFirstName() throws ServletException, IOException {
        request = setupPostRequest(session,
                "123",
                "lastName",
                "123-12-12",
                "4000-01-01",
                "deliveryAddress",
                "cash");
        servlet.doPost(request, response);
        verify(messagesHandler).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(any());
    }

    @Test
    public void testDoPostIllegalLastName() throws ServletException, IOException {
        request = setupPostRequest(session,
                "fistName",
                "123",
                "123-12-12",
                "4000-01-01",
                "deliveryAddress",
                "cash");
        servlet.doPost(request, response);
        verify(messagesHandler).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(any());
    }

    @Test
    public void testDoPostEmptyCart() throws ServletException, IOException {
        request = setupPostRequest(session,
                "firstName",
                "lastName",
                "123-12-12",
                "4000-01-01",
                "deliveryAddress",
                "cash");
        when(orderService.order(any(), any(), any(), any())).thenThrow(new CartEmptyException());
        when(request.getParameterMap()).thenReturn(Map.of());
        servlet.doPost(request, response);
        verify(messagesHandler).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(eq(contextPath + "/checkout"));
    }
}