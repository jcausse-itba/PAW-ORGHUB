<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id"       required="true"  %>
<%@ attribute name="title"    required="true"  %>
<%@ attribute name="cssClass" required="false" %>

<c:set var="modalClasses" value="modal ${empty cssClass ? '' : cssClass}"/>
<div id="${id}" class="${modalClasses}" aria-hidden="true">
    <div class="modal-content">
        <paw:button text="×" type="button" variant="secondary" cssClass="modal-close" dataClose="true"/>
        <paw:heading text="${title}" level="second" color="blue-dark"/>
        <jsp:doBody/>
    </div>
</div>