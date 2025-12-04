<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="message" required="true" type="java.lang.String" %>

<c:if test="${not empty message}">
    <script>
        window.onload = function() {
            alert("<c:out value='${message}'/>");
        };
    </script>
</c:if>