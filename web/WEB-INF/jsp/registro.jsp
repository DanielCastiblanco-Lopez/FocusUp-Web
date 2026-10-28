<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.focusup.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>FocusUp — Registro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"/>
</head>
<body>

<header class="topbar">
    <a href="${pageContext.request.contextPath}/login" class="brand">FocusUp</a>
    <nav>
        <a href="${pageContext.request.contextPath}/login">Iniciar sesión</a>
    </nav>
</header>

<div class="container" style="max-width:560px;">
    <div class="card">
        <h2>Crear cuenta nueva</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.escapar(request.getAttribute("error")) %></div>
        <% } %>

        <%--
            Formulario de registro público.
            Método POST → RegistroServlet.doPost()
        --%>
        <form action="${pageContext.request.contextPath}/registro" method="post">

            <div class="form-row">
                <div class="form-group">
                    <label for="nombres">Nombres *</label>
                    <input type="text" id="nombres" name="nombres"
                           value="<%= HtmlUtil.escapar(request.getParameter("nombres")) %>"
                           placeholder="Ej: Daniel" required/>
                </div>
                <div class="form-group">
                    <label for="apellidos">Apellidos *</label>
                    <input type="text" id="apellidos" name="apellidos"
                           value="<%= HtmlUtil.escapar(request.getParameter("apellidos")) %>"
                           placeholder="Ej: Castiblanco" required/>
                </div>
            </div>

            <div class="form-group">
                <label for="cedula">Cédula *</label>
                <input type="text" id="cedula" name="cedula"
                       value="<%= HtmlUtil.escapar(request.getParameter("cedula")) %>"
                       placeholder="Número de documento (mín. 6 dígitos)" required/>
            </div>

            <div class="form-group">
                <label for="correo">Correo electrónico *</label>
                <input type="email" id="correo" name="correo"
                       value="<%= HtmlUtil.escapar(request.getParameter("correo")) %>"
                       placeholder="tu@correo.com" required/>
            </div>

            <div class="form-group">
                <label for="contrasena">Contraseña * (mínimo 6 caracteres)</label>
                <input type="password" id="contrasena" name="contrasena"
                       placeholder="••••••" required/>
            </div>

            <div class="form-group">
                <label for="fechaNac">Fecha de nacimiento</label>
                <input type="date" id="fechaNac" name="fechaNac"
                       value="<%= HtmlUtil.escapar(request.getParameter("fechaNac")) %>"/>
            </div>

            <div style="display:flex; gap:12px; margin-top:8px;">
                <button type="submit" class="btn btn-primary">Registrarme</button>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<footer>FocusUp © 2026 — GA7-220501096-AA2-EV02 | Daniel Castiblanco</footer>
</body>
</html>
