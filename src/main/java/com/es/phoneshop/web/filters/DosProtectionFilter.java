package com.es.phoneshop.web.filters;

import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.security.dosProtection.service.DosProtectionService;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DosProtectionFilter implements Filter {
    private DosProtectionService dosProtectionService;

    public DosProtectionFilter(Configuration configuration) {
        this.dosProtectionService = configuration.getDosProtectionService();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (dosProtectionService.isAllowed(request.getRemoteAddr())) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(429);
        }
    }

    @Override
    public void destroy() {

    }
}
