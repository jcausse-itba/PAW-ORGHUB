<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%@ attribute name="date" required="false" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="description" required="true" %>
<%@ attribute name="location" required="true" %>
<%@ attribute name="eventDate" required="true" type="java.lang.String" %>
<%@ attribute name="author" required="false" %>
<%@ attribute name="authorID" required="false" %>
<%@ attribute name="tags" required="false" type="java.util.Collection" %>
<%@ attribute name="eventID" required="true" type="java.lang.Long" %>
<%@ attribute name="likeCount" required="false" type="java.lang.Long"%>
<%@ attribute name="commentCount" required="false" type="java.lang.Long"%>
<%@ attribute name="showFooter" required="false" type="java.lang.Boolean" %>
<%@ attribute name="order" required="false" %>
<%@ attribute name="status" required="false" %>
<%@ attribute name="isRecommended" required="false" type="java.lang.Boolean" %>
<%@ attribute name="userCanManageEvents" required="false" type="java.lang.Boolean" %>
<%@ attribute name="isFinished" required="false" type="java.lang.Boolean"%>
<%@ attribute name="disableInteractions" required="false" type="java.lang.Boolean" %>

<c:set var="footer" value="${showFooter ne null ? showFooter : true}"/>
<c:set var="interactionsDisabled" value="${disableInteractions ne null ? disableInteractions : false}"/>

<div class="content content--type-event ${empty tags ? 'content--no-tags' : ''} ${interactionsDisabled ? 'content--disabled' : ''}" data-has-tags="${not empty tags}">
    <div class="content__content">
        <c:url var="eventUrl" value="/event/${eventID}"/>
        <a href="${eventUrl}" class="URL-style">
            <div class="content__top">
                <c:if test="${not empty date}">
                    <div class="content__date-container">
                        <c:if test="${isRecommended}">
                            <div class="recommendation-badge">
                                <span class="recommendation-badge__icon">✨</span>
                                <span class="recommendation-badge__text"><spring:message code="event.recommended"/></span>
                            </div>
                        </c:if>
                        <div class="content__date-right">
                            <paw:paragraph text="${date}" italic="true" cssClass="content__meta-date"/>
                            <div class="content__type-icon">📅</div>
                        </div>
                    </div>
                </c:if>
                <div class="content__header">
                    <div class="content__title-container">
                        <paw:heading text="${title}" level="first" color="mid"/>
                    </div>
                    <c:if test="${not empty author}">
                        <div class="content__author-container">
                            <c:choose>
                                <c:when test="${not empty authorID}">
                                    <a href="<c:url value='/org/${authorID}/profile'/>" class="content__meta-author">
                                        <i><c:out value="${author}"/></i>
                                    </a>
                                </c:when>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="content__desc">
                <paw:paragraph text="${description}" />
            </div>

            <div class="content__meta-tags">
                <div class="content__meta">
                    <spring:message code="event.location" var="locationLabel"/>
                    <paw:paragraph text="📍${location}" />
                    <span class="content__meta-sep">•</span>
                    <spring:message code="event.date" var="dateLabel"/>
                    <paw:paragraph text="📅 ${eventDate}" />
                </div>

                <div class="content__tags">
                    <c:if test="${not empty tags}">
                        <c:forEach var="t" items="${tags}">
                            <c:url var="tagLink" value="/home">
                                <c:param name="tag" value="${t.tagID}"/>
                                <c:param name="order" value="${order}"/>
                                <c:param name="type" value="events"/>
                            </c:url>
                            <a href="<c:out value='${tagLink}'/>" class="badge">
                                <c:out value="${t.tagName}"/>
                            </a>
                        </c:forEach>
                    </c:if>
                </div>
            </div>
        </a>
    </div>

    <c:if test="${footer}">
        <div class="content__footer">
            <div class="content__footer-right">
                <c:if test="${userCanManageEvents}">
                    <div class="event__admin-actions">
                        <form action="<c:url value='/org/${authorID}/event/${eventID}/manage'/>" method="get">
                            <spring:message code="event.manage.enrolled" var="manageEnrolled"/>
                            <paw:button text="${manageEnrolled}" variant="primary" size="md" type="submit"/>
                        </form>
                    </div>
                </c:if>
                <c:choose>
                    <c:when test="${isFinished}">
                        <div class="event__status-badge closed">
                            <spring:message code="event.finished"/>
                        </div>
                    </c:when>
                    <c:when test="${not empty status}">
                        <div class="event__user-actions">
                            <div class="event__status-badge ${status}">
                                <spring:message code="event.status.label"/>
                                <c:choose>
                                    <c:when test="${status eq 'ACCEPTED'}"><spring:message code="event.status.accepted"/></c:when>
                                    <c:when test="${status eq 'PENDING'}"><spring:message code="event.status.pending"/></c:when>
                                    <c:when test="${status eq 'REJECTED'}"><spring:message code="event.status.rejected"/></c:when>
                                </c:choose>
                            </div>
                            <c:if test="${status eq 'ACCEPTED' || status eq 'PENDING'}">
                                <form action="<c:url value='/event/${eventID}/unenroll'/>" method="post">
                                    <spring:message code="event.unenroll" var="unenrollText"/>
                                    <paw:button text="${unenrollText}" variant="secondary" size="md" type="submit"/>
                                </form>
                            </c:if>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form action="<c:url value='/event/${eventID}/enroll'/>" method="post">
                            <spring:message code="event.enroll" var="enrollText"/>
                            <paw:button text="${enrollText}" variant="primary" size="md" type="submit"/>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>
</div>