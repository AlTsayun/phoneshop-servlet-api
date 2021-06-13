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
import java.util.stream.Collectors;

public class MiniCartServlet extends HttpServlet {

    private final CartService cartService;

    private final ProductDao productDao;

    public MiniCartServlet(Configuration configuration) {
        this.cartService = configuration.getCartService();
        this.productDao = configuration.getProductDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try {
        List<ProductInCart> productsInCart = cartService.getCart(request.getSession()).getItems().stream()
                .map(it -> new ProductInCart(productDao.getById(it.getProductId()).get(), it.getQuantity()))
                .collect(Collectors.toList());
        request.setAttribute("productsInCartCount", getTotalQuantity(productsInCart));
        request.setAttribute("totalCartPriceValue", getTotalCartPriceValue(productsInCart ));
        request.setAttribute("totalCartPriceCurrency", getCurrency());
        request.getRequestDispatcher("/WEB-INF/pages/miniCart.jsp").include(request, response);
//        } catch (NoSuchElementException e){
//            //todo: handle no product found with such id
//        }
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
