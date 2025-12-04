<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><spring:message code="position.details.title"/> <c:out value="${position.title}"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>

<div class="content__detail-container">
    <div class="content__detail-card">
        <div class="content__title-container" style="display: flex; justify-content: space-between; align-items: center;">
            <paw:heading text="${position.title}" level="first" color="mid"/>
            <c:if test="${canPublish}">
                <div style="display: flex; gap: 8px; align-items: center;">
                    <c:url value="/org/${position.organization.orgID}/open-position/${position.positionID}/edit" var="editUrl"/>
                    <a href="${editUrl}">
                        <spring:message code="position.edit.button" var="editButtonText"/>
                        <paw:button text="${editButtonText}" variant="primary" size="sm"/>
                    </a>
                    <c:url value="/org/${position.organization.orgID}/open-position/${position.positionID}/delete" var="deleteUrl"/>
                    <form action="${deleteUrl}" method="post" style="margin: 0;" onsubmit="return confirm('<spring:message code="position.delete.confirm"/>');">
                        <spring:message code="position.delete.button" var="deleteButtonText"/>
                        <paw:button text="${deleteButtonText}" variant="danger" size="sm" type="submit"/>
                    </form>
                </div>
            </c:if>
        </div>

        <div class="content__desc">
            <paw:paragraph text="${position.description}"/>
        </div>

        <div class="content__meta">
            <spring:message code="position.details.requirements" var="requirementsLabel"/>
            <paw:paragraph text="📋 ${requirementsLabel} ${position.requirements}" />
            <c:if test="${not empty position.workHours}">
                <span class="content__meta-sep">•</span>
                <spring:message code="position.details.workhours" var="workhoursLabel"/>
                <paw:paragraph text="⏰ ${workhoursLabel} ${position.workHours}" />
            </c:if>
        </div>

        <div class="content__meta">
            <spring:message code="position.details.published.by" var="publishedBy"/>
            <paw:paragraph text="${publishedBy}" italic="true"/>
            <c:url value="/org/${position.organization.orgID}/profile" var="orgProfileUrl"/>
            <a href="${orgProfileUrl}" class="content__meta-author">
                <paw:paragraph text="${position.organization.orgName}" italic="true" bold="true"/>
            </a>
            <spring:message code="position.details.published.on" var="publishedOn"/>
            <paw:paragraph text="${publishedOn} ${position.getFormattedDateTime(dateFormatter)}" italic="true" cssClass="content__meta-date"/>
        </div>

        <c:if test="${not empty position.tags}">
            <div class="content__tags">
                <c:forEach var="t" items="${position.tags}">
                    <c:url var="tagLink" value="/home">
                        <c:param name="tag" value="${t.tagID}"/>
                        <c:param name="type" value="openpositions"/>
                    </c:url>
                    <a href="${tagLink}" class="badge">
                        <c:out value="${t.tagName}"/>
                    </a>
                </c:forEach>
            </div>
        </c:if>

        <div class="content__footer">
            <div class="content__footer-right">
                <c:if test="${userCanManageOpenPosition}">
                    <c:url value="/org/${position.organization.orgID}/open-position/${position.positionID}/manage" var="manageUrl"/>
                    <form action="${manageUrl}" method="get" style="display: inline; margin-right: 10px;">
                        <spring:message code="position.details.view.applicants" var="viewApplicants"/>
                        <paw:button text="${viewApplicants}" variant="primary" size="md" type="submit"/>
                    </form>
                </c:if>
                
                <c:choose>
                    <c:when test="${not empty status}">
                        <div class="event__status-badge ${status}">
                            <spring:message code="position.details.status.label"/>
                            <c:choose>
                                <c:when test="${status eq 'ACCEPTED'}"><spring:message code="position.details.status.accepted"/></c:when>
                                <c:when test="${status eq 'PENDING'}"><spring:message code="position.details.status.pending"/></c:when>
                                <c:when test="${status eq 'REJECTED'}"><spring:message code="position.details.status.rejected"/></c:when>
                            </c:choose>
                        </div>
                        <c:if test="${(status eq 'PENDING' || status eq 'ACCEPTED') && position.status ne 'CLOSED'}">
                            <c:url value="/org/${position.organization.orgID}/open-position/${position.positionID}/withdraw" var="withdrawUrl"/>
                            <form action="${withdrawUrl}" method="post" style="display: inline;">
                                <spring:message code="position.details.action.withdraw" var="withdrawText"/>
                                <paw:button text="${withdrawText}" variant="secondary" size="md" type="submit"/>
                            </form>
                        </c:if>
                        <c:if test="${position.status eq 'CLOSED'}">
                            <div class="position-closed-notice">
                                <spring:message code="position.closed.notice"/>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${position.status eq 'CLOSED'}">
                                <div class="position-closed-notice">
                                    <spring:message code="position.closed.notice"/>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:url value="/org/${position.organization.orgID}/open-position/${position.positionID}/apply" var="applyUrl"/>
                                <form action="${applyUrl}" method="post" style="display: inline;">
                                    <spring:message code="position.details.action.apply" var="applyText"/>
                                    <paw:button text="${applyText}" variant="primary" size="md" type="submit"/>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

</body>
</html>
