<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="postID" required="true" type="java.lang.Long" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="description" required="true" %>
<%@ attribute name="author" required="false" %>
<%@ attribute name="date" required="false" %>
<%@ attribute name="tags" required="false" type="java.util.Collection" %>
<%@ attribute name="order" required="false" %>

<spring:message code="comment.modal.title" var="modalTitle"/>
<paw:modal id="commentModal-${postID}" title="${modalTitle}" cssClass="modal--clean">

    <div class="comment-modal__content">
        <div class="comment-modal__post-preview">
            <div class="comment-modal__post-header">
                <div class="comment-modal__post-type">
                    <span class="comment-modal__post-icon">📝</span>
                    <spring:message code="comment.post.type" var="postType"/>
                    <span class="comment-modal__post-type-text"><c:out value="${postType}"/></span>
                </div>
                <div class="comment-modal__post-meta">
                    <c:if test="${not empty author}">
                        <spring:message code="comment.modal.author.by" var="authorBy"/>
                        <span class="comment-modal__post-author"><c:out value="${authorBy}"/> <strong><c:out value="${author}"/></strong></span>
                    </c:if>
                    <c:if test="${not empty date}">
                        <span class="comment-modal__post-date"><c:out value="${date}"/></span>
                    </c:if>
                </div>
            </div>
            
            <div class="comment-modal__post-content">
                <h3 class="comment-modal__post-title"><c:out value="${title}"/></h3>
                <p class="comment-modal__post-description"><c:out value="${description}"/></p>
            </div>
        </div>

        <div class="comment-modal__form-container">
            <div class="comment-modal__form-header">
                <spring:message code="comment.modal.form.title" var="formTitle"/>
                <h4><c:out value="${formTitle}"/></h4>
            </div>
            
            <c:url value="/comments/create" var="commentCreateUrl"/>
            <paw:form id="commentForm" cssClass="js-comment-form" method="POST" action="${commentCreateUrl}" novalidate="true" modelAttribute="commentForm">
                <input type="hidden" name="postID" value="<c:out value="${postID}"/>"/>
                <input type="hidden" name="context" value="modal"/>

                <div class="comment-modal__textarea-container">
                    <spring:message code="comment.modal.placeholder" var="placeholder"/>
                    <paw:input name="content" id="comment_content" label="" type="textarea" required="true"
                               placeholder="${placeholder}" 
                               cssClass="comment-modal__textarea" modelAttribute="commentForm"/>
                </div>

                <div class="comment-modal__submit-container">
                    <spring:message code="comment.modal.submit" var="submitText"/>
                    <paw:button text="${submitText}" variant="primary" size="md" type="submit"/>
                </div>
            </paw:form>
        </div>
    </div>

</paw:modal>