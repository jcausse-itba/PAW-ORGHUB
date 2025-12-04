<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="cssClass" required="false" %>
<%@ attribute name="bold" required="false" type="java.lang.Boolean" %>
<%@ attribute name="italic" required="false" type="java.lang.Boolean" %>
<%@ attribute name="underline" required="false" type="java.lang.Boolean" %>

<c:set var="classes" value="text-primary ${empty cssClass ? '' : cssClass}"/>
<c:if test="${bold}"><c:set var="classes" value="${classes} text-bold"/></c:if>
<c:if test="${italic}"><c:set var="classes" value="${classes} text-italic"/></c:if>
<c:if test="${underline}"><c:set var="classes" value="${classes} text-underline"/></c:if>

<p class="${classes}"><c:out value="${text}"/></p>