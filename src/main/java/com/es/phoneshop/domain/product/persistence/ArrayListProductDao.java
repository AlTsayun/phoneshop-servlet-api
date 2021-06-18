package com.es.phoneshop.domain.product.persistence;

import com.es.phoneshop.domain.common.model.SortingOrder;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.model.ProductsRequest;
import com.es.phoneshop.domain.product.model.ProductSortingCriteria;
import com.es.phoneshop.utils.LongIdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayListProductDao implements ProductDao {

    private final List<Product> products;

    private final LongIdGenerator idGenerator;

    private final ReadWriteLock lock;

    public ArrayListProductDao(LongIdGenerator idGenerator) {
        this.products = new ArrayList<>();
        this.idGenerator = idGenerator;
        this.lock = new ReentrantReadWriteLock();
    }

    public static List<Product> getSampleProducts() {
        List<Product> result = new ArrayList<>();
        Currency usd = Currency.getInstance("USD");
        result.add(new Product(null, "sgs", "Samsung Galaxy S", 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100), usd),
                        new ProductPrice(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0), new BigDecimal(200), usd))));
        result.add(new Product(null, "sgs2", "Samsung Galaxy S II", 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(100), usd),
                        new ProductPrice(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0), new BigDecimal(110), usd))));
        result.add(new Product(null, "sgs3", "Samsung Galaxy S III", 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(300), usd),
                        new ProductPrice(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0), new BigDecimal(290), usd))));
        result.add(new Product(null, "iphone", "Apple iPhone", 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(200), usd),
                        new ProductPrice(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0), new BigDecimal(200), usd))));
        result.add(new Product(null, "iphone6", "Apple iPhone 6", 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(1000), usd))));
        result.add(new Product(null, "htces4g", "HTC EVO Shift 4G", 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(320), usd))));
        result.add(new Product(null, "sec901", "Sony Ericsson C901", 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(420), usd))));
        result.add(new Product(null, "xperiaxz", "Sony Xperia XZ", 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(120), usd))));
        result.add(new Product(null, "nokia3310", "Nokia 3310", 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(70), usd))));
        result.add(new Product(null, "palmp", "Palm Pixi", 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(70), usd))));
        result.add(new Product(null, "simc56", "Siemens C56", 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(70), usd))));
        result.add(new Product(null, "simc61", "Siemens C61", 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(80), usd))));
        result.add(new Product(null, "simsxg75", "Siemens SXG75", 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg",
                List.of(new ProductPrice(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0), new BigDecimal(150), usd))));

        return result;
    }

    @Override
    public Optional<Product> getById(Long id) {
        lock.readLock().lock();
        try {
            return products.stream()
                    .filter(it -> id.equals(it.getId()))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Product> getAllByRequest(ProductsRequest request) {

        lock.readLock().lock();
        try {
            return this.products.stream()
                    .filter(it -> productMatches(it, request))
                    .sorted(getProductsComparator(request))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    private int getWordMatchesCount(String word, List<String> wordsToMatch) {
        return (int) wordsToMatch.stream().filter(it -> it.equalsIgnoreCase(word)).count();
    }

    private boolean productMatches(Product product, ProductsRequest request) {
        List<String> queryWords = getWords(request.getQuery());
        return product.getStock() >= request.getMinStockInclusive() &&
                product.getActualPrice().getValue() != null &&
                getWords(product.getDescription()).stream()
                        .anyMatch(word -> queryWords.isEmpty() || queryWords.stream()
                                .anyMatch(queryWord -> queryWord.equalsIgnoreCase(word)));
    }

    private List<String> getWords(String str) {
        return str != null && !str.isEmpty() ? Arrays.asList(str.split(" ")) : Collections.emptyList();
    }

    private Comparator<Product> getProductsComparator(ProductsRequest request) {

        List<String> queryWords = getWords(request.getQuery());

        Comparator<Product> comparator;

        if (request.getSortingCriteria() == ProductSortingCriteria.PRICE) {
            comparator = Comparator.comparing(it -> it.getActualPrice().getValue());
        } else if (request.getSortingCriteria() == ProductSortingCriteria.DESCRIPTION) {
            comparator = Comparator.comparing(Product::getDescription);
        } else {
            comparator = Comparator.comparing(it -> getWords(it.getDescription()).stream()
                    .mapToInt(word -> -getWordMatchesCount(word, queryWords))
                    .sum());
        }

        if (request.getSortingOrder() == SortingOrder.DESC) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    @Override
    public List<Product> getAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(products);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Long save(Product product) {
        if (product.getId() == null) {
            return create(product);
        } else {
            update(product);
            return product.getId();
        }
    }

    private void update(Product product) {
        lock.writeLock().lock();
        try {
            if (product.getId() != null) {
                int insertingPosition = IntStream.range(0, products.size())
                        .filter(i -> product.getId().equals(products.get(i).getId()))
                        .findFirst().orElseThrow(ProductPresistenceException::new);

                products.set(insertingPosition, product);
            } else {
                throw new ProductPresistenceException();
            }

        } finally {
            lock.writeLock().unlock();
        }
    }

    private Long create(Product product) {
        if (product.getId() == null) {
            Long productId = idGenerator.getId();
            product.setId(productId);
            lock.writeLock().lock();
            try {
                products.add(product);
            } finally {
                lock.writeLock().unlock();
            }
            return productId;
        } else {
            throw new ProductPresistenceException();
        }
    }

    @Override
    public void delete(Long id) {
        lock.writeLock().lock();
        try {
            int foundIndex = IntStream.range(0, products.size())
                    .filter(i -> id.equals(products.get(i).getId()))
                    .findFirst().orElseThrow(ProductPresistenceException::new);
            products.remove(foundIndex);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
