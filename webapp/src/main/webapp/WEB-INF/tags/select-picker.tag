<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="label"    required="false" %>
<%@ attribute name="name"     required="true"  %>
<%@ attribute name="id"       required="false" %>
<%@ attribute name="items"    required="true"  type="java.util.List" %>
<%@ attribute name="value"    required="false" %>
<%@ attribute name="cssClass" required="false" %>
<%@ attribute name="required" required="false" type="java.lang.Boolean" %>
<%@ attribute name="displayProperty" required="false" %>
<%@ attribute name="valueProperty" required="false" %>
<%@ attribute name="noneLabel" required="false" %>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean" %>

<c:set var="inputID" value="${empty id ? name : id}"/>
<c:set var="displayProp" value="${empty displayProperty ? 'positionName' : displayProperty}"/>
<c:set var="valueProp" value="${empty valueProperty ? 'organigramID' : valueProperty}"/>

<div class="form-row ${empty cssClass ? '' : cssClass}">
    <c:if test="${not empty label}">
        <label for="${inputID}"><c:out value="${label}"/></label>
    </c:if>

    <div class="ms ${disabled ? 'ms--disabled' : ''}" id="${inputID}">
        <details class="ms__details" ${disabled ? 'style="pointer-events: none; opacity: 0.6;"' : ''}>
            <summary class="ms__summary" ${disabled ? 'style="cursor: not-allowed; opacity: 0.6;"' : ''}>
                <c:set var="selectedID" value="${inputID}_selected"/>
                <span class="ms__text" id="${selectedID}">
                    <c:choose>
                        <c:when test="${not empty noneLabel}">
                            <c:out value="${noneLabel}"/>
                        </c:when>
                        <c:otherwise>
                            <spring:message code="select.picker.choose"/>
                        </c:otherwise>
                    </c:choose>
                </span>
                <span class="ms__chev">▾</span>
            </summary>
            <div class="ms__panel">
                <div class="ms__list">
                    <c:if test="${not empty noneLabel}">
                        <label class="ms__option ms__option--radio">
                            <input type="radio" 
                                   name="${name}" 
                                   value=""
                                   <c:set var="noneID" value="${inputID}_none"/>
                                   id="${noneID}"
                                   <c:if test="${empty value}">checked</c:if>
                                   <c:if test="${disabled}">disabled</c:if> />
                            <span class="ms__radio-box"></span>
                            <span class="ms__label"><c:out value="${noneLabel}"/></span>
                        </label>
                    </c:if>
                    <c:forEach var="item" items="${items}">
                        <c:set var="itemValue" value=""/>
                        <c:set var="itemDisplay" value=""/>
                        <c:choose>
                            <c:when test="${displayProp eq 'positionName'}">
                                <c:set var="itemDisplay">
                                    <c:out value="${item.positionName}"/>
                                    <c:if test="${not empty item.level}">
                                        (<spring:message code="organigram.position.level.display"/> ${item.level})
                                    </c:if>
                                </c:set>
                                <c:set var="itemValue" value="${item.organigramID}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="itemDisplay" value="${item[displayProp]}"/>
                                <c:set var="itemValue" value="${item[valueProp]}"/>
                            </c:otherwise>
                        </c:choose>
                        <c:set var="isSelected" value="false"/>
                        <c:if test="${not empty value and not empty itemValue}">
                            <c:set var="currentValue" value="${value}"/>
                            <c:set var="currentItemValue" value="${itemValue}"/>
                            <c:if test="${currentValue == currentItemValue or currentValue eq currentItemValue}">
                                <c:set var="isSelected" value="true"/>
                            </c:if>
                        </c:if>
                        <label class="ms__option ms__option--radio">
                            <input type="radio" 
                                   name="${name}" 
                                   value="${itemValue}"
                                   <c:set var="itemRadioID" value="${inputID}_${itemValue}"/>
                                   id="${itemRadioID}"
                                   <c:if test="${isSelected}">checked</c:if>
                                   <c:if test="${disabled}">disabled</c:if>
                                   <c:if test="${required and empty noneLabel and empty value}">required</c:if> />
                            <span class="ms__radio-box"></span>
                            <span class="ms__label"><c:out value="${itemDisplay}"/></span>
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
    const selectedText = picker.querySelector('#${inputID}_selected');
    const radios = picker.querySelectorAll('input[type="radio"]');
    const isDisabled = ${disabled ? 'true' : 'false'};
    
    if (!isDisabled) {
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
    }
    
    if (!isDisabled) {
    radios.forEach(function(radio) {
        radio.addEventListener('change', function() {
            if (this.checked) {
                const label = this.closest('.ms__option').querySelector('.ms__label').textContent.trim();
                selectedText.textContent = label;
                details.removeAttribute('open');
                picker.classList.remove('ms--open');
            }
        });
    });
    }
    
    const checkedRadio = picker.querySelector('input[type="radio"]:checked');
    if (checkedRadio) {
        const label = checkedRadio.closest('.ms__option').querySelector('.ms__label').textContent.trim();
        selectedText.textContent = label;
    }
})();
</script>

