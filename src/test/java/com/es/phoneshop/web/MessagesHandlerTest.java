package com.es.phoneshop.web;

import com.es.phoneshop.domain.product.model.Product;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessagesHandlerTest{

    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private MessagesHandler messagesHandler;

    private List<String> successMessages;

    private List<String> errorMessages;

    private HttpSession session;

    @Captor
    private ArgumentCaptor<List<String>> paramCaptor;

    @Before
    public void setup(){
        successMessages = setupSuccessMessages();
        errorMessages = setupErrorMessages();
        messagesHandler = new MessagesHandler();
    }

    private List<String> setupErrorMessages() {
        List<String> successMessages = new ArrayList<>();
        successMessages.add("success 1");
        successMessages.add("success 2");
        return successMessages;
    }

    private List<String> setupSuccessMessages() {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("error 1");
        errorMessages.add("error 2");
        return errorMessages;
    }


    private HttpServletRequest setupRequest(HttpSession session) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);
        return request;
    }

    private HttpSession setupSession(List<String> successMessages, List<String> errorMessages){
        HttpSession session = mock(HttpSession.class);
        List<String> successMessagesCopy = new ArrayList<>(successMessages);
        List<String> errorMessagesCopy = new ArrayList<>(errorMessages);
        when(session.getAttribute(MessagesHandler.SUCCESS_MESSAGES_SESSION_ATTRIBUTE)).thenReturn(successMessagesCopy);
        when(session.getAttribute(MessagesHandler.ERROR_MESSAGES_SESSION_ATTRIBUTE)).thenReturn(errorMessagesCopy);
        return session;
    }

    private HttpSession setupEmptySession(){
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(MessagesHandler.SUCCESS_MESSAGES_SESSION_ATTRIBUTE)).thenReturn(null);
        when(session.getAttribute(MessagesHandler.ERROR_MESSAGES_SESSION_ATTRIBUTE)).thenReturn(null);
        return session;
    }

    @Test
    public void testAdd() {

        session = setupSession(successMessages, errorMessages);
        request = setupRequest(session);

        String successMessage = "new success message";
        messagesHandler.add(request, response, MessagesHandler.MessageType.SUCCESS, successMessage);
        verify(session).setAttribute(
                eq(MessagesHandler.SUCCESS_MESSAGES_SESSION_ATTRIBUTE),
                paramCaptor.capture());
        List<String> messages = paramCaptor.getAllValues().get(paramCaptor.getAllValues().size() - 1);
        assertEquals(successMessages.size() + 1, messages.size());
        assertTrue(messages.contains(successMessage));
        successMessages.forEach(it -> assertTrue(messages.contains(it)));
    }

    @Test
    public void testAddEmptySession(){

        session = setupEmptySession();
        request = setupRequest(session);

        String errorMessage = "new error message";
        messagesHandler.add(request, response, MessagesHandler.MessageType.ERROR, errorMessage);
        verify(session).setAttribute(
                eq(MessagesHandler.ERROR_MESSAGES_SESSION_ATTRIBUTE),
                paramCaptor.capture());
        List<String> messages = paramCaptor.getAllValues().get(paramCaptor.getAllValues().size() - 1);
        assertEquals(1, messages.size());
        assertTrue(messages.contains(errorMessage));
    }
}