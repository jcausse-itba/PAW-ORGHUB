<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="event.manage.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>"/>
</head>
<body>

<paw:header userFirstName="${userFirstName}"/>

<div class="container mt-4">
    <spring:message code="event.manage.title" var="manageTitle"/>
    <c:set var="headingText" value="${manageTitle}: ${event.title}"/>
    <paw:heading text="${headingText}" level="First" color="dark"/>
    
    <div class="content__detail-card">
        <div class="content__desc">
            <paw:paragraph text="${event.description}"/>
        </div>
        <div class="content__meta">
            <spring:message code="event.manage.date" var="dateLabel"/>
            <paw:paragraph text="📅 ${dateLabel} ${event.getFormattedEventDateTime(dateFormatter)}" />
            <c:if test="${not empty event.location}">
                <span class="content__meta-sep">•</span>
                <spring:message code="event.manage.location" var="locationLabel"/>
                <paw:paragraph text="📍 ${locationLabel} ${event.location}" />
            </c:if>
        </div>
    </div>

    <div class="filter-section mt-4 mb-3">
        <div class="filter-buttons">
            <c:url value="/org/${orgID}/event/${eventID}/manage" var="allUrl">
                <c:param name="status" value="all"/>
            </c:url>
            <c:url value="/org/${orgID}/event/${eventID}/manage" var="pendingUrl">
                <c:param name="status" value="pending"/>
            </c:url>
            <c:url value="/org/${orgID}/event/${eventID}/manage" var="acceptedUrl">
                <c:param name="status" value="accepted"/>
            </c:url>
            <c:url value="/org/${orgID}/event/${eventID}/manage" var="rejectedUrl">
                <c:param name="status" value="rejected"/>
            </c:url>

            <a href="${allUrl}" class="filter-btn ${statusFilter == 'all' ? 'active' : ''}">
                <spring:message code="event.manage.filters.all"/> (${totalCount})
            </a>
            <a href="${pendingUrl}" class="filter-btn ${statusFilter == 'pending' ? 'active' : ''}">
                <spring:message code="event.manage.filters.pending"/> (${pendingCount})
            </a>
            <a href="${acceptedUrl}" class="filter-btn ${statusFilter == 'accepted' ? 'active' : ''}">
                <spring:message code="event.manage.filters.accepted"/> (${acceptedCount})
            </a>
            <a href="${rejectedUrl}" class="filter-btn ${statusFilter == 'rejected' ? 'active' : ''}">
                <spring:message code="event.manage.filters.rejected"/> (${rejectedCount})
            </a>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty enrollments}">
            <div class="alert alert-info mt-3">
                        <c:choose>
                            <c:when test="${statusFilter == 'all'}">
                                <spring:message code="event.manage.no.enrollments" var="noEnrollments"/>
                                <paw:paragraph text="${noEnrollments}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'pending'}">
                                <spring:message code="event.manage.no.pending" var="noPending"/>
                                <paw:paragraph text="${noPending}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'accepted'}">
                                <spring:message code="event.manage.no.accepted" var="noAccepted"/>
                                <paw:paragraph text="${noAccepted}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'rejected'}">
                                <spring:message code="event.manage.no.rejected" var="noRejected"/>
                                <paw:paragraph text="${noRejected}" italic="true"/>
                            </c:when>
                            <c:otherwise>
                                <spring:message code="event.manage.no.enrollments" var="noEnrollmentsDefault"/>
                                <paw:paragraph text="${noEnrollmentsDefault}" italic="true"/>
                            </c:otherwise>
                        </c:choose>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover table-bordered rounded shadow-sm mt-3">
                    <thead class="thead-dark">
                    <tr>
                        <th scope="col"><spring:message code="event.manage.table.name"/></th>
                        <th scope="col"><spring:message code="event.manage.table.email"/></th>
                        <th scope="col"><spring:message code="event.manage.table.status"/></th>
                        <th scope="col" class="text-center"><spring:message code="event.manage.table.actions"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="enrollment" items="${enrollments}">
                        <tr>
                            <td><c:out value="${enrollment.user.firstName}"/> <c:out value="${enrollment.user.lastName}"/></td>
                            <td><c:out value="${enrollment.user.userEmail}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${enrollment.status.name() == 'PENDING'}">
                                        <span class="status-badge badge-warning text-uppercase"><spring:message code="event.manage.status.pending"/></span>
                                    </c:when>
                                    <c:when test="${enrollment.status.name() == 'ACCEPTED'}">
                                        <span class="status-badge badge-success text-uppercase"><spring:message code="event.manage.status.accepted"/></span>
                                    </c:when>
                                    <c:when test="${enrollment.status.name() == 'REJECTED'}">
                                        <span class="status-badge badge-danger text-uppercase"><spring:message code="event.manage.status.rejected"/></span>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <form method="post" action="<c:url value='/org/${orgID}/event/${eventID}/manage/update'/>" style="display:inline-block; margin-right: 4px;">
                                    <input type="hidden" name="userID" value="<c:out value="${enrollment.user.userID}"/>"/>
                                    <input type="hidden" name="status" value="ACCEPTED"/>
                                    <button type="submit" class="event-btn-pill btn-accept" 
                                            ${enrollment.status.name() == 'ACCEPTED' ? 'disabled' : ''}><spring:message code="event.manage.actions.accept"/></button>
                                </form>
                                <form method="post" action="<c:url value='/org/${orgID}/event/${eventID}/manage/update'/>" style="display:inline-block;">
                                    <input type="hidden" name="userID" value="<c:out value="${enrollment.user.userID}"/>"/>
                                    <input type="hidden" name="status" value="REJECTED"/>
                                    <button type="submit" class="event-btn-pill btn-reject" 
                                            ${enrollment.status.name() == 'REJECTED' ? 'disabled' : ''}><spring:message code="event.manage.actions.reject"/></button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>