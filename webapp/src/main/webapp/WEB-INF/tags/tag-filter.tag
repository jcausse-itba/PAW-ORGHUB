<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%@ attribute name="action"      required="true"  %>
<%@ attribute name="items"       required="true"  type="java.util.List" %>
<%@ attribute name="values"      required="false" type="java.util.List" %>
<%@ attribute name="paramName"   required="false" %>
<%@ attribute name="carryName"   required="false" %>
<%@ attribute name="carrySort" required="false" %>
<%@ attribute name="showMatch"   required="false" type="java.lang.Boolean" %>
<%@ attribute name="match"       required="false" %>
<%@ attribute name="carryType"   required="false" %>

<c:set var="qname" value="${empty paramName ? 'tag' : paramName}" />
<c:set var="matchMode" value="${empty match ? 'any' : match}" />

<c:url var="clearUrl" value="${action}">
    <c:if test="${not empty carryName and not empty carrySort}">
        <c:param name="${carryName}" value="${carrySort}"/>
    </c:if>
    <c:if test="${not empty carryType}">
        <c:param name="type" value="${carryType}"/>
    </c:if>
</c:url>

<div class="ms">
    <form method="get" action="<c:url value='${action}'/>" class="ms__form">
        <c:if test="${not empty carryName and not empty carrySort}">
            <input type="hidden" name="${carryName}" value="<c:out value="${carrySort}"/>"/>
        </c:if>
        <c:if test="${not empty carryType}">
            <input type="hidden" name="type" value="<c:out value="${carryType}"/>"/>
        </c:if>

        <details class="ms__details">
            <summary class="ms__summary">
                <span class="ms__text">
                  <c:choose>
                      <c:when test="${empty values}"><spring:message code="tag.filter.title"/></c:when>
                      <c:otherwise><c:out value="${fn:length(values)}"/> <spring:message code="tag.filter.selected"/></c:otherwise>
                  </c:choose>
                </span>
                <span class="ms__chev">▾</span>
            </summary>
            <div class="ms__panel">
                <div class="ms__head">
                    <div class="ms__match">
                        <span class="ms__match-title"><spring:message code="tag.filter.match.title"/></span>
                        <label class="ms__radio">
                            <input type="radio" name="match" value="any"
                                   <c:if test="${matchMode eq 'any'}">checked</c:if>/>
                            <span><spring:message code="tag.filter.match.any"/></span>
                        </label>
                        <label class="ms__radio">
                            <input type="radio" name="match" value="all"
                                   <c:if test="${matchMode eq 'all'}">checked</c:if>/>
                            <span><spring:message code="tag.filter.match.all"/></span>
                        </label>
                    </div>
                </div>

                <div class="ms__list">
                    <c:forEach var="t" items="${items}">
                        <c:set var="checked" value="false"/>
                        <c:if test="${not empty values}">
                            <c:forEach var="v" items="${values}">
                                <c:if test="${v == t.tagID}"><c:set var="checked" value="true"/></c:if>
                            </c:forEach>
                        </c:if>

                        <label class="ms__option">
                            <input type="checkbox" name="${qname}" value="<c:out value="${t.tagID}"/>"
                                   <c:if test="${checked}">checked</c:if> />
                            <span class="ms__box"></span>
                            <span class="ms__label"><c:out value="${t.tagName}"/></span>
                        </label>
                    </c:forEach>
                </div>

                <div class="ms__actions">
                    <spring:message code="tag.filter.clear" var="clearText"/>
                    <paw:button
                            text="${clearText}"
                            variant="secondary"
                            size="md"
                            onclick="event.preventDefault(); window.location.href='${clearUrl}'"/>
                    <spring:message code="tag.filter.apply" var="applyText"/>
                    <paw:button
                            text="${applyText}"
                            variant="primary"
                            size="md"
                            type="submit"/>
                </div>
            </div>
        </details>
    </form>
</div>