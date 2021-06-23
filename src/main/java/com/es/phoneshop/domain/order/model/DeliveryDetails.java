package com.es.phoneshop.domain.order.model;

import com.es.phoneshop.domain.common.model.Price;

import java.time.LocalDate;

public class DeliveryDetails {
    private String destinationAddress;
    private LocalDate arrivalDate;
    private Price price;

    public DeliveryDetails(String destinationAddress, LocalDate arrivalDate, Price price) {
        this.destinationAddress = destinationAddress;
        this.arrivalDate = arrivalDate;
        this.price = price;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public Price getPrice() {
        return price;
    }
}
