<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="event.edit.title"/></title>
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
    <spring:message code="event.edit.heading" var="editHeading"/>
    <paw:heading text="${editHeading}" level="First" color="dark"/>
    <c:url value="/org/${orgID}/event/${eventID}/edit" var="eventEditUrl"/>

    <paw:form id="eventForm"
              modelAttribute="eventForm"
              action="${eventEditUrl}"
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
                   cssClass="form-control"
                   value="${eventForm.title}"/>

        <spring:message code="event.description.label" var="descriptionLabel"/>
        <spring:message code="event.description.placeholder" var="descriptionPlaceholder"/>
        <paw:input type="textarea"
                   label="${descriptionLabel}"
                   name="description"
                   id="event_description"
                   rows="6"
                   required="true"
                   placeholder="${descriptionPlaceholder}"
                   cssClass="form-control"
                   value="${eventForm.description}"/>

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
                   cssClass="form-control"
                   value="${eventForm.location}"/>

        <c:if test="${not empty currentImageID}">
            <div style="margin-bottom: 1rem;">
                <spring:message code="event.edit.current.image" var="currentImageLabel"/>
                <label style="display: block; margin-bottom: 0.5rem; font-weight: 600;">${currentImageLabel}</label>
                <c:url value="/image/${currentImageID}" var="currentImageUrl"/>
                <img src="${currentImageUrl}" alt="Current flyer" style="max-width: 300px; max-height: 200px; border-radius: 8px; border: 1px solid #e5e7eb;"/>
            </div>
        </c:if>

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
                items="${allTags}"
                selectedValues="${eventForm.tags}"/>

        <div class="form-actions">
            <spring:message code="event.edit.submit" var="submitText"/>
            <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
            <c:url value="/event/${eventID}" var="cancelUrl"/>
            <spring:message code="event.edit.cancel" var="cancelText"/>
            <a href="${cancelUrl}">
                <paw:button text="${cancelText}" type="button" variant="secondary" size="md"/>
            </a>
        </div>
    </paw:form>
</div>
</body>
</html>

