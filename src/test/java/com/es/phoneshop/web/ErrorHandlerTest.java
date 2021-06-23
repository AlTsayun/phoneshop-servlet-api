package com.es.phoneshop.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;

    private ErrorHandler errorHandler;

    @Before
    public void setup(){
        errorHandler = new ErrorHandler();
        when(request.getRequestDispatcher(any())).thenReturn(requestDispatcher);
    }

    @Test
    public void testProductNotFound() throws ServletException, IOException {
        String productIdStr = "blah";
        errorHandler.productNotFound(request, response, productIdStr);

        verify(request).setAttribute(eq("productIdStr"), eq(productIdStr));
        verify(response).setStatus(eq(404));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testOrderNotFound() throws ServletException, IOException {
        String orderIdStr = "blah";
        errorHandler.orderNotFound(request, response, orderIdStr);

        verify(request).setAttribute(eq("orderIdStr"), eq(orderIdStr));
        verify(response).setStatus(eq(404));
        verify(requestDispatcher).forward(request, response);
    }
}