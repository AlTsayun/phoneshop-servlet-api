package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.ProductNotFoundInCartException;
import com.es.phoneshop.domain.cart.service.ProductQuantityTooLowException;
import com.es.phoneshop.domain.cart.service.ProductStockNotEnoughException;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import com.es.phoneshop.web.MessagesHandler.MessageType;
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

import static com.es.phoneshop.web.MessagesHandler.MessageType.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CartItemDeleteServletTest extends TestCase{

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;
    @Mock
    private MessagesHandler messagesHandler;

    private CartService cartService;

    private HttpSession session;

    private MockedStatic<ConfigurationImpl> configurationStatic;
    private CartItemDeleteServlet servlet;

    private String requestContextPath = "requestContextPath";

    @Before
    public void setup() throws ServletException {

        session = setupSession();

        cartService = setupCartService();
        Configuration configuration = setupConfiguration(cartService);
        servlet = setupServlet(configuration, messagesHandler, config);

        when(request.getContextPath()).thenReturn(requestContextPath);
    }

    private HttpSession setupSession() {
        return mock(HttpSession.class);
    }

    private CartItemDeleteServlet setupServlet(
            Configuration configuration,
            MessagesHandler messagesHandler,
            ServletConfig config) throws ServletException {
        CartItemDeleteServlet servlet = new CartItemDeleteServlet(configuration, messagesHandler);
        servlet.init(config);
        return servlet;
    }

    private Configuration setupConfiguration(
            CartService cartService) {
        Configuration configuration = mock(ConfigurationImpl.class);
        when(configuration.getCartService()).thenReturn(cartService);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    private CartService setupCartService() {
        return mock(CartService.class);
    }

    @After
    public void cleanUp() {
        configurationStatic.close();
    }

    @Test
    public void testDoPostProductNotFound() throws IOException {
        Long productId = 0L;
        String returnPath = "/products/0";

        doThrow(new ProductNotFoundInCartException(productId.toString()))
                .when(cartService).deleteById(session, productId);
        when(request.getPathInfo()).thenReturn("/" + productId);
        when(request.getParameter("returnPath")).thenReturn(returnPath);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(messagesHandler).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + returnPath));
    }

    @Test
    public void testDoPost() throws IOException {
        Long productId = 0L;
        String returnPath = "/products/0";

        when(request.getPathInfo()).thenReturn("/" + productId);
        when(request.getParameter("returnPath")).thenReturn(returnPath);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).deleteById(eq(session), eq(productId));
        verify(messagesHandler).add(any(), any(), eq(SUCCESS), any());
        verify(response).sendRedirect(eq(requestContextPath + returnPath));
    }
}