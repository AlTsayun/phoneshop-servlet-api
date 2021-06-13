<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="productsInCartCount" type="java.lang.Integer" scope="request"/>
<jsp:useBean id="totalCartPriceValue" type="java.math.BigDecimal" scope="request"/>
<jsp:useBean id="totalCartPriceCurrency" type="java.util.Currency" scope="request"/>


<fmt:formatNumber
        value="${totalCartPriceValue}"
        type="currency"
        currencySymbol="${totalCartPriceCurrency.symbol}"
        var="totalCartPrice"/>

<a href="${pageContext.servletContext.contextPath}/cart">
    ${productsInCartCount} products in cart (${totalCartPrice})
</a>
