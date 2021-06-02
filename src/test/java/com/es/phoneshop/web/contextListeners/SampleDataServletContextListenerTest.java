package com.es.phoneshop.web.contextListeners;


import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ArrayListProductDao;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public class SampleDataServletContextListenerTest {

    private ServletContextListener listener = new SampleDataServletContextListener();

    @Test
    public void testContextInitialized() {
        ServletContext context = mock(ServletContext.class);
        when(context.getInitParameter("insertSampleData")).thenReturn("true");

        ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        listener.contextInitialized(event);

        List<Product> sampleProducts = ArrayListProductDao.getSampleProducts();
        List<Product> insertedProducts = ConfigurationImpl.getInstance().getProductDao().getAll();
        assertEquals(sampleProducts.size(), insertedProducts.size());
    }
}