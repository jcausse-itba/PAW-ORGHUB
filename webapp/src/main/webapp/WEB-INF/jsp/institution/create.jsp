<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="institution.create.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
</head>
<body>
<paw:header userFirstName="${userFirstName}" userRole="${userRole}"/>
<div class="form-container">
    <spring:message code="institution.create.title" var="createTitle"/>
    <paw:heading text="${createTitle}" level="First" color="dark"/>
    <c:url value="/sysadmin/institution/create" var="postPath"/>
    <paw:form id="institutionForm"
              modelAttribute="institutionForm"
              action="${postPath}"
              method="post"
              autocomplete="off"
              novalidate="true">

        <spring:message code="institution.create.name.label" var="nameLabel"/>
        <paw:input label="${nameLabel}"
                   name="name"
                   id="institution_name"
                   required="true"
                   cssClass="form-control"/>

        <spring:message code="institution.create.domain.label" var="domainLabel"/>
        <spring:message code="institution.create.domain.placeholder" var="domainPlaceholder"/>
        <paw:input label="${domainLabel}"
                   name="domainName"
                   id="institution_domainName"
                   required="true"
                   placeholder="${domainPlaceholder}"
                   cssClass="form-control"/>

        <spring:message code="institution.create.admin.label" var="adminLabel"/>
        <spring:message code="institution.create.admin.placeholder" var="adminPlaceholder"/>
        <paw:input label="${adminLabel}"
                   name="adminEmail"
                   id="institution_adminEmail"
                   required="true"
                   placeholder="${adminPlaceholder}"
                   cssClass="form-control"/>

        <div class="form-actions">
            <spring:message code="institution.create.submit" var="submitText"/>
            <paw:button text="${submitText}" type="submit" variant="primary" size="md"/>
        </div>
    </paw:form>
</div>
<script>
    (function () {
        const form = document.getElementById('institutionForm')
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