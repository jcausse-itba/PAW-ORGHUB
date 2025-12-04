<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="org.create.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <script>
        window.i18n = {
            validation: {
                org: {
                    logoFormat: '<spring:message code="validation.org.logo.format" javaScriptEscape="true"/>',
                    nameRequired: '<spring:message code="validation.org.name.required" javaScriptEscape="true"/>',
                    nameMin: '<spring:message code="validation.org.name.min" javaScriptEscape="true"/>',
                    nameMax: '<spring:message code="validation.org.name.max" javaScriptEscape="true"/>',
                    emailRequired: '<spring:message code="validation.org.email.required" javaScriptEscape="true"/>',
                    emailInvalid: '<spring:message code="validation.org.email.invalid" javaScriptEscape="true"/>',
                    emailMax: '<spring:message code="validation.org.email.max" javaScriptEscape="true"/>',
                    managerEmailRequired: '<spring:message code="validation.org.manager.email.required" javaScriptEscape="true"/>',
                    managerEmailInvalid: '<spring:message code="validation.org.manager.email.invalid" javaScriptEscape="true"/>',
                    managerEmailMax: '<spring:message code="validation.org.manager.email.max" javaScriptEscape="true"/>',
                    descriptionRequired: '<spring:message code="validation.org.description.required" javaScriptEscape="true"/>',
                    descriptionMin: '<spring:message code="validation.org.description.min" javaScriptEscape="true"/>',
                    descriptionMax: '<spring:message code="validation.org.description.max" javaScriptEscape="true"/>',
                    instagramUrl: '<spring:message code="validation.org.instagram.url" javaScriptEscape="true"/>',
                    instagramMax: '<spring:message code="validation.org.instagram.max" javaScriptEscape="true"/>',
                    twitterUrl: '<spring:message code="validation.org.twitter.url" javaScriptEscape="true"/>',
                    twitterMax: '<spring:message code="validation.org.twitter.max" javaScriptEscape="true"/>'
                }
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/orgForm.js'/>" defer></script>
</head>
<body>
<paw:header userFirstName="${userFirstName}"/>
<div class="form-container">
    <spring:message code="org.create.title" var="createTitle"/>
    <paw:heading text="${createTitle}" level="First" color="dark"/>
    <spring:message code="org.create.desc" var="createDesc"/>
    <paw:paragraph text="${createDesc}"/>
    <c:url value="/org/create" var="postPath"/>

    <paw:form id="orgForm"
              action="${postPath}"
              method="post"
              autocomplete="off"
              novalidate="true"
              modelAttribute="orgForm">

        <spring:message code="org.form.name" var="nameLabel"/>
        <spring:message code="org.form.name.placeholder" var="namePlaceholder"/>
        <paw:input label="${nameLabel}"
                   name="orgName"
                   id="org_name"
                   required="true"
                   placeholder="${namePlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="org.form.email" var="emailLabel"/>
        <spring:message code="org.form.email.placeholder" var="emailPlaceholder"/>
        <paw:input label="${emailLabel}"
                   name="orgEmail"
                   id="org_email"
                   required="true"
                   placeholder="${emailPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="org.form.manager.email" var="managerEmailLabel"/>
        <spring:message code="org.form.manager.email.placeholder" var="managerEmailPlaceholder"/>
        <paw:input label="${managerEmailLabel}"
                   name="managerEmail"
                   id="manager_email"
                   required="true"
                   placeholder="${managerEmailPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="org.form.description" var="descriptionLabel"/>
        <spring:message code="org.form.description.placeholder" var="descriptionPlaceholder"/>
        <paw:input label="${descriptionLabel}"
                   name="description"
                   id="org_desc"
                   type="textarea"
                   rows="3"
                   placeholder="${descriptionPlaceholder}"
                   required="true"/>

        <spring:message code="org.form.instagram" var="instagramLabel"/>
        <spring:message code="org.form.instagram.placeholder" var="instagramPlaceholder"/>
        <paw:input label="${instagramLabel}"
                   name="socialLink1"
                   id="org_ig"
                   type="url"
                   placeholder="${instagramPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="org.form.twitter" var="twitterLabel"/>
        <spring:message code="org.form.twitter.placeholder" var="twitterPlaceholder"/>
        <paw:input label="${twitterLabel}"
                   name="socialLink2"
                   id="org_tw"
                   type="url"
                   placeholder="${twitterPlaceholder}"
                   cssClass="form-control"/>

    <%--    <paw:input type="file"--%>
    <%--               label="Logo (PNG/JPG)"--%>
    <%--               name="logo" id="org_logo"--%>
    <%--               preview="true"/>--%>

        <div class="form-actions">
            <spring:message code="org.form.submit" var="submitText"/>
            <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
        </div>
    </paw:form>
</div>
</body>
</html>