package com.es.phoneshop.domain.product.persistence;

import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductsRequest;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getById(Long id);

    List<Product> getAllByRequest(ProductsRequest productsRequest);

    List<Product> getAll();

    Long save(Product product);

    void delete(Long id);
}
