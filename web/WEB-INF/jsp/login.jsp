<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.focusup.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>FocusUp — Iniciar sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"/>
</head>
<body>

<div class="login-wrap">
    <div class="login-brand">
        <h1>FocusUp</h1>
        <p>Módulo de Gestión de Usuarios</p>
    </div>

    <div class="card">
        <h2>Iniciar sesión</h2>

        <%-- Mostrar mensaje de éxito (ej: después de registro) --%>
        <%
            String exito = request.getParameter("exito");
            if (exito != null && !exito.isBlank()) {
        %>
            <div class="alert alert-success"><%= HtmlUtil.escapar(exito) %></div>
        <% } %>

        <%-- Mostrar mensaje de sesión cerrada --%>
        <%
            String mensaje = request.getParameter("mensaje");
            if ("sesion_cerrada".equals(mensaje)) {
        %>
            <div class="alert alert-info">Has cerrado sesión correctamente.</div>
        <% } %>

        <%-- Mostrar errores de autenticación (enviados por el Servlet vía request) --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.escapar(request.getAttribute("error")) %></div>
        <% } %>

        <%--
            Formulario de inicio de sesión.
            Método POST → LoginServlet.doPost()
            Los datos viajan en el cuerpo de la petición (no visibles en la URL).
        --%>
        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="form-group">
                <label for="correo">Correo electrónico</label>
                <input type="email"
                       id="correo"
                       name="correo"
                       value="<%= HtmlUtil.escapar(request.getAttribute("correo")) %>"
                       placeholder="usuario@correo.com"
                       required/>
            </div>

            <div class="form-group">
                <label for="contrasena">Contraseña</label>
                <input type="password"
                       id="contrasena"
                       name="contrasena"
                       placeholder="••••••"
                       required/>
            </div>

            <button type="submit" class="btn btn-primary btn-full" style="margin-top:8px;">
                Ingresar
            </button>
        </form>

        <p style="text-align:center; margin-top:18px; font-size:.85rem; color:#7F8C8D;">
            ¿No tienes cuenta?
            <a href="${pageContext.request.contextPath}/registro">Regístrate aquí</a>
        </p>
    </div>
</div>

<footer>FocusUp © 2026 — GA7-220501096-AA2-EV02 | Daniel Castiblanco</footer>
</body>
</html>
