<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="auth.login.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
</head>
<body>
    <paw:header hideLoginButton="true" hideShortcuts="true" hideSideMenu="true"/>
    <div class="form-container">
        <spring:message code="auth.login.title" var="loginTitle"/>
        <paw:heading text="${loginTitle}" level="First" color="dark"/>
        <c:if test="${param.error != null}">
                <spring:message code="auth.login.error" var="loginError"/>
                <paw:paragraph text="${loginError}" cssClass="form-error"/>
        </c:if>
        <c:url value="/login" var="postPath"/>
        <paw:form id="loginForm"
                    action="${postPath}"
                    method="post"
                    autocomplete="off"
                    novalidate="true">

            <spring:message code="auth.login.email" var="emailLabel"/>
            <paw:input label="${emailLabel}"
                       name="email"
                       id="login_email"
                       required="true"
                       cssClass="form-control"/>

            <spring:message code="auth.login.password" var="passwordLabel"/>
            <paw:input label="${passwordLabel}"
                       type="password"
                       name="password"
                       id="login_password"
                       required="true"
                       cssClass="form-control"/>

            <label>
                <input name="rememberMe" type="checkbox"/> <spring:message code="auth.login.remember"/>
            </label>

            <div class="form-actions">
                <spring:message code="auth.login.submit" var="submitText"/>
                <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
            </div>
        </paw:form>

        <a href="<c:url value='/register'/>">
            <spring:message code="auth.register.link" var="registerLink"/>
            <paw:paragraph text="${registerLink}"/>
        </a>

<script>
    (function () {
        const form = document.getElementById('loginForm')
        form.addEventListener('submit', function() {
            const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
            }
        });
    })();
</script>
</body>
</html>
