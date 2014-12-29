<%-- 
    Document   : nav
    Created on : 24 déc. 2014, 11:31:08
    Author     : bascool
--%>

<div>
    <c:url value="/j_spring_security_logout" var="logoutUrl" />
    <form action="${logoutUrl}" method="post" id="logoutForm">
        <input type="hidden" name="${_csrf.parameterName}"
               value="${_csrf.token}" />
    </form>
    <script>
            function formSubmit() {
                    document.getElementById("logoutForm").submit();
            }
    </script>

    <c:if test="${pageContext.request.userPrincipal.name != null}">
        <h2>
            User : ${pageContext.request.userPrincipal.name} | <a
                href="javascript:formSubmit()"> Déconnexion</a>
        </h2>
    </c:if>
    <a href="Redirect?p=a">Accueil</a>
    <a href="Redirect?p=l">Liste</a>
    <a href="Redirect?p=r">Recherche</a>
</div>
