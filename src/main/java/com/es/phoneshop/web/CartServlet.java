package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.ProductQuantityTooLowException;
import com.es.phoneshop.domain.cart.service.ProductStockNotEnoughException;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;
import static com.es.phoneshop.web.MessagesHandler.MessageType.SUCCESS;

public class CartServlet extends HttpServlet {

    private final CartService cartService;

    private final MessagesHandler messagesHandler;

    public CartServlet(Configuration configuration, MessagesHandler messagesHandler) {
        this.cartService = configuration.getCartService();
        this.messagesHandler = messagesHandler;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");

        try {
            int quantity;
            quantity = Math.toIntExact(NumberFormat.getInstance(request.getLocale()).parse(quantityStr).longValue());

            Long productId = Long.valueOf(productIdStr);

            Cart cart = cartService.getCart(request.getSession());
            cartService.add(cart, productId, quantity);

            messagesHandler.add(request, response, SUCCESS, "Product successfully added to your cart.");
        } catch (ParseException | ArithmeticException e) {
            messagesHandler.add(request, response, ERROR, "Entered quantity is not a valid number.");
        } catch (NumberFormatException | ProductNotFoundException e) {
            messagesHandler.add(request, response, ERROR, "Product with id" + productIdStr + "is not found.");
        } catch (ProductStockNotEnoughException e) {
            messagesHandler.add(request, response, ERROR, "Product stock is not enough.");
        } catch (ProductQuantityTooLowException e) {
            messagesHandler.add(request, response, ERROR, "Quantity " + quantityStr + " is too low.");
        }

        response.sendRedirect(request.getContextPath() + "/products/" + productIdStr);
    }
}
