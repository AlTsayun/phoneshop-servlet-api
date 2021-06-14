package com.es.phoneshop.web.contextListeners;

import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import com.es.phoneshop.web.*;
import com.es.phoneshop.web.filters.CartFilter;
import com.es.phoneshop.web.filters.RecentlyViewedProductsFilter;

import javax.servlet.*;
import java.util.EnumSet;

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

            FilterRegistration.Dynamic cartFilter = servletContext.addFilter("cart", new CartFilter(configuration));
            cartFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

            FilterRegistration.Dynamic recentlyViewedProductsFilter = servletContext.addFilter(
                    "recentlyViewedProducts",
                    new RecentlyViewedProductsFilter(configuration));
            recentlyViewedProductsFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
