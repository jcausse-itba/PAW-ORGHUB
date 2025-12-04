<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%@ attribute name="title" required="true" %>
<%@ attribute name="description" required="true" %>
<%@ attribute name="requirements" required="true" %>
<%@ attribute name="workhours" required="false" %>
<%@ attribute name="datetime" required="false" type="java.lang.String" %>
<%@ attribute name="author" required="false" %>
<%@ attribute name="authorID" required="false" %>
<%@ attribute name="positionID" required="true" type="java.lang.Long" %>
<%@ attribute name="orgID" required="true" type="java.lang.Long" %>
<%@ attribute name="status" required="false" %>
<%@ attribute name="positionStatus" required="false" %>
<%@ attribute name="userCanManageOpenPositions" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showFooter" required="false" type="java.lang.Boolean" %>
<%@ attribute name="tags" required="false" type="java.util.Collection" %>
<%@ attribute name="order" required="false" %>
<%@ attribute name="isRecommended" required="false" type="java.lang.Boolean" %>
<%@ attribute name="disableInteractions" required="false" type="java.lang.Boolean" %>

<c:set var="footer" value="${showFooter ne null ? showFooter : true}"/>
<c:set var="interactionsDisabled" value="${disableInteractions ne null ? disableInteractions : false}"/>

<div class="content content--type-position ${empty tags ? 'content--no-tags' : ''} ${interactionsDisabled ? 'content--disabled' : ''}" data-has-tags="${not empty tags}">
    <div class="content__content">
        <a href="<c:url value='/org/${orgID}/open-position/${positionID}'/>" class="URL-style">
            <div class="content__top">
                <c:if test="${not empty datetime}">
                    <div class="content__date-container">
                        <c:if test="${isRecommended}">
                            <div class="recommendation-badge">
                                <span class="recommendation-badge__icon">✨</span>
                                <span class="recommendation-badge__text"><spring:message code="position.recommended"/></span>
                            </div>
                        </c:if>
                        <div class="content__date-right">
                            <paw:paragraph text="${datetime}" italic="true" cssClass="content__meta-date"/>
                            <div class="content__type-icon">💼</div>
                        </div>
                    </div>
                </c:if>
                <div class="content__header">
                    <div class="content__title-container">
                        <paw:heading text="${title}" level="first" color="mid"/>
                    </div>
                    <c:if test="${not empty author}">
                        <div class="content__author-container">
                            <c:if test="${not empty authorID}"> <a href="<c:url value='/org/${authorID}/profile'/>" class="content__meta-author">
                                <i><c:out value="${author}"/></i>
                            </a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="content__desc">
                <paw:paragraph text="${description}" />
            </div>

            <div class="content__meta-tags">
                <div class="content__meta">
                    <spring:message code="position.requirements" var="requirementsLabel"/>
                    <c:set var="reqText" value="📋 ${requirementsLabel} ${requirements}" />
                    <paw:paragraph text="${reqText}" />
                    <c:if test="${not empty workhours}">
                        <span class="content__meta-sep">•</span>
                        <spring:message code="position.hours" var="hoursLabel"/>
                        <c:set var="hoursText" value="⏰ ${hoursLabel} ${workhours}" />
                        <paw:paragraph text="${hoursText}" />
                    </c:if>
                </div>

                <div class="content__tags">
                    <c:if test="${not empty tags}">
                        <c:forEach var="t" items="${tags}">
                            <c:url var="tagLink" value="/home">
                                <c:param name="tag" value="${t.tagID}"/>
                                <c:param name="order" value="${order}"/>
                                <c:param name="type" value="openpositions"/>
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
                <c:if test="${userCanManageOpenPositions}">
                    <div class="position__admin-actions">
                        <form action="<c:url value='/org/${authorID}/open-position/${positionID}/manage'/>" method="get">
                            <spring:message code="position.manage.candidates" var="manageCandidates"/>
                            <paw:button text="${manageCandidates}" variant="primary" size="md" type="submit"/>
                        </form>
                    </div>
                </c:if>
                <c:choose>
                    <c:when test="${not empty status}">
                        <div class="event__user-actions">
                            <div class="event__status-badge ${status}">
                                <spring:message code="position.status.label"/>
                                <c:choose>
                                    <c:when test="${status eq 'ACCEPTED'}"><spring:message code="position.status.accepted"/></c:when>
                                    <c:when test="${status eq 'PENDING'}"><spring:message code="position.status.pending"/></c:when>
                                    <c:when test="${status eq 'REJECTED'}"><spring:message code="position.status.rejected"/></c:when>
                                </c:choose>
                            </div>
                            <c:if test="${(status eq 'PENDING' || status eq 'ACCEPTED') && positionStatus ne 'CLOSED'}">
                                <form action="<c:url value='/org/${orgID}/open-position/${positionID}/withdraw'/>" method="post">
                                    <spring:message code="position.withdraw" var="withdrawText"/>
                                    <paw:button text="${withdrawText}" variant="secondary" size="md" type="submit"/>
                                </form>
                            </c:if>
                            <c:if test="${positionStatus eq 'CLOSED'}">
                                <div class="position-closed-notice">
                                    <spring:message code="position.closed.notice"/>
                                </div>
                            </c:if>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${positionStatus eq 'CLOSED'}">
                                <div class="position-closed-notice">
                                    <spring:message code="position.closed.notice"/>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <form action="<c:url value='/org/${orgID}/open-position/${positionID}/apply'/>" method="post">
                                    <spring:message code="position.apply" var="applyText"/>
                                    <paw:button text="${applyText}" variant="primary" size="md" type="submit"/>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>
</div>