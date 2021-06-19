package com.es.phoneshop.domain.cart.service;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.CartItem;
import com.es.phoneshop.domain.cart.model.DisplayCartItem;
import com.es.phoneshop.domain.cart.model.MiniCart;
import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.domain.product.service.ProductNotFoundException;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

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

            Optional<CartItem> oldItem = getCartItemById(cart, productId);

            int adjustedQuantity = Math.addExact(quantity, oldItem.map(CartItem::getQuantity).orElse(0));
            if (product.getStock() < adjustedQuantity) {
                throw new ProductStockNotEnoughException();
            }

            if (oldItem.isPresent()) {
                oldItem.get().setQuantity(adjustedQuantity);
            } else {
                cart.getItems().add(new CartItem(productId, adjustedQuantity));
            }
        });
    }

    private Optional<CartItem> getCartItemById(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(it -> productId.equals(it.getProductId()))
                .findFirst();
    }

    private void modifyCart(HttpSession session, Long productId, ModificationAction modificationAction) {

        Optional<Product> productOptional = productDao.getById(productId);
        if (!productOptional.isPresent()) {
            throw new ProductNotFoundException(productId.toString());
        }

        Product product = productOptional.get();
        Lock lock = sessionLockProvider.getLock(session).writeLock();
        lock.lock();
        try {
            Cart cart = (Cart) session.getAttribute(cartSessionAttributeName);

            if (cart == null) {
                session.setAttribute(cartSessionAttributeName, cart = new Cart(new ArrayList<>()));
            }

            modificationAction.apply(cart, product);

        } finally {
            lock.unlock();
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

            Optional<CartItem> oldItem = getCartItemById(cart, productId);

            if (product.getStock() < quantity) {
                throw new ProductStockNotEnoughException();
            }
            if (oldItem.isPresent()) {
                oldItem.get().setQuantity(quantity);
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
    public MiniCart getMiniCart(HttpSession session) {
        List<DisplayCartItem> products = getCart(session).getItems().stream()
                .filter(it -> productDao.getById(it.getProductId()).isPresent())
                .map(it -> new DisplayCartItem(productDao.getById(it.getProductId()).get(), it.getQuantity()))
                .collect(Collectors.toList());
        return new MiniCart(getTotalQuantity(products), getTotalCartPriceValue(products), getCurrency(products));
    }

    @Override
    public Cart getCart(HttpSession session) {
        Lock lock = sessionLockProvider.getLock(session).writeLock();
        lock.lock();
        try {
            Cart cart = (Cart) session.getAttribute(cartSessionAttributeName);

            if (cart == null) {
                session.setAttribute(cartSessionAttributeName, cart = new Cart(new ArrayList<>()));
            }
            return cart;

        } finally {
            lock.unlock();
        }
    }

    private int getTotalQuantity(List<DisplayCartItem> productsInCart) {
        return productsInCart.stream()
                .map(DisplayCartItem::getQuantity)
                .reduce(0, Integer::sum);
    }

    public Currency getCurrency(List<DisplayCartItem> productsInCart) {
        return Currency.getInstance("USD");
    }

    private BigDecimal getTotalCartPriceValue(List<DisplayCartItem> productsInCart) {
        return productsInCart.stream()
                .map(it -> it.getProduct().getActualPrice().getValue().multiply(new BigDecimal(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private interface ModificationAction {
        void apply(Cart cart, Product product);
    }
}
