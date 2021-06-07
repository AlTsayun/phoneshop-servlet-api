package com.es.phoneshop.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandler {
    public void productNotFound(HttpServletRequest request, HttpServletResponse response, String productIdStr) throws ServletException, IOException {
        request.setAttribute("productIdStr", productIdStr);
        response.setStatus(404);
        request.getRequestDispatcher("/WEB-INF/pages/productNotFound.jsp").forward(request, response);
    }

    public void illegalParameterValue(
        HttpServletRequest request,
        HttpServletResponse response,
        String resourcePath,
        String parameterName,
        String parameterValue
    ) throws ServletException, IOException {
        request.setAttribute("resourcePath", resourcePath);
        request.setAttribute("parameterName", parameterName);
        request.setAttribute("parameterValue", parameterValue);
        response.setStatus(400);
        request.getRequestDispatcher("/WEB-INF/pages/illegalParameterValue.jsp").forward(request, response);
    }

    public void addingToCartError(
        HttpServletRequest request,
        HttpServletResponse response,
        String errorMessage,
        String productName,
        String returnPath
    ) throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("productName", productName);
        request.setAttribute("returnPath", returnPath);
        request.getRequestDispatcher("/WEB-INF/pages/addingToCartError.jsp").forward(request, response);
    }


}
