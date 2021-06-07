package com.es.phoneshop.infra.config;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationImplTest{

    private Configuration configuration;

    @Before
    public void setup(){
        configuration = ConfigurationImpl.getInstance();
    }

    @Test
    public void testGetInstance(){
        assertEquals(configuration, ConfigurationImpl.getInstance());
    }

    @Test
    public void testGetProductDao(){
        assertEquals(configuration.getProductDao(), configuration.getProductDao());
    }

    @Test
    public void testGetLongIdGenerator(){
        assertEquals(configuration.getLongIdGenerator(), configuration.getLongIdGenerator());
    }

    @Test
    public void testGetViewedProductsHistoryService() {
        assertEquals(configuration.getViewedProductsHistoryService(), configuration.getViewedProductsHistoryService());
    }
}