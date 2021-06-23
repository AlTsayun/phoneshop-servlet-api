package com.es.phoneshop.domain.order.persistence;

import com.es.phoneshop.domain.common.model.PaymentMethod;
import com.es.phoneshop.domain.common.model.Price;
import com.es.phoneshop.domain.order.model.ContactDetails;
import com.es.phoneshop.domain.order.model.DeliveryDetails;
import com.es.phoneshop.domain.order.model.Order;
import com.es.phoneshop.domain.order.model.OrderItem;
import com.es.phoneshop.utils.LongIdGenerator;
import com.es.phoneshop.utils.LongIdGeneratorImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class ArrayListOrderDaoTest {

    private ArrayListOrderDao orderDao;

    @Before
    public void setup() {
        LongIdGenerator longIdGenerator = setupLongIdGenerator();
        orderDao = new ArrayListOrderDao(longIdGenerator);
        getTestOrders().forEach(it -> orderDao.save(it));
    }

    private List<Order> getTestOrders() {
        List<Order> orders = new ArrayList<>();
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(0L, 1, new Price(new BigDecimal(100), Currency.getInstance("USD"))));
        orders.add(new Order(
                null,
                UUID.fromString("a0000000-0000-0000-0000-000000000000"),
                orderItems,
                new DeliveryDetails("address",
                        LocalDate.of(2000, 1, 1),
                        new Price(new BigDecimal(100), Currency.getInstance("USD"))),
                new ContactDetails("firstName", "lastName", "123-12-12"),
                PaymentMethod.CASH));
        return orders;
    }

    private LongIdGenerator setupLongIdGenerator() {
        return new LongIdGeneratorImpl(0L);
    }

    @Test
    public void testGetById() {
        Long id = 0L;
        assertTrue(orderDao.getById(id).isPresent());
    }

    @Test
    public void testGetByIdWrongId() {
        Long id = 1L;
        assertFalse(orderDao.getById(id).isPresent());
    }

    @Test
    public void testGetBySecureId() {
        UUID id = UUID.fromString("a0000000-0000-0000-0000-000000000000");
        assertTrue(orderDao.getBySecureId(id).isPresent());
    }

    @Test
    public void testGetBySecureIdWrongId() {
        UUID id = UUID.fromString("a0000000-0000-0000-0000-000000000001");
        assertFalse(orderDao.getBySecureId(id).isPresent());
    }

    @Test
    public void testUpdate() {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(0L, 1, new Price(new BigDecimal(100), Currency.getInstance("USD"))));
        Order order = new Order(
                0L,
                UUID.fromString("a0000000-0000-0000-0000-000000000001"),
                orderItems,
                new DeliveryDetails("address",
                        LocalDate.of(2000, 1, 1),
                        new Price(new BigDecimal(100), Currency.getInstance("USD"))),
                new ContactDetails("firstName", "lastName", "123-12-12"),
                PaymentMethod.CASH);

        assertEquals((Long) 0L, orderDao.save(order));
        assertEquals(order, orderDao.getById(order.getId()).get());
    }

    @Test(expected = OrderPersistenceException.class)
    public void testUpdateWrongId() {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(0L, 1, new Price(new BigDecimal(100), Currency.getInstance("USD"))));
        Order order = new Order(
                1L,
                UUID.fromString("a0000000-0000-0000-0000-000000000001"),
                orderItems,
                new DeliveryDetails("address",
                        LocalDate.of(2000, 1, 1),
                        new Price(new BigDecimal(100), Currency.getInstance("USD"))),
                new ContactDetails("firstName", "lastName", "123-12-12"),
                PaymentMethod.CASH);

        orderDao.save(order);
    }


    @Test
    public void testCreate() {
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(0L, 1, new Price(new BigDecimal(100), Currency.getInstance("USD"))));
        Order order = new Order(
                null,
                UUID.fromString("a0000000-0000-0000-0000-000000000001"),
                orderItems,
                new DeliveryDetails("address",
                        LocalDate.of(2000, 1, 1),
                        new Price(new BigDecimal(100), Currency.getInstance("USD"))),
                new ContactDetails("firstName", "lastName", "123-12-12"),
                PaymentMethod.CASH);

        assertEquals((Long) 1L, orderDao.save(order));
        assertEquals(order, orderDao.getById(order.getId()).get());
    }


}