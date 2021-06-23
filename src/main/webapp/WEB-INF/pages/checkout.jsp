<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="productsInCart" type="java.util.ArrayList" scope="request"/>
<jsp:useBean id="deliveryPrice" type="com.es.phoneshop.domain.common.model.Price" scope="request"/>
<jsp:useBean id="subtotal" type="com.es.phoneshop.domain.common.model.Price" scope="request"/>
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
        <c:forEach var="item" items="${productsInCart}">
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
                    <c:set var="price" value="${item.product.getActualPrice()}" scope="application"/>
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

    <%--todo: get subtotal, deliveryPrice and total --%>

    <p>
            <%--            <c:set var="subtotal" value="${order.getSubtotal()}" scope="page"/>--%>
        Cart subtotal:
        <fmt:formatNumber value="${subtotal.value}" type="currency" currencySymbol="${subtotal.currency.symbol}"/>
    </p>
    <p>
            <%--            <c:set var="deliveryPrice" value="${order.deliveryDetails.price}" scope="page"/>--%>
        Delivery cost:
        <fmt:formatNumber value="${deliveryPrice.value}" type="currency"
                          currencySymbol="${deliveryPrice.currency.symbol}"/>
    </p>
    <p>
        Order total:
        <fmt:formatNumber value="${deliveryPrice.value.add(subtotal.value)}" type="currency"
                          currencySymbol="${subtotal.currency.symbol}"/>
    </p>

    <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
        <table>
            <tr>
                <td>
                    First name:
                </td>
                <td>
                    <input name="firstName" value="${param.firstName}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Last name:
                </td>
                <td>
                    <input name="lastName" value="${param.lastName}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Phone number:
                </td>
                <td>
                    <input name="phoneNumber" value="${param.phoneNumber}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Delivery date:
                </td>
                <td>
                    <input type="date" name="deliveryDate" value="${param.deliveryDate}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Delivery address:
                </td>
                <td>
                    <input name="deliveryAddress" value="${param.deliveryAddress}"/>
                </td>
            </tr>
            <tr>
                <td>
                    Payment method:
                </td>
                <td>
                    <select name="paymentMethod">
                        <option value="cash" ${param.paymentMethod.equals("cash") ? "selected" : ""}>Cash</option>
                        <option value="credit_card" ${param.paymentMethod.equals("credit_card") ? "selected" : ""}>
                            Credit card
                        </option>
                    </select>
                </td>
            </tr>
        </table>
        <c:if test="${not empty productsInCart}">
            <button type="submit">Confirm order</button>
        </c:if>
    </form>
</tags:master>