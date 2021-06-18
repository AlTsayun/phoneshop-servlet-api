package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.MiniCart;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MiniCartServlet extends HttpServlet {

    private final CartService cartService;

    private final ProductDao productDao;

    private final MessagesHandler messagesHandler;

    public MiniCartServlet(Configuration configuration, MessagesHandler messagesHandler) {
        this.cartService = configuration.getCartService();
        this.productDao = configuration.getProductDao();
        this.messagesHandler = messagesHandler;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MiniCart miniCart = cartService.getMiniCart(request.getSession());

        request.setAttribute("productsInCartCount", miniCart.getTotalProductsQuantity());
        request.setAttribute("totalCartPriceValue", miniCart.getTotalPriceValue());
        request.setAttribute("totalCartPriceCurrency", miniCart.getCurrency());

        request.getRequestDispatcher("/WEB-INF/pages/miniCart.jsp").include(request, response);
    }
}
