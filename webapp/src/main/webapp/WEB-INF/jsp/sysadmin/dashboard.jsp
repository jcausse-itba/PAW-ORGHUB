<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="sysadmin.dashboard.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/header.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/components.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/views.css'/>">
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
</head>
<body>
    <paw:header userFirstName="${userFirstName}" userRole="${userRole}" hideShortcuts="true"/>
    
    <main class="main-content">
        <div class="container">
            <div class="dashboard-header">
                <spring:message code="sysadmin.dashboard.heading" var="dashboardHeading"/>
                <paw:heading text="${dashboardHeading}" level="1"/>
                <spring:message code="sysadmin.dashboard.description" var="dashboardDescription"/>
                <paw:paragraph text="${dashboardDescription}" cssClass="text-muted"/>
            </div>

            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">🏛️</div>
                    <div class="stat-content">
                        <paw:heading text="${totalInstitutions}" level="3" cssClass="stat-number"/>
                        <spring:message code="sysadmin.dashboard.stats.institutions" var="institutionsLabel"/>
                        <paw:paragraph text="${institutionsLabel}" cssClass="stat-label"/>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">🏢</div>
                    <div class="stat-content">
                        <paw:heading text="${totalOrganizations}" level="3" cssClass="stat-number"/>
                        <spring:message code="sysadmin.dashboard.stats.organizations" var="organizationsLabel"/>
                        <paw:paragraph text="${organizationsLabel}" cssClass="stat-label"/>
                    </div>
                </div>
                
                <div class="stat-card stat-card--warning">
                    <div class="stat-icon">⏳</div>
                    <div class="stat-content">
                        <paw:heading text="${pendingRequests}" level="3" cssClass="stat-number"/>
                        <spring:message code="sysadmin.dashboard.stats.pending" var="pendingLabel"/>
                        <paw:paragraph text="${pendingLabel}" cssClass="stat-label"/>
                    </div>
                </div>
                
                <div class="stat-card stat-card--success">
                    <div class="stat-icon">✅</div>
                    <div class="stat-content">
                        <paw:heading text="${acceptedRequests}" level="3" cssClass="stat-number"/>
                        <spring:message code="sysadmin.dashboard.stats.accepted" var="acceptedLabel"/>
                        <paw:paragraph text="${acceptedLabel}" cssClass="stat-label"/>
                    </div>
                </div>
            </div>

            <div class="quick-actions">
                <spring:message code="sysadmin.dashboard.actions.title" var="actionsTitle"/>
                <paw:heading text="${actionsTitle}" level="2"/>
                <div class="action-buttons">
                    <a href="<c:url value='/sysadmin/institutions'/>" class="action-btn">
                        <span class="action-icon">🏛️</span>
                        <span class="action-text"><spring:message code="sysadmin.dashboard.actions.manage.institutions"/></span>
                    </a>
                    
                    <a href="<c:url value='/sysadmin/partnership'/>" class="action-btn">
                        <span class="action-icon">🤝</span>
                        <span class="action-text"><spring:message code="sysadmin.dashboard.actions.manage.requests"/></span>
                    </a>
                    
                    <a href="<c:url value='/sysadmin/institution/create'/>" class="action-btn">
                        <span class="action-icon">➕</span>
                        <span class="action-text"><spring:message code="sysadmin.dashboard.actions.create.institution"/></span>
                    </a>
                </div>
            </div>

        </div>
    </main>
</body>
</html>
