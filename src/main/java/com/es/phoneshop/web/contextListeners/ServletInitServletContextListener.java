package com.es.phoneshop.web.contextListeners;

import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import com.es.phoneshop.web.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

public class ServletInitServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        if (servletContext.getInitParameter("configuration").equals("default")) {

            Configuration configuration = ConfigurationImpl.getInstance();
            ErrorHandler errorHandler = new ErrorHandler();

            ServletRegistration.Dynamic productList = servletContext.addServlet(
                    "productList",
                    new ProductListPageServlet(configuration, errorHandler));
            productList.addMapping("/products");

            ServletRegistration.Dynamic productDetails = servletContext.addServlet(
                    "productDetails",
                    new ProductDetailsPageServlet(configuration, errorHandler));
            productDetails.addMapping("/products/*");


            ServletRegistration.Dynamic productPricesHistory = servletContext.addServlet(
                    "productPricesHistory",
                    new ProductPricesHistoryServlet(configuration, errorHandler));
            productPricesHistory.addMapping("/product-prices-history/*");


            ServletRegistration.Dynamic cart = servletContext.addServlet(
                    "cart",
                    new CartServlet(configuration, errorHandler));
            cart.addMapping("/cart");

        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
