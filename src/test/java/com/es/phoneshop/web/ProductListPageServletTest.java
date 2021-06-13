package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.service.CartServiceImpl;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryServiceImpl;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
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

    private MockedStatic<ConfigurationImpl> configurationStatic;

    private ProductListPageServlet servlet;

    private List<Product> testProducts;

    @Before
    public void setup() throws ServletException {

        testProducts = setupTestProducts();
        ProductDao productDao = setupProductDao(testProducts);
        Configuration configuration = setupConfiguration(productDao);
        servlet = setupServlet(configuration, errorHandler, config);

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    private List<Product> setupTestProducts() {
        return List.of(new Product(0L, "code", "descrition", 1, null,
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                        Currency.getInstance("USD")))));
    }

    private ProductDao setupProductDao(List<Product> products) {
        ProductDao productDao = mock(ProductDao.class);
        when(productDao.getAllByRequest(any())).thenReturn(products);
        return productDao;
    }

    private ProductListPageServlet setupServlet(
            Configuration configuration,
            ErrorHandler errorHandler,
            ServletConfig config) throws ServletException {
        ProductListPageServlet servlet = new ProductListPageServlet(configuration, errorHandler);
        servlet.init(config);
        return servlet;
    }

    private Configuration setupConfiguration(ProductDao productDao) {
        Configuration configuration = mock(ConfigurationImpl.class);
        when(configuration.getProductDao()).thenReturn(productDao);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    @After
    public void cleanUp() {
        configurationStatic.close();
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute(eq("products"), eq(testProducts));
    }
}