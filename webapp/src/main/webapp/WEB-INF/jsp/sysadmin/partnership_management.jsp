<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="sysadmin.partnership.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/views.css'/>">
</head>
<body>
<paw:header userFirstName="${userFirstName}" userRole="${userRole}" hideShortcuts="true"/>
<div class="container mt-4">
    <div class="row">
        <div class="col-12">
            <spring:message code="sysadmin.partnership.title" var="partnershipTitle"/>
            <paw:heading text="${partnershipTitle}" level="First" color="dark"/>
            <spring:message code="sysadmin.partnership.description" var="partnershipDescription"/>
            <paw:paragraph text="${partnershipDescription}" cssClass="text-muted" italic="true"/>
        </div>
    </div>

    <div class="row mt-4">
        <div class="col-12">
            <div class="filter-section">
                <div class="filter-buttons">
                    <c:url value="/sysadmin/partnership" var="allUrl">
                        <c:param name="status" value="all"/>
                    </c:url>
                    <c:url value="/sysadmin/partnership" var="pendingUrl">
                        <c:param name="status" value="pending"/>
                    </c:url>
                    <c:url value="/sysadmin/partnership" var="acceptedUrl">
                        <c:param name="status" value="accepted"/>
                    </c:url>
                    <c:url value="/sysadmin/partnership" var="rejectedUrl">
                        <c:param name="status" value="rejected"/>
                    </c:url>
                    
                    <a href="${allUrl}" class="filter-btn ${statusFilter == 'all' ? 'active' : ''}">
                        <spring:message code="sysadmin.partnership.filter.all"/> (${totalCount})
                    </a>
                    <a href="${pendingUrl}" class="filter-btn ${statusFilter == 'pending' ? 'active' : ''}">
                        <spring:message code="sysadmin.partnership.filter.pending"/> (${pendingCount})
                    </a>
                    <a href="${acceptedUrl}" class="filter-btn ${statusFilter == 'accepted' ? 'active' : ''}">
                        <spring:message code="sysadmin.partnership.filter.accepted"/> (${acceptedCount})
                    </a>
                    <a href="${rejectedUrl}" class="filter-btn ${statusFilter == 'rejected' ? 'active' : ''}">
                        <spring:message code="sysadmin.partnership.filter.rejected"/> (${rejectedCount})
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="row mt-4">
        <div class="col-12">
            <c:choose>
                <c:when test="${empty requests}">
                    <div class="alert alert-info mt-3">
                        <c:choose>
                            <c:when test="${statusFilter == 'all'}">
                                <spring:message code="sysadmin.partnership.empty.all" var="emptyAll"/>
                                <paw:paragraph text="${emptyAll}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'pending'}">
                                <spring:message code="sysadmin.partnership.empty.pending" var="emptyPending"/>
                                <paw:paragraph text="${emptyPending}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'accepted'}">
                                <spring:message code="sysadmin.partnership.empty.accepted" var="emptyAccepted"/>
                                <paw:paragraph text="${emptyAccepted}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'rejected'}">
                                <spring:message code="sysadmin.partnership.empty.rejected" var="emptyRejected"/>
                                <paw:paragraph text="${emptyRejected}" italic="true"/>
                            </c:when>
                            <c:otherwise>
                                <spring:message code="sysadmin.partnership.empty.all" var="emptyAllDefault"/>
                                <paw:paragraph text="${emptyAllDefault}" italic="true"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table table-hover table-bordered rounded shadow-sm mt-3">
                            <thead class="thead-dark">
                            <tr>
                                <th scope="col"><spring:message code="sysadmin.partnership.table.institution"/></th>
                                <th scope="col"><spring:message code="sysadmin.partnership.table.domain"/></th>
                                <th scope="col"><spring:message code="sysadmin.partnership.table.admin.email"/></th>
                                <th scope="col"><spring:message code="sysadmin.partnership.table.status"/></th>
                                <th scope="col"><spring:message code="sysadmin.partnership.table.request.date"/></th>
                                <th scope="col" class="text-center"><spring:message code="sysadmin.partnership.table.actions"/></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="request" items="${requests}">
                                <tr>
                                    <td><c:out value="${request.institutionName}"/></td>
                                    <td><c:out value="${request.domainName}"/></td>
                                    <td><c:out value="${request.adminEmail}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${request.status.name() == 'PENDING'}">
                                                <span class="status-badge badge-warning text-uppercase"><spring:message code="sysadmin.partnership.status.pending"/></span>
                                            </c:when>
                                            <c:when test="${request.status.name() == 'ACCEPTED'}">
                                                <span class="status-badge badge-success text-uppercase"><spring:message code="sysadmin.partnership.status.accepted"/></span>
                                            </c:when>
                                            <c:when test="${request.status.name() == 'REJECTED'}">
                                                <span class="status-badge badge-danger text-uppercase"><spring:message code="sysadmin.partnership.status.rejected"/></span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:out value="${request.getFormattedRequestDate(dateFormatter)}"/>
                                    </td>
                                    <td class="text-center">
                                        <c:if test="${request.status.name() == 'PENDING'}">
                                            <form method="post" action="<c:url value='/sysadmin/partnership/${request.requestID}/approve'/>" style="display:inline-block; margin-right: 4px;">
                                                <button type="submit" class="event-btn-pill btn-accept"><spring:message code="sysadmin.partnership.action.approve"/></button>
                                            </form>
                                            <form method="post" action="<c:url value='/sysadmin/partnership/${request.requestID}/reject'/>" style="display:inline-block;">
                                                <button type="submit" class="event-btn-pill btn-reject"><spring:message code="sysadmin.partnership.action.reject"/></button>
                                            </form>
                                        </c:if>
                                        <c:if test="${request.status.name() != 'PENDING'}">
                                            <span class="text-muted"><spring:message code="sysadmin.partnership.action.processed"/></span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
</body>
</html>
