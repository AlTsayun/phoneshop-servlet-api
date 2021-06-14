package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.ProductQuantityTooLowException;
import com.es.phoneshop.domain.cart.service.ProductStockNotEnoughException;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
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
import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartItemAddServletTest extends TestCase{

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;
    @Mock
    private MessagesHandler messagesHandler;

    private Cart testCart;
    private CartService cartService;

    private HttpSession session;

    private MockedStatic<ConfigurationImpl> configurationStatic;
    private CartItemAddServlet servlet;

    private String requestContextPath = "requestContextPath";

    @Before
    public void setup() throws ServletException {

        session = setupSession();
        testCart = setupCart();

        cartService = setupCartService(testCart);
        Configuration configuration = setupConfiguration(cartService);
        servlet = setupServlet(configuration, messagesHandler, config);

        when(request.getContextPath()).thenReturn(requestContextPath);
    }

    private HttpSession setupSession() {
        return mock(HttpSession.class);
    }

    private CartItemAddServlet setupServlet(
            Configuration configuration,
            MessagesHandler messagesHandler,
            ServletConfig config) throws ServletException {
        CartItemAddServlet servlet = new CartItemAddServlet(configuration, messagesHandler);
        servlet.init(config);
        return servlet;
    }

    private Cart setupCart() {
        return new Cart(List.of(new CartItem(0L, 10)));
    }

    private Configuration setupConfiguration(
            CartService cartService) {
        Configuration configuration = mock(ConfigurationImpl.class);
        when(configuration.getCartService()).thenReturn(cartService);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    private CartService setupCartService(Cart cart) {
        return mock(CartService.class);
    }

    @After
    public void cleanUp() {
        configurationStatic.close();
    }

    @Test
    public void testDoPostNegativeQuantity() throws IOException {
        int quantity = -1;
        Long productId = 0L;
        String returnPath = "/products/0";

        doThrow(new ProductQuantityTooLowException(quantity)).when(cartService).add(session, productId, quantity);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getParameter("returnPath")).thenReturn(returnPath);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).add(any(), eq(productId), eq(quantity));
        verify(messagesHandler).add(any(), any(), eq(MessagesHandler.MessageType.ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + returnPath));
    }
    @Test
    public void testDoPostProductNotFound() throws IOException {
        int quantity = 1;
        Long productId = 1000L;
        String returnPath = "/products/0";

        doThrow(new ProductNotFoundException(productId.toString())).when(cartService).add(session, productId, quantity);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getParameter("returnPath")).thenReturn(returnPath);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).add(any(), eq(productId), eq(quantity));
        verify(messagesHandler).add(any(), any(), eq(MessagesHandler.MessageType.ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + returnPath));
    }

    @Test
    public void testDoPostProductStockNotEnough() throws IOException {
        int quantity = 1000;
        Long productId = 0L;
        String returnPath = "/products/0";

        doThrow(new ProductStockNotEnoughException()).when(cartService).add(session, productId, quantity);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getParameter("returnPath")).thenReturn(returnPath);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).add(any(), eq(productId), eq(quantity));
        verify(messagesHandler).add(any(), any(), eq(MessagesHandler.MessageType.ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + returnPath));
    }

    @Test
    public void testDoPost() throws IOException {
        Long productId = 0L;
        int quantity = 1;
        String returnPath = "/products/0";

        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getParameter("returnPath")).thenReturn(returnPath);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).add(any(), eq(productId), eq(quantity));
        verify(messagesHandler).add(any(), any(), eq(MessagesHandler.MessageType.SUCCESS), any());
        verify(response).sendRedirect(eq(requestContextPath + returnPath));
    }

}