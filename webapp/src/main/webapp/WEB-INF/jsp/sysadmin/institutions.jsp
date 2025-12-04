<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="sysadmin.institutions.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/header.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/components.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/views.css'/>">
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
</head>
<body>
    <paw:header userFirstName="${userFirstName}" userRole="${userRole}" hideShortcuts="true"/>
    
    <main class="main-content">
        <div class="container">
            <div class="page-header">
                <div class="page-header-content">
                    <spring:message code="sysadmin.institutions.heading" var="institutionsHeading"/>
                    <paw:heading text="${institutionsHeading}" level="1"/>
                    <spring:message code="sysadmin.institutions.description" var="institutionsDescription"/>
                    <paw:paragraph text="${institutionsDescription}" cssClass="text-muted"/>
                </div>
                <div class="page-header-actions">
                    <a href="<c:url value='/sysadmin'/>" class="btn btn-secondary">
                        <spring:message code="sysadmin.institutions.back.dashboard"/>
                    </a>
                    <a href="<c:url value='/sysadmin/institution/create'/>" class="btn btn-primary">
                        <spring:message code="sysadmin.institutions.create.institution"/>
                    </a>
                </div>
            </div>

            <div class="stats-summary">
                <div class="stat-item">
                    <span class="stat-number">${totalCount}</span>
                    <span class="stat-label"><spring:message code="sysadmin.institutions.stats.institutions"/></span>
                </div>
            </div>

            <div class="content-section">
                <c:choose>
                    <c:when test="${not empty institutions}">
                        <div class="institution-grid">
                            <c:forEach var="institution" items="${institutions}">
                                <a href="<c:url value='/sysadmin/institutions/${institution.institutionID}/organizations'/>" class="institution-card institution-card-link">
                                    <div class="institution-main">
                                        <div class="institution-header">
                                            <div class="institution-icon">🏛️</div>
                                            <div class="institution-info">
                                                <paw:heading text="${institution.institutionName}" level="4"/>
                                                <paw:paragraph text="${institution.domainName}" cssClass="text-muted"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="institution-email">
                                        <span class="email-label"><spring:message code="sysadmin.institutions.email.admin"/></span>
                                        <span class="email-value"><c:out value="${institution.adminEmail}"/></span>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">🏛️</div>
                            <spring:message code="sysadmin.institutions.empty.title" var="emptyTitle"/>
                            <paw:heading text="${emptyTitle}" level="3"/>
                            <spring:message code="sysadmin.institutions.empty.description" var="emptyDescription"/>
                            <paw:paragraph text="${emptyDescription}" cssClass="text-muted"/>
                            <a href="<c:url value='/sysadmin/institution/create'/>" class="btn btn-primary">
                                <spring:message code="sysadmin.institutions.empty.create"/>
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</body>
</html>
