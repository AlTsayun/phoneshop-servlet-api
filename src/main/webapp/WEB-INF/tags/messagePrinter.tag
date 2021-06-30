<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="errorMessagesKey" required="false" %>
<%@ attribute name="warningMessagesKey" required="false"%>
<%@ attribute name="successMessagesKey" required="false"%>

<c:if test="${! empty errorMessagesKey}">
    <c:set var="messages" value="${sessionScope.get(errorMessagesKey)}"/>
    <c:if test="${messages != null}">
        <c:forEach var="message" items="${messages}">
            <div style="color: red">
                    ${message}
            </div>
        </c:forEach>
        ${sessionScope.remove(errorMessagesKey)}
    </c:if>
</c:if>


<c:if test="${! empty warningMessagesKey}">
    <c:set var="messages" value="${sessionScope.get(warningMessagesKey)}"/>
    <c:if test="${messages != null}">
        <c:forEach var="message" items="${messages}">
            <div>
                    ${message}
            </div>
        </c:forEach>
        ${sessionScope.remove(warningMessagesKey)}
    </c:if>
</c:if>

<c:if test="${! empty successMessagesKey}">
    <c:set var="messages" value="${sessionScope.get(successMessagesKey)}"/>
    <c:if test="${messages != null}">
        <c:forEach var="message" items="${messages}">
            <div>
                    ${message}
            </div>
        </c:forEach>
        ${sessionScope.remove(successMessagesKey)}
    </c:if>
</c:if>