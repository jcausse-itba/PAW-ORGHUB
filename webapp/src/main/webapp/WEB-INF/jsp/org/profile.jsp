<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><c:out value="${org.orgName}"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <script>
        window.i18n = {
            validation: {
                comment: {
                    empty: '<spring:message code="validation.comment.empty" javaScriptEscape="true"/>',
                    min: '<spring:message code="validation.comment.min" javaScriptEscape="true"/>',
                    max: '<spring:message code="validation.comment.max" javaScriptEscape="true"/>'
                }
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/modal.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/tabs.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}" userRole="${userRole}"/>
<div class="container">
    <div class="profile-card">
        <div class="profile-header">
            <div class="avatar">
                <c:set var="orgInitials" value="${fn:toUpperCase(fn:substring(org.orgName,0,1))}${fn:toUpperCase(fn:substring(org.orgName,1,2))}"/>
                <c:out value="${orgInitials}"/>
            </div>
            <div>
                <p class="profile-name">
                    <c:out value="${org.orgName}"/>
                </p>
                <c:if test="${not empty institutionName}">
                    <p class="muted"><c:out value="${institutionName}"/></p>
                </c:if>
                <p class="muted">
                    <c:choose>
                        <c:when test="${userIsOrganizationManager}"><spring:message code="org.manager.desc"/></c:when>
                        <c:when test="${userIsOrganizationPublisher}"><spring:message code="org.publisher.desc"/></c:when>
                    </c:choose>
                </p>
            </div>
            <div class="profile-actions">
                <c:choose>
                    <c:when test="${isSubscribed}">
                        <form action="<c:url value='/org/${orgID}/unsubscribe'/>" method="post">
                            <spring:message code="org.unsubscribe" var="unsubscribeText"/>
                            <paw:button
                                    text="${unsubscribeText}"
                                    variant="secondary"
                                    size="md"
                                    cssClass="btn-pill"
                                    type="submit"
                                    disabled="${isSystemAdmin}"/>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form action="<c:url value='/org/${orgID}/subscribe'/>" method="post">
                            <spring:message code="org.subscribe" var="subscribeText"/>
                            <paw:button
                                    text="${subscribeText}"
                                    variant="primary"
                                    size="md"
                                    cssClass="btn-pill"
                                    type="submit"
                                    disabled="${isSystemAdmin}"/>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="info-grid-org">
            <div class="info-item">
                <div class="info-value"><c:out value="${not empty org.description ? org.description : '—'}"/></div>
            </div>
            
            <div class="info-item">
                <span class="info-label"><spring:message code="org.organigram.label"/></span>
                <div class="info-value">
                    <c:url value="/org/${orgID}/organigram" var="organigramUrl"/>
                    <a href="${organigramUrl}" class="organigram-link">
                        <spring:message code="org.organigram.view"/>
                    </a>
                </div>
            </div>
            <c:if test="${not empty org.socialLink1 || not empty org.socialLink2 || not empty org.orgEmail}">
                <div class="info-item">
                    <span class="info-label"><spring:message code="org.contact.label"/></span>
                    <div class="info-value" style="display: flex; gap: 12px; align-items: center;">
                        <c:if test="${not empty org.orgEmail}">
                            <a href="mailto:<c:out value='${org.orgEmail}'/>" style="font-size: 20px; text-decoration: none;">📧</a>
                        </c:if>
                        <c:if test="${not empty org.socialLink1}">
                            <a href="<c:out value='${org.socialLink1}'/>" target="_blank" rel="noopener" style="font-size: 20px; text-decoration: none;">📷</a>
                        </c:if>
                        <c:if test="${not empty org.socialLink2}">
                            <a href="<c:out value='${org.socialLink2}'/>" target="_blank" rel="noopener" style="font-size: 20px; text-decoration: none;">🐦</a>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>
    </div>

    <div class="hr"></div>

    <div class="tabs-container" data-storage-key="activeTab_userProfile">
        <div class="tabs-nav">
            <button class="tab-button active" data-tab="posts"><spring:message code="home.posts"/></button>
            <button class="tab-button" data-tab="events"><spring:message code="home.events"/></button>
            <button class="tab-button" data-tab="openPositions"><spring:message code="home.openpositions"/></button>
        </div>

        <div class="tab-content-container">
            <div id="posts" class="tab-content active">
                <c:if test="${userCanPublish}">
                    <form action="<c:url value='/org/${orgID}/post/create'/>" method="get" style="margin-bottom: 1rem;">
                        <spring:message code="org.create.post" var="createPostText"/>
                        <paw:button text="${createPostText}" variant="primary" size="md" type="submit"/>
                    </form>
                </c:if>
                <c:choose>
                    <c:when test="${empty posts}">
                        <spring:message code="org.no.posts" var="noPosts"/>
                        <paw:paragraph text="${noPosts}" italic="true"/>
                    </c:when>
                    <c:otherwise>
                        <div class="events-grid">
                            <c:forEach var="p" items="${posts}">
                                <paw:post
                                        title="${p.title}"
                                        description="${p.body}"
                                        author="${p.organization.orgName}"
                                        authorID="${p.organization.orgID}"
                                        date="${p.getFormattedDateTime(dateFormatter)}"
                                        tags="${p.tags}"
                                        likeCount="${p.likeCount}"
                                        commentCount="${p.commentCount}"
                                        showFooter="true"
                                        postID="${p.postID}"
                                        order="${sort}"
                                        liked="${likedPostsIDs != null && likedPostsIDs.contains(p.postID)}"
                                        disableInteractions="${isSystemAdmin}">
                                </paw:post>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
                <c:if test="${hasPreviousOrgPostsPage or hasNextOrgPostsPage or fn:length(posts) > 0}">
                    <c:set var="prevOrgPostsPage" value="${orgPostCurrentPage - 1}"/>
                    <c:set var="nextOrgPostsPage" value="${orgPostCurrentPage + 1}"/>
                    <c:url var="prevOrgPostsUrl" value="/org/${org.orgID}/profile">
                        <c:param name="postPage" value="${prevOrgPostsPage}"/>
                    </c:url>
                    <c:url var="nextOrgPostsUrl" value="/org/${org.orgID}/profile">
                        <c:param name="postPage" value="${nextOrgPostsPage}"/>
                    </c:url>
                    <spring:message code="home.pagination.previous" var="orgPostsPrevLabel"/>
                    <spring:message code="home.pagination.next" var="orgPostsNextLabel"/>
                    <spring:message code="home.pagination.page" var="orgPostsPageLabel"/>
                    <div class="pagination-controls">
                        <paw:button
                                text="${orgPostsPrevLabel}"
                                variant="secondary"
                                size="md"
                                cssClass="pagination-button"
                                disabled="${not hasPreviousOrgPostsPage}"
                                onclick="window.location.href='${prevOrgPostsUrl}#posts'"/>
                        <div class="pagination-info">
                            <span>${orgPostsPageLabel} ${orgPostCurrentPage}</span>
                        </div>
                        <paw:button
                                text="${orgPostsNextLabel}"
                                variant="primary"
                                size="md"
                                cssClass="pagination-button"
                                disabled="${not hasNextOrgPostsPage}"
                                onclick="window.location.href='${nextOrgPostsUrl}#posts'"/>
                    </div>
                </c:if>
            </div>

            <div id="events" class="tab-content">
                <c:if test="${userCanPublish}">
                    <form action="<c:url value='/org/${orgID}/event/create'/>" method="get" style="margin-bottom: 1rem;">
                        <spring:message code="org.create.event" var="createEventText"/>
                        <paw:button text="${createEventText}" variant="primary" size="md" type="submit"/>
                    </form>
                </c:if>
                <c:choose>
                    <c:when test="${empty events}">
                        <spring:message code="org.no.events" var="noEvents"/>
                        <paw:paragraph text="${noEvents}" italic="true"/>
                    </c:when>
                    <c:otherwise>
                        <div class="events-grid">
                            <c:forEach var="e" items="${events}">
                                <c:set var="status" value="${statusMap[e.eventID]}" />
                                <paw:event title="${e.title}"
                                           author="${e.organization.orgName}"
                                           authorID="${e.organization.orgID}"
                                           date="${e.getFormattedDateTime(dateFormatter)}"
                                           description="${e.description}"
                                           location="${e.location}"
                                           eventDate="${e.getFormattedEventDateTime(dateFormatter)}"
                                           eventID="${e.eventID}"
                                           tags="${e.tags}"
                                           status="${status}"
                                           userCanManageEvents="${userCanManageEvents}"
                                           isFinished="${e.isFinished()}"
                                           disableInteractions="${isSystemAdmin}"/>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
                <c:if test="${hasPreviousOrgEventsPage or hasNextOrgEventsPage or fn:length(events) > 0}">
                    <c:set var="prevOrgEventsPage" value="${orgEventCurrentPage - 1}"/>
                    <c:set var="nextOrgEventsPage" value="${orgEventCurrentPage + 1}"/>
                    <c:url var="prevOrgEventsUrl" value="/org/${org.orgID}/profile">
                        <c:param name="postPage" value="${orgPostCurrentPage}"/>
                        <c:param name="eventPage" value="${prevOrgEventsPage}"/>
                    </c:url>
                    <c:url var="nextOrgEventsUrl" value="/org/${org.orgID}/profile">
                        <c:param name="postPage" value="${orgPostCurrentPage}"/>
                        <c:param name="eventPage" value="${nextOrgEventsPage}"/>
                    </c:url>
                    <spring:message code="home.pagination.previous" var="orgEventsPrevLabel"/>
                    <spring:message code="home.pagination.next" var="orgEventsNextLabel"/>
                    <spring:message code="home.pagination.page" var="orgEventsPageLabel"/>
                    <div class="pagination-controls">
                        <paw:button
                                text="${orgEventsPrevLabel}"
                                variant="secondary"
                                size="md"
                                cssClass="pagination-button"
                                disabled="${not hasPreviousOrgEventsPage}"
                                onclick="window.location.href='${prevOrgEventsUrl}#events'"/>
                        <div class="pagination-info">
                            <span>${orgEventsPageLabel} ${orgEventCurrentPage}</span>
                        </div>
                        <paw:button
                                text="${orgEventsNextLabel}"
                                variant="primary"
                                size="md"
                                cssClass="pagination-button"
                                disabled="${not hasNextOrgEventsPage}"
                                onclick="window.location.href='${nextOrgEventsUrl}#events'"/>
                    </div>
                </c:if>
            </div>

            <div id="openPositions" class="tab-content">
                <c:if test="${userCanPublish}">
                    <form action="<c:url value='/org/${orgID}/open-position/create'/>" method="get" style="margin-bottom: 1rem;">
                        <spring:message code="org.create.position" var="createPositionText"/>
                        <paw:button text="${createPositionText}" variant="primary" size="md" type="submit"/>
                    </form>
                </c:if>
                <c:choose>
                    <c:when test="${empty openPositions}">
                        <spring:message code="org.no.positions" var="noPositions"/>
                        <paw:paragraph text="${noPositions}" italic="true"/>
                    </c:when>
                    <c:otherwise>
                        <div class="events-grid">
                            <c:forEach var="op" items="${openPositions}">
                                <c:set var="applicationStatus" value="${applicationStatusMap[op.positionID]}" />
                                <paw:open-position
                                        title="${op.title}"
                                        description="${op.description}"
                                        requirements="${op.requirements}"
                                        workhours="${op.workHours}"
                                        datetime="${op.getFormattedDateTime(dateFormatter)}"
                                        author="${op.organization.orgName}"
                                        authorID="${op.organization.orgID}"
                                        orgID="${op.organization.orgID}"
                                        positionID="${op.positionID}"
                                        tags="${op.tags}"
                                        status="${applicationStatus}"
                                        positionStatus="${op.status}"
                                        userCanManageOpenPositions="${userCanManageOpenPositions}"
                                        disableInteractions="${isSystemAdmin}"/>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
                <c:if test="${hasPreviousOrgPositionsPage or hasNextOrgPositionsPage or fn:length(openPositions) > 0}">
                    <c:set var="prevOrgPositionsPage" value="${orgPositionCurrentPage - 1}"/>
                    <c:set var="nextOrgPositionsPage" value="${orgPositionCurrentPage + 1}"/>
                    <c:url var="prevOrgPositionsUrl" value="/org/${org.orgID}/profile">
                        <c:param name="postPage" value="${orgPostCurrentPage}"/>
                        <c:param name="eventPage" value="${orgEventCurrentPage}"/>
                        <c:param name="positionPage" value="${prevOrgPositionsPage}"/>
                    </c:url>
                    <c:url var="nextOrgPositionsUrl" value="/org/${org.orgID}/profile">
                        <c:param name="postPage" value="${orgPostCurrentPage}"/>
                        <c:param name="eventPage" value="${orgEventCurrentPage}"/>
                        <c:param name="positionPage" value="${nextOrgPositionsPage}"/>
                    </c:url>
                    <spring:message code="home.pagination.previous" var="orgPositionsPrevLabel"/>
                    <spring:message code="home.pagination.next" var="orgPositionsNextLabel"/>
                    <spring:message code="home.pagination.page" var="orgPositionsPageLabel"/>
                    <div class="pagination-controls">
                        <paw:button
                                text="${orgPositionsPrevLabel}"
                                variant="secondary"
                                size="md"
                                cssClass="pagination-button"
                                disabled="${not hasPreviousOrgPositionsPage}"
                                onclick="window.location.href='${prevOrgPositionsUrl}#openPositions'"/>
                        <div class="pagination-info">
                            <span>${orgPositionsPageLabel} ${orgPositionCurrentPage}</span>
                        </div>
                        <paw:button
                                text="${orgPositionsNextLabel}"
                                variant="primary"
                                size="md"
                                cssClass="pagination-button"
                                disabled="${not hasNextOrgPositionsPage}"
                                onclick="window.location.href='${nextOrgPositionsUrl}#openPositions'"/>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>
</body>
</html>
