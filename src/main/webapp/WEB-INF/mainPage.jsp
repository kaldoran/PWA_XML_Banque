<%-- 
    Document   : mainPage
    Created on : 24 déc. 2014, 11:29:40
    Author     : bascool
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="includes/header.jsp"/>

<sec:authorize access="hasRole('ROLE_USER')">
        <h1>Hello World!</h1>
</sec:authorize>

<jsp:include page="includes/footer.jsp"/>