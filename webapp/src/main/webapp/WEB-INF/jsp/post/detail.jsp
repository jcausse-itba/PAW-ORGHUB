<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><spring:message code="post.detail.title"/> <c:out value="${post.title}"/></title>
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
            <paw:heading text="${post.title}" level="first" color="mid"/>
            <c:if test="${canPublish}">
                <div style="display: flex; gap: 8px;">
                    <c:url value="/org/${post.organization.orgID}/post/${post.postID}/edit" var="editUrl"/>
                    <a href="${editUrl}">
                        <spring:message code="post.edit.button" var="editButtonText"/>
                        <paw:button text="${editButtonText}" variant="primary" size="sm"/>
                    </a>
                    <c:url value="/org/${post.organization.orgID}/post/${post.postID}/delete" var="deleteUrl"/>
                    <form action="${deleteUrl}" method="post" style="margin: 0;" onsubmit="return confirm('<spring:message code="post.delete.confirm"/>');">
                        <spring:message code="post.delete.button" var="deleteButtonText"/>
                        <paw:button text="${deleteButtonText}" variant="danger" size="sm" type="submit"/>
                    </form>
                </div>
            </c:if>
        </div>

        <div class="content__desc">
            <paw:paragraph text="${post.body}"/>
        </div>

        <c:if test="${not empty post.tags}">
            <div class="content__tags">
                <c:forEach var="t" items="${post.tags}">
                    <c:url var="tagLink" value="/home">
                        <c:param name="tag" value="${t.tagID}"/>
                    </c:url>
                    <a href="${tagLink}" class="badge">
                        <c:out value="${t.tagName}"/>
                    </a>
                </c:forEach>
            </div>
        </c:if>

        <div class="content__meta">
            <spring:message code="post.detail.published.by" var="publishedBy"/>
            <paw:paragraph text="${publishedBy}" italic="true"/>
            <c:url value="/org/${post.organization.orgID}/profile" var="orgProfileUrl"/>
            <a href="${orgProfileUrl}" class="content__meta-author">
                <paw:paragraph text="${post.organization.orgName}" italic="true" bold="true"/>
            </a>
            <spring:message code="post.detail.published.on" var="publishedOn"/>
            <paw:paragraph text="${publishedOn} ${post.getFormattedDateTime(dateFormatter)}" italic="true" cssClass="content__meta-date"/>
        </div>

        <div class="content__footer">
            <div class="content__footer-left">
                <c:url value="/likes/toggle" var="likeToggleUrl"/>
                <form action="${likeToggleUrl}" method="post" class="content__icon-form">
                    <input type="hidden" name="postID" value="<c:out value="${post.postID}"/>" />
                    <button type="submit" class="content__icon-btn">
                        <spring:message code="post.detail.like.alt" var="likeAlt"/>
                        <img src="<c:url value='${liked ? "/resources/images/liked.ico" : "/resources/images/like.ico"}'/>" alt="${likeAlt}" class="content__icon-img">
                        <span><c:out value="${post.likeCount}" /></span>
                    </button>
                </form>

                <button class="content__icon-btn" data-scroll-to-comment="true">
                    <spring:message code="post.detail.comment.alt" var="commentAlt"/>
                    <img src="<c:url value='/resources/images/comment.ico'/>" alt="${commentAlt}" class="content__icon-img">
                    <span><c:out value="${post.commentCount}"/></span>
                </button>
            </div>
        </div>

        <hr class="content__separator"/>

        <div class="comments-section" id="comments-section">
            <div class="comments-header">
                <spring:message code="post.detail.comments.title" var="commentsTitle"/>
                <paw:heading text="${commentsTitle}" level="second"/>
            </div>

            <c:if test="${empty comments}">
                <spring:message code="post.detail.no.comments" var="noComments"/>
                <paw:paragraph text="${noComments}" italic="true"/>
            </c:if>

            <c:forEach var="comment" items="${comments}">
                <paw:comment
                        author="${users[comment.userID].firstName} ${users[comment.userID].lastName}"
                        text="${comment.content}"
                        commentID="${comment.commentID}"
                        postID="${post.postID}"
                        orgID="${post.organization.orgID}"
                        userPositions="${userPositions[comment.userID]}"
                />
            </c:forEach>

            <c:if test="${hasPreviousComments or hasNextComments or fn:length(comments) > 0}">
                <c:set var="prevCommentPage" value="${commentCurrentPage - 1}"/>
                <c:set var="nextCommentPage" value="${commentCurrentPage + 1}"/>
                <c:url var="prevCommentsPageUrl" value="/post/${post.postID}">
                    <c:param name="commentPage" value="${prevCommentPage}"/>
                </c:url>
                <c:url var="nextCommentsPageUrl" value="/post/${post.postID}">
                    <c:param name="commentPage" value="${nextCommentPage}"/>
                </c:url>
                <spring:message code="home.pagination.previous" var="commentsPreviousLabel"/>
                <spring:message code="home.pagination.next" var="commentsNextLabel"/>
                <spring:message code="home.pagination.page" var="commentsPageLabel"/>
                <div class="pagination-controls">
                    <paw:button
                            text="${commentsPreviousLabel}"
                            variant="secondary"
                            size="md"
                            cssClass="pagination-button"
                            disabled="${not hasPreviousComments}"
                            onclick="window.location.href='${prevCommentsPageUrl}#comments-section'"/>
                    <div class="pagination-info">
                        <span>${commentsPageLabel} ${commentCurrentPage}</span>
                    </div>
                    <paw:button
                            text="${commentsNextLabel}"
                            variant="primary"
                            size="md"
                            cssClass="pagination-button"
                            disabled="${not hasNextComments}"
                            onclick="window.location.href='${nextCommentsPageUrl}#comments-section'"/>
                </div>
            </c:if>

            <div class="comment-form-inline" id="comment-box">
                <c:url value="/comments/create" var="commentCreateUrl"/>
                <paw:form id="commentForm" cssClass="js-comment-form" method="post" action="${commentCreateUrl}" novalidate="true"  modelAttribute="commentForm">
                    <input type="hidden" name="postID" value="<c:out value="${post.postID}"/>"/>
                    <input type="hidden" name="orgID" value="<c:out value='${post.organization.orgID}'/>"/>
                    <input type="hidden" name="context" value="detail"/>
                    <input type="hidden" name="commentPage" value="${commentCurrentPage}"/>

                    <spring:message code="post.detail.comment.label" var="commentLabel"/>
                    <spring:message code="post.detail.comment.placeholder" var="commentPlaceholder"/>
                    <paw:input
                            name="content"
                            id="comment_content"
                            label="${commentLabel}"
                            type="textarea"
                            required="true"
                            placeholder="${commentPlaceholder}"
                            cssClass="form-control"
                            modelAttribute="commentForm"
                    />

                    <div class="btn-container">
                        <spring:message code="post.detail.comment.submit" var="commentSubmit"/>
                        <paw:button text="${commentSubmit}" variant="primary" size="md" type="submit"/>
                    </div>
                </paw:form>
            </div>
        </div>
    </div>
</div>
</body>
</html>