<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="home.title"/></title>
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
<paw:header userFirstName="${userFirstName}"/>
<div class="container">
    
    <div class="tabs-container" data-storage-key="activeTab_home">
        <div class="tabs-nav">
            <button class="tab-button ${type == 'posts' ? 'active' : ''}" data-tab="posts" onclick="changeTab('posts')"><spring:message code="home.posts"/></button>
            <button class="tab-button ${type == 'events' ? 'active' : ''}" data-tab="events" onclick="changeTab('events')"><spring:message code="home.events"/></button>
            <button class="tab-button ${type == 'openpositions' ? 'active' : ''}" data-tab="openPositions" onclick="changeTab('openpositions')"><spring:message code="home.openpositions"/></button>
        </div>
    </div>
    <div class="filters-sticky">
        <div class="filters-bar">
            <paw:date-filter action="/home"
                             value="${sort}"
                             carryName="tag"
                             carryTags="${selectedTags}"
                             carryMatch="${match}"
                             carryType="${type}"/>
            <paw:tag-filter action="/home"
                            items="${tags}"
                            values="${selectedTags}"
                            paramName="tag"
                            carryName="order"
                            carrySort="${sort}"
                            showMatch="true"
                            carryType="${type}"/>
        </div>
    </div>
    <div class="tab-content-container">
        <div id="posts" class="tab-content ${type == 'posts' ? 'active' : ''}">
            <c:if test="${not isSubscribedToOrgs}">
                <div class="recommendation-notice">
                    <div class="recommendation-notice-header">
                        <div class="recommendation-notice-icon">ℹ</div>
                        <spring:message code="home.recommended.content" var="recommendedContent"/>
                        <paw:heading text="${recommendedContent}" level="Third" color="dark"/>
                    </div>
                    <spring:message code="home.recommendation.posts" var="recommendationPosts"/>
                    <paw:paragraph 
                        text="${recommendationPosts}" 
                        cssClass="recommendation-notice-text"/>
                    <div class="recommendation-notice-button">
                        <c:url value="/org" var="orgUrl"/>
                        <a href="${orgUrl}" class="btn btn-secondary btn-sm text-primary">
                            <span><spring:message code="home.explore.organizations"/></span>
                        </a>
                    </div>
                </div>
            </c:if>
            <c:choose>
                <c:when test="${fn:length(posts) == 0 and fn:length(selectedTags) > 0}">
                    <spring:message code="home.no.posts.found" var="noPostsFound"/>
                    <paw:paragraph text="${noPostsFound}" italic="true"/>
                </c:when>
                <c:when test="${fn:length(posts) == 0 and fn:length(selectedTags) == 0}">
                    <spring:message code="home.no.posts" var="noPosts"/>
                    <paw:paragraph text="${noPosts}" italic="true"/>
                </c:when>
                <c:otherwise>
                    <div class="contents-grid">
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
                                    liked="${likedPostIDs != null && likedPostIDs.contains(p.postID)}"
                                    isRecommended="${not isSubscribedToOrgs}"/>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
            <c:if test="${isSubscribedToOrgs and (hasPreviousPage or hasNextPage or fn:length(posts) > 0)}">
                <c:set var="prevPage" value="${currentPage - 1}"/>
                <c:set var="nextPage" value="${currentPage + 1}"/>
                <c:url var="prevPageUrl" value="/home">
                    <c:param name="order" value="${sort}"/>
                    <c:if test="${not empty selectedTags}">
                        <c:forEach var="tagID" items="${selectedTags}">
                            <c:param name="tag" value="${tagID}"/>
                        </c:forEach>
                    </c:if>
                    <c:param name="match" value="${match}"/>
                    <c:param name="type" value="posts"/>
                    <c:param name="page" value="${prevPage}"/>
                </c:url>
                <c:url var="nextPageUrl" value="/home">
                    <c:param name="order" value="${sort}"/>
                    <c:if test="${not empty selectedTags}">
                        <c:forEach var="tagID" items="${selectedTags}">
                            <c:param name="tag" value="${tagID}"/>
                        </c:forEach>
                    </c:if>
                    <c:param name="match" value="${match}"/>
                    <c:param name="type" value="posts"/>
                    <c:param name="page" value="${nextPage}"/>
                </c:url>
                <div class="pagination-controls">
                    <spring:message code="home.pagination.previous" var="previousLabel"/>
                    <spring:message code="home.pagination.next" var="nextLabel"/>
                    <spring:message code="home.pagination.page" var="pageLabel"/>
                    <paw:button
                            text="${previousLabel}"
                            variant="secondary"
                            size="md"
                            cssClass="pagination-button"
                            disabled="${not hasPreviousPage}"
                            onclick="window.location.href='${prevPageUrl}'"/>
                    <div class="pagination-info">
                        <span>${pageLabel} ${currentPage}</span>
                    </div>
                    <paw:button
                            text="${nextLabel}"
                            variant="primary"
                            size="md"
                            cssClass="pagination-button"
                            disabled="${not hasNextPage}"
                            onclick="window.location.href='${nextPageUrl}'"/>
                </div>
            </c:if>
        </div>

        <div id="events" class="tab-content ${type == 'events' ? 'active' : ''}">
            <c:if test="${not isSubscribedToOrgs}">
                <div class="recommendation-notice">
                    <div class="recommendation-notice-header">
                        <div class="recommendation-notice-icon">📅</div>
                        <spring:message code="home.recommended.events" var="recommendedEvents"/>
                        <paw:heading text="${recommendedEvents}" level="Third" color="dark"/>
                    </div>
                    <spring:message code="home.recommendation.events" var="recommendationEvents"/>
                    <paw:paragraph 
                        text="${recommendationEvents}" 
                        cssClass="recommendation-notice-text"/>
                    <div class="recommendation-notice-button">
                        <c:url value="/org" var="orgUrl"/>
                        <a href="${orgUrl}" class="btn btn-secondary btn-sm text-primary">
                            <span><spring:message code="home.explore.organizations"/></span>
                        </a>
                    </div>
                </div>
            </c:if>
            <c:choose>
                <c:when test="${fn:length(events) == 0 and fn:length(selectedTags) > 0}">
                    <spring:message code="home.no.events.found" var="noEventsFound"/>
                    <paw:paragraph text="${noEventsFound}" italic="true"/>
                </c:when>
                <c:when test="${fn:length(events) == 0 and fn:length(selectedTags) == 0}">
                    <spring:message code="home.no.events" var="noEvents"/>
                    <paw:paragraph text="${noEvents}" italic="true"/>
                </c:when>
                <c:otherwise>
                    <div class="contents-grid">
                        <c:forEach var="e" items="${events}">
                            <c:set var="status" value="${statusMap[e.eventID]}" />
                            <paw:event
                                    title="${e.title}"
                                    author="${e.organization.orgName}"
                                    date="${e.getFormattedDateTime(dateFormatter)}"
                                    authorID="${e.organization.orgID}"
                                    description="${e.description}"
                                    location="${e.location}"
                                    eventDate="${e.getFormattedEventDateTime(dateFormatter)}"
                                    eventID="${e.eventID}"
                                    tags="${e.tags}"
                                    order="${sort}"
                                    status="${status}"
                                    userCanManageEvents="${userCanManageEvents}"
                                    isFinished="${e.isFinished()}"
                                    isRecommended="${not isSubscribedToOrgs}"/>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
            <c:if test="${isSubscribedToOrgs and (hasPreviousEventsPage or hasNextEventsPage or fn:length(events) > 0)}">
                <c:set var="prevEventPage" value="${eventCurrentPage - 1}"/>
                <c:set var="nextEventPage" value="${eventCurrentPage + 1}"/>
                <c:url var="prevEventPageUrl" value="/home">
                    <c:param name="order" value="${sort}"/>
                    <c:if test="${not empty selectedTags}">
                        <c:forEach var="tagID" items="${selectedTags}">
                            <c:param name="tag" value="${tagID}"/>
                        </c:forEach>
                    </c:if>
                    <c:param name="match" value="${match}"/>
                    <c:param name="type" value="events"/>
                    <c:param name="eventPage" value="${prevEventPage}"/>
                </c:url>
                <c:url var="nextEventPageUrl" value="/home">
                    <c:param name="order" value="${sort}"/>
                    <c:if test="${not empty selectedTags}">
                        <c:forEach var="tagID" items="${selectedTags}">
                            <c:param name="tag" value="${tagID}"/>
                        </c:forEach>
                    </c:if>
                    <c:param name="match" value="${match}"/>
                    <c:param name="type" value="events"/>
                    <c:param name="eventPage" value="${nextEventPage}"/>
                </c:url>
                <spring:message code="home.pagination.previous" var="eventsPreviousLabel"/>
                <spring:message code="home.pagination.next" var="eventsNextLabel"/>
                <spring:message code="home.pagination.page" var="eventsPageLabel"/>
                <div class="pagination-controls">
                    <paw:button
                            text="${eventsPreviousLabel}"
                            variant="secondary"
                            size="md"
                            cssClass="pagination-button"
                            disabled="${not hasPreviousEventsPage}"
                            onclick="window.location.href='${prevEventPageUrl}'"/>
                    <div class="pagination-info">
                        <span>${eventsPageLabel} ${eventCurrentPage}</span>
                    </div>
                    <paw:button
                            text="${eventsNextLabel}"
                            variant="primary"
                            size="md"
                            cssClass="pagination-button"
                            disabled="${not hasNextEventsPage}"
                            onclick="window.location.href='${nextEventPageUrl}'"/>
                </div>
            </c:if>
        </div>

        <div id="openPositions" class="tab-content ${type == 'openpositions' ? 'active' : ''}">
            <c:if test="${not isSubscribedToOrgs}">
                <div class="recommendation-notice">
                    <div class="recommendation-notice-header">
                        <div class="recommendation-notice-icon">💼</div>
                        <spring:message code="home.recommended.opportunities" var="recommendedOpportunities"/>
                        <paw:heading text="${recommendedOpportunities}" level="Third" color="dark"/>
                    </div>
                    <spring:message code="home.recommendation.positions" var="recommendationPositions"/>
                    <paw:paragraph 
                        text="${recommendationPositions}" 
                        cssClass="recommendation-notice-text"/>
                    <div class="recommendation-notice-button">
                        <c:url value="/org" var="orgUrl"/>
                        <a href="${orgUrl}" class="btn btn-secondary btn-sm text-primary">
                            <span><spring:message code="home.explore.organizations"/></span>
                        </a>
                    </div>
                </div>
            </c:if>
            <c:choose>
                <c:when test="${fn:length(openPositions) == 0 and fn:length(selectedTags) > 0}">
                    <spring:message code="home.no.positions.found" var="noPositionsFound"/>
                    <paw:paragraph text="${noPositionsFound}" italic="true"/>
                </c:when>
                <c:when test="${fn:length(openPositions) == 0 and fn:length(selectedTags) == 0}">
                    <spring:message code="home.no.positions" var="noPositions"/>
                    <paw:paragraph text="${noPositions}" italic="true"/>
                </c:when>
                <c:otherwise>
                    <div class="contents-grid">
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
                                    positionID="${op.positionID}"
                                    orgID="${op.organization.orgID}"
                                    tags="${op.tags}"
                                    order="${sort}"
                                    status="${applicationStatus}"
                                    positionStatus="${op.status}"
                                    userCanManageOpenPositions="${userCanManageOpenPositions}"
                                    isRecommended="${not isSubscribedToOrgs}"/>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
            <c:if test="${isSubscribedToOrgs and (hasPreviousPositionsPage or hasNextPositionsPage or fn:length(openPositions) > 0)}">
                <c:set var="prevPositionPage" value="${positionCurrentPage - 1}"/>
                <c:set var="nextPositionPage" value="${positionCurrentPage + 1}"/>
                <c:url var="prevPositionPageUrl" value="/home">
                    <c:param name="order" value="${sort}"/>
                    <c:if test="${not empty selectedTags}">
                        <c:forEach var="tagID" items="${selectedTags}">
                            <c:param name="tag" value="${tagID}"/>
                        </c:forEach>
                    </c:if>
                    <c:param name="match" value="${match}"/>
                    <c:param name="type" value="openpositions"/>
                    <c:param name="positionPage" value="${prevPositionPage}"/>
                </c:url>
                <c:url var="nextPositionPageUrl" value="/home">
                    <c:param name="order" value="${sort}"/>
                    <c:if test="${not empty selectedTags}">
                        <c:forEach var="tagID" items="${selectedTags}">
                            <c:param name="tag" value="${tagID}"/>
                        </c:forEach>
                    </c:if>
                    <c:param name="match" value="${match}"/>
                    <c:param name="type" value="openpositions"/>
                    <c:param name="positionPage" value="${nextPositionPage}"/>
                </c:url>
                <div class="pagination-controls">
                    <spring:message code="home.pagination.previous" var="positionsPreviousLabel"/>
                    <spring:message code="home.pagination.next" var="positionsNextLabel"/>
                    <spring:message code="home.pagination.page" var="positionsPageLabel"/>
                    <paw:button
                            text="${positionsPreviousLabel}"
                            variant="secondary"
                            size="md"
                            cssClass="pagination-button"
                            disabled="${not hasPreviousPositionsPage}"
                            onclick="window.location.href='${prevPositionPageUrl}'"/>
                    <div class="pagination-info">
                        <span>${positionsPageLabel} ${positionCurrentPage}</span>
                    </div>
                    <paw:button
                            text="${positionsNextLabel}"
                            variant="primary"
                            size="md"
                            cssClass="pagination-button"
                            disabled="${not hasNextPositionsPage}"
                            onclick="window.location.href='${nextPositionPageUrl}'"/>
                </div>
            </c:if>
        </div>
    </div>
</div>

</body>

<script>
    function changeTab(newType) {
        const url = new URL(window.location);
        url.searchParams.set('type', newType);
        if (newType === 'posts') {
            url.searchParams.set('page', '1');
            url.searchParams.delete('eventPage');
            url.searchParams.delete('positionPage');
        } else if (newType === 'events') {
            url.searchParams.set('eventPage', '1');
            url.searchParams.delete('page');
            url.searchParams.delete('positionPage');
        } else if (newType === 'openpositions') {
            url.searchParams.set('positionPage', '1');
            url.searchParams.delete('page');
            url.searchParams.delete('eventPage');
        } else {
            url.searchParams.delete('page');
            url.searchParams.delete('eventPage');
            url.searchParams.delete('positionPage');
        }
        window.location.href = url.toString();
    }

    document.addEventListener("click", function (event) {
        const dropdowns = document.querySelectorAll(".ms__details");

        dropdowns.forEach(function (dropdown) {
            if (!dropdown.contains(event.target)) {
                dropdown.removeAttribute("open");
            }
        });
    });
</script>
</html>
