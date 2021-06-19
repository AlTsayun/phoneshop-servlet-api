<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="productsInCart" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Cart">
    <form method="post">
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
                <td></td>
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
                        <fmt:formatNumber value="${item.quantity}" var="quantity"/>
                        <input name="quantity" value="${quantity}"/>
                        <input type="hidden" name="productId" value="${item.product.id}"/>
                    </td>
                    <td>
                        <button formaction="${pageContext.servletContext.contextPath}/cart/delete?productId=${item.product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <c:if test="${not empty productsInCart}">
            <button type="submit">Update</button>
            <button formaction="${pageContext.servletContext.contextPath}/checkout" formmethod="get">
                Checkout
            </button>
        </c:if>
    </form>
</tags:master>