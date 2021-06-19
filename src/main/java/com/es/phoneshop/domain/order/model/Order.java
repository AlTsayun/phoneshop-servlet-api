package com.es.phoneshop.domain.order.model;

import com.es.phoneshop.domain.common.model.PaymentMethod;

import java.util.List;

public class Order {

    private Long id;

    private List<OrderItem> items;

    private DeliveryDetails deliveryDetails;

    private ContactDetails contactDetails;

    private PaymentMethod paymentMethod;

    public Order(Long id, List<OrderItem> items, DeliveryDetails deliveryDetails, ContactDetails contactDetails, PaymentMethod paymentMethod) {
        this.id = id;
        this.items = items;
        this.deliveryDetails = deliveryDetails;
        this.contactDetails = contactDetails;
        this.paymentMethod = paymentMethod;
    }

    public DeliveryDetails getDeliveryDetails() {
        return deliveryDetails;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
