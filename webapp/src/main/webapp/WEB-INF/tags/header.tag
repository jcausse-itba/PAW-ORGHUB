<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>

<%@ attribute name="userFirstName"%> <!-- This value is shown in the user button. If null, "Iniciar Sesion". -->
<%@ attribute name="userRole" required="false" %>
<%@ attribute name="hideShortcuts" required="false" %>
<%@ attribute name="hideLoginButton" required="false" %>
<%@ attribute name="hideSideMenu" required="false" %>

<header class="main-header">
    <div class="header-content">
        <a href="<c:url value='/'/>" class="logo">
            <img src="<c:url value='/resources/images/siteid.png'/>" alt="OrgHub" class="logo-image">
        </a>

        <ul class="nav-links">
            <c:if test="${hideShortcuts == null || hideShortcuts == false}">
                <c:if test="${userRole != 'SYSTEM_ADMIN'}">
                    <li>
                        <a href="<c:url value='/org'/>" class="nav-link">
                            <span class="nav-icon">🏛️</span>
                            <span class="nav-text"><spring:message code="nav.organizations"/></span>
                        </a>
                    </li>
                </c:if>
            </c:if>
            <c:if test="${hideLoginButton == null || hideLoginButton == false}">
                <c:choose>
                    <c:when test="${not empty userFirstName}">
                        <li>
                            <c:choose>
                                <c:when test="${userRole == 'SYSTEM_ADMIN'}">
                                    <a href="<c:url value='/logout'/>" class="nav-link">
                                        <span class="nav-icon">🚪</span>
                                        <span class="nav-text"><spring:message code="nav.logout"/></span>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="<c:url value='/profile'/>" class="nav-link">
                                        <span class="nav-icon">👤</span>
                                        <span class="nav-text"><c:out value="${userFirstName}"/></span>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li>
                            <a href="<c:url value='/login'/>" class="nav-link">
                                <span class="nav-icon">👤</span>
                                <span class="nav-text"><spring:message code="nav.login"/></span>
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </ul>
        
        <c:if test="${userRole != 'SYSTEM_ADMIN'}">
            <c:set var="currentLang" value="${pageContext.request.locale.language}"/>
            <c:if test="${empty currentLang}">
                <c:set var="currentLang" value="es"/>
            </c:if>
            <c:if test="${not empty requestScope['org.springframework.web.servlet.i18n.LocaleResolver.LOCALE']}">
                <c:set var="currentLang" value="${requestScope['org.springframework.web.servlet.i18n.LocaleResolver.LOCALE'].language}"/>
            </c:if>
            <div class="language-selector">
                <a href="<c:url value='/changeLanguage?lang=es'/>" class="lang-link ${currentLang == 'es' ? 'active' : ''}">
                    <spring:message code="language.spanish"/>
                </a>
                <span class="lang-separator">|</span>
                <a href="<c:url value='/changeLanguage?lang=en'/>" class="lang-link ${currentLang == 'en' ? 'active' : ''}">
                    <spring:message code="language.english"/>
                </a>
            </div>
        </c:if>
        
        <c:if test="${hideSideMenu == null || hideSideMenu == false}">
            <paw:side-menu userFirstName="${userFirstName}" userRole="${userRole}" canCreateOrganizations="${canCreateOrganizations}" />
        </c:if>
    </div>
</header>

<script src="<c:url value='/resources/js/hamburgerMenu.js'/>"></script>
