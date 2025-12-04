<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="user.profile.title"/></title>
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
    <script type="module" src="<c:url value='/resources/js/tabs.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/modal.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>

<div class="container">
    <div class="profile-card">
        <div class="profile-header">
            <div class="avatar">
                <c:set var="initials"
                       value="${not empty userFirstName ? fn:toUpperCase(fn:substring(userFirstName,0,1)) : 'U'}${not empty userLastName ? fn:toUpperCase(fn:substring(userLastName,0,1)) : ''}"/>
                <c:out value="${initials}"/>
            </div>
            <div>
                <p class="profile-name">
                    <c:out value="${userFirstName}"/> <c:out value="${userLastName}"/>
                </p>
                <p class="muted">
                    <c:choose>
                        <c:when test="${isInstitutionAdmin}"><spring:message code="user.profile.role.admin"/></c:when>
                    </c:choose>
                </p>
                <c:if test="${not empty userPositions}">
                    <div class="user-positions">
                <c:forEach var="position" items="${userPositions}">
                    <span class="comment-position-badge level-${position.level}">
                        <spring:message code="user.position.badge.format" arguments="${position.positionName},${position.organization.orgName}"/>
                    </span>
                </c:forEach>
                    </div>
                </c:if>
            </div>
            <div class="profile-actions">
                <form id="logoutForm" method="post" action="<c:url value='/logout'/>">
                    <spring:message code="user.profile.logout" var="logoutText"/>
                    <paw:button
                            text="${logoutText}"
                            variant="primary"
                            size="md"
                            cssClass="btn-pill"
                            type="submit"/>
                </form>
            </div>
        </div>

        <div class="info-grid">
            <div class="info-item">
                <span class="info-label"><spring:message code="user.profile.email.label"/></span>
                <div class="info-value"><c:out value="${userEmail != null ? userEmail : '—'}"/></div>
            </div>
            <div class="info-item">
                <span class="info-label"><spring:message code="user.profile.institution.label"/></span>
                <div class="info-value"><c:out value="${institutionName != null ? institutionName : '—'}"/></div>
            </div>
        </div>

    </div>

    <div class="hr"></div>
    <div class="tabs-container" data-storage-key="activeTab_orgProfile">
        <div class="tabs-nav">
            <button class="tab-button active" data-tab="likes"><spring:message code="user.profile.tab.likes"/></button>
            <button class="tab-button" data-tab="events"><spring:message code="user.profile.tab.events"/></button>
            <button class="tab-button" data-tab="applications"><spring:message code="user.profile.tab.applications"/></button>
        </div>

        <div class="tab-content-container">
            <div id="likes" class="tab-content active">
                <c:choose>
                    <c:when test="${empty likedPosts}">
                        <spring:message code="user.profile.empty.likes" var="emptyLikes"/>
                        <paw:paragraph text="${emptyLikes}" italic="true"/>
                    </c:when>
                    <c:otherwise>
                        <div class="events-grid">
                            <c:forEach var="p" items="${likedPosts}">
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
                                            liked="${likedPostsIDs != null && likedPostsIDs.contains(p.postID)}">
                                    </paw:post>
                                </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div id="events" class="tab-content">
                <c:choose>
                    <c:when test="${empty enrolledEvents}">
                        <spring:message code="user.profile.empty.events" var="emptyEvents"/>
                        <paw:paragraph text="${emptyEvents}" italic="true"/>
                    </c:when>
                    <c:otherwise>
                        <div class="events-grid">
                            <c:forEach var="enrollment" items="${enrolledEvents}">
                                <paw:event title="${enrollment.event.title}"
                                           author="${enrollment.event.organization.orgName}"
                                           authorID="${enrollment.event.organization.orgID}"
                                           date="${enrollment.event.getFormattedDateTime(dateFormatter)}"
                                           description="${enrollment.event.description}"
                                           location="${enrollment.event.location}"
                                           eventDate="${enrollment.event.getFormattedEventDateTime(dateFormatter)}"
                                           eventID="${enrollment.event.eventID}"
                                           tags="${enrollment.event.tags}"
                                           status="${enrollment.status}"
                                           isFinished="${enrollment.event.isFinished()}"
                                           userCanManageEvents="${userCanManageEvents}"/>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div id="applications" class="tab-content">
                <c:choose>
                    <c:when test="${empty applications}">
                        <spring:message code="user.profile.empty.applications" var="emptyApplications"/>
                        <paw:paragraph text="${emptyApplications}" italic="true"/>
                    </c:when>
                    <c:otherwise>
                        <div class="events-grid">
                            <c:forEach var="application" items="${applications}">
                                <c:set var="position" value="${null}"/>
                                <c:forEach var="pos" items="${applicationPositions}">
                                    <c:if test="${pos.positionID == application.openPosition.positionID}">
                                        <c:set var="position" value="${pos}"/>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${position != null}">
                                    <paw:open-position title="${position.title}"
                                                      author="${position.organization.orgName}"
                                                      authorID="${position.organization.orgID}"
                                                      datetime="${position.getFormattedDateTime(dateFormatter)}"
                                                      description="${position.description}"
                                                      requirements="${position.requirements}"
                                                      workhours="${position.workHours}"
                                                      positionID="${position.positionID}"
                                                      orgID="${position.organization.orgID}"
                                                      tags="${position.tags}"
                                                      status="${application.status}"
                                                      positionStatus="${position.status}"/>
                                </c:if>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
</body>
</html>
