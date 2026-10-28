<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Página de inicio: redirige según si hay sesión activa o no --%>
<%
    if (session.getAttribute("usuarioActivo") != null) {
        response.sendRedirect(request.getContextPath() + "/usuarios");
    } else {
        response.sendRedirect(request.getContextPath() + "/login");
    }
%>
