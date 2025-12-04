<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="label"    required="false" %>
<%@ attribute name="name"     required="true"  %>
<%@ attribute name="id"       required="false" %>
<%@ attribute name="items"    required="true"  type="java.util.List" %>
<%@ attribute name="cssClass" required="false" %>
<%@ attribute name="required" required="false" type="java.lang.Boolean" %>
<%@ attribute name="selectedValues" required="false" type="java.util.List" %>

<c:set var="inputID" value="${empty id ? name : id}"/>

<div class="form-row ${empty cssClass ? '' : cssClass}">
    <c:if test="${not empty label}">
        <label for="${inputID}"><c:out value="${label}"/></label>
    </c:if>
    <div class="tags-preview"></div>

    <div class="ms" id="${inputID}">
        <details class="ms__details">
            <summary class="ms__summary">
                <span class="ms__text"><spring:message code="tag.picker.choose"/></span>
                <span class="ms__chev">▾</span>
            </summary>
            <c:set var="limitMessageID" value="tag-limit-message-${inputID}"/>
            <div id="${limitMessageID}" class="tag-limit-message">
                <spring:message code="tag.limit.reached"/>
            </div>
            <div class="ms__panel">
                <div class="ms__list">
                    <c:forEach var="t" items="${items}">
                        <label class="ms__option">
                            <input type="checkbox"
                                   name="${name}"
                                   value="<c:out value="${t.tagID}"/>"
                                   <c:if test="${required}">required</c:if>
                                   <c:if test="${not empty selectedValues}">
                                       <c:forEach var="selectedTag" items="${selectedValues}">
                                           <c:if test="${selectedTag == t.tagID}">checked</c:if>
                                       </c:forEach>
                                   </c:if> />
                            <span class="ms__box"></span>
                            <span class="ms__label"><c:out value="${t.tagName}"/></span>
                        </label>
                    </c:forEach>
                </div>
            </div>
        </details>
    </div>
</div>

<script>
(function() {
    const picker = document.getElementById('${inputID}');
    if (!picker) return;
    
    const details = picker.querySelector('.ms__details');
    if (!details) return;
    
    details.addEventListener('toggle', function() {
        if (details.hasAttribute('open')) {
            picker.classList.add('ms--open');
            
            document.querySelectorAll('.ms__details[open]').forEach(function(otherDetails) {
                if (otherDetails !== details) {
                    otherDetails.removeAttribute('open');
                    const otherPicker = otherDetails.closest('.ms');
                    if (otherPicker) {
                        otherPicker.classList.remove('ms--open');
                    }
                }
            });
        } else {
            picker.classList.remove('ms--open');
        }
    });
})();
</script>