package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.model.DisplayCartItem;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;
import static com.es.phoneshop.web.MessagesHandler.MessageType.SUCCESS;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest extends TestCase {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;
    @Mock
    private MessagesHandler messagesHandler;
    @Mock
    private RequestDispatcher requestDispatcher;

    private CartService cartService;

    private HttpSession session;

    private MockedStatic<ConfigurationImpl> configurationStatic;
    private CartPageServlet servlet;

    private Cart testCart;

    private List<Product> testProducts;

    private final String requestContextPath = "requestContextPath";

    @Captor
    private ArgumentCaptor<List<DisplayCartItem>> productInCartArgumentCaptor;

    @Before
    public void setup() throws ServletException {

        session = setupSession();
        testCart = setupCart();
        cartService = setupCartService(session, testCart);
        testProducts = setupTestProducts();
        Configuration configuration = setupConfiguration(setupProductDao(testProducts), cartService);
        servlet = setupServlet(configuration, messagesHandler, config);

        when(request.getContextPath()).thenReturn(requestContextPath);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(any())).thenReturn(requestDispatcher);
    }

    private List<Product> setupTestProducts() {
        return List.of(new Product(0L, "code", "descrition", 1, null,
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100),
                        Currency.getInstance("USD")))));
    }

    private Cart setupCart() {
        return new Cart(List.of(new CartItem(0L, 10)));
    }

    private HttpSession setupSession() {
        return mock(HttpSession.class);
    }

    private CartPageServlet setupServlet(
            Configuration configuration,
            MessagesHandler messagesHandler,
            ServletConfig config) throws ServletException {
        CartPageServlet servlet = new CartPageServlet(configuration, messagesHandler);
        servlet.init(config);
        return servlet;
    }

    private ProductDao setupProductDao(List<Product> products) {
        ProductDao productDao = mock(ProductDao.class);
        products.forEach(it -> when(productDao.getById(it.getId())).thenReturn(Optional.of(it)));
        return productDao;
    }

    private Configuration setupConfiguration(ProductDao productDao, CartService cartService) {
        Configuration configuration = mock(ConfigurationImpl.class);
        when(configuration.getCartService()).thenReturn(cartService);
        when(configuration.getProductDao()).thenReturn(productDao);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    private CartService setupCartService(HttpSession session, Cart cart) {
        CartService cartService = mock(CartService.class);
        when(cartService.get(session)).thenReturn(cart);
        return cartService;
    }

    @After
    public void cleanUp() {
        configurationStatic.close();
    }

    @Test
    public void testDoPost() throws IOException {
        Long[] productIds = new Long[] {0L, 1L};
        int[] quantities = new int[] {1, 2};

        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        when(request.getParameterValues("productId")).thenReturn(Arrays.stream(productIds)
                .map(Object::toString)
                .toArray(String[]::new));
        when(request.getParameterValues("quantity")).thenReturn(Arrays.stream(quantities)
                .mapToObj(Integer::toString)
                .toArray(String[]::new));

        servlet.doPost(request, response);

        for (int i = 0; i < productIds.length; i++) {
            verify(cartService).updateCartItem(eq(session), eq(productIds[i]), eq(quantities[i]));
        }
        verify(messagesHandler, times(productIds.length)).add(any(), any(), eq(SUCCESS), any());
        verify(response).sendRedirect(eq(requestContextPath + "/cart"));

    }

    @Test
    public void testDoPostIllegalId() throws IOException {
        String[] productIds = new String[]{"0", "wasd"};
        int[] quantities = new int[]{1, 2};

        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(Arrays.stream(quantities)
                .mapToObj(Integer::toString)
                .toArray(String[]::new));

        servlet.doPost(request, response);

        verify(messagesHandler, times(1)).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + "/cart"));
    }

    @Test
    public void testDoPostProductNotFound() throws IOException {
        Long[] productIds = new Long[]{0L, 100L};
        int[] quantities = new int[]{1, 2};

        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        when(request.getParameterValues("productId")).thenReturn(Arrays.stream(productIds)
                .map(Object::toString)
                .toArray(String[]::new));
        when(request.getParameterValues("quantity")).thenReturn(Arrays.stream(quantities)
                .mapToObj(Integer::toString)
                .toArray(String[]::new));

        doThrow(new ProductNotFoundException(((Long) 100L).toString())).when(cartService).updateCartItem(any(), eq(100L),
                anyInt());

        servlet.doPost(request, response);

        verify(messagesHandler, times(1)).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + "/cart"));
    }

    @Test
    public void testDoPostProductStockNotEnough() throws IOException {
        Long[] productIds = new Long[]{0L, 1L};
        int[] quantities = new int[]{1, 200};

        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        when(request.getParameterValues("productId")).thenReturn(Arrays.stream(productIds)
                .map(Object::toString)
                .toArray(String[]::new));
        when(request.getParameterValues("quantity")).thenReturn(Arrays.stream(quantities)
                .mapToObj(Integer::toString)
                .toArray(String[]::new));

        doThrow(new ProductStockNotEnoughException()).when(cartService).updateCartItem(
                any(),
                eq(1L),
                gt(10));

        servlet.doPost(request, response);

        verify(messagesHandler, times(1)).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + "/cart"));
    }

    @Test
    public void testDoPostQuantityTooBig() throws IOException {
        Long[] productIds = new Long[]{0L, 100L};
        String[] quantities = new String[]{"1", Long.toString(Integer.MAX_VALUE + 1L)};

        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        when(request.getParameterValues("productId")).thenReturn(Arrays.stream(productIds)
                .map(Object::toString)
                .toArray(String[]::new));
        when(request.getParameterValues("quantity")).thenReturn(quantities);

        servlet.doPost(request, response);

        verify(messagesHandler, times(1)).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + "/cart"));
    }

    @Test
    public void testDoPostQuantityTooLow() throws IOException {
        Long[] productIds = new Long[]{0L, 100L};
        String[] quantities = new String[]{"1", "-1"};

        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        when(request.getParameterValues("productId")).thenReturn(Arrays.stream(productIds)
                .map(Object::toString)
                .toArray(String[]::new));
        when(request.getParameterValues("quantity")).thenReturn(quantities);


        doThrow(new ProductQuantityTooLowException(-1)).when(cartService).updateCartItem(
                any(),
                any(),
                lt(1));

        servlet.doPost(request, response);

        verify(messagesHandler, times(1)).add(any(), any(), eq(ERROR), any());
        verify(response).sendRedirect(eq(requestContextPath + "/cart"));
    }

    @Test
    public void testDoGet() throws IOException, ServletException {

        List<DisplayCartItem> expectedProducts = testCart.getItems().stream()
                .map(it -> new DisplayCartItem(testProducts.get(0), it.getQuantity()))
                .collect(Collectors.toList());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("productsInCart"), productInCartArgumentCaptor.capture());
        List<DisplayCartItem> capturedProductsInCart = productInCartArgumentCaptor.getValue();
        assertEquals(expectedProducts.size(), capturedProductsInCart.size());
        capturedProductsInCart.forEach(it -> assertTrue(expectedProducts.contains(it)));
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testDoGetProductNotFound() throws IOException, ServletException {

        List<CartItem> illegalCartItems = new ArrayList<>();
        illegalCartItems.add(new CartItem(100L, 1));
        Cart illegalCart = new Cart(illegalCartItems);
        when(cartService.get(session)).thenReturn(illegalCart);

        servlet.doGet(request, response);

        verify(messagesHandler, times(1)).add(any(), any(), eq(ERROR), any());

        verify(request).setAttribute(eq("productsInCart"), productInCartArgumentCaptor.capture());
        List<DisplayCartItem> capturedProductsInCart = productInCartArgumentCaptor.getValue();
        assertEquals(illegalCartItems.size() - 1, capturedProductsInCart.size());
        verify(requestDispatcher).forward(request, response);

    }
}