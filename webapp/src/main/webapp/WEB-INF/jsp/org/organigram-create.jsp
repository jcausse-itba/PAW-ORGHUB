<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="organigram.create.position"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/views.css'/>">
    <script>
        window.i18n = {
            validation: {
                organigram: {
                    positionNameRequired: '<spring:message code="validation.organigram.positionName.required" javaScriptEscape="true"/>',
                    positionNameLength: '<spring:message code="validation.organigram.positionName.length" javaScriptEscape="true"/>',
                    descriptionRequired: '<spring:message code="validation.organigram.description.required" javaScriptEscape="true"/>',
                    descriptionMax: '<spring:message code="validation.organigram.description.max" javaScriptEscape="true"/>',
                    sortOrderRequired: '<spring:message code="validation.organigram.sortOrder.required" javaScriptEscape="true"/>',
                    emailInvalid: '<spring:message code="validation.org.email.invalid" javaScriptEscape="true"/>'
                }
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>
<div class="container">
    <div class="organigram-create-container">
        <spring:message code="organigram.create.position.title.for" var="createPositionTitleFor"/>
        <c:set var="fullCreateTitle" value="${createPositionTitleFor} ${organizationName}"/>
        <paw:heading text="${fullCreateTitle}" level="First" color="dark"/>
        
        <c:if test="${not empty error}">
            <paw:paragraph text="${error}" cssClass="form-error"/>
        </c:if>
        
        <div class="organigram-form-section">
            <div class="organigram-help">
                <spring:message code="organigram.help.title" var="helpTitle"/>
                <paw:heading text="📋 ${helpTitle}" level="Third" color="dark"/>
                <div class="help-content">
                    <div class="help-item">
                        <strong><spring:message code="organigram.help.levels.title"/></strong> <spring:message code="organigram.help.levels.description"/>
                        <ul>
                            <li><strong><spring:message code="organigram.help.levels.level0"/></strong></li>
                            <li><strong><spring:message code="organigram.help.levels.level1"/></strong></li>
                            <li><strong><spring:message code="organigram.help.levels.level2"/></strong></li>
                            <li><strong><spring:message code="organigram.help.levels.continue"/></strong></li>
                        </ul>
                    </div>
                    <div class="help-item">
                        <strong><spring:message code="organigram.help.order.title"/></strong> <spring:message code="organigram.help.order.description"/>
                        <ul>
                            <li><strong><spring:message code="organigram.help.order.order1"/></strong></li>
                            <li><strong><spring:message code="organigram.help.order.order2"/></strong></li>
                            <li><strong><spring:message code="organigram.help.order.continue"/></strong></li>
                        </ul>
                    </div>
                </div>
            </div>
            
            <c:url value="/org/${orgID}/organigram/create" var="createUrl"/>
            <form:form modelAttribute="organigramForm" method="post" action="${createUrl}">
                <div class="form-row">
                    <div class="form-group">
                        <form:label path="positionName">
                            <spring:message code="organigram.position.name"/>
                        </form:label>
                        <spring:message code="organigram.form.placeholder.position" var="positionPlaceholder"/>
                        <form:input path="positionName" cssClass="form-control" cssErrorClass="form-control input--error" placeholder="${positionPlaceholder}"/>
                        <form:errors path="positionName" cssClass="form-error"/>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <form:label path="description">
                            <spring:message code="organigram.description"/>
                        </form:label>
                        <spring:message code="organigram.form.placeholder.description" var="descriptionPlaceholder"/>
                        <form:textarea path="description" cssClass="form-control" cssErrorClass="form-control input--error" rows="3" placeholder="${descriptionPlaceholder}"/>
                        <form:errors path="description" cssClass="form-error"/>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <spring:message code="organigram.parent.position" var="parentLabel"/>
                        <spring:message code="organigram.parent.position.none" var="noParentLabel"/>
                        <paw:select-picker
                                label="${parentLabel}"
                                name="parentPositionID"
                                id="parentPositionID"
                                items="${organigram}"
                                value="${organigramForm.parentPositionID}"
                                noneLabel="${noParentLabel}"/>
                        <form:errors path="parentPositionID" cssClass="form-error"/>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <form:label path="sortOrder">
                            <spring:message code="organigram.sort.order"/>
                        </form:label>
                        <form:input path="sortOrder" type="number" cssClass="form-control" cssErrorClass="form-control input--error"/>
                        <small class="form-help"><spring:message code="organigram.sort.order.help"/></small>
                        <form:errors path="sortOrder" cssClass="form-error"/>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <form:label path="assignedUserEmail">
                            <spring:message code="organigram.assigned.user.email"/>
                        </form:label>
                        <spring:message code="organigram.form.placeholder.email" var="emailPlaceholder"/>
                        <form:input path="assignedUserEmail" cssClass="form-control" cssErrorClass="form-control input--error" placeholder="${emailPlaceholder}"/>
                        <small class="form-help"><spring:message code="organigram.assigned.user.email.help"/></small>
                        <form:errors path="assignedUserEmail" cssClass="form-error"/>
                    </div>
                </div>
                
                <div class="form-actions">
                    <spring:message code="organigram.create" var="createText"/>
                    <paw:button text="${createText}" variant="primary" type="submit"/>
                    
                    <c:url value="/org/${orgID}/organigram" var="backUrl"/>
                    <spring:message code="organigram.view" var="viewText"/>
                    <a href="${backUrl}">
                        <paw:button text="${viewText}" variant="secondary"/>
                    </a>
                </div>
            </form:form>
        </div>
    </div>
</div>

</body>
</html>
