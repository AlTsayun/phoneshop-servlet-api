package com.es.phoneshop.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandler {

    public final String productNotFoundPagePath = "/WEB-INF/pages/productNotFound.jsp";
    public final String orderNotFoundPagePath = "/WEB-INF/pages/orderNotFound.jsp";

    public void productNotFound(HttpServletRequest request, HttpServletResponse response, String productIdStr) throws ServletException, IOException {
        request.setAttribute("productIdStr", productIdStr);
        response.setStatus(404);
        request.getRequestDispatcher(productNotFoundPagePath).forward(request, response);
    }

    public void orderNotFound(HttpServletRequest request, HttpServletResponse response, String orderIdStr) throws ServletException, IOException {
        request.setAttribute("orderIdStr", orderIdStr);
        response.setStatus(404);
        request.getRequestDispatcher(orderNotFoundPagePath).forward(request, response);
    }

}
