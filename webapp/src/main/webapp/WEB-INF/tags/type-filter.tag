<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="action"      required="true"  %>
<%@ attribute name="value"       required="false" %>
<%@ attribute name="carryName"   required="false" %>
<%@ attribute name="carryTags" required="false" type="java.util.List" %>
<%@ attribute name="carryMatch"  required="false" %>
<%@ attribute name="carrySort"  required="false" %>

<c:set var="current" value="${empty value ? 'posts' : value}" />

<div class="ms">
    <form method="get" action="<c:url value='${action}'/>" class="ms__form">
        <c:if test="${not empty carryName and not empty carryTags}">
            <c:forEach var="v" items="${carryTags}">
                <input type="hidden" name="${carryName}" value="<c:out value='${v}'/>" />
            </c:forEach>
        </c:if>
        <c:if test="${not empty carryMatch}">
            <input type="hidden" name="match" value="<c:out value='${carryMatch}'/>" />
        </c:if>
        <c:if test="${not empty carrySort}">
            <input type="hidden" name="order" value="<c:out value='${carrySort}'/>" />
        </c:if>

        <details class="ms__details">
            <summary class="ms__summary">
                <span class="ms__text">
                    <c:choose>
                        <c:when test="${current eq 'events'}"><spring:message code="filter.type.events"/></c:when>
                        <c:when test="${current eq 'posts'}"><spring:message code="filter.type.posts"/></c:when>
                        <c:when test="${current eq 'openpositions'}"><spring:message code="filter.type.openpositions"/></c:when>
                        <c:otherwise><spring:message code="filter.type.all"/></c:otherwise>
                    </c:choose>
                </span>
                <span class="ms__chev">▾</span>
            </summary>
            <div class="ms__panel">
                <div class="ms__list">
                    <label class="ms__radio">
                        <input type="radio" name="type" value="posts"
                               <c:if test="${current eq 'posts'}">checked</c:if>
                               onchange="this.form.submit()"/>
                        <span><spring:message code="filter.type.posts"/></span>
                    </label>

                    <label class="ms__radio">
                        <input type="radio" name="type" value="events"
                               <c:if test="${current eq 'events'}">checked</c:if>
                               onchange="this.form.submit()"/>
                        <span><spring:message code="filter.type.events"/></span>
                    </label>
                    <label class="ms__radio">
                        <input type="radio" name="type" value="openpositions"
                               <c:if test="${current eq 'openpositions'}">checked</c:if>
                               onchange="this.form.submit()"/>
                        <span><spring:message code="filter.type.openpositions"/></span>
                    </label>
                    <label class="ms__radio">
                        <input type="radio" name="type" value="all"
                               <c:if test="${current eq 'all'}">checked</c:if>
                               onchange="this.form.submit()"/>
                        <span><spring:message code="filter.type.all"/></span>
                    </label>
                </div>
            </div>
        </details>
    </form>
</div>