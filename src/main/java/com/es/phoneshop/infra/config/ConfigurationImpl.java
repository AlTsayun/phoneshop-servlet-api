package com.es.phoneshop.infra.config;

import com.es.phoneshop.domain.product.persistence.ArrayListProductDao;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.utils.LongIdGenerator;
import com.es.phoneshop.utils.LongIdGeneratorImpl;

import java.time.Clock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigurationImpl implements Configuration {

    private static ConfigurationImpl instance;

    private ProductDao productDao;

    private LongIdGenerator longIdGenerator;

    private static final Lock instanceLock = new ReentrantLock();
    private final Lock productDaoLock = new ReentrantLock();
    private final Lock longIdGeneratorLock = new ReentrantLock();

    private ConfigurationImpl() { }

    public static ConfigurationImpl getInstance() {
        instanceLock.lock();
        try {
            if (instance == null){
                instance = new ConfigurationImpl();
            }
            return instance;
        } finally {
            instanceLock.unlock();
        }
    }

    @Override
    public ProductDao getProductDao() {
        productDaoLock.lock();
        try {
            if (productDao == null){
                productDao = new ArrayListProductDao(getLongIdGenerator(), Clock.systemUTC());
            }
            return productDao;
        } finally {
            productDaoLock.unlock();
        }
    }

    @Override
    public LongIdGenerator getLongIdGenerator() {
        longIdGeneratorLock.lock();
        try {
            if (longIdGenerator == null){
                longIdGenerator = new LongIdGeneratorImpl(0L);
            }
            return longIdGenerator;
        } finally {
            longIdGeneratorLock.unlock();
        }
    }
}
