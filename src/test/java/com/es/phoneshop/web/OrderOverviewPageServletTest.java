package com.es.phoneshop.web;

import com.es.phoneshop.domain.common.model.PaymentMethod;
import com.es.phoneshop.domain.common.model.Price;
import com.es.phoneshop.domain.order.model.ContactDetails;
import com.es.phoneshop.domain.order.model.DeliveryDetails;
import com.es.phoneshop.domain.order.model.Order;
import com.es.phoneshop.domain.order.model.OrderItem;
import com.es.phoneshop.domain.order.persistence.OrderDao;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private OrderOverviewPageServlet servlet;

    @Mock
    private ErrorHandler errorHandler;

    private ProductDao productDao;
    private OrderDao orderDao;
    private List<Product> testProducts;
    private List<Order> testOrders;

    @Mock
    private RequestDispatcher requestDispatcher;
    private HttpSession session;

    @Before
    public void setup() {
        testProducts = setupTestProducts();
        testOrders = setupTestOrders();
        productDao = setupProductDao(testProducts);
        orderDao = setupOrderDao(testOrders);
        session = setupSession();
        servlet = new OrderOverviewPageServlet(setupConfiguration(productDao, orderDao), errorHandler);
        when(request.getRequestDispatcher(any())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        when(request.getPathInfo()).thenReturn("/a0000000-0000-0000-0000-000000000000");
    }

    private List<Order> setupTestOrders() {
        return List.of(new Order(
                0L,
                UUID.fromString("a0000000-0000-0000-0000-000000000000"),
                List.of(new OrderItem(0L, 1, new Price(new BigDecimal(100), Currency.getInstance("USD")))),
                new DeliveryDetails("address",
                        LocalDate.of(2000, 1, 1),
                        new Price(new BigDecimal(100), Currency.getInstance("USD"))),
                new ContactDetails("firstName", "lastName", "123-12-12"),
                PaymentMethod.CASH));
    }

    private OrderDao setupOrderDao(List<Order> testOrders) {
        OrderDao orderDao = mock(OrderDao.class);
        for (Order order : testOrders) {
            when(orderDao.getBySecureId(order.getSecureId())).thenReturn(Optional.of(order));
        }
        return orderDao;
    }

    private HttpSession setupSession() {
        return mock(HttpSession.class);
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

    private Configuration setupConfiguration(ProductDao productDao, OrderDao orderDao) {
        Configuration config = mock(Configuration.class);
        when(config.getProductDao()).thenReturn(productDao);
        when(config.getOrderDao()).thenReturn(orderDao);
        return config;
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("order"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWrongSecureId() throws ServletException, IOException {
        String orderSecureId = "a0000000-0000-0000-0000-000000000001";
        when(request.getPathInfo()).thenReturn("/" + orderSecureId);
        servlet.doGet(request, response);
        verify(errorHandler).orderNotFound(request, response, orderSecureId);
    }
}