package com.es.phoneshop.web.filters;

import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.security.dosProtection.service.DosProtectionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DosProtectionFilterTest {

    private DosProtectionFilter filter;
    private DosProtectionService dosProtectionService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    private String remoteAddress;

    @Before
    public void setup() {
        remoteAddress = "0.0.0.1";
        dosProtectionService = setupDosProtectionService(remoteAddress);
        filter = new DosProtectionFilter(setupConfiguration(dosProtectionService));
        when(request.getRemoteAddr()).thenReturn(remoteAddress);
    }

    private Configuration setupConfiguration(DosProtectionService dosProtectionService) {
        Configuration config = mock(Configuration.class);
        when(config.getDosProtectionService()).thenReturn(dosProtectionService);
        return config;
    }

    private DosProtectionService setupDosProtectionService(String remoteAddress) {
        DosProtectionService service = mock(DosProtectionService.class);
        when(service.isAllowed(remoteAddress)).thenReturn(true);
        return service;
    }

    @Test
    public void testDoFilterAllowed() throws ServletException, IOException {
        filter.doFilter(request, response, chain);
        verify(dosProtectionService).isAllowed(eq(remoteAddress));
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testDoFilterNotAllowed() throws ServletException, IOException {
        when(dosProtectionService.isAllowed(remoteAddress)).thenReturn(false);
        filter.doFilter(request, response, chain);
        verify(response).setStatus(eq(429));
    }
}