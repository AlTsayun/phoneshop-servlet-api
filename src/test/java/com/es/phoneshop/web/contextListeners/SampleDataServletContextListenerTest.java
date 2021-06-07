package com.es.phoneshop.web.contextListeners;


import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ArrayListProductDao;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SampleDataServletContextListenerTest {

    private ServletContextListener listener = new SampleDataServletContextListener();

    private MockedStatic<ConfigurationImpl> configurationStatic;

    private List<Product> sampleProducts;

    private ProductDao productDao;

    @Captor
    private ArgumentCaptor<Product> paramCaptor;

    @Before
    public void setup() {
        sampleProducts = setupSampleProducts();
        productDao = setupEmptyProductDao();
        setupConfiguration(productDao);
    }

    private ProductDao setupEmptyProductDao() {
        return mock(ProductDao.class);
    }

    private List<Product> setupSampleProducts(){
        return ArrayListProductDao.getSampleProducts();
    }

    private Configuration setupConfiguration(ProductDao productDao) {
        Configuration configuration = mock(ConfigurationImpl.class);
        when(configuration.getProductDao()).thenReturn(productDao);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    @After
    public void cleanUp(){
        configurationStatic.close();
    }

    @Test
    public void testContextInitialized() {
        ServletContext context = mock(ServletContext.class);
        when(context.getInitParameter("insertSampleData")).thenReturn("true");

        ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        listener.contextInitialized(event);
        verify(productDao, times(sampleProducts.size())).save(paramCaptor.capture());

        List<Product> allValues = paramCaptor.getAllValues();

        //todo: override equals for Product
        sampleProducts.forEach(it -> assertTrue(allValues.contains(it)));
    }
}