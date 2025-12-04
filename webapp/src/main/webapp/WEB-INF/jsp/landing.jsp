<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="paw" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title><spring:message code="landing.title"/></title>
    <link rel="icon" type="image/x-icon" href="<c:url value='/resources/images/favicon.ico'/>">
    <link rel="stylesheet" href="<c:url value='/resources/css/index.css'/>">
</head>
<body>
    <paw:header hideShortcuts="true" hideSideMenu="true"/>
    <div class="landing-hero">
        <div class="landing-section">
            <h1><spring:message code="landing.hero.title"/></h1>
            <p><spring:message code="landing.hero.description"/></p>
            <spring:message code="landing.cta.student" var="studentText"/>
            <spring:message code="landing.cta.admin" var="adminText"/>
            <div class="landing-cta">
                <a href="<c:url value='/login'/>">
                    <paw:button text="${studentText}" variant="primary" size="lg"/>
                </a>
                <a href="<c:url value='/partnership'/>">
                    <paw:button text="${adminText}" variant="secondary" size="lg"/>
                </a>
            </div>
        </div>
    </div>
    <div class="landing-section">
        <div class="landing-section">
            <spring:message code="landing.why.title" var="whyTitle"/>
            <paw:heading text="${whyTitle}" level="Second" color="dark"/>
            <div class="landing-features">
                <div class="feature-card">
                    <div class="feature-icon">🎯</div>
                    <h3><spring:message code="landing.feature.discover.title"/></h3>
                    <p><spring:message code="landing.feature.discover.desc"/></p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">📅</div>
                    <h3><spring:message code="landing.feature.events.title"/></h3>
                    <p><spring:message code="landing.feature.events.desc"/></p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">💬</div>
                    <h3><spring:message code="landing.feature.connect.title"/></h3>
                    <p><spring:message code="landing.feature.connect.desc"/></p>
                </div>
            </div>
        </div>
    </div>
    <div class="landing-section">
        <div class="landing-section">
            <spring:message code="landing.target.title" var="targetTitle"/>
            <paw:heading text="${targetTitle}" level="Second" color="dark"/>
            <div class="landing-target">
                <div class="target-card">
                    <h3>👨‍🎓 <spring:message code="landing.students.title"/></h3>
                    <p><spring:message code="landing.students.desc"/></p>
                    <ul>
                        <li><spring:message code="landing.students.feature1"/></li>
                        <li><spring:message code="landing.students.feature2"/></li>
                        <li><spring:message code="landing.students.feature3"/></li>
                        <li><spring:message code="landing.students.feature4"/></li>
                        <li><spring:message code="landing.students.feature5"/></li>
                    </ul>
                    <spring:message code="landing.students.cta" var="studentsCta"/>
                    <div class="landing-cta-button">
                        <a href="<c:url value='/login'/>">
                            <paw:button text="${studentsCta}" variant="primary" size="md"/>
                        </a>
                    </div>
                </div>
                
                <div class="target-card">
                    <h3>🏛️ <spring:message code="landing.admins.title"/></h3>
                    <p><spring:message code="landing.admins.desc"/></p>
                    <ul>
                        <li><spring:message code="landing.admins.feature1"/></li>
                        <li><spring:message code="landing.admins.feature2"/></li>
                        <li><spring:message code="landing.admins.feature3"/></li>
                        <li><spring:message code="landing.admins.feature4"/></li>
                        <li><spring:message code="landing.admins.feature5"/></li>
                    </ul>
                    <spring:message code="landing.admins.cta" var="adminsCta"/>
                    <div class="landing-cta-button">
                        <a href="<c:url value='/partnership'/>">
                            <paw:button text="${adminsCta}" variant="primary" size="md"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
