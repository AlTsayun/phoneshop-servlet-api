<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="productName" type="java.lang.String" scope="request"/>
<jsp:useBean id="returnPath" type="java.lang.String" scope="request"/>
<tags:master pageTitle="Successfull">
    <p>
        Product "${productName}" is successfully added to your cart.
        <a href="${returnPath}">Go back to shopping</a>.
    </p>
</tags:master>