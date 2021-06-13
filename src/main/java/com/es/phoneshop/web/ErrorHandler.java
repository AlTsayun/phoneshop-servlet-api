package com.es.phoneshop.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandler {

    public final String productNotFoundPagePath = "/WEB-INF/pages/productNotFound.jsp";

    public void productNotFound(HttpServletRequest request, HttpServletResponse response, String productIdStr) throws ServletException, IOException {
        request.setAttribute("productIdStr", productIdStr);
        response.setStatus(404);
        request.getRequestDispatcher(productNotFoundPagePath).forward(request, response);
    }

}
