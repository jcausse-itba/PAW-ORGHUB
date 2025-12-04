<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="organigram.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/views.css'/>">
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>
<div class="organigram-container">
        <div class="organigram-header">
            <spring:message code="organigram.title.of" var="organigramTitleOf"/>
            <c:set var="fullTitle" value="${organigramTitleOf} ${organizationName}"/>
            <paw:heading text="${fullTitle}" level="First" color="dark"/>
            
            <c:if test="${userCanManageOrganization}">
                <div class="organigram-actions">
                    <c:url value="/org/${orgID}/organigram/create" var="createUrl"/>
                    <a href="${createUrl}">
                        <spring:message code="organigram.add.position" var="addPositionText"/>
                        <paw:button text="${addPositionText}" variant="primary"/>
                    </a>
                </div>
            </c:if>
        </div>
        
        <c:if test="${empty organigram}">
            <spring:message code="organigram.empty" var="emptyMessage"/>
            <paw:paragraph text="${emptyMessage}" cssClass="organigram-empty"/>
        </c:if>
        
        <c:if test="${not empty organigram}">
            <div class="organigram-tree">
                <c:set var="rootPositions" value="${organigram.stream().filter(p -> p.level == 0).sorted((a,b) -> a.sortOrder.compareTo(b.sortOrder)).toList()}" />
                <c:forEach var="rootPosition" items="${rootPositions}">
                    <c:set var="position" value="${rootPosition}" scope="request"/>
                    <jsp:include page="organigram-position.jsp"/>
                </c:forEach>
            </div>
        </c:if>
</div>

</body>
</html>
