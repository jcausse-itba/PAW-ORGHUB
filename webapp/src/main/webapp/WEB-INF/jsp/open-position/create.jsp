<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="position.create.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <script>
        window.i18n = {
            validation: {
                position: {
                    titleRequired: '<spring:message code="validation.position.title.required" javaScriptEscape="true"/>',
                    titleMin: '<spring:message code="validation.position.title.min" javaScriptEscape="true"/>',
                    titleMax: '<spring:message code="validation.position.title.max" javaScriptEscape="true"/>',
                    descriptionRequired: '<spring:message code="validation.position.description.required" javaScriptEscape="true"/>',
                    descriptionMax: '<spring:message code="validation.position.description.max" javaScriptEscape="true"/>',
                    requirementsRequired: '<spring:message code="validation.position.requirements.required" javaScriptEscape="true"/>',
                    requirementsMax: '<spring:message code="validation.position.requirements.max" javaScriptEscape="true"/>',
                    questionsAllEmpty: '<spring:message code="validation.position.questions.allEmpty" javaScriptEscape="true"/>',
                    questionsEmpty: '<spring:message code="validation.position.questions.empty" javaScriptEscape="true"/>',
                    questionsMax: '<spring:message code="validation.position.questions.max" javaScriptEscape="true"/>',
                    questionsCompleteAll: '<spring:message code="validation.position.questions.completeAll" javaScriptEscape="true"/>'
                }
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/openPositionForm.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>
<div class="form-container">
    <spring:message code="position.create.title" var="createTitle"/>
    <paw:heading text="${createTitle}" level="First" color="dark"/>
    <c:url value="/org/${orgID}/open-position/create" var="positionCreateUrl"/>

    <paw:form id="openPositionForm"
              modelAttribute="openPositionForm"
              action="${positionCreateUrl}"
              method="post"
              autocomplete="off"
              novalidate="true">

        <spring:message code="position.create.title.label" var="titleLabel"/>
        <spring:message code="position.create.title.placeholder" var="titlePlaceholder"/>
        <paw:input label="${titleLabel}"
                   name="title"
                   id="position_title"
                   required="true"
                   placeholder="${titlePlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="position.create.description.label" var="descriptionLabel"/>
        <spring:message code="position.create.description.placeholder" var="descriptionPlaceholder"/>
        <paw:input type="textarea"
                   label="${descriptionLabel}"
                   name="description"
                   id="position_description"
                   rows="6"
                   required="true"
                   placeholder="${descriptionPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="position.create.requirements.label" var="requirementsLabel"/>
        <spring:message code="position.create.requirements.placeholder" var="requirementsPlaceholder"/>
        <paw:input type="textarea"
                   label="${requirementsLabel}"
                   name="requirements"
                   id="position_requirements"
                   rows="6"
                   required="true"
                   placeholder="${requirementsPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="position.create.workhours.label" var="workhoursLabel"/>
        <spring:message code="position.create.workhours.placeholder" var="workhoursPlaceholder"/>
        <paw:input label="${workhoursLabel}"
                   name="workhours"
                   id="position_workhours"
                   placeholder="${workhoursPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="position.create.organigram.label" var="organigramLabel"/>
        <spring:message code="position.create.organigram.none" var="noOrganigramLabel"/>
        <paw:select-picker
                label="${organigramLabel}"
                name="organigramPositionID"
                id="organigramPositionID"
                items="${vacantOrganigramPositions}"
                value="${openPositionForm.organigramPositionID}"
                noneLabel="${noOrganigramLabel}"/>
        <small class="form-help">
            <spring:message code="position.create.organigram.help"/>
        </small>

        <spring:message code="position.create.tags.label" var="tagsLabel"/>
        <paw:tag-picker
                label="${tagsLabel}"
                name="tags"
                id="tags"
                items="${allTags}"/>

        <spring:message code="position.create.questions.title" var="questionsTitle"/>
        <paw:heading text="${questionsTitle}" level="second" color="mid"/>
        <div id="questions-container" class="questions-block">
            <c:choose>
                <c:when test="${not empty openPositionForm.questions and openPositionForm.questions.size() > 1}">
                    <c:forEach var="question" items="${openPositionForm.questions}" varStatus="status">
                        <div class="question-item">
                            <div class="question-header">
                                <span class="question-label"><spring:message code="position.create.question.label"/> ${status.index + 1}</span>
                            </div>
                            <div class="input-with-button">
                                <spring:message code="position.create.question.placeholder" var="questionPlaceholder"/>
                                <paw:input
                                        name="questions"
                                        id="question_${status.index + 1}"
                                        placeholder="${questionPlaceholder}"
                                        cssClass="form-control"
                                        value="${question}"/>
                                <c:if test="${status.last}">
                                    <button type="button" class="add-btn" onclick="addQuestion()">+</button>
                                </c:if>
                                <c:if test="${not status.first}">
                                    <button type="button" class="remove-btn" onclick="removeQuestion(this)">×</button>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="question-item">
                        <div class="question-header">
                            <span class="question-label"><spring:message code="position.create.question.label"/> 1</span>
                        </div>
                        <div class="input-with-button">
                            <spring:message code="position.create.question.placeholder" var="questionPlaceholderDefault"/>
                            <paw:input
                                    name="questions"
                                    id="question_1"
                                    placeholder="${questionPlaceholderDefault}"
                                    cssClass="form-control"/>
                            <button type="button" class="add-btn" onclick="addQuestion()">+</button>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="form-actions">
            <spring:message code="position.create.submit" var="submitText"/>
            <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
            <spring:message code="position.create.cancel" var="cancelText"/>
            <paw:button text="${cancelText}" type="button" variant="secondary" size="md" onclick="history.back()"/>
        </div>
    </paw:form>
</div>
</body>
</html>
