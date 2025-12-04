<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="partnership.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
</head>
<body>
<paw:header hideShortcuts="true" hideSideMenu="true" hideLoginButton="true"/>
<div class="form-container">
    <spring:message code="partnership.title" var="partnershipTitle"/>
    <paw:heading text="${partnershipTitle}" level="First" color="dark"/>
    <spring:message code="partnership.description" var="partnershipDescription"/>
    <paw:paragraph 
        text="${partnershipDescription}" 
        cssClass="text-primary" bold="false"/>
    
    <c:url value="/partnership" var="postPath"/>
    <paw:form id="partnershipForm"
              modelAttribute="partnershipRequestForm"
              action="${postPath}"
              method="post"
              autocomplete="off"
              novalidate="true">

        <spring:message code="partnership.institution.label" var="institutionLabel"/>
        <spring:message code="partnership.institution.placeholder" var="institutionPlaceholder"/>
        <paw:input label="${institutionLabel}"
                   name="requestedName"
                   id="partnership_requestedName"
                   required="true"
                   placeholder="${institutionPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="partnership.domain.label" var="domainLabel"/>
        <spring:message code="partnership.domain.placeholder" var="domainPlaceholder"/>
        <paw:input label="${domainLabel}"
                   name="requestedDomainName"
                   id="partnership_requestedDomainName"
                   required="true"
                   placeholder="${domainPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="partnership.admin.label" var="adminLabel"/>
        <spring:message code="partnership.admin.placeholder" var="adminPlaceholder"/>
        <paw:input label="${adminLabel}"
                   name="requestedAdminEmail"
                   id="partnership_requestedAdminEmail"
                   type="email"
                   required="true"
                   placeholder="${adminPlaceholder}"
                   cssClass="form-control"/>
        <spring:message code="partnership.admin.help" var="adminHelp"/>
        <paw:paragraph text="${adminHelp}" cssClass="text-primary" bold="false"/>

<%--        <paw:input label="Email de contacto"--%>
<%--                   name="contactEmail"--%>
<%--                   id="partnership_contactEmail"--%>
<%--                   type="email"--%>
<%--                   required="true"--%>
<%--                   placeholder="Ej.: contacto@itba.edu.ar"--%>
<%--                   cssClass="form-control"/>--%>
<%--        <paw:paragraph text="A este email se le enviará una notificación cuando se apruebe la solicitud, o se requiera alguna información." cssClass="text-primary" bold="false"/>--%>

        <div class="form-actions">
            <spring:message code="partnership.submit" var="submitText"/>
            <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
        </div>
    </paw:form>
</div>
<script>
    (function () {
        const form = document.getElementById('partnershipForm')
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
