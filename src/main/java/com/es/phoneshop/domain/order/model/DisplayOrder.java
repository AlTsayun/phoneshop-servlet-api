package com.es.phoneshop.domain.order.model;

import com.es.phoneshop.domain.common.model.PaymentMethod;
import com.es.phoneshop.domain.common.model.Price;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public class DisplayOrder {
    private Long id;

    private List<DisplayOrderItem> items;

    private DeliveryDetails deliveryDetails;

    private ContactDetails contactDetails;

    private PaymentMethod paymentMethod;

    public DisplayOrder(Long id, List<DisplayOrderItem> items, DeliveryDetails deliveryDetails, ContactDetails contactDetails, PaymentMethod paymentMethod) {
        this.id = id;
        this.items = items;
        this.deliveryDetails = deliveryDetails;
        this.contactDetails = contactDetails;
        this.paymentMethod = paymentMethod;
    }

    public DeliveryDetails getDeliveryDetails() {
        return deliveryDetails;
    }

    public List<DisplayOrderItem> getItems() {
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

    public Price getSubtotal() {
        return new Price(items.stream()
                .map(it -> it.getPrice().getValue())
                .reduce(new BigDecimal(0), BigDecimal::add),
                Currency.getInstance("USD"));
    }
}
