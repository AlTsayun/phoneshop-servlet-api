package com.es.phoneshop.domain.order.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    private ProductDao productDao;
    private OrderDao orderDao;

    public OrderServiceImpl(ProductDao productDao, OrderDao orderDao) {
        this.productDao = productDao;
        this.orderDao = orderDao;
    }

    @Override
    public UUID order(Cart cart, DeliveryDetails deliveryDetails, ContactDetails contactDetails, PaymentMethod paymentMethod) {
        UUID secureId = UUID.randomUUID();
        List<CartItem> cartItems = cart.getItems();
        if (cartItems.isEmpty()) {
            throw new CartEmptyException();
        }
        List<OrderItem> items = cartItems.stream()
                .filter(it -> productDao.getById(it.getProductId()).isPresent())
                .map(it -> {
                    Product item = productDao.getById(it.getProductId()).get();
                    ProductPrice price = item.getActualPrice();
                    return new OrderItem(it.getProductId(), it.getQuantity(), new Price(price.getValue(), price.getCurrency()));
                })
                .collect(Collectors.toList());
        orderDao.save(new Order(null, secureId, items, deliveryDetails, contactDetails, paymentMethod));
        return secureId;
    }
}
