<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="resourcePath" type="java.lang.String" scope="request"/>
<jsp:useBean id="parameterName" type="java.lang.String" scope="request"/>
<jsp:useBean id="parameterValue" type="java.lang.String" scope="request"/>

<tags:master pageTitle="Ooops">
    <p>
        Looks like you have entered an incorrect (${parameterValue}) value for ${parameterName} while proceeding
        request "${resourcePath}". Try to start from our
        <a href="${pageContext.servletContext.contextPath}/">homepage</a>.
    </p>
</tags:master>