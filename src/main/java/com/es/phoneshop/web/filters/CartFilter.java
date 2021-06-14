package com.es.phoneshop.web.filters;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class CartFilter implements Filter {

    private final String cartSessionAttributeName;
    private final SessionLockProvider sessionLockProvider;

    public CartFilter(Configuration configuration) {
        this.cartSessionAttributeName = configuration.getCartSessionAttributeName();
        this.sessionLockProvider = configuration.getCartSessionLockProvider();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpSession session = ((HttpServletRequest) request).getSession();

        Lock lock = sessionLockProvider.getLock(session).writeLock();
        lock.lock();
        try {
            Cart cart = (Cart) session.getAttribute(cartSessionAttributeName);
            if (cart == null) {
                session.setAttribute(cartSessionAttributeName, new Cart(new ArrayList<>()));
            }
        } finally {
            lock.unlock();
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
