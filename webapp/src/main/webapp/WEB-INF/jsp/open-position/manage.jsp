<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="position.manage.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>"/>
    <script>
        window.i18n = {
            validation: {
                comment: {
                    empty: '<spring:message code="validation.comment.empty" javaScriptEscape="true"/>',
                    min: '<spring:message code="validation.comment.min" javaScriptEscape="true"/>',
                    max: '<spring:message code="validation.comment.max" javaScriptEscape="true"/>'
                }
            }
        };
    </script>
    <script type="module" src="<c:url value='/resources/js/modal.js'/>" defer></script>
</head>
<body>

<paw:header userFirstName="${userFirstName}"/>

<div class="container mt-4">
    <spring:message code="position.manage.title" var="manageTitle"/>
    <c:set var="headingText" value="${manageTitle}: ${position.title}"/>
    <paw:heading text="${headingText}" level="First" color="dark"/>
    
    <div class="content__detail-card">
        <div class="content__desc">
            <paw:paragraph text="${position.description}"/>
        </div>
        <div class="content__meta">
            <spring:message code="position.manage.requirements" var="requirementsLabel"/>
            <paw:paragraph text="📋 ${requirementsLabel} ${position.requirements}" />
            <c:if test="${not empty position.workHours}">
                <span class="content__meta-sep">•</span>
                <spring:message code="position.manage.workhours" var="workhoursLabel"/>
                <paw:paragraph text="⏰ ${workhoursLabel} ${position.workHours}" />
            </c:if>
        </div>
    </div>

    <div class="filter-section mt-4 mb-3">
        <div class="filter-buttons">
            <c:url value="/org/${orgID}/open-position/${positionID}/manage" var="allUrl">
                <c:param name="status" value="all"/>
            </c:url>
            <c:url value="/org/${orgID}/open-position/${positionID}/manage" var="pendingUrl">
                <c:param name="status" value="pending"/>
            </c:url>
            <c:url value="/org/${orgID}/open-position/${positionID}/manage" var="acceptedUrl">
                <c:param name="status" value="accepted"/>
            </c:url>
            <c:url value="/org/${orgID}/open-position/${positionID}/manage" var="rejectedUrl">
                <c:param name="status" value="rejected"/>
            </c:url>
            
            <a href="${allUrl}" class="filter-btn ${statusFilter == 'all' ? 'active' : ''}">
                <spring:message code="position.manage.filters.all"/> (${fn:length(allApplications)})
            </a>
            <a href="${pendingUrl}" class="filter-btn ${statusFilter == 'pending' ? 'active' : ''}">
                <spring:message code="position.manage.filters.pending"/> (${pendingCount})
            </a>
            <a href="${acceptedUrl}" class="filter-btn ${statusFilter == 'accepted' ? 'active' : ''}">
                <spring:message code="position.manage.filters.accepted"/> (${acceptedCount})
            </a>
            <a href="${rejectedUrl}" class="filter-btn ${statusFilter == 'rejected' ? 'active' : ''}">
                <spring:message code="position.manage.filters.rejected"/> (${rejectedCount})
            </a>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty applications}">
            <div class="alert alert-info mt-3">
                        <c:choose>
                            <c:when test="${statusFilter == 'all'}">
                                <spring:message code="position.manage.no.applications" var="noApplications"/>
                                <paw:paragraph text="${noApplications}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'pending'}">
                                <spring:message code="position.manage.no.pending" var="noPending"/>
                                <paw:paragraph text="${noPending}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'accepted'}">
                                <spring:message code="position.manage.no.accepted" var="noAccepted"/>
                                <paw:paragraph text="${noAccepted}" italic="true"/>
                            </c:when>
                            <c:when test="${statusFilter == 'rejected'}">
                                <spring:message code="position.manage.no.rejected" var="noRejected"/>
                                <paw:paragraph text="${noRejected}" italic="true"/>
                            </c:when>
                            <c:otherwise>
                                <spring:message code="position.manage.no.applications" var="noApplicationsDefault"/>
                                <paw:paragraph text="${noApplicationsDefault}" italic="true"/>
                            </c:otherwise>
                        </c:choose>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-hover table-bordered rounded shadow-sm mt-3">
                    <thead class="thead-dark">
                    <tr>
                        <th scope="col"><spring:message code="position.manage.table.name"/></th>
                        <th scope="col"><spring:message code="position.manage.table.email"/></th>
                        <th scope="col"><spring:message code="position.manage.table.status"/></th>
                        <c:if test="${hasQuestions}">
                            <th scope="col" class="text-center"><spring:message code="position.manage.table.form"/></th>
                        </c:if>
                        <th scope="col" class="text-center"><spring:message code="position.manage.table.actions"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="application" items="${applications}">
                        <tr>
                            <td><c:out value="${application.user.firstName}"/> <c:out value="${application.user.lastName}"/></td>
                            <td><c:out value="${application.user.userEmail}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${application.status.name() == 'PENDING'}">
                                        <span class="status-badge badge-warning text-uppercase"><spring:message code="position.manage.status.pending"/></span>
                                    </c:when>
                                    <c:when test="${application.status.name() == 'ACCEPTED'}">
                                        <span class="status-badge badge-success text-uppercase"><spring:message code="position.manage.status.accepted"/></span>
                                    </c:when>
                                    <c:when test="${application.status.name() == 'REJECTED'}">
                                        <span class="status-badge badge-danger text-uppercase"><spring:message code="position.manage.status.rejected"/></span>
                                    </c:when>
                                </c:choose>
                            </td>
                            <c:if test="${hasQuestions}">
                                <td class="text-center">
                                    <c:set var="appAnswers" value="${answersMap[application.applicationID]}"/>
                                    <c:if test="${not empty appAnswers}">
                                        <button type="button" class="event-btn-pill btn-view" data-modal-target="#application-modal-${application.applicationID}">
                                            <spring:message code="position.manage.actions.view.form"/>
                                        </button>
                                    </c:if>
                                </td>
                            </c:if>
                            <td class="text-center">
                                <form method="post" action="<c:url value='/org/${orgID}/open-position/${positionID}/manage/update'/>" style="display:inline-block; margin-right: 4px;">
                                    <input type="hidden" name="applicationID" value="${application.applicationID}"/>
                                    <input type="hidden" name="status" value="ACCEPTED"/>
                                    <button type="submit" class="event-btn-pill btn-accept" 
                                            ${application.status.name() == 'ACCEPTED' ? 'disabled' : ''}><spring:message code="position.manage.actions.accept"/></button>
                                </form>
                                <form method="post" action="<c:url value='/org/${orgID}/open-position/${positionID}/manage/update'/>" style="display:inline-block;">
                                    <input type="hidden" name="applicationID" value="${application.applicationID}"/>
                                    <input type="hidden" name="status" value="REJECTED"/>
                                    <button type="submit" class="event-btn-pill btn-reject" 
                                            ${application.status.name() == 'REJECTED' ? 'disabled' : ''}><spring:message code="position.manage.actions.reject"/></button>
                                </form>
                            </td>
                        </tr>
                        
                        <c:if test="${hasQuestions}">
                            <c:set var="appAnswers" value="${answersMap[application.applicationID]}"/>
                            <c:if test="${not empty appAnswers}">
                                <spring:message code="position.manage.modal.title" var="modalTitle"/>
                                <paw:modal id="application-modal-${application.applicationID}" title="${modalTitle}">
                                <div class="application-details">
                                    <spring:message code="position.manage.modal.applicant" var="applicantLabel"/>
                                    <paw:paragraph text="${applicantLabel} ${application.user.firstName} ${application.user.lastName}" bold="true"/>
                                    <spring:message code="position.manage.modal.email" var="emailLabel"/>
                                    <paw:paragraph text="${emailLabel} ${application.user.userEmail}" bold="true"/>
                                    <p>
                                        <strong><spring:message code="position.manage.modal.status"/> </strong>
                                        <c:choose>
                                            <c:when test="${application.status.name() == 'PENDING'}">
                                                <span class="status-badge badge-warning"><spring:message code="position.manage.status.pending"/></span>
                                            </c:when>
                                            <c:when test="${application.status.name() == 'ACCEPTED'}">
                                                <span class="status-badge badge-success"><spring:message code="position.manage.status.accepted"/></span>
                                            </c:when>
                                            <c:when test="${application.status.name() == 'REJECTED'}">
                                                <span class="status-badge badge-danger"><spring:message code="position.manage.status.rejected"/></span>
                                            </c:when>
                                        </c:choose>
                                    </p>
                                    
                                    <hr>
                                    
                                    <spring:message code="position.manage.modal.answers.title" var="answersTitle"/>
                                    <paw:heading text="${answersTitle}" level="Third" color="dark"/>
                                    
                                    <c:choose>
                                        <c:when test="${not empty appAnswers}">
                                            <c:forEach var="answer" items="${appAnswers}" varStatus="answerStatus">
                                                <div class="answer-section">
                                                    <div class="question-text"><spring:message code="position.manage.modal.question"/> ${answerStatus.index + 1}: <c:out value="${answer.question.question}"/></div>
                                                    <div class="answer-text"><c:out value="${answer.answer}"/></div>
                                                </div>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message code="position.manage.modal.no.answers" var="noAnswers"/>
                                            <paw:paragraph text="${noAnswers}" italic="true"/>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                </paw:modal>
                            </c:if>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
