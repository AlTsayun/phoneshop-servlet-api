package com.es.phoneshop.domain.order.persistence;

import com.es.phoneshop.domain.order.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderDao {
    Optional<Order> getById(Long id);

    Optional<Order> getBySecureId(UUID id);

    Long save(Order order);
}
