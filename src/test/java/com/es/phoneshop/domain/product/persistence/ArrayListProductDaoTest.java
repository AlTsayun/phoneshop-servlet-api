package com.es.phoneshop.domain.product.persistence;

import com.es.phoneshop.domain.common.model.SortingOrder;
import com.es.phoneshop.domain.product.model.*;
import com.es.phoneshop.utils.LongIdGeneratorImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;


public class ArrayListProductDaoTest {

    @Before
    public void setup() {
        productDao = new ArrayListProductDao(new LongIdGeneratorImpl(0L));
        ArrayListProductDao.getSampleProducts().forEach(it -> productDao.save(it));
        initialProducts = productDao.getAll();
    }

    @Test
    public void testGetProductById() {
        Long id = 1L;
        assertTrue(productDao.getById(id).isPresent());
    }

    @Test
    public void testGetProductByWrongIdNoResult() {
        Long id = 500L;
        assertFalse(productDao.getById(id).isPresent());
    }

    @Test
    public void testUpdateProduct() {
        Long id = 1L;
        Product toUpdate = new Product(id, "code", "description", 10, "",
                List.of(new ProductPrice(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0),
                        new BigDecimal(500),
                        Currency.getInstance("USD"))));

        productDao.save(toUpdate);
        Product updated = productDao.getById(id).get();
        assertEquals(toUpdate, updated);
    }

    @Test
    public void testGetAllByRequest() {
        List<Product> products = productDao.getAllByRequest(new ProductsRequest("Samsung III", null, null, 1));
        assertEquals(2, products.size());
        assertEquals(products.get(0), initialProducts.get(2));
        assertEquals(products.get(1), initialProducts.get(0));

        products = productDao.getAllByRequest(new ProductsRequest(null, ProductSortingCriteria.PRICE, SortingOrder.ASC, 1));
        assertEquals(12, products.size());
        assertEquals(products.get(0), initialProducts.get(8));
        assertEquals(products.get(1), initialProducts.get(9));
        assertEquals(products.get(2), initialProducts.get(10));
        assertEquals(products.get(3), initialProducts.get(11));
        assertEquals(products.get(4), initialProducts.get(7));
        assertEquals(products.get(5), initialProducts.get(12));
        assertEquals(products.get(6), initialProducts.get(0));
        assertEquals(products.get(7), initialProducts.get(3));
        assertEquals(products.get(8), initialProducts.get(2));
        assertEquals(products.get(9), initialProducts.get(5));
        assertEquals(products.get(10), initialProducts.get(6));
        assertEquals(products.get(11), initialProducts.get(4));


        products = productDao.getAllByRequest(new ProductsRequest(
                "Samsung III",
                ProductSortingCriteria.DESCRIPTION,
                SortingOrder.ASC,
                1));
        assertEquals(2, products.size());
        assertEquals(products.get(0), initialProducts.get(0));
        assertEquals(products.get(1), initialProducts.get(2));

    }

    @Test
    public void testGetAllByAdvancedSearchRequest() {
        List<Product> products = productDao.getAllByAdvancedSearchRequest(new AdvancedSearchRequest("Samsung III", QueryType.ANY_WORD, null, null));
        assertEquals(3, products.size());
        assertTrue(products.contains(initialProducts.get(0)));
        assertTrue(products.contains(initialProducts.get(1)));
        assertTrue(products.contains(initialProducts.get(2)));

        products = productDao.getAllByAdvancedSearchRequest(new AdvancedSearchRequest("Samsung Galaxy S", QueryType.ALL_WORDS, null, null));
        assertEquals(1, products.size());
        assertTrue(products.contains(initialProducts.get(0)));

        products = productDao.getAllByAdvancedSearchRequest(new AdvancedSearchRequest("Samsung III", QueryType.ANY_WORD, new BigDecimal(150), null));
        assertEquals(2, products.size());
        assertTrue(products.contains(initialProducts.get(0)));
        assertTrue(products.contains(initialProducts.get(2)));

        products = productDao.getAllByAdvancedSearchRequest(new AdvancedSearchRequest("Samsung III", QueryType.ANY_WORD, new BigDecimal(150), new BigDecimal(250)));
        assertEquals(1, products.size());
        assertTrue(products.contains(initialProducts.get(0)));

    }

    @Test
    public void testDeleteProduct() {
        Long id = 1L;
        productDao.delete(id);
        assertFalse(productDao.getById(id).isPresent());
    }

    @Test(expected = ProductPersistenceException.class)
    public void testDeleteProductWrongId() {
        Long id = 500L;
        productDao.delete(id);
    }

    @Test(expected = ProductPersistenceException.class)
    public void testUpdateProductWrongId() {
        Long id = 500L;
        Product toUpdate = new Product(id, "code", "description", 10, "",
                List.of(new ProductPrice(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0),
                        new BigDecimal(200),
                        Currency.getInstance("USD"))));
        productDao.save(toUpdate);
    }

    @Test
    public void testCreateProduct() {
        Product productToCreate = new Product(null, "code", "description", 10, "",
                List.of(new ProductPrice(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0),
                        new BigDecimal(200),
                        Currency.getInstance("USD"))));
        Long id = productDao.save(productToCreate);
        productToCreate.setId(id);
        assertTrue(productDao.getById(id).isPresent());
        assertEquals(productToCreate, productDao.getById(id).get());
    }

    @Test
    public void testCreateProductMultiThread() throws InterruptedException {
        int initialSize = productDao.getAll().size();
        int threadsCount = 10;
        Thread[] threads = new Thread[threadsCount];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() ->
                    productDao.save(new Product(null, "code", "description", 10, null,
                            List.of(new ProductPrice(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0),
                                    new BigDecimal(200),
                                    Currency.getInstance("USD"))))));
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        int resultSize = productDao.getAll().size();
        assertEquals(initialSize + threadsCount, resultSize);
    }


    @Test
    public void testDeleteProductMultiThread() throws InterruptedException {
        int initialSize = productDao.getAll().size();
        int threadsCount = 10;
        Thread[] threads = new Thread[threadsCount];

        for (int i = 0; i < threads.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> productDao.delete((long) finalI));
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        int resultSize = productDao.getAll().size();
        assertEquals(initialSize - threadsCount, resultSize);
    }


    private ProductDao productDao;

    private List<Product> initialProducts;
}