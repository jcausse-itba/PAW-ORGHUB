<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%@ attribute name="author" required="true" type="java.lang.String" %>
<%@ attribute name="text" required="true" type="java.lang.String" %>
<%@ attribute name="commentID" required="false" type="java.lang.Long" %>
<%@ attribute name="postID" required="false" type="java.lang.Long" %>
<%@ attribute name="orgID" required="false" type="java.lang.Long" %>
<%@ attribute name="userPositions" required="false" type="java.util.List" %>

<div class="comment-card">
    <div class="comment-header">
        <div class="comment-author-info">
            <paw:paragraph text="${author}" bold="true"/>
            <c:if test="${not empty userPositions}">
                <div class="comment-user-positions">
                    <c:forEach var="position" items="${userPositions}">
                        <span class="comment-position-badge level-${position.level}">
                            <c:out value="${position.positionName}"/>
                        </span>
                    </c:forEach>
                </div>
            </c:if>
        </div>
        <c:if test="${canDeleteComments && not empty commentID && not empty postID}">
            <form action="<c:url value='/comments/delete'/>" method="post" class="comment-delete-form">
                <input type="hidden" name="commentID" value="${commentID}"/>
                <input type="hidden" name="postID" value="${postID}"/>
                <input type="hidden" name="orgID" value="${orgID}"/>
                <button type="submit" class="comment-delete-btn" onclick="return confirm('<spring:message code="comment.delete.confirm"/>')">
                    🗑️
                </button>
            </form>
        </c:if>
    </div>
    <paw:paragraph text="${text}"/>
</div>