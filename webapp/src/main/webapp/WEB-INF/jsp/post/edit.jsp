<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="post.edit.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <script>
        window.i18n = {
            validation: {
                post: {
                    imageFormat: '<spring:message code="validation.post.image.format" javaScriptEscape="true"/>',
                    titleRequired: '<spring:message code="validation.post.title.required" javaScriptEscape="true"/>',
                    titleMin: '<spring:message code="validation.post.title.min" javaScriptEscape="true"/>',
                    titleMax: '<spring:message code="validation.post.title.max" javaScriptEscape="true"/>',
                    bodyRequired: '<spring:message code="validation.post.body.required" javaScriptEscape="true"/>',
                    bodyMin: '<spring:message code="validation.post.body.min" javaScriptEscape="true"/>',
                    bodyMax: '<spring:message code="validation.post.body.max" javaScriptEscape="true"/>'
                }
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/postForm.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>
<div class="form-container">
    <spring:message code="post.edit.heading" var="editHeading"/>
    <paw:heading text="${editHeading}" level="First" color="dark"/>
    <c:url value="/org/${orgID}/post/${postID}/edit" var="postEditUrl"/>

    <paw:form id="postForm" modelAttribute="postForm" method="post" action="${postEditUrl}" novalidate="true" autocomplete="off">
        <spring:message code="post.create.title.label" var="titleLabel"/>
        <spring:message code="post.create.title.placeholder" var="titlePlaceholder"/>
        <paw:input
                label="${titleLabel}"
                name="title"
                id="post_title"
                required="true"
                placeholder="${titlePlaceholder}"
                cssClass="form-control"
                value="${postForm.title}"/>

        <spring:message code="post.create.body.label" var="bodyLabel"/>
        <spring:message code="post.create.body.placeholder" var="bodyPlaceholder"/>
        <paw:input
                type="textarea"
                label="${bodyLabel}"
                name="body"
                id="post_body"
                rows="6"
                required="true"
                placeholder="${bodyPlaceholder}"
                cssClass="form-control"
                value="${postForm.body}"/>

        <spring:message code="post.create.tags.label" var="tagsLabel"/>
        <paw:tag-picker
                label="${tagsLabel}"
                name="tags"
                id="tags"
                items="${allTags}"
                selectedValues="${postForm.tags}"/>

        <div class="form-actions">
            <spring:message code="post.edit.submit" var="submitText"/>
            <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
            <c:url value="/post/${postID}" var="cancelUrl"/>
            <spring:message code="post.edit.cancel" var="cancelText"/>
            <a href="${cancelUrl}">
                <paw:button text="${cancelText}" type="button" variant="secondary" size="md"/>
            </a>
        </div>
    </paw:form>
</div>
</body>
</html>

