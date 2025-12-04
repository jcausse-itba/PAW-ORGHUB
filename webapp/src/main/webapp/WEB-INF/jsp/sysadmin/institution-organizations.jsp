<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="sysadmin.institution.orgs.title"/> <c:out value="${institution.institutionName}"/> - OrgHub</title>
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
                    <div class="breadcrumb">
                        <a href="<c:url value='/sysadmin'/>" class="breadcrumb-link"><spring:message code="sysadmin.institution.orgs.breadcrumb.dashboard"/></a>
                        <span class="breadcrumb-separator">›</span>
                        <a href="<c:url value='/sysadmin/institutions'/>" class="breadcrumb-link"><spring:message code="sysadmin.institution.orgs.breadcrumb.institutions"/></a>
                        <span class="breadcrumb-separator">›</span>
                        <span class="breadcrumb-current"><c:out value="${institution.institutionName}"/></span>
                    </div>
                    <spring:message code="sysadmin.institution.orgs.heading" var="orgsHeading"/>
                    <paw:heading text="${orgsHeading} ${institution.institutionName}" level="1"/>
                    <spring:message code="sysadmin.institution.orgs.description" var="orgsDescription"/>
                    <paw:paragraph text="${orgsDescription}" cssClass="text-muted"/>
                </div>
                <div class="page-header-actions">
                    <a href="<c:url value='/sysadmin/institutions'/>" class="btn btn-secondary">
                        <spring:message code="sysadmin.institution.orgs.back.institutions"/>
                    </a>
                </div>
            </div>

            <div class="institution-info-card">
                <div class="institution-main">
                    <div class="institution-header">
                        <div class="institution-icon">🏛️</div>
                        <div class="institution-details">
                            <paw:heading text="${institution.institutionName}" level="3"/>
                            <paw:paragraph text="${institution.domainName}" cssClass="text-muted"/>
                            <spring:message code="sysadmin.institution.orgs.admin" var="adminLabel"/>
                            <paw:paragraph text="${adminLabel} ${institution.adminEmail}" cssClass="text-muted"/>
                        </div>
                    </div>
                    <div class="institution-count">
                        <span class="count-number">${totalCount}</span>
                        <span class="count-label"><spring:message code="sysadmin.institution.orgs.count.organizations"/></span>
                    </div>
                </div>
            </div>

            <div class="content-section">
                <c:choose>
                    <c:when test="${not empty organizations}">
                        <div class="organization-grid">
                            <c:forEach var="organization" items="${organizations}">
                                <a href="<c:url value='/org/${organization.orgID}/profile'/>" class="organization-card organization-card-link">
                                    <div class="organization-header">
                                        <div class="organization-icon">🏢</div>
                                        <div class="organization-info">
                                            <paw:heading text="${organization.orgName}" level="4"/>
                                            <paw:paragraph text="${organization.description}" cssClass="text-muted"/>
                                        </div>
                                    </div>
                                    
                                    <div class="organization-details">
                                        <div class="detail-item">
                                            <span class="detail-label"><spring:message code="sysadmin.institution.orgs.detail.email"/></span>
                                            <span class="detail-value"><c:out value="${organization.orgEmail}"/></span>
                                        </div>
                                        <div class="detail-item">
                                            <span class="detail-label"><spring:message code="sysadmin.institution.orgs.detail.description"/></span>
                                            <span class="detail-value"><c:out value="${organization.description}"/></span>
                                        </div>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">🏢</div>
                            <spring:message code="sysadmin.institution.orgs.empty.title" var="emptyTitle"/>
                            <paw:heading text="${emptyTitle}" level="3"/>
                            <spring:message code="sysadmin.institution.orgs.empty.description" var="emptyDescription"/>
                            <paw:paragraph text="${emptyDescription}" cssClass="text-muted"/>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>
</body>
</html>
