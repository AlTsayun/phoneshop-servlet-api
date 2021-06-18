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
            <c:forEach var="productInCart" items="${productsInCart}">
                <tr>
                    <td>
                        <img class="product-tile" src="${productInCart.product.imageUrl}" alt="product image">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${productInCart.product.id}">
                                ${productInCart.product.description}
                        </a>
                    </td>
                    <td class="price">
                        <c:set var="price" value="${productInCart.product.getActualPrice()}" scope="application"/>
                        <a href="${pageContext.servletContext.contextPath}/product-prices-history/${productInCart.product.id}">
                            <fmt:formatNumber value="${price.value}" type="currency"
                                              currencySymbol="${price.currency.symbol}"/>
                        </a>
                    </td>
                    <td>
                        <fmt:formatNumber value="${productInCart.quantity}" var="quantity"/>
                        <input name="quantity" value="${quantity}"/>
                        <input type="hidden" name="productId" value="${productInCart.product.id}"/>
                    </td>
                    <td>
                        <button formaction="${pageContext.servletContext.contextPath}/cart/delete?productId=${productInCart.product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <button type="submit">Update</button>
    </form>
</tags:master>