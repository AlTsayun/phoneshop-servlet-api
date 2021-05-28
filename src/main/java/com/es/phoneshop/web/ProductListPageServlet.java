package com.es.phoneshop.web;

import com.es.phoneshop.domain.common.model.SortingOrder;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import com.es.phoneshop.domain.product.model.ProductRequest;
import com.es.phoneshop.domain.product.model.ProductSortingCriteria;
import com.es.phoneshop.domain.product.persistence.ProductDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProductListPageServlet extends HttpServlet {

    private Configuration configuration;

    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        configuration = ConfigurationImpl.getInstance();
        productDao = configuration.getProductDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("searchQuery");
        ProductSortingCriteria sortingCriteria = ProductSortingCriteria.fromString(request.getParameter("sortingCriteria"));
        SortingOrder sortingOrder = SortingOrder.fromString(request.getParameter("sortingOrder"));

        request.setAttribute("products", productDao.getAllByRequest(new ProductRequest(query, sortingCriteria, sortingOrder, 1)));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }
}
