<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="userFirstName" required="false" %>
<%@ attribute name="userRole" required="false" %>
<%@ attribute name="canCreateOrganizations" required="false" %>

<!-- Hamburger Menu Button -->
<button id="hamburger-menu-btn" class="hamburger-menu-btn" aria-label="<spring:message code='menu.open.aria'/>" title="<spring:message code='menu.open.title'/>">
    ☰
</button>

<!-- Menu Modal -->
<div id="hamburger-menu-modal" class="hamburger-menu-modal" aria-hidden="true">
    <div class="hamburger-menu-backdrop"></div>
    <div class="hamburger-menu-content">
        <div class="hamburger-menu-header">
            <h2 class="hamburger-menu-title"><spring:message code="menu.title"/></h2>
            <button class="hamburger-menu-close" aria-label="<spring:message code='menu.close'/>">✖</button>
        </div>
        <nav class="hamburger-menu-nav">
            <ul class="hamburger-menu-list">
                <c:choose>
                    <c:when test="${userRole != 'SYSTEM_ADMIN'}">
                        <li class="hamburger-menu-item">
                            <a href="<c:url value='/'/>" class="hamburger-menu-link">
                                <span class="hamburger-menu-icon">🏠</span>
                                <span class="hamburger-menu-text"><spring:message code="menu.home"/></span>
                            </a>
                        </li>
                        <li class="hamburger-menu-item">
                            <a href="<c:url value='/org'/>" class="hamburger-menu-link">
                                <span class="hamburger-menu-icon">🏛️</span>
                                <span class="hamburger-menu-text"><spring:message code="menu.organizations"/></span>
                            </a>
                        </li>
                        <c:if test="${not empty requestScope.managedOrganizations}">
                            <li class="hamburger-menu-item hamburger-menu-dropdown">
                                <div class="hamburger-menu-dropdown-header">
                                    <span class="hamburger-menu-icon">⚙️</span>
                                    <span class="hamburger-menu-text"><spring:message code="menu.my.organizations"/></span>
                                    <span class="hamburger-menu-dropdown-arrow">▼</span>
                                </div>
                                <ul class="hamburger-menu-dropdown-list">
                                    <c:forEach items="${requestScope.managedOrganizations}" var="org">
                                        <li class="hamburger-menu-dropdown-item">
                                            <a href="<c:url value='/org/${org.orgID}/profile'/>" class="hamburger-menu-dropdown-link">
                                                <span class="hamburger-menu-dropdown-text"><c:out value="${org.orgName}"/></span>
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </li>
                        </c:if>
                        <c:if test="${canCreateOrganizations == true}">
                            <li class="hamburger-menu-item">
                                <a href="<c:url value='/org/create'/>" class="hamburger-menu-link">
                                    <span class="hamburger-menu-icon">➕</span>
                                    <span class="hamburger-menu-text"><spring:message code="menu.create.org"/></span>
                                </a>
                            </li>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <li class="hamburger-menu-item hamburger-menu-divider">
                            <span class="hamburger-menu-text hamburger-menu-section-title"><spring:message code="menu.administration"/></span>
                        </li>
                        <li class="hamburger-menu-item">
                            <a href="<c:url value='/sysadmin'/>" class="hamburger-menu-link">
                                <span class="hamburger-menu-icon">⚙️</span>
                                <span class="hamburger-menu-text"><spring:message code="menu.dashboard"/></span>
                            </a>
                        </li>
                        <li class="hamburger-menu-item">
                            <a href="<c:url value='/sysadmin/institutions'/>" class="hamburger-menu-link">
                                <span class="hamburger-menu-icon">🏛️</span>
                                <span class="hamburger-menu-text"><spring:message code="menu.institutions"/></span>
                            </a>
                        </li>
                        <li class="hamburger-menu-item">
                            <a href="<c:url value='/sysadmin/partnership'/>" class="hamburger-menu-link">
                                <span class="hamburger-menu-icon">🤝</span>
                                <span class="hamburger-menu-text"><spring:message code="menu.requests"/></span>
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${not empty userFirstName}">
                        <li class="hamburger-menu-item">
                            <c:choose>
                                <c:when test="${userRole == 'SYSTEM_ADMIN'}">
                                    <form method="post" action="<c:url value='/logout'/>" class="hamburger-menu-link-form">
                                        <button type="submit" class="hamburger-menu-link hamburger-menu-link--button">
                                            <span class="hamburger-menu-icon">🚪</span>
                                            <span class="hamburger-menu-text"><spring:message code="nav.logout"/></span>
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <a href="<c:url value='/profile'/>" class="hamburger-menu-link">
                                        <span class="hamburger-menu-icon">👤</span>
                                        <span class="hamburger-menu-text"><c:out value='${userFirstName}'/></span>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="hamburger-menu-item">
                            <a href="<c:url value='/login'/>" class="hamburger-menu-link">
                                <span class="hamburger-menu-icon">👤</span>
                                <span class="hamburger-menu-text"><spring:message code="menu.login"/></span>
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
    </div>
</div>
