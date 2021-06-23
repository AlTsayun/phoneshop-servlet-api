package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.ProductQuantityTooLowException;
import com.es.phoneshop.domain.cart.service.ProductStockNotEnoughException;
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
import java.util.List;
import java.util.Locale;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;
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

    private String referer = "/referer";

    @Before
    public void setup() throws ServletException {

        session = setupSession();
        testCart = setupCart();

        cartService = setupCartService(testCart);
        Configuration configuration = setupConfiguration(cartService);
        servlet = setupServlet(configuration, messagesHandler, config);

        when(request.getHeader(eq("referer"))).thenReturn(referer);
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

        doThrow(new ProductQuantityTooLowException(quantity)).when(cartService).addCartItem(session, productId, quantity);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).addCartItem(any(), eq(productId), eq(quantity));
        verify(messagesHandler).add(any(), any(), eq(MessagesHandler.MessageType.ERROR), any());
        verify(response).sendRedirect(eq(referer));
    }
    @Test
    public void testDoPostProductNotFound() throws IOException {
        int quantity = 1;
        Long productId = 1000L;

        doThrow(new ProductNotFoundException(productId.toString())).when(cartService).addCartItem(session, productId, quantity);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).addCartItem(any(), eq(productId), eq(quantity));
        verify(messagesHandler).add(any(), any(), eq(MessagesHandler.MessageType.ERROR), any());
        verify(response).sendRedirect(eq(referer));
    }

    @Test
    public void testDoPostProductStockNotEnough() throws IOException {
        int quantity = 1000;
        Long productId = 0L;

        doThrow(new ProductStockNotEnoughException()).when(cartService).addCartItem(session, productId, quantity);
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).addCartItem(any(), eq(productId), eq(quantity));
        verify(messagesHandler).add(any(), any(), eq(MessagesHandler.MessageType.ERROR), any());
        verify(response).sendRedirect(eq(referer));
    }

    @Test
    public void testDoPost() throws IOException {
        Long productId = 0L;
        int quantity = 1;

        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).addCartItem(any(), eq(productId), eq(quantity));
        verify(messagesHandler).add(any(), any(), eq(MessagesHandler.MessageType.SUCCESS), any());
        verify(response).sendRedirect(eq(referer));
    }

    @Test
    public void testDoPostQuantityTooBig() throws IOException {
        Long productId = 0L;
        String quantity = Long.toString(Integer.MAX_VALUE + 1L);

        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        when(request.getParameter("productId")).thenReturn(productId.toString());
        when(request.getParameter("quantity")).thenReturn(quantity);

        servlet.doPost(request, response);

        verify(messagesHandler, times(1)).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(eq(referer));
    }

}