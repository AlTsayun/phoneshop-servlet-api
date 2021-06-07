package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryService;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartServletTest extends TestCase{

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private ErrorHandler errorHandler;

    private Product testProduct;
    private Cart testCart;
    private CartService cartService;

    private HttpSession session;

    private MockedStatic<ConfigurationImpl> configurationStatic;
    private CartServlet servlet;

    private String requestContextPath = "requestContextPath";

    @Before
    public void setup() throws ServletException {

        session = setupSession();
        testProduct = setupTestProduct();
        ProductDao productDao = setupProductDao(testProduct);
        testCart = setupCart();

        cartService = setupCartService(testCart);
        Configuration configuration = setupConfiguration(
                productDao,
                cartService);
        servlet = setupServlet(configuration, errorHandler, config);

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn(requestContextPath);
    }

    private HttpSession setupSession() {
        return mock(HttpSession.class);
    }

    private Product setupTestProduct() {
        return new Product(0L, "code", "descrition", 1, null,
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                        Currency.getInstance("USD"))));
    }

    private ProductDao setupProductDao(Product product) {
        ProductDao productDao = mock(ProductDao.class);
        when(productDao.getById(any())).thenReturn(Optional.of(product));
        return productDao;
    }

    private CartServlet setupServlet(
            Configuration configuration,
            ErrorHandler errorHandler,
            ServletConfig config) throws ServletException {
        CartServlet servlet = new CartServlet(configuration, errorHandler);
        servlet.init(config);
        return servlet;
    }

    private Cart setupCart() {
        return new Cart(List.of(new CartItem(0L, 10)));
    }

    private Configuration setupConfiguration(
            ProductDao productDao,
            CartService cartService) {
        Configuration configuration = mock(ConfigurationImpl.class);
        when(configuration.getProductDao()).thenReturn(productDao);
        when(configuration.getCartService()).thenReturn(cartService);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    private CartService setupCartService(Cart cart) {
        CartService cartService = mock(CartService.class);
        when(cartService.getCart(any())).thenReturn(cart);
        when(cartService.getCart(any())).thenReturn(cart);
        return cartService;
    }

    @After
    public void cleanUp() {
        configurationStatic.close();
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        Long productId = 0L;
        int quantity = 1;
        when(request.getParameter("productId")).thenReturn(String.valueOf(productId));
        when(request.getParameter("quantity")).thenReturn(String.valueOf(quantity));
        when(request.getLocale()).thenReturn(Locale.ENGLISH);
        when(request.getSession()).thenReturn(session);

        servlet.doPost(request, response);

        verify(cartService).add(any(), eq(productId), eq(quantity));

        verify(request).setAttribute(eq("productName"), eq(testProduct.getDescription()));
        verify(request).setAttribute(eq("returnPath"), eq(requestContextPath + "/products/" + productId));
    }

}