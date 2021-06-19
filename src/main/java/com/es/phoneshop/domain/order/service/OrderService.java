package com.es.phoneshop.domain.order.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.common.model.PaymentMethod;
import com.es.phoneshop.domain.order.model.ContactDetails;
import com.es.phoneshop.domain.order.model.DeliveryDetails;

public interface OrderService {
    Long order(Cart cart, DeliveryDetails deliveryDetails, ContactDetails contactDetails, PaymentMethod paymentMethod);
}
