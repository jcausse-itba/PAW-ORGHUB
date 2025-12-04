<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><spring:message code="event.details.title"/> <c:out value="${event.title}"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <script>
        window.i18n = {
            validation: {
                comment: {
                    required: '<spring:message code="validation.comment.required" javaScriptEscape="true"/>',
                    min: '<spring:message code="validation.comment.min" javaScriptEscape="true"/>',
                    max: '<spring:message code="validation.comment.max" javaScriptEscape="true"/>'
                }
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/postDetails.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>

<div class="content__detail-container">
    <div class="content__detail-card">
        <div class="content__title-container" style="display: flex; justify-content: space-between; align-items: center;">
            <paw:heading text="${event.title}" level="first" color="mid"/>
            <c:if test="${canPublish}">
                <div style="display: flex; gap: 8px;">
                    <c:url value="/org/${event.organization.orgID}/event/${event.eventID}/edit" var="editUrl"/>
                    <a href="${editUrl}">
                        <spring:message code="event.edit.button" var="editButtonText"/>
                        <paw:button text="${editButtonText}" variant="primary" size="sm"/>
                    </a>
                    <c:url value="/org/${event.organization.orgID}/event/${event.eventID}/delete" var="deleteUrl"/>
                    <form action="${deleteUrl}" method="post" style="margin: 0;" onsubmit="return confirm('<spring:message code="event.delete.confirm"/>');">
                        <spring:message code="event.delete.button" var="deleteButtonText"/>
                        <paw:button text="${deleteButtonText}" variant="danger" size="sm" type="submit"/>
                    </form>
                </div>
            </c:if>
        </div>

        <div class="content__desc">
            <paw:paragraph text="${event.description}"/>
        </div>

        <c:if test="${not empty event.image}">
            <div class="content__image-container" style="margin-bottom: 1rem;">
                <c:url value="/image/${event.image.imageID}" var="imageUrl"/>
                <img src="${imageUrl}" alt="${event.title}" class="content__image"/>
            </div>
        </c:if>

        <div class="content__meta">
            <spring:message code="event.details.location" var="locationLabel"/>
            <paw:paragraph text="📍 ${locationLabel} ${location}" />
            <span class="content__meta-sep">•</span>
            <spring:message code="event.details.date" var="dateLabel"/>
            <paw:paragraph text="📅 ${dateLabel} ${eventDate}" />
        </div>

        <c:if test="${not empty event.tags}">
            <div class="content__tags">
                <c:forEach var="t" items="${event.tags}">
                    <c:url var="tagLink" value="/home">
                        <c:param name="tag" value="${t.tagID}"/>
                        <c:param name="type" value="events"/>
                    </c:url>
                    <a href="${tagLink}" class="badge">
                        <c:out value="${t.tagName}"/>
                    </a>
                </c:forEach>
            </div>
        </c:if>

        <div class="content__meta">
            <spring:message code="event.details.published.by" var="publishedBy"/>
            <paw:paragraph text="${publishedBy}" italic="true"/>
            <c:url value="/org/${event.organization.orgID}/profile" var="orgProfileUrl"/>
            <a href="${orgProfileUrl}" class="content__meta-author">
                <paw:paragraph text="${event.organization.orgName}" italic="true" bold="true"/>
            </a>
            <spring:message code="event.details.published.on" var="publishedOn"/>
            <paw:paragraph text="${publishedOn} ${event.getFormattedDateTime(dateFormatter)}" italic="true" cssClass="content__meta-date"/>
        </div>

        <div class="content__footer">
            <div class="content__footer-left">
            </div>
            <div class="content__footer-right">
                <c:if test="${userCanManageEvents}">
                    <c:url value="/org/${event.organization.orgID}/event/${event.eventID}/manage" var="manageUrl"/>
                    <form action="${manageUrl}" method="get" style="display: inline; margin-right: 10px;">
                        <spring:message code="event.details.view.enrollments" var="viewEnrollments"/>
                        <paw:button text="${viewEnrollments}" variant="primary" size="md" type="submit"/>
                    </form>
                </c:if>
                
                <c:choose>
                    <c:when test="${event.isFinished()}">
                        <div class="event__status-badge closed">
                            <spring:message code="event.details.status.finished"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${not empty status}">
                                <div class="event__status-badge ${status}">
                                    <spring:message code="event.details.status.label"/>
                                    <c:choose>
                                        <c:when test="${status eq 'ACCEPTED'}"><spring:message code="event.details.status.accepted"/></c:when>
                                        <c:when test="${status eq 'PENDING'}"><spring:message code="event.details.status.pending"/></c:when>
                                        <c:when test="${status eq 'REJECTED'}"><spring:message code="event.details.status.rejected"/></c:when>
                                    </c:choose>
                                </div>
                                <c:if test="${status eq 'ACCEPTED' || status eq 'PENDING'}">
                                    <c:url value="/event/${event.eventID}/unenroll" var="unenrollUrl"/>
                                    <form action="${unenrollUrl}" method="post" style="display: inline;">
                                        <spring:message code="event.details.action.unenroll" var="unenrollText"/>
                                        <paw:button text="${unenrollText}" variant="secondary" size="md" type="submit"/>
                                    </form>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <c:url value="/event/${event.eventID}/enroll" var="enrollUrl"/>
                                <form action="${enrollUrl}" method="post" style="display: inline;">
                                    <spring:message code="event.details.action.enroll" var="enrollText"/>
                                    <paw:button text="${enrollText}" variant="primary" size="md" type="submit"/>
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
