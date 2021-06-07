package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.ProductStockNotEnoughException;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.NumberFormatter;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.NoSuchElementException;

public class CartServlet extends HttpServlet {

    private final Configuration configuration;

    private final ProductDao productDao;

    private final CartService cartService;

    private final ErrorHandler errorHandler;

    public CartServlet(Configuration configuration, ErrorHandler errorHandler) {
        this.configuration = configuration;
        this.productDao = configuration.getProductDao();
        this.cartService = configuration.getCartService();
        this.errorHandler = errorHandler;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");

        int quantity;
        try {
            quantity = NumberFormat.getInstance(request.getLocale()).parse(quantityStr).intValue();
        } catch (ParseException e) {
            errorHandler.illegalParameterValue(request, response, "/cart", "quantity", quantityStr);
            return;
        }

        Product product;
        try {
            product = productDao.getById(Long.valueOf(productIdStr)).get();
        } catch (NumberFormatException | NoSuchElementException e) {
            errorHandler.productNotFound(request, response, productIdStr);
            return;
        }

        Cart cart = cartService.getCart(request.getSession());

        String returnPath = request.getContextPath() + "/products/" + productIdStr;
        try {
            cartService.add(cart, product.getId(), quantity);

        } catch (ProductStockNotEnoughException e){
            errorHandler.addingToCartError(request, response, "Stock is not enough.", product.getDescription(), returnPath);
            return;
        }

        request.setAttribute("productName", product.getDescription());
        request.setAttribute("returnPath", returnPath);
        request.getRequestDispatcher("/WEB-INF/pages/addingToCartSuccess.jsp").forward(request, response);

    }
}
