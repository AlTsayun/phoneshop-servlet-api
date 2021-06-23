<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.domain.order.model.DisplayOrder" scope="request"/>
<tags:master pageTitle="Checkout">
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
            </td>
            <td class="price">
                Price
            </td>
            <td>
                Quantity
            </td>
        </tr>
        </thead>
        <c:forEach var="item" items="${order.items}">
            <tr>
                <td>
                    <img class="product-tile" src="${item.product.imageUrl}" alt="product image">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                            ${item.product.description}
                    </a>
                </td>
                <td class="price">
                    <c:set var="price" value="${item.price}" scope="page"/>
                    <a href="${pageContext.servletContext.contextPath}/product-prices-history/${item.product.id}">
                        <fmt:formatNumber value="${price.value}" type="currency"
                                          currencySymbol="${price.currency.symbol}"/>
                    </a>
                </td>
                <td>
                    <fmt:formatNumber value="${item.quantity}"/>
                </td>
            </tr>
        </c:forEach>
    </table>

    <p>
        <c:set var="subtotal" value="${order.getSubtotal()}" scope="page"/>
        Cart subtotal:
        <fmt:formatNumber value="${subtotal.value}" type="currency" currencySymbol="${subtotal.currency.symbol}"/>
    </p>
    <p>
        <c:set var="deliveryPrice" value="${order.deliveryDetails.price}" scope="page"/>
        Delivery cost:
        <fmt:formatNumber value="${deliveryPrice.value}" type="currency"
                          currencySymbol="${deliveryPrice.currency.symbol}"/>
    </p>
    <p>
        Order total:
        <fmt:formatNumber value="${deliveryPrice.value.add(subtotal.value)}" type="currency"
                          currencySymbol="${subtotal.currency.symbol}"/>
    </p>

    <table>
        <tr>
            <td>
                First name:
            </td>
            <td>
                    ${order.contactDetails.firstName}
            </td>
        </tr>
        <tr>
            <td>
                Last name:
            </td>
            <td>
                    ${order.contactDetails.lastName}
            </td>
        </tr>
        <tr>
            <td>
                Phone number:
            </td>
            <td>
                    ${order.contactDetails.phoneNumber}
            </td>
        </tr>
        <tr>
            <td>
                Delivery date:
            </td>
            <td>
                    ${order.deliveryDetails.arrivalDate}
            </td>
        </tr>
        <tr>
            <td>
                Delivery address:
            </td>
            <td>
                    ${order.deliveryDetails.destinationAddress}
            </td>
        </tr>
        <tr>
            <td>
                Payment method:
            </td>
            <td>
                <c:choose>
                    <c:when test="${order.paymentMethod == 'CASH'}">
                        Cash
                    </c:when>
                    <c:when test="${order.paymentMethod == 'CREDIT_CARD'}">
                        Credit card
                    </c:when>
                    <c:otherwise>
                        Not selected.
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>
</tags:master>