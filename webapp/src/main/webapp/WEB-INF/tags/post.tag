<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%@ attribute name="title" required="true" %>
<%@ attribute name="description" required="true" %>
<%@ attribute name="author" required="false" %>
<%@attribute name="authorID" required="false" %>
<%@ attribute name="date" required="false" %>
<%@ attribute name="tags" required="false" type="java.util.Collection" %>
<%@ attribute name="likeCount" required="false" type="java.lang.Long"%>
<%@ attribute name="liked" required="false" type="java.lang.Boolean" %>
<%@ attribute name="commentCount" required="false" type="java.lang.Long"%>
<%@ attribute name="showActions" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showFooter" required="false" type="java.lang.Boolean" %>
<%@ attribute name="cssClass" required="false" %>
<%@ attribute name="postID" required="true" type="java.lang.Long" %>
<%@ attribute name="order" required="false" %>
<%@ attribute name="isRecommended" required="false" type="java.lang.Boolean" %>
<%@ attribute name="disableInteractions" required="false" type="java.lang.Boolean" %>

<c:set var="actions" value="${showActions ne null ? showActions : true}"/>
<c:set var="footer" value="${showFooter  ne null ? showFooter  : true}"/>
<c:set var="interactionsDisabled" value="${disableInteractions ne null ? disableInteractions : false}"/>

<div class="content content--type-publication ${empty cssClass ? '' : cssClass} ${empty tags ? 'content--no-tags' : ''} ${interactionsDisabled ? 'content--disabled' : ''}" data-has-tags="${not empty tags}">
    <div class="content__content">
        <a href="<c:url value='/post/${postID}'/>" class="URL-style">
            <div class="content__top">
                <c:if test="${not empty date}">
                    <div class="content__date-container">
                        <c:if test="${isRecommended}">
                            <div class="recommendation-badge">
                                <span class="recommendation-badge__icon">✨</span>
                                <span class="recommendation-badge__text"><spring:message code="post.recommended"/></span>
                            </div>
                        </c:if>
                        <div class="content__date-right">
                            <paw:paragraph text="${date}" italic="true" cssClass="content__meta-date"/>
                            <div class="content__type-icon">📝</div>
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
                                    <a href="<c:url value='/org/${authorID}/profile'/>" class="content__meta-author"><i><c:out value="${author}"/></i></a>
                                </c:when>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>

            <div class="content__desc">
                <paw:paragraph text="${description}"/>
            </div>

            <div class="content__meta-tags">
                <div class="content__tags">
                    <c:if test="${not empty tags}">
                        <c:forEach var="t" items="${tags}">
                            <c:url var="tagLink" value="/home">
                                <c:param name="tag" value="${t.tagID}"/>
                                <c:param name="order" value="${order}"/>
                                <c:param name="type" value="posts"/>
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
            <div class="content__footer-left">
                <form action="<c:url value='/likes/toggle'/>" method="post" class="content__icon-form">
                    <input type="hidden" name="postID" value="<c:out value='${postID}'/>" />
                    <button type="submit" class="content__icon-btn">
                        <img src="<c:url value='${liked ? "/resources/images/liked.ico" : "/resources/images/like.ico"}'/>" class="content__icon-img">
                        <span><c:out value="${likeCount}" /></span>
                    </button>
                </form>
                <button class="content__icon-btn" data-modal-target="#commentModal-${postID}">
                    <img src="<c:url value='/resources/images/comment.ico'/>" alt="<spring:message code='post.comment.alt'/>" class="content__icon-img">
                    <span><c:out value="${commentCount}"/></span>
                </button>
            </div>
            <div class="content__footer-right">
                <div class="content__footer-spacer"></div>
            </div>
        </div>
    </c:if>

    <paw:comment-modal
            postID="${postID}"
            title="${title}"
            description="${description}"
            author="${author}"
            date="${date}"
            tags="${tags}"/>
</div>