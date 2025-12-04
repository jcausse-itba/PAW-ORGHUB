<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="text"     required="true" %>
<%@ attribute name="level"    required="false" %>
<%@ attribute name="cssClass" required="false" %>
<%@ attribute name="color" required="false" %>

<c:set var="lvl" value="${empty level ? 'secondary' : fn:toLowerCase(level)}" />

<c:set var="classes"
       value="heading heading--${lvl} ${empty cssClass ? '' : cssClass} ${empty color ? '' : 'heading-' += color}" />
<c:choose>
    <c:when test="${lvl eq 'first'}">
        <h1 class="${classes}"><c:out value="${text}"/></h1>
    </c:when>
    <c:when test="${lvl eq 'second'}">
        <h2 class="${classes}"><c:out value="${text}"/></h2>
    </c:when>
    <c:when test="${lvl eq 'third'}">
        <h3 class="${classes}"><c:out value="${text}"/></h3>
    </c:when>
    <c:otherwise>
        <span class="${classes}"><c:out value="${text}"/></span>
    </c:otherwise>
</c:choose>