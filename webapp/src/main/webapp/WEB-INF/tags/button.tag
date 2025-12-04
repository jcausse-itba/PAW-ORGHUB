<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="text" required="true" %>
<%@ attribute name="size" required="false" %>
<%@ attribute name="cssClass" required="false" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="variant" required="false" %>
<%@ attribute name="icon" required="false" %>
<%@ attribute name="type" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="dataClose" required="false" type="java.lang.Boolean" %>
<%@ attribute name="dataModalTarget" required="false" %>
<%@attribute name="onclick" required="false" %>

<c:set var="btnSize" value="${empty size ? 'md' : size}"/>
<c:set var="btnVariant" value="${empty variant ? 'primary' : variant}"/>
<c:set var="btnClass" value="${empty cssClass ? '' : cssClass}"/>
<c:set var="btnType" value="${empty type ? 'button' : type}"/>

<c:set var="classes" value="btn btn-${btnVariant} btn-${btnSize} ${btnClass}"/>

<button
        type="${btnType}"
        class="${classes}"
        <c:if test="${not empty id}">id="${id}"</c:if>
        <c:if test="${disabled}">disabled</c:if>
        <c:if test="${dataClose}">data-close</c:if>
        <c:if test="${not empty dataModalTarget}">data-modal-target="${dataModalTarget}"</c:if>
        <c:if test="${not empty onclick}">onclick="${onclick}"</c:if>>
    <c:if test="${not empty icon}">
        <span class="btn__icon"><c:out value="${icon}"/></span>
    </c:if>
    <span><c:out value="${text}"/></span>
</button>
