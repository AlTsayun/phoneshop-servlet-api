package com.es.phoneshop.domain.cart.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
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
    public CartServiceImpl(ProductDao productDao, SessionLockWrapper sessionLockWrapper) {
        this.productDao = productDao;
        this.sessionLockProvider = sessionLockWrapper.getSessionLockProvider(CART_SESSION_LOCK_ATTRIBUTE);
    }

    @Override
    public void add(HttpSession session, Long productId, int quantity) throws ProductNotFoundException,
            ProductStockNotEnoughException,
            ProductQuantityTooLowException {
        modifyCart(session, productId, (cart, product) -> {

            if (quantity <= 0) {
                throw new ProductQuantityTooLowException(quantity);
            }

            CartItem oldItem = getCartItemById(cart, productId);

            int adjustedQuantity = Math.addExact(quantity, oldItem == null ? 0 : oldItem.getQuantity());
            if (product.getStock() < adjustedQuantity) {
                throw new ProductStockNotEnoughException();
            }

            if (oldItem != null) {
                oldItem.setQuantity(adjustedQuantity);
            } else {
                cart.getItems().add(new CartItem(productId, adjustedQuantity));
            }
        });
    }

    private CartItem getCartItemById(Cart cart, Long productId) {
        CartItem oldItem = cart.getItems().stream()
                .filter(it -> productId.equals(it.getProductId()))
                .findFirst()
                .orElse(null);
        return oldItem;
    }

    private void modifyCart(HttpSession session, Long productId, ModificationAction modificationAction) {

        Optional<Product> productOptional = productDao.getById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            sessionLockProvider.getLock(session).writeLock().lock();
            try {
                Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
                if (cart == null) {
                    session.setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart(new ArrayList<>()));
                }

                modificationAction.apply(cart, product);

            } finally {
                sessionLockProvider.getLock(session).writeLock().unlock();
            }

        } else {
            throw new ProductNotFoundException(productId.toString());
        }
    }

    @Override
    public void update(HttpSession session, Long productId, int quantity) throws ProductNotFoundException,
            ProductNotFoundInCartException,
            ProductStockNotEnoughException,
            ProductQuantityTooLowException {
        modifyCart(session, productId, (cart, product) -> {

            if (quantity <= 0) {
                throw new ProductQuantityTooLowException(quantity);
            }

            CartItem oldItem = getCartItemById(cart, productId);

            if (product.getStock() < quantity) {
                throw new ProductStockNotEnoughException();
            }
            if (oldItem != null) {
                oldItem.setQuantity(quantity);
            } else {
                throw new ProductNotFoundInCartException(productId.toString());
            }
        });
    }

    @Override
    public void deleteById(HttpSession session, Long productId) throws ProductNotFoundException,
            ProductNotFoundInCartException {
        modifyCart(session, productId, (cart, product) -> {
            if (!cart.getItems().removeIf(it -> it.getProductId().equals(productId))) {
                throw new ProductNotFoundInCartException(productId.toString());
            }
        });
    }

    @Override
    public Cart getCart(HttpSession session) {
        sessionLockProvider.getLock(session).readLock().lock();
        try {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                session.setAttribute(CART_SESSION_ATTRIBUTE, cart = new Cart(new ArrayList<>()));
            }
            return cart;
        } finally {
            sessionLockProvider.getLock(session).readLock().unlock();
        }
    }

    private interface ModificationAction {
        void apply(Cart cart, Product product);
    }
}
