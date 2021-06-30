<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:catch var="productsNotSetException">
    <jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
</c:catch>
<tags:master pageTitle="Advanced search">
    <form>
        <table>
            <tr>
                <td>
                    Description
                </td>
                <td>
                    <input name="searchQuery" value="${param.searchQuery}"/>
                </td>
                <td>
                    <select name="searchQueryType">
                        <option value="all_words" ${param.searchQueryType.equals("all_words") ? "selected" : ""}>all
                            words
                        </option>
                        <option value="any_word" ${param.searchQueryType.equals("any_word") ? "selected" : ""}>any
                            word
                        </option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    Min price
                </td>
                <td>
                    <input name="minPrice" value="${param.minPrice}"/>
                </td>
                <td></td>
            </tr>
            <tr>
                <td>
                    Max price
                </td>
                <td>
                    <input name="maxPrice" value="${param.maxPrice}"/>
                </td>
                <td></td>
            </tr>
        </table>
        <button type="submit">Search</button>
    </form>
    <c:if test="${empty productsNotSetException}">
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
                    Add to cart
                </td>
            </tr>
            </thead>
            <c:forEach var="product" items="${products}">
                <tr>
                    <td>
                        <img class="product-tile" src="${product.imageUrl}" alt="product image">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                ${product.description}
                        </a>
                    </td>
                    <td class="price">
                        <c:set var="price" value="${product.getActualPrice()}" scope="application"/>
                        <a href="${pageContext.servletContext.contextPath}/product-prices-history/${product.id}">
                            <fmt:formatNumber value="${price.value}" type="currency"
                                              currencySymbol="${price.currency.symbol}"/>
                        </a>
                    </td>
                    <td>
                        <form method="post" action="${pageContext.servletContext.contextPath}/cart/add">
                            <input type="hidden" name="productId" value="${product.id}"/>
                            <div>
                                Quantity:
                                <input type="number" name="quantity" value="1"/>
                                <button type="submit">Add to cart</button>
                            </div>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</tags:master>