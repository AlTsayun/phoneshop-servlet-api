package com.es.phoneshop.web.contextListeners;

import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import com.es.phoneshop.web.*;
import com.es.phoneshop.web.filters.DosProtectionFilter;

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

            registerServlet(
                    servletContext,
                    "productList",
                    "/products",
                    new ProductListPageServlet(configuration, errorHandler));

            registerServlet(
                    servletContext,
                    "productDetails",
                    "/products/*",
                    new ProductDetailsPageServlet(configuration, errorHandler));

            registerServlet(
                    servletContext,
                    "productPricesHistory",
                    "/product-prices-history/*",
                    new ProductPricesHistoryServlet(configuration, errorHandler));

            registerServlet(
                    servletContext,
                    "cartItemAdd",
                    "/cart/add",
                    new CartItemAddServlet(configuration, messagesHandler));

            registerServlet(
                    servletContext,
                    "cart",
                    "/cart",
                    new CartPageServlet(configuration, messagesHandler));

            registerServlet(
                    servletContext,
                    "cartItemDelete",
                    "/cart/delete/*",
                    new CartItemDeleteServlet(configuration, messagesHandler));

            registerServlet(
                    servletContext,
                    "miniCart",
                    "/cart/minicart",
                    new MiniCartServlet(configuration, messagesHandler));

            registerServlet(
                    servletContext,
                    "checkout",
                    "/checkout",
                    new CheckoutPageServlet(configuration, messagesHandler));

            registerServlet(
                    servletContext,
                    "orderOverview",
                    "/order/overview/*",
                    new OrderOverviewPageServlet(configuration, errorHandler));

            FilterRegistration.Dynamic dosProtection = servletContext.addFilter("dosProtection", new DosProtectionFilter(configuration));
            dosProtection.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
        }
    }

    private void registerServlet(ServletContext context, String name, String mapping, Servlet servlet) {
        ServletRegistration.Dynamic servletRegistration = context.addServlet(name, servlet);
        servletRegistration.addMapping(mapping);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
