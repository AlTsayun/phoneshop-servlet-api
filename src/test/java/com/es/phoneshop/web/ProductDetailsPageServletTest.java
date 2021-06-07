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
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest extends TestCase {

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
    private List<Long> viewedProductsIds;
    private Cart testCart;
    private CartService cartService;
    private ViewedProductsHistoryService viewedProductsHistoryService;

    private MockedStatic<ConfigurationImpl> configurationStatic;
    private ProductDetailsPageServlet servlet;

    @Before
    public void setup() throws ServletException {

        testProduct = setupTestProduct();
        ProductDao productDao = setupProductDao(testProduct);
        viewedProductsIds = setupViewedProductsIds();
        testCart = setupCart();

        cartService = setupCartService(testCart);
        viewedProductsHistoryService = setupViewedProductsHistoryService(viewedProductsIds);
        Configuration configuration = setupConfiguration(
                productDao,
                cartService,
                viewedProductsHistoryService);
        servlet = setupServlet(configuration, errorHandler, config);

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
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

    private ProductDetailsPageServlet setupServlet(
            Configuration configuration,
            ErrorHandler errorHandler,
            ServletConfig config) throws ServletException {
        ProductDetailsPageServlet servlet = new ProductDetailsPageServlet(configuration, errorHandler);
        servlet.init(config);
        return servlet;
    }

    private List<Long> setupViewedProductsIds() {
        return List.of();
    }

    private Cart setupCart() {
        return new Cart(List.of(new CartItem(0L, 10)));
    }

    private Configuration setupConfiguration(
            ProductDao productDao,
            CartService cartService,
            ViewedProductsHistoryService viewedProductsHistoryService) {
        Configuration configuration = mock(ConfigurationImpl.class);
        when(configuration.getProductDao()).thenReturn(productDao);
        when(configuration.getCartService()).thenReturn(cartService);
        when(configuration.getViewedProductsHistoryService()).thenReturn(viewedProductsHistoryService);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    private CartService setupCartService(Cart cart) {
        CartService cartService = mock(CartService.class);
        when(cartService.getCart(any())).thenReturn(cart);
        return cartService;
    }

    private ViewedProductsHistoryService setupViewedProductsHistoryService(List<Long> viewedProductsIds) {
        ViewedProductsHistoryService service = mock(ViewedProductsHistoryService.class);
        when(service.getProductIds(any())).thenReturn(viewedProductsIds);
        return service;
    }

    @After
    public void cleanUp() {
        configurationStatic.close();
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        Long productId = 0L;
        when(request.getPathInfo()).thenReturn("/" + productId);
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("product"), eq(testProduct));
        verify(cartService).getCart(any());
        verify(viewedProductsHistoryService).add(any(), eq(productId));
    }

    @Test
    public void testDoGetWrongProductId() throws ServletException, IOException {
        String productIdStr = "asd";
        when(request.getPathInfo()).thenReturn("/" + productIdStr);
        servlet.doGet(request, response);
        verify(errorHandler).productNotFound(request, response, productIdStr);
    }

}