package com.es.phoneshop.web;

import com.es.phoneshop.domain.order.model.DisplayOrder;
import com.es.phoneshop.domain.order.model.DisplayOrderItem;
import com.es.phoneshop.domain.order.model.Order;
import com.es.phoneshop.domain.order.persistence.OrderDao;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class OrderOverviewPageServlet extends HttpServlet {

    private OrderDao orderDao;

    private ProductDao productDao;

    public OrderOverviewPageServlet(Configuration configuration) {
        this.orderDao = configuration.getOrderDao();
        this.productDao = configuration.getProductDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderIdStr = request.getPathInfo().substring(1);
        Order order;
        try {
            order = orderDao.getById(Long.valueOf(orderIdStr)).get();
        } catch (NumberFormatException | NoSuchElementException e) {
//            errorHandler.productNotFound(request, response, orderIdStr);
            return;
        }
        request.setAttribute("order",
                new DisplayOrder(order.getId(), order.getItems().stream()
                        .filter(it -> productDao.getById(it.getProductId()).isPresent())
                        .map(it -> new DisplayOrderItem(productDao.getById(it.getProductId()).get(), it.getQuantity(), it.getPrice()))
                        .collect(Collectors.toList()),
                        order.getDeliveryDetails(),
                        order.getContactDetails(),
                        order.getPaymentMethod()));

        request.getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp").forward(request, response);
    }
}
