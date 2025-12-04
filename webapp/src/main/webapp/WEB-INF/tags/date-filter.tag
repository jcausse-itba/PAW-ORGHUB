<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="action"      required="true"  %>
<%@ attribute name="value"       required="false" %>
<%@ attribute name="carryName"   required="false" %>
<%@ attribute name="carryTags" required="false" type="java.util.List" %>
<%@ attribute name="carryMatch"   required="false" %>
<%@ attribute name="carryType"   required="false" %>

<c:set var="current" value="${empty value ? 'newest' : value}" />

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
        <c:if test="${not empty carryType}">
            <input type="hidden" name="type" value="<c:out value='${carryType}'/>" />
        </c:if>
        <details class="ms__details">
            <summary class="ms__summary">
        <span class="ms__text">
          <c:choose>
              <c:when test="${current eq 'oldest'}"><spring:message code="date.filter.oldest"/></c:when>
              <c:otherwise><spring:message code="date.filter.newest"/></c:otherwise>
          </c:choose>
        </span>
                <span class="ms__chev">▾</span>
            </summary>
            <div class="ms__panel">
                <div class="ms__list">
                    <label class="ms__radio">
                        <input type="radio" name="order" value="newest"
                               <c:if test="${current ne 'oldest'}">checked</c:if>
                               onchange="this.form.submit()"/>
                        <span><spring:message code="date.filter.newest"/></span>
                    </label>

                    <label class="ms__radio">
                        <input type="radio" name="order" value="oldest"
                               <c:if test="${current eq 'oldest'}">checked</c:if>
                               onchange="this.form.submit()"/>
                        <span><spring:message code="date.filter.oldest"/></span>
                    </label>
                </div>
            </div>
        </details>
    </form>
</div>