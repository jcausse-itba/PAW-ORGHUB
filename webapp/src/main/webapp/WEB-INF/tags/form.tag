<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="id"           required="false" %>
<%@ attribute name="action"       required="false" %>
<%@ attribute name="method"       required="false" %>
<%@ attribute name="cssClass"     required="false" %>
<%@ attribute name="novalidate"   required="false" type="java.lang.Boolean" %>
<%@ attribute name="multipart"    required="false" type="java.lang.Boolean" %>
<%@ attribute name="autocomplete" required="false" %>
<%@ attribute name="modelAttribute" required="false" %>

<c:set var="mthd"    value="${empty method ? 'post' : method}" />
<c:set var="classes" value="${empty cssClass ? '' : cssClass}" />
<c:set var="act"     value="${empty action ? '' : action}" />
<c:if test="${not empty modelAttribute}">
    <c:set var="pawModelAttribute" value="${modelAttribute}" scope="request"/>
</c:if>

<form
        <c:if test="${not empty id}">id="${id}"</c:if>
        <c:if test="${not empty act}">action="${act}"</c:if>
        method="${mthd}"
        <c:if test="${not empty classes}">class="${classes}"</c:if>
        <c:if test="${novalidate}">novalidate</c:if>
        <c:if test="${multipart}">enctype="multipart/form-data"</c:if>
        <c:if test="${not empty autocomplete}">autocomplete="${autocomplete}"</c:if>
>
    <jsp:doBody/>
</form>