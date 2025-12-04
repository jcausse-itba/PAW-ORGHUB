<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="partnership.success.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
</head>
<body>
<paw:header hideShortcuts="true" hideSideMenu="true" hideLoginButton="true"/>
<div class="form-container">
    <div style="text-align: center; padding: 2rem 0;">
        <div style="font-size: 4rem; margin-bottom: 1rem;">✅</div>
        <spring:message code="partnership.success.heading" var="successHeading"/>
        <paw:heading text="${successHeading}" level="First" color="dark"/>
        <spring:message code="partnership.success.description" var="successDescription"/>
        <paw:paragraph 
            text="${successDescription}" 
            cssClass="text-primary" bold="false"/>
        
        <div style="margin-top: 2rem;">
            <a href="<c:url value='/landing'/>">
                <spring:message code="partnership.success.back" var="backText"/>
                <paw:button text="${backText}" variant="primary" size="md"/>
            </a>
        </div>
    </div>
</div>
</body>
</html>
