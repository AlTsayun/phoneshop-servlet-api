<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="viewedProducts" type="java.util.ArrayList" scope="request"/>
<jsp:useBean id="product" type="com.es.phoneshop.domain.product.model.Product" scope="request"/>
<jsp:useBean id="productsInCart" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product details">

    <img src="${product.imageUrl}" alt="product image">
    <table>
        <thead>
        <tr>
            <td>
                Description
            </td>
            <td>
                Code
            </td>
            <td class="price">
                Price
            </td>
            <td class="stock">
                Stock
            </td>
        </tr>
        </thead>
        <tr>
            <td>
                    ${product.description}
            </td>
            <td>
                    ${product.code}
            </td>
            <td class="price">
                <c:set var="price" value="${product.getActualPrice()}" scope="page"/>
                <fmt:formatNumber value="${price.value}" type="currency" currencySymbol="${price.currency.symbol}"/>
            </td>
            <td class="stock">
                <fmt:formatNumber value="${product.stock}" type="number"/>
            </td>
        </tr>
    </table>
    <form method="post" action="${pageContext.request.contextPath}/cart">
        <input type="hidden" name="productId" value="${product.id}"/>
        <span>
            Quantity:
            <input type="number" name="quantity" value="1"/>
        </span>
        <button type="submit">Add to cart</button>
    </form>

    <div>
        Your cart:

        <c:choose>
            <c:when test="${empty productsInCart}">
                <p>Cart is empty.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <c:forEach var="productInCart" items="${productsInCart}">
                            <c:set var="product" value="${productInCart.product}" scope="page"/>
                            <td>
                                <img class="product-tile" src="${product.imageUrl}" alt="product image"/>
                                <div>
                                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                            ${product.description}
                                    </a>
                                </div>
                                <p>Quantity: ${productInCart.quantity}</p>
                            </td>
                        </c:forEach>
                    </tr>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div>
        You recently viewed:
        <c:choose>
            <c:when test="${empty viewedProducts}">
                <p>No products are recently viewed.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <tr>
                        <c:forEach var="product" items="${viewedProducts}">
                            <td>
                                <img class="product-tile" src="${product.imageUrl}" alt="product image"/>
                                <div>
                                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                            ${product.description}
                                    </a>
                                </div>
                            </td>
                        </c:forEach>
                    </tr>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

</tags:master>