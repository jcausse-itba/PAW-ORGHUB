<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<c:forEach var="position" items="${organigram}">
    <c:if test="${position.parentPosition != null && position.parentPosition.organigramID == currentPosition.organigramID}">
        <div class="organigram-level organigram-level-${position.level}">
            <div class="organigram-position">
                <paw:heading text="${position.positionName}" level="Third" color="dark"/>
                <paw:paragraph text="${position.description}" cssClass="position-description"/>
                <c:if test="${not empty position.assignedUser}">
                    <div class="position-assigned-user">
                        <strong>👤 <c:out value="${position.assignedUser.userEmail}"/></strong>
                    </div>
                </c:if>
                <c:if test="${empty position.assignedUser}">
                    <div class="position-vacant">
                        <span class="vacant-badge">📄 <spring:message code="organigram.position.vacant"/></span>
                    </div>
                </c:if>
            </div>
            <c:set var="currentPosition" value="${position}" scope="request"/>
            <jsp:include page="organigram-children.jsp"/>
        </div>
    </c:if>
</c:forEach>
