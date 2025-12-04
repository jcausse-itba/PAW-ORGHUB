<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="event.create.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <script>
        window.i18n = {
            validation: {
                event: {
                    titleRequired: '<spring:message code="validation.event.title.required" javaScriptEscape="true"/>',
                    titleLength: '<spring:message code="validation.event.title.length" javaScriptEscape="true"/>',
                    descriptionRequired: '<spring:message code="validation.event.description.required" javaScriptEscape="true"/>',
                    descriptionMax: '<spring:message code="validation.event.description.max" javaScriptEscape="true"/>',
                    locationRequired: '<spring:message code="validation.event.location.required" javaScriptEscape="true"/>',
                    locationMax: '<spring:message code="validation.event.location.max" javaScriptEscape="true"/>',
                    dateRequired: '<spring:message code="validation.event.date.required" javaScriptEscape="true"/>',
                    dateFuture: '<spring:message code="validation.event.date.future" javaScriptEscape="true"/>'
                }
            },
            datePicker: {
                select: '<spring:message code="date.picker.select" javaScriptEscape="true"/>'
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/eventForm.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>
<div class="form-container">
    <spring:message code="event.create.title" var="createTitle"/>
    <paw:heading text="${createTitle}" level="First" color="dark"/>
    <c:url value="/org/${orgID}/event/create" var="eventCreateUrl"/>

    <paw:form id="eventForm"
              modelAttribute="eventForm"
              action="${eventCreateUrl}"
              method="post"
              autocomplete="off"
              novalidate="true"
              multipart="true">

        <spring:message code="event.title.label" var="titleLabel"/>
        <spring:message code="event.title.placeholder" var="titlePlaceholder"/>
        <paw:input label="${titleLabel}"
                   name="title"
                   id="event_title"
                   required="true"
                   placeholder="${titlePlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="event.description.label" var="descriptionLabel"/>
        <spring:message code="event.description.placeholder" var="descriptionPlaceholder"/>
        <paw:input type="textarea"
                   label="${descriptionLabel}"
                   name="description"
                   id="event_description"
                   rows="6"
                   required="true"
                   placeholder="${descriptionPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="event.date.time.label" var="dateTimeLabel"/>
        <paw:input type="datetime-local"
                   label="${dateTimeLabel}"
                   name="eventDate"
                   id="event_date"
                   required="true"
                   cssClass="form-control"/>

        <spring:message code="event.location.label" var="locationLabel"/>
        <spring:message code="event.location.placeholder" var="locationPlaceholder"/>
        <paw:input label="${locationLabel}"
                   name="location"
                   id="event_location"
                   required="true"
                   placeholder="${locationPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="event.flyer.label" var="flyerLabel"/>
        <paw:input type="file"
                   label="${flyerLabel}"
                   name="flyer"
                   id="event_flyer"
                   accept="image/png,image/jpeg"
                   preview="true"
                   cssClass="form-control"/>

        <spring:message code="event.tags.label" var="tagsLabel"/>
        <paw:tag-picker
                label="${tagsLabel}"
                name="tags"
                id="tags"
                items="${allTags}"/>

        <div class="form-actions">
            <spring:message code="event.create.submit" var="submitText"/>
            <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
            <spring:message code="event.create.cancel" var="cancelText"/>
            <paw:button text="${cancelText}" type="button" variant="secondary" size="md" onclick="history.back()"/>
        </div>
    </paw:form>
</div>
</body>
</html>