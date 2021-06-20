package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.MiniCart;
import com.es.phoneshop.domain.cart.service.CartService;
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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MiniCartServletTest extends TestCase {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;
    @Mock
    private RequestDispatcher requestDispatcher;

    private CartService cartService;

    private HttpSession session;

    private MockedStatic<ConfigurationImpl> configurationStatic;
    private MiniCartServlet servlet;

    private MiniCart testMiniCart;

    @Before
    public void setup() throws ServletException {

        session = setupSession();
        testMiniCart = setupMiniCart();
        cartService = setupCartService(session, testMiniCart);
        Configuration configuration = setupConfiguration(cartService);
        servlet = setupServlet(configuration, config);

        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(any())).thenReturn(requestDispatcher);
    }

    private MiniCart setupMiniCart() {
        return new MiniCart(10, new BigDecimal(100), Currency.getInstance("USD"));
    }


    private HttpSession setupSession() {
        return mock(HttpSession.class);
    }

    private MiniCartServlet setupServlet(Configuration configuration, ServletConfig config) throws ServletException {
        MiniCartServlet servlet = new MiniCartServlet(configuration);
        servlet.init(config);
        return servlet;
    }

    private Configuration setupConfiguration(CartService cartService) {
        Configuration configuration = mock(ConfigurationImpl.class);
        when(configuration.getCartService()).thenReturn(cartService);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    private CartService setupCartService(HttpSession session, MiniCart cart) {
        CartService cartService = mock(CartService.class);
        when(cartService.getMiniCart(session)).thenReturn(cart);
        return cartService;
    }

    @After
    public void cleanUp() {
        configurationStatic.close();
    }


    @Test
    public void testDoGet() throws IOException, ServletException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq("productsInCartCount"), eq(10));
        verify(request).setAttribute(eq("totalCartPriceValue"), eq(new BigDecimal(100)));
        verify(request).setAttribute(eq("totalCartPriceCurrency"), eq(Currency.getInstance("USD")));

        verify(requestDispatcher).include(request, response);
    }
}