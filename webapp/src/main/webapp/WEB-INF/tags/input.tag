<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags" %>

<%@ attribute name="label" required="false" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="type" required="false" %>
<%@ attribute name="rows" required="false" %>
<%@ attribute name="required" required="false" type="java.lang.Boolean" %>
<%@ attribute name="cssClass" required="false" %>
<%@ attribute name="placeholder" required="false" %>
<%@ attribute name="value" required="false" %>
<%@ attribute name="preview"   required="false" type="java.lang.Boolean" %>
<%@ attribute name="previewID" required="false" %>
<%@ attribute name="accept"    required="false" %>
<%@ attribute name="modelAttribute" required="false" %>

<c:set var="inputID" value="${empty id ? name : id}"/>
<c:set var="classes" value="input ${empty cssClass ? '' : cssClass}"/>
<c:set var="inputType" value="${empty type ? 'text' : type}"/>
<c:set var="r" value="${empty rows ? 3 : rows}"/>
<c:set var="model" value="${empty modelAttribute ? requestScope.pawModelAttribute : modelAttribute}" />

<div class="form-row">
    <c:if test="${not empty label}">
        <label for="${inputID}"><c:out value="${label}"/></label>
    </c:if>
    <c:choose>
        <c:when test="${not empty model}">
            <spring:bind path="${model}.${name}">
                <c:set var="hasError" value="${not empty status.errorMessages}"/>
                <c:set var="resolvedValue" value="${empty status.value ? value : status.value}"/>
                <c:set var="classesBound" value="${classes}${hasError ? ' input--error' : ''}"/>

                <c:choose>
                    <c:when test="${inputType eq 'textarea'}">
                        <textarea id="${inputID}" name="${name}" rows="${r}" class="${classesBound}"
                                  <c:if test="${not empty placeholder}">placeholder="<c:out value='${placeholder}'/>"</c:if>
                                  <c:if test="${required}">required</c:if>
                                  <c:if test="${hasError}">aria-invalid="true"</c:if>><c:out value="${status.value}"/></textarea>
                    </c:when>

                    <c:when test="${inputType eq 'file'}">
                        <input type="file" id="${inputID}" name="${name}" class="${classesBound}" accept="${empty accept ? 'image/png,image/jpeg' : accept}"
                               <c:if test="${required}">required</c:if> />
                        <c:if test="${preview}">
                            <c:set var="previewImageID" value="${empty previewID ? inputID.concat('_preview') : previewID}"/>
                            <img id="${previewImageID}" alt="<spring:message code='tag.input.preview.alt'/>" class="image-preview">
                        </c:if>
                    </c:when>

                    <c:when test="${inputType eq 'tags'}">
                        <div class="tags">
                            <c:set var="listID" value="${inputID}_list"/>
                            <c:set var="entryID" value="${inputID}_entry"/>
                            <div class="tags__list" id="${listID}"></div>
                            <input type="text" id="${entryID}" class="${classesBound}"
                                   <c:if test="${not empty placeholder}">placeholder="<c:out value='${placeholder}'/>"</c:if>
                                   <c:if test="${required}">required</c:if> />
                        </div>
                        <input type="hidden" id="${inputID}" name="${name}" value="<c:out value="${status.value}"/>" />
                    </c:when>

                    <c:when test="${inputType eq 'datetime-local'}">
                        <div class="datetime-picker-wrapper" data-datetime-picker>
                            <input type="${inputType}" id="${inputID}" name="${name}" class="datetime-picker-input" value="<c:out value='${status.value}'/>"
                                   <c:if test="${required}">required</c:if> />
                            <div class="datetime-picker-inner">
                                <div class="datetime-picker-fake">
                                    <span class="datetime-text"><spring:message code="date.picker.select"/></span>
                                    <span class="datetime-icon">📅</span>
                                </div>
                            </div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <input type="${inputType}" id="${inputID}" name="${name}" class="${classesBound}" value="<c:out value='${status.value}'/>"
                               <c:if test="${not empty placeholder}">placeholder="<c:out value='${placeholder}'/>"</c:if>
                               <c:if test="${hasError}">aria-invalid="true"</c:if>
                               <c:if test="${required}">required</c:if> />
                    </c:otherwise>
                </c:choose>
                <c:if test="${hasError}">
                    <c:forEach var="errorMsg" items="${status.errorMessages}">
                        <div class="form-error"><c:out value="${errorMsg}"/></div>
                    </c:forEach>
                </c:if>
            </spring:bind>
        </c:when>

        <c:otherwise>
            <c:choose>
                <c:when test="${inputType eq 'textarea'}">
                    <textarea id="${inputID}" name="${name}" rows="${r}" class="${classes}"
                              <c:if test="${not empty placeholder}">placeholder="<c:out value='${placeholder}'/>"</c:if>
                              <c:if test="${required}">required</c:if>><c:out value="${value}"/></textarea>
                </c:when>

                <c:when test="${inputType eq 'file'}">
                    <input type="file" id="${inputID}" name="${name}" class="${classes}" accept="${empty accept ? 'image/png,image/jpeg' : accept}"
                           <c:if test="${required}">required</c:if>/>
                    <c:if test="${preview}">
                        <c:set var="previewImageID2" value="${empty previewID ? inputID.concat('_preview') : previewID}"/>
                        <img id="${previewImageID2}" alt="<spring:message code='tag.input.preview.alt'/>" class="image-preview">
                    </c:if>
                </c:when>

                <c:when test="${inputType eq 'tags'}">
                    <div class="tags">
                        <c:set var="listID2" value="${inputID}_list"/>
                        <c:set var="entryID2" value="${inputID}_entry"/>
                        <div class="tags__list" id="${listID2}"></div>
                        <input type="text" id="${entryID2}" class="${classes}"
                               <c:if test="${not empty placeholder}">placeholder="<c:out value='${placeholder}'/>"</c:if>
                               <c:if test="${required}">required</c:if> />
                    </div>
                    <input type="hidden" id="${inputID}" name="${name}" value="<c:out value="${value}"/>" />
                </c:when>

                <c:otherwise>
                    <input type="${inputType}" id="${inputID}" name="${name}" class="${classes}" value="<c:out value='${value}'/>"
                           <c:if test="${not empty placeholder}">placeholder="<c:out value='${placeholder}'/>"</c:if>
                           <c:if test="${required}">required</c:if> />
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</div>