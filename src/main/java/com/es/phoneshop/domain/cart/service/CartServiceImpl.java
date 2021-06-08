package com.es.phoneshop.domain.cart.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Optional;

public class CartServiceImpl implements CartService {

    public static final String CART_SESSION_ATTRIBUTE = CartServiceImpl.class.getName() + ".cart";
    public static final String CART_SESSION_LOCK_ATTRIBUTE = CartServiceImpl.class.getName() + ".cart.lock";

    private final ProductDao productDao;
    private final SessionLockProvider sessionLockProvider;

    @Override
    public void add(Cart cart, Long productId, int quantity) {

        if(quantity <= 0){
            throw new ProductQuantityTooLowException();
        }

        Optional<Product> productOptional = productDao.getById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            CartItem oldItem = cart.getItems().stream()
                    .filter(it -> productId.equals(it.getProductId()))
                    .findFirst()
                    .orElse(null);

            quantity += oldItem == null ? 0 : oldItem.getQuantity();

            if (product.getStock() >= quantity){
                cart.getItems().remove(oldItem);
                cart.getItems().add(new CartItem(productId, quantity));
            } else {
                throw new ProductStockNotEnoughException();
            }
        }
    }

    public CartServiceImpl(ProductDao productDao, SessionLockWrapper sessionLockWrapper) {
        this.productDao = productDao;
        this.sessionLockProvider = sessionLockWrapper.getSessionLockProvider(CART_SESSION_LOCK_ATTRIBUTE);
    }

    @Override
    public Cart getCart(HttpSession session) {
        sessionLockProvider.getLock(session).lock();
        try {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                session.setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart(new ArrayList<>()));
            }
            return cart;
        } finally {
            sessionLockProvider.getLock(session).unlock();
        }
    }
}
