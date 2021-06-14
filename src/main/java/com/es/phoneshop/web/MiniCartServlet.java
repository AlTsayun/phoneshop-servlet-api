package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.ProductInCart;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;

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
        List<ProductInCart> productsInCart = cartService.getCart(request.getSession()).getItems().stream()
                .filter(it -> isPresentInDao(it.getProductId(), id ->
                    messagesHandler.add(
                            request,
                            response,
                            ERROR,
                            "Item wih id " + it.getProductId() + "is not present in catalog and hence deleted from cart.")
                ))
                .map(it -> new ProductInCart(productDao.getById(it.getProductId()).get(), it.getQuantity()))
                .collect(Collectors.toList());
        request.setAttribute("productsInCartCount", getTotalQuantity(productsInCart));
        request.setAttribute("totalCartPriceValue", getTotalCartPriceValue(productsInCart ));
        request.setAttribute("totalCartPriceCurrency", getCurrency());

        request.getRequestDispatcher("/WEB-INF/pages/miniCart.jsp").include(request, response);
    }

    private boolean isPresentInDao(Long productId, Consumer<Long> negativeAction){
        if (productDao.getById(productId).isPresent()) {
            return true;
        } else {
            negativeAction.accept(productId);
            return false;
        }
    }

    private int getTotalQuantity(List<ProductInCart> productsInCart){
        return productsInCart.stream()
                .map(ProductInCart::getQuantity)
                .reduce(0, Integer::sum);
    }

    private Currency getCurrency(){
        return Currency.getInstance("USD");
    }

    private BigDecimal getTotalCartPriceValue(List<ProductInCart> productsInCart){
        return productsInCart.stream()
                .map(it -> it.getProduct().getActualPrice().getValue().multiply(new BigDecimal(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
