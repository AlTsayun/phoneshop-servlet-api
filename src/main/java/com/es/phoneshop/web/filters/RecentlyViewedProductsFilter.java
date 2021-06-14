package com.es.phoneshop.web.filters;

import com.es.phoneshop.domain.product.service.ViewedProductsHistoryService;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.utils.sessionLock.SessionLockProvider;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class RecentlyViewedProductsFilter implements Filter{

    private final String viewedProductsSessionAttributeName;
    private final SessionLockProvider sessionLockProvider;

    public RecentlyViewedProductsFilter(Configuration configuration) {
        this.viewedProductsSessionAttributeName = configuration.getViewedProductsSessionAttributeName();
        this.sessionLockProvider = configuration.getViewedProductsSessionLockProvider();
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
            List<Long> products = (List<Long>) session.getAttribute(viewedProductsSessionAttributeName);
            if (products == null) {
                session.setAttribute(viewedProductsSessionAttributeName, new ArrayList<>());
            }
            chain.doFilter(request, response);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void destroy() {

    }
}
