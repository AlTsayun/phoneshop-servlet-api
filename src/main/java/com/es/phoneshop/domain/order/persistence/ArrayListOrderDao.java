package com.es.phoneshop.domain.order.persistence;

import com.es.phoneshop.domain.order.model.Order;
import com.es.phoneshop.utils.LongIdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class ArrayListOrderDao implements OrderDao {

    private final List<Order> items;

    private final LongIdGenerator idGenerator;

    private final ReadWriteLock lock;

    public ArrayListOrderDao(LongIdGenerator idGenerator) {
        this.items = new ArrayList<>();
        this.idGenerator = idGenerator;
        this.lock = new ReentrantReadWriteLock();
    }

    @Override
    public Optional<Order> getById(Long id) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(it -> id.equals(it.getId()))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Optional<Order> getBySecureId(UUID id) {
        lock.readLock().lock();
        try {
            return items.stream()
                    .filter(it -> id.equals(it.getSecureId()))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Order> getAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(items);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Long save(Order order) {
        if (order.getId() == null) {
            return create(order);
        } else {
            update(order);
            return order.getId();
        }
    }

    private void update(Order product) {
        lock.writeLock().lock();
        try {
            if (product.getId() != null) {
                int insertingPosition = IntStream.range(0, items.size())
                        .filter(i -> product.getId().equals(items.get(i).getId()))
                        .findFirst().orElseThrow(OrderPersistenceException::new);

                items.set(insertingPosition, product);
            } else {
                throw new OrderPersistenceException();
            }

        } finally {
            lock.writeLock().unlock();
        }
    }

    private Long create(Order order) {
        if (order.getId() == null) {
            Long productId = idGenerator.getId();
            order.setId(productId);
            lock.writeLock().lock();
            try {
                items.add(order);
            } finally {
                lock.writeLock().unlock();
            }
            return productId;
        } else {
            throw new OrderPersistenceException();
        }
    }
}
