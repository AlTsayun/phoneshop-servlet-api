package com.es.phoneshop.web.contextListeners;

import com.es.phoneshop.domain.product.model.Product;
import com.es.phoneshop.domain.product.persistence.ArrayListProductDao;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;
import com.es.phoneshop.infra.config.ConfigurationImpl;
import junit.framework.TestCase;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServletInitServletContextListenerTest{

    private ServletInitServletContextListener listener = new ServletInitServletContextListener();

    @Mock
    private ServletRegistration.Dynamic servletRegistration;

    private MockedStatic<ConfigurationImpl> configurationStatic;

    @Captor
    private ArgumentCaptor<String> paramCaptor;

    private ServletContext context;

    private List<String> servletNames;

    @Before
    public void setup() {
        servletNames = List.of("productList", "productDetails", "productPricesHistory", "cart");
        context = setupContext();
        setupConfiguration();
    }

    private ServletContext setupContext() {
        ServletContext context = mock(ServletContext.class);
        when(context.addServlet(any(), (Servlet) any())).thenReturn(servletRegistration);
        return context;
    }

    private Configuration setupConfiguration() {
        Configuration configuration = mock(ConfigurationImpl.class);

        configurationStatic = mockStatic(ConfigurationImpl.class);
        configurationStatic.when(ConfigurationImpl::getInstance).thenReturn(configuration);
        return configuration;
    }

    @After
    public void cleanUp(){
        configurationStatic.close();
    }

    @Test
    public void testContextInitialized() {
        when(context.getInitParameter("configuration")).thenReturn("default");

        ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        listener.contextInitialized(event);
        verify(context, times(servletNames.size())).addServlet(paramCaptor.capture(), (Servlet) any());
        List<String> allValues = paramCaptor.getAllValues();

        servletNames.forEach(it -> assertTrue(allValues.contains(it)));
        verify(servletRegistration, times(servletNames.size())).addMapping(any());
    }

    @Test
    public void testContextInitializedListenerDisabled() {
        when(context.getInitParameter("configuration")).thenReturn("notDefault");

        ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        listener.contextInitialized(event);

        verify(context, times(0)).addServlet(any(), (Servlet) any());
        verify(servletRegistration, times(0)).addMapping(any());
    }

}