<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="position.apply.title"/> <c:out value="${position.title}"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>"/>
</head>
<body>

<paw:header userFirstName="${userFirstName}"/>

<div class="container mt-4">
    <spring:message code="position.apply.title" var="applyTitle"/>
    <c:set var="headingText" value="${applyTitle} ${position.title}"/>
    <paw:heading text="${headingText}" level="First" color="dark"/>

    <div class="content__detail-card">
        <div class="content__desc">
            <paw:paragraph text="${position.description}"/>
        </div>

        <div class="content__meta">
            <spring:message code="position.apply.requirements" var="requirementsLabel"/>
            <paw:paragraph text="📋 ${requirementsLabel} ${position.requirements}" />
            <c:if test="${not empty position.workHours}">
                <span class="content__meta-sep">•</span>
                <spring:message code="position.apply.workhours" var="workhoursLabel"/>
                <paw:paragraph text="⏰ ${workhoursLabel} ${position.workHours}" />
            </c:if>
        </div>

        <div class="content__meta">
            <spring:message code="position.apply.published.by" var="publishedBy"/>
            <paw:paragraph text="${publishedBy}" italic="true"/>
            <a href="<c:url value='/org/${position.organization.orgID}/profile'/>" class="content__meta-author">
                <paw:paragraph text="${position.organization.orgName}" italic="true" bold="true"/>
            </a>
        </div>
    </div>

    <div class="application-form">
        <spring:message code="position.apply.questions.title" var="questionsTitle"/>
        <paw:heading text="${questionsTitle}" level="Second" color="dark"/>
        <spring:message code="position.apply.questions.instruction" var="questionsInstruction"/>
        <paw:paragraph text="${questionsInstruction}" italic="true"/>

        <c:url value="/org/${position.organization.orgID}/open-position/${position.positionID}/apply-form" var="applyFormUrl"/>
        <paw:form action="${applyFormUrl}" method="post" modelAttribute="applicationForm" novalidate="true">
            <c:forEach var="question" items="${questions}" varStatus="status">
                <spring:message code="position.apply.question.label" var="questionLabel"/>
                <spring:message code="position.apply.answer.placeholder" var="answerPlaceholder"/>
                <c:set var="questionLabelText" value="${questionLabel} ${status.index + 1}: ${question.question}"/>
                <paw:input
                        label="${questionLabelText}"
                        name="answers[${question.questionID}]"
                        id="question_${question.questionID}"
                        type="textarea"
                        rows="4"
                        placeholder="${answerPlaceholder}"
                        modelAttribute="applicationForm"/>
            </c:forEach>
                    
            <div class="form-actions">
                <spring:message code="position.apply.submit" var="submitText"/>
                <paw:button text="${submitText}" variant="primary" size="lg" type="submit"/>
                <button type="button" onclick="window.location.href='<c:url value='/org/${position.organization.orgID}/open-position/${position.positionID}'/>'" class="btn btn-secondary btn-lg">
                    <spring:message code="position.apply.cancel"/>
                </button>
            </div>
        </paw:form>
    </div>
</div>

</body>
</html>