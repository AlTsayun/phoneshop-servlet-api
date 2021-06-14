package com.es.phoneshop.web.contextListeners;

import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServletInitServletContextListenerTest {

    private ServletInitServletContextListener listener = new ServletInitServletContextListener();

    @Mock
    private ServletRegistration.Dynamic servletRegistration;
    @Mock
    private FilterRegistration.Dynamic filterRegistration;

    private MockedStatic<ConfigurationImpl> configurationStatic;

    @Captor
    private ArgumentCaptor<String> servletRegistrationCaptor;
    @Captor
    private ArgumentCaptor<String> filterRegistrationCaptor;

    private ServletContext context;

    private List<String> servletNames;
    private List<String> filterNames;

    @Before
    public void setup() {
        servletNames = List.of("productList", "productDetails", "productPricesHistory", "cartItemAdd", "cart",
                "cartItemDelete", "miniCart");
        filterNames = List.of("cart", "recentlyViewedProducts");
        context = setupContext(servletRegistration, filterRegistration);
        setupConfiguration();
    }

    private ServletContext setupContext(ServletRegistration.Dynamic servletRegistration,
                                        FilterRegistration.Dynamic filterRegistration) {
        ServletContext context = mock(ServletContext.class);
        when(context.addServlet(any(), (Servlet) any())).thenReturn(servletRegistration);
        when(context.addFilter(any(), (Filter) any())).thenReturn(filterRegistration);
        return context;
    }

    private Configuration setupConfiguration() {
        Configuration configuration = mock(ConfigurationImpl.class);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    @After
    public void cleanUp() {
        configurationStatic.close();
    }

    @Test
    public void testContextInitialized() {
        when(context.getInitParameter("configuration")).thenReturn("default");

        ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        listener.contextInitialized(event);
        verify(context, times(servletNames.size())).addServlet(servletRegistrationCaptor.capture(), (Servlet) any());
        List<String> capturedServletNames = servletRegistrationCaptor.getAllValues();

        assertEquals(servletNames.size(), capturedServletNames.size());
        servletNames.forEach(it -> assertTrue(capturedServletNames.contains(it)));

        verify(context, times(filterNames.size())).addFilter(filterRegistrationCaptor.capture(), (Filter) any());
        List<String> capturedFilterNames = filterRegistrationCaptor.getAllValues();

        assertEquals(filterNames.size(), capturedFilterNames.size());
        filterNames.forEach(it -> assertTrue(capturedFilterNames.contains(it)));
    }

    @Test
    public void testContextInitializedListenerDisabled() {
        when(context.getInitParameter("configuration")).thenReturn("notDefault");

        ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        listener.contextInitialized(event);

        verify(context, times(0)).addServlet(any(), (Servlet) any());
        verify(context, times(0)).addFilter(any(), (Filter) any());
    }

}