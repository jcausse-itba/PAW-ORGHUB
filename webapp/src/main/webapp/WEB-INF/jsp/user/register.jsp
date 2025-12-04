<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="user.register.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
    <script>
        window.i18n = {
            validation: {
                register: {
                    emailRequired: '<spring:message code="validation.register.email.required" javaScriptEscape="true"/>',
                    emailInvalid: '<spring:message code="validation.register.email.invalid" javaScriptEscape="true"/>',
                    emailMax: '<spring:message code="validation.register.email.max" javaScriptEscape="true"/>',
                    passwordRequired: '<spring:message code="validation.register.password.required" javaScriptEscape="true"/>',
                    passwordLength: '<spring:message code="validation.register.password.length" javaScriptEscape="true"/>',
                    confirmPasswordRequired: '<spring:message code="validation.register.confirmPassword.required" javaScriptEscape="true"/>',
                    confirmPasswordMatch: '<spring:message code="validation.register.confirmPassword.match" javaScriptEscape="true"/>',
                    firstNameRequired: '<spring:message code="validation.register.firstName.required" javaScriptEscape="true"/>',
                    firstNameLength: '<spring:message code="validation.register.firstName.length" javaScriptEscape="true"/>',
                    lastNameRequired: '<spring:message code="validation.register.lastName.required" javaScriptEscape="true"/>',
                    lastNameMax: '<spring:message code="validation.register.lastName.max" javaScriptEscape="true"/>'
                }
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/utils.js'/>" defer></script>
    <script type="module" src="<c:url value='/resources/js/registerForm.js'/>" defer></script>
</head>
<body>
<paw:header hideLoginButton="true" hideShortcuts="true" hideSideMenu="true"/>
<div class="form-container">
    <spring:message code="user.register.heading" var="registerHeading"/>
    <paw:heading text="${registerHeading}" level="First" color="dark"/>
    <c:url value="/register" var="postPath"/>
    <paw:form id="registerForm"
              modelAttribute="registerForm"
              action="${postPath}"
              method="post"
              autocomplete="off"
              novalidate="true">

        <spring:message code="user.register.email.label" var="emailLabel"/>
        <paw:input label="${emailLabel}"
                   name="email"
                   id="register_email"
                   required="true"
                   cssClass="form-control"/>

        <spring:message code="user.register.password.label" var="passwordLabel"/>
        <paw:input label="${passwordLabel}"
                   type="password"
                   name="password"
                   id="register_password"
                   required="true"
                   cssClass="form-control"/>

        <spring:message code="user.register.confirm.password.label" var="confirmPasswordLabel"/>
        <paw:input label="${confirmPasswordLabel}"
                   type="password"
                   name="confirmPassword"
                   id="register_confirmPassword"
                   required="true"
                   cssClass="form-control"/>
        <form:errors path="confirmPassword" cssClass="form-error"/>

        <spring:message code="user.register.first.name.label" var="firstNameLabel"/>
        <paw:input label="${firstNameLabel}"
                   name="firstName"
                   id="register_firstName"
                   required="true"
                   cssClass="form-control"/>

        <spring:message code="user.register.last.name.label" var="lastNameLabel"/>
        <paw:input label="${lastNameLabel}"
                   name="lastName"
                   id="register_lastName"
                   required="true"
                   cssClass="form-control"/>

    <div class="form-actions">
        <spring:message code="user.register.submit" var="submitText"/>
        <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
    </div>
    </paw:form>

    <a href="<c:url value='/login'/>">
        <spring:message code="user.register.login.link" var="loginLink"/>
        <paw:paragraph text="${loginLink}"/>
    </a>
</body>
</html>
