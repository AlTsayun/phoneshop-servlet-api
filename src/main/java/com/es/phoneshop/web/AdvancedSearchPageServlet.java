package com.es.phoneshop.web;

import com.es.phoneshop.domain.product.model.AdvancedSearchRequest;
import com.es.phoneshop.domain.product.model.QueryType;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;

public class AdvancedSearchPageServlet extends HttpServlet {

    private ProductDao productDao;
    private MessagesHandler messagesHandler;

    public AdvancedSearchPageServlet(Configuration configuration, MessagesHandler messagesHandler) {
        this.productDao = configuration.getProductDao();
        this.messagesHandler = messagesHandler;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("searchQuery");
        String searchQueryTypeStr = request.getParameter("searchQueryType");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");


        if (query == null && searchQueryTypeStr == null && minPriceStr == null && maxPriceStr == null) {
            request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);
            return;
        }

        QueryType searchQueryType = QueryType.fromString(searchQueryTypeStr);
        if (searchQueryType == null) {
            showError(request, response, "Please select query type from dropdown menu.");
            return;
        }

        if (!verifyPrice(minPriceStr)) {
            showError(request, response, "Min price is incorrect.");
            return;
        }
        BigDecimal minPrice = minPriceStr.isEmpty() ? null : new BigDecimal(minPriceStr.replaceAll(",", "."));

        if (!verifyPrice(maxPriceStr)) {
            showError(request, response, "Max price is incorrect.");
            return;
        }
        BigDecimal maxPrice = maxPriceStr.isEmpty() ? null : new BigDecimal(maxPriceStr.replaceAll(",", "."));

        if (maxPrice != null && minPrice != null && maxPrice.compareTo(minPrice) < 0) {
            showError(request, response, "Max price must be greater than min price.");
            return;
        }

        request.setAttribute("products",
                productDao.getAllByAdvancedSearchRequest(
                        new AdvancedSearchRequest(query, searchQueryType, minPrice, maxPrice)));
        request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);
    }

    private boolean verifyPrice(String priceStr) {
        return priceStr != null && (priceStr.matches("\\d+([.,]\\d{1,2})?") || priceStr.isEmpty());
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws IOException {
        messagesHandler.add(request, response, ERROR, errorMessage);
        response.sendRedirect(request.getContextPath() + "/advanced-search");
    }
}
