package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.model.ProductInCart;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.ProductNotFoundInCartException;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import com.es.phoneshop.web.MessagesHandler.MessageType;
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

import static com.es.phoneshop.web.MessagesHandler.MessageType.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MiniCartServletTest extends TestCase {

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
    private MiniCartServlet servlet;

    private Cart testCart;

    private List<Product> testProducts;

    @Before
    public void setup() throws ServletException {

        session = setupSession();
        testCart = setupCart();
        cartService = setupCartService(session, testCart);
        testProducts = setupTestProducts();
        Configuration configuration = setupConfiguration(setupProductDao(testProducts), cartService);
        servlet = setupServlet(configuration, messagesHandler, config);

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

    private MiniCartServlet setupServlet(
            Configuration configuration,
            MessagesHandler messagesHandler,
            ServletConfig config) throws ServletException {
        MiniCartServlet servlet = new MiniCartServlet(configuration, messagesHandler);
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
        when(cartService.getCart(session)).thenReturn(cart);
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
        verify(request).setAttribute(eq("totalCartPriceValue"), eq(new BigDecimal(1000)));
        verify(request).setAttribute(eq("totalCartPriceCurrency"), eq(Currency.getInstance("USD")));

        verify(requestDispatcher).include(request, response);
    }
    @Test
    public void testDoGetCartWithWrongProductId() throws IOException, ServletException {
        ArrayList<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(0L, 1));
        cartItems.add(new CartItem(100L, 1));
        Cart cartWithWrongId = new Cart(cartItems);
        when(cartService.getCart(session)).thenReturn(cartWithWrongId);

        servlet.doGet(request, response);

        verify(messagesHandler).add(any(), any(), eq(ERROR), any());

        verify(request).setAttribute(eq("productsInCartCount"), eq(1));
        verify(request).setAttribute(eq("totalCartPriceValue"), eq(new BigDecimal(100)));
        verify(request).setAttribute(eq("totalCartPriceCurrency"), eq(Currency.getInstance("USD")));
        verify(requestDispatcher).include(request, response);
    }
}