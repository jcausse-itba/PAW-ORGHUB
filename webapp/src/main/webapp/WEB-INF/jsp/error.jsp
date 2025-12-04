<%@ page contentType="text/html; charset=UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><spring:message code="error.title"/> <c:out value="${errorCode != null ? errorCode : '500'}"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
</head>
<body>
<paw:header hideSideMenu="true" hideShortcuts="true" hideLoginButton="true"/>

<main class="container">
    <section>

        <spring:message code="error.generic" var="errorGeneric"/>
        <paw:heading text="${errorGeneric} ${errorCode != null ? errorCode : ''}" level="first" color="dark" />

        <!-- Mensaje principal desde el controller -->
        <c:if test="${not empty errorMessage}">
            <paw:paragraph text="${errorMessage}" cssClass="error-main-message"/>
        </c:if>

        <!-- Detalles tecnicos (si estan disponibles) -->
        <c:if test="${not empty errorDetails}">
            <div>
                <spring:message code="error.technical.details" var="technicalDetails"/>
                <paw:paragraph text="${technicalDetails}: ${errorDetails}" italic="true" />
            </div>
        </c:if>

        <!-- Botones de accion -->
        <div>
            <spring:message code="error.back.home" var="backHome"/>
            <paw:button text="${backHome}"
                        variant="primary"
                        size="lg"
                        onclick="window.location.href='${pageContext.request.contextPath}/home'" />

            <c:if test="${errorCode != '404'}">
                <spring:message code="error.try.again" var="tryAgain"/>
                <paw:button text="${tryAgain}"
                            variant="secondary"
                            size="lg"
                            onclick="window.location.reload()"/>
            </c:if>
        </div>
    </section>
</main>
</body>
</html>
