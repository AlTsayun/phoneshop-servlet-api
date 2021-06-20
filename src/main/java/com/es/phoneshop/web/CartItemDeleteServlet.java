package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.ProductNotFoundInCartException;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;
import static com.es.phoneshop.web.MessagesHandler.MessageType.SUCCESS;

public class CartItemDeleteServlet extends HttpServlet {

    private final CartService cartService;

    private final ProductDao productDao;

    private final MessagesHandler messagesHandler;

    public CartItemDeleteServlet(Configuration configuration, MessagesHandler messagesHandler) {
        this.cartService = configuration.getCartService();
        this.productDao = configuration.getProductDao();
        this.messagesHandler = messagesHandler;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String productIdStr = request.getParameter("productId");
        String referer = request.getHeader("referer");

        try {
            Long productId = Long.valueOf(productIdStr);
            cartService.deleteCartItemById(request.getSession(), productId);
            messagesHandler.add(request, response, SUCCESS, "Product is successfully deleted from your cart.");
        } catch (NumberFormatException | ProductNotFoundException e) {
            messagesHandler.add(request, response, ERROR, "Product is not found.");
        } catch (ProductNotFoundInCartException e) {
            messagesHandler.add(request, response, ERROR, "Product is not present in cart.");
        }

        response.sendRedirect(referer != null ? referer : "/cart");
    }
}
