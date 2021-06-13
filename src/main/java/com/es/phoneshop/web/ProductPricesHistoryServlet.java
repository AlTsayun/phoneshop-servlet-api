package com.es.phoneshop.web;

import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

public class ProductPricesHistoryServlet extends HttpServlet {

    private final Configuration configuration;

    private final ProductDao productDao;

    private final ErrorHandler errorHandler;

    public ProductPricesHistoryServlet(Configuration configuration, ErrorHandler errorHandler) {
        this.configuration = configuration;
        this.errorHandler = errorHandler;
        this.productDao = configuration.getProductDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdStr = request.getPathInfo().substring(1);
        try {
            Long id = Long.valueOf(productIdStr);
            Product product = productDao.getById(id).get();
            request.setAttribute("product", product);
            request.setAttribute("prices", product.getPricesHistory());
            request.getRequestDispatcher("/WEB-INF/pages/productPrices.jsp").forward(request, response);
        } catch (NumberFormatException | NoSuchElementException e) {
            errorHandler.productNotFound(request, response, productIdStr);
        }
    }
}
