package com.es.phoneshop.domain.cart.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;
import com.es.phoneshop.utils.sessionLock.SessionLockWrapper;

import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

public class CartServiceImpl implements CartService {

    private final String cartSessionAttributeName;
    private final ProductDao productDao;
    private final SessionLockProvider sessionLockProvider;
    public CartServiceImpl(ProductDao productDao,
                           String cartSessionAttributeName,
                           SessionLockProvider sessionLockProvider) {
        this.productDao = productDao;
        this.cartSessionAttributeName = cartSessionAttributeName;
        this.sessionLockProvider = sessionLockProvider;
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

            Lock lock = sessionLockProvider.getLock(session).writeLock();
            lock.lock();
            try {
                Cart cart = (Cart) session.getAttribute(cartSessionAttributeName);

                modificationAction.apply(cart, product);

            } finally {
                lock.unlock();
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
        Lock lock = sessionLockProvider.getLock(session).readLock();
        lock.lock();
        try {
            return (Cart) session.getAttribute(cartSessionAttributeName);
        } finally {
            lock.unlock();
        }
    }

    private interface ModificationAction {
        void apply(Cart cart, Product product);
    }
}
