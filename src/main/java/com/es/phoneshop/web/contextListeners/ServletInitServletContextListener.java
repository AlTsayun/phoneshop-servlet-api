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
            MessagesHandler messagesHandler = new MessagesHandler();

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

            ServletRegistration.Dynamic cartItemAddServlet = servletContext.addServlet(
                    "cartItemAdd",
                    new CartItemAddServlet(configuration, messagesHandler));
            cartItemAddServlet.addMapping("/cart/add");

            ServletRegistration.Dynamic cart = servletContext.addServlet(
                    "cart",
                    new CartPageServlet(configuration, messagesHandler));
            cart.addMapping("/cart");

            ServletRegistration.Dynamic cartItemDeleteServlet = servletContext.addServlet(
                    "cartItemDelete",
                    new CartItemDeleteServlet(configuration, messagesHandler));
            cartItemDeleteServlet.addMapping("/cart/delete/*");

            ServletRegistration.Dynamic miniCartServlet = servletContext.addServlet(
                    "miniCart",
                    new MiniCartServlet(configuration, messagesHandler));
            miniCartServlet.addMapping("/cart/minicart");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
