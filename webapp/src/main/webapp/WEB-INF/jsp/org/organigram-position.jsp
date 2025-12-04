<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:set var="children" value="${organigram.stream().filter(p -> p.parentPosition != null && p.parentPosition.organigramID == position.organigramID).sorted((a,b) -> a.sortOrder.compareTo(b.sortOrder)).toList()}" />
<div class="organigram-position-container ${not empty children ? 'has-children' : ''}">
    <div class="organigram-position organigram-level-${position.level}">
        <paw:heading text="${position.positionName}" level="Third" color="dark"/>
        <paw:paragraph text="${position.description}" cssClass="position-description"/>
        <c:if test="${not empty position.assignedUser}">
            <div class="position-assigned-user">
                <strong>👤 <c:out value="${position.assignedUser.userEmail}"/></strong>
            </div>
        </c:if>
        <c:if test="${empty position.assignedUser}">
            <div class="position-vacant">
                <c:set var="openPositionID" value="${organigramToOpenPositionMap[position.organigramID]}"/>
                <c:choose>
                    <c:when test="${not empty openPositionID and not isSystemAdministrator}">
                        <c:url value="/org/${orgID}/open-position/${openPositionID}" var="openPositionUrl"/>
                        <a href="${openPositionUrl}" class="vacant-badge-link">
                            <span class="vacant-badge">📄 <spring:message code="organigram.position.vacant"/></span>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <span class="vacant-badge">📄 <spring:message code="organigram.position.vacant"/></span>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>
    
    <c:if test="${not empty children}">
        <div class="organigram-children ${children.size() == 1 ? 'single-child' : ''}">
            <c:forEach var="child" items="${children}">
                <c:set var="position" value="${child}" scope="request"/>
                <jsp:include page="organigram-position.jsp"/>
            </c:forEach>
        </div>
    </c:if>
</div>
