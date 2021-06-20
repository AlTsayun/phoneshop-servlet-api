package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.DisplayCartItem;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.cart.service.ProductQuantityTooLowException;
import com.es.phoneshop.domain.cart.service.ProductStockNotEnoughException;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;
import static com.es.phoneshop.web.MessagesHandler.MessageType.SUCCESS;

public class CartPageServlet extends HttpServlet {

    private final CartService cartService;

    private final ProductDao productDao;

    private final MessagesHandler messagesHandler;

    public CartPageServlet(Configuration configuration, MessagesHandler messagesHandler) {
        this.cartService = configuration.getCartService();
        this.productDao = configuration.getProductDao();
        this.messagesHandler = messagesHandler;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] productIdStrings = request.getParameterValues("productId");
        String[] quantityStrings = request.getParameterValues("quantity");

        try {
            for (int i = 0; i < productIdStrings.length; i++) {

                int quantity;
                quantity = Math.toIntExact(
                        NumberFormat.getInstance(request.getLocale()).parse(quantityStrings[i]).longValue());

                Long productId;
                try {
                    productId = Long.valueOf(productIdStrings[i]);
                } catch (NumberFormatException e) {
                    throw new ProductNotFoundException(productIdStrings[i]);
                }

                cartService.updateCartItem(request.getSession(), productId, quantity);
                messagesHandler.add(request,
                        response,
                        SUCCESS,
                        "Product " + (i + 1) + " is successfully updated in your cart.");
            }

        } catch (ParseException | ArithmeticException e) {
            messagesHandler.add(request, response, ERROR, "Entered quantity is not a valid number.");
        } catch (NumberFormatException | ProductNotFoundException e) {
            messagesHandler.add(request, response, ERROR, "Product is not found.");
        } catch (ProductStockNotEnoughException e) {
            messagesHandler.add(request, response, ERROR, "Product stock is not enough.");
        } catch (ProductQuantityTooLowException e) {
            messagesHandler.add(request, response, ERROR, "Quantity " + e.getQuantity() + " is too low.");
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<DisplayCartItem> productsInCart = cartService.get(request.getSession()).getItems().stream()
                .filter(it -> isPresentInDao(it.getProductId(), id ->
                        messagesHandler.add(
                                request,
                                response,
                                ERROR,
                                "Item wih id " + it.getProductId() + "is not present in catalog and hence deleted from cart.")
                ))
                .map(it -> new DisplayCartItem(productDao.getById(it.getProductId()).get(), it.getQuantity()))
                .collect(Collectors.toList());
        request.setAttribute("productsInCart", productsInCart);
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);

    }

    private boolean isPresentInDao(Long productId, Consumer<Long> negativeAction){
        if (productDao.getById(productId).isPresent()) {
            return true;
        } else {
            negativeAction.accept(productId);
            return false;
        }
    }
}
