package com.es.phoneshop.web.contextListeners;

import com.es.phoneshop.domain.product.persistence.ArrayListProductDao;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.ConfigurationImpl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SampleDataServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {

        if (Boolean.parseBoolean(event.getServletContext().getInitParameter("insertSampleData"))){
            Configuration configuration = ConfigurationImpl.getInstance();
            ProductDao productDao = configuration.getProductDao();
            ArrayListProductDao.getSampleProducts().forEach(productDao::save);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
