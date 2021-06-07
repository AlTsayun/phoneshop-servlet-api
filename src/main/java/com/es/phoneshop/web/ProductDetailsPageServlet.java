package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.ProductInCart;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ViewedProductsHistoryService;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class ProductDetailsPageServlet extends HttpServlet {

    private CartService cartService;
    private Configuration configuration;

    private ProductDao productDao;

    private ViewedProductsHistoryService viewedProductsHistoryService;

    private ErrorHandler errorHandler;

    public ProductDetailsPageServlet(Configuration configuration, ErrorHandler errorHandler) {
        this.configuration = configuration;
        this.errorHandler = errorHandler;
        this.productDao = configuration.getProductDao();
        this.viewedProductsHistoryService = configuration.getViewedProductsHistoryService();
        this.cartService = configuration.getCartService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdStr = request.getPathInfo().substring(1);
        Product product;
        try {
            product = productDao.getById(Long.valueOf(productIdStr)).get();
        } catch (NumberFormatException | NoSuchElementException e) {
            errorHandler.productNotFound(request, response, productIdStr);
            return;
        }
        request.setAttribute("product", product);

        setProductsInCartAttribute(request, response);
        addViewedProduct(request, response, product.getId());
        setViewedProductsAttribute(request, response);

        request.getRequestDispatcher("/WEB-INF/pages/productDetails.jsp").forward(request, response);
    }

    private void setProductsInCartAttribute(HttpServletRequest request, HttpServletResponse response){
        List<ProductInCart> productsInCart;

        productsInCart = cartService.getCart(request.getSession()).getItems().stream()
                .filter(it -> productDao.getById(it.getProductId()).isPresent())
                .map(it -> new ProductInCart(productDao.getById(it.getProductId()).get(), it.getQuantity()))
                .collect(Collectors.toList());

        request.setAttribute("productsInCart", productsInCart);
    }

    private void addViewedProduct(HttpServletRequest request, HttpServletResponse response, Long productId) {
        List<Long> viewedProductsIds = viewedProductsHistoryService.getProductIds(request.getSession());
        viewedProductsHistoryService.add(viewedProductsIds, productId);
    }

    private void setViewedProductsAttribute(HttpServletRequest request, HttpServletResponse response){
        List<Long> viewedProductsIds = viewedProductsHistoryService.getProductIds(request.getSession());

        List<Product> viewedProducts = viewedProductsIds.stream()
                .map(id -> productDao.getById(id).get())
                .collect(Collectors.toList());

        request.setAttribute("viewedProducts", viewedProducts);
    }


}
