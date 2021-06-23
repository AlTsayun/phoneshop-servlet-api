package com.es.phoneshop.domain.order.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.common.model.PaymentMethod;
import com.es.phoneshop.domain.common.model.Price;
import com.es.phoneshop.domain.order.model.ContactDetails;
import com.es.phoneshop.domain.order.model.DeliveryDetails;
import com.es.phoneshop.domain.order.persistence.OrderDao;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class OrderServiceImplTest {

    private OrderServiceImpl orderService;

    private List<Product> testProducts;

    private Cart testCart;

    private DeliveryDetails deliveryDetails;

    private ContactDetails contactDetails;
    private OrderDao orderDao;

    @Before
    public void setup() {
        testCart = setupCart();
        testProducts = setupTestProducts();
        orderDao = setupOrderDao();
        orderService = new OrderServiceImpl(setupProductDao(testProducts), orderDao);
        deliveryDetails = setupDeliveryDetails();
        contactDetails = setupContactDetails();
    }

    private ContactDetails setupContactDetails() {
        return new ContactDetails("firstName", "lastName", "123-12-12");
    }

    private DeliveryDetails setupDeliveryDetails() {
        return new DeliveryDetails(
                "address",
                LocalDate.of(2000, 1, 1),
                new Price(new BigDecimal(10), Currency.getInstance("USD")));
    }

    public OrderDao setupOrderDao() {
        return mock(OrderDao.class);
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

    private Cart setupCart() {
        return new Cart(List.of(new CartItem(0L, 10)));
    }


    @Test
    public void testOrder() {
        orderService.order(testCart, deliveryDetails, contactDetails, PaymentMethod.CASH);
        verify(orderDao).save(any());
    }

    @Test(expected = CartEmptyException.class)
    public void testOrderEmptyCart() {
        orderService.order(new Cart(new ArrayList<>()), deliveryDetails, contactDetails, PaymentMethod.CASH);
        verify(orderDao).save(any());
    }
}