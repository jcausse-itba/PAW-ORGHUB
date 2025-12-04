<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="nav.organizations"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>

<main class="container">
    <spring:message code="nav.organizations" var="organizationsTitle"/>
    <paw:heading text="${organizationsTitle}" level="First" color="dark"/>

    <div class="organizations-grid">
        <c:choose>
            <c:when test="${empty organizations}">
                <div class="no-organizations-message">
                    <spring:message code="org.no.organizations" var="noOrganizations"/>
                    <paw:paragraph text="${noOrganizations}"/>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${organizations}" var="org">
                    <div class="org-card">
                        <div class="org-card-header">
                            <a href="<c:url value='/org/${org.orgID}/profile'/>" class="org-name-link">
                                <paw:heading text="${org.orgName}" level="Second" color="mid"/>
                            </a>
                        </div>
                        <div class="org-card-content">
                            <div class="org-description">
                                <paw:paragraph text="${org.description}"/>
                            </div>
                            <div class="org-card-actions">
                                <c:choose>
                                    <c:when test="${userService.isSubscribed(currentUser.userID, org.orgID)}">
                                        <form action="<c:url value='/org/${org.orgID}/unsubscribe'/>" method="post" class="org-subscribe-form">
                                            <spring:message code="org.unsubscribe" var="unsubscribeText"/>
                                            <paw:button text="${unsubscribeText}" variant="secondary" size="sm" type="submit"/>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="<c:url value='/org/${org.orgID}/subscribe'/>" method="post" class="org-subscribe-form">
                                            <spring:message code="org.subscribe" var="subscribeText"/>
                                            <paw:button text="${subscribeText}" variant="primary" size="sm" type="submit"/>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</main>
</body>
</html>
