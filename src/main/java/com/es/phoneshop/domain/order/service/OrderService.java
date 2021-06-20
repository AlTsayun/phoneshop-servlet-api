package com.es.phoneshop.domain.order.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.common.model.PaymentMethod;
import com.es.phoneshop.domain.order.model.ContactDetails;
import com.es.phoneshop.domain.order.model.DeliveryDetails;

import java.util.UUID;

public interface OrderService {
    UUID order(Cart cart, DeliveryDetails deliveryDetails, ContactDetails contactDetails, PaymentMethod paymentMethod);
}
