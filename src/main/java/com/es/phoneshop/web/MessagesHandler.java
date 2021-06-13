package com.es.phoneshop.web;

import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class MessagesHandler {

    enum MessageType{
        ERROR, SUCCESS
    }

    public static final String SUCCESS_MESSAGES_SESSION_ATTRIBUTE = "successMessages";
    public static final String ERROR_MESSAGES_SESSION_ATTRIBUTE = "errorMessages";

    public void add(
            HttpServletRequest request,
            HttpServletResponse response,
            MessageType type,
            String message
    ){
        String sessionAttributeName = getMessagesSessionAttribute(type);
        List<String> messages = (List<String>) request.getSession().getAttribute(sessionAttributeName);
        if (messages == null){
            messages = new ArrayList<>();
        }
        messages.add(StringEscapeUtils.escapeHtml4(message));
        request.getSession().setAttribute(sessionAttributeName, messages);
    }

    private String getMessagesSessionAttribute(MessageType type){
        switch (type){
            case ERROR: return ERROR_MESSAGES_SESSION_ATTRIBUTE;
            case SUCCESS: return SUCCESS_MESSAGES_SESSION_ATTRIBUTE;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

}
