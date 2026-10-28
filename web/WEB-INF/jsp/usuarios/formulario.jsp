<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.focusup.modelo.Usuario, com.focusup.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>FocusUp — <%= HtmlUtil.escapar(request.getAttribute("titulo")) %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"/>
</head>
<body>

<%
    com.focusup.modelo.Usuario usuarioSesion =
        (com.focusup.modelo.Usuario) session.getAttribute("usuarioActivo");
    com.focusup.modelo.Usuario usuarioEditar =
        (com.focusup.modelo.Usuario) request.getAttribute("usuario");
    boolean esEdicion = (usuarioEditar != null);
%>

<header class="topbar">
    <a href="${pageContext.request.contextPath}/usuarios" class="brand">FocusUp</a>
    <nav>
        <a href="${pageContext.request.contextPath}/usuarios">← Volver al listado</a>
        <a href="${pageContext.request.contextPath}/logout">Cerrar sesión</a>
    </nav>
</header>

<div class="container" style="max-width:600px;">
    <div class="card">
        <h2><%= HtmlUtil.escapar(request.getAttribute("titulo")) %></h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.escapar(request.getAttribute("error")) %></div>
        <% } %>

        <%--
            Formulario de usuario reutilizable para crear y editar.
            Método POST → UsuarioServlet.doPost() con accion=guardar
            Si hay id (edición): UPDATE; si no (nuevo): INSERT
        --%>
        <form action="${pageContext.request.contextPath}/usuarios" method="post">

            <%-- Campo oculto: accion para identificar la operación en el Servlet --%>
            <input type="hidden" name="accion" value="guardar"/>

            <%-- Campo oculto: id para distinguir entre inserción y actualización --%>
            <% if (esEdicion) { %>
                <input type="hidden" name="id" value="<%= usuarioEditar.getId() %>"/>
            <% } %>

            <div class="form-row">
                <div class="form-group">
                    <label for="nombres">Nombres *</label>
                    <input type="text" id="nombres" name="nombres"
                           value="<%= esEdicion ? HtmlUtil.escapar(usuarioEditar.getNombres()) : "" %>"
                           placeholder="Ej: Daniel" required/>
                </div>
                <div class="form-group">
                    <label for="apellidos">Apellidos *</label>
                    <input type="text" id="apellidos" name="apellidos"
                           value="<%= esEdicion ? HtmlUtil.escapar(usuarioEditar.getApellidos()) : "" %>"
                           placeholder="Ej: Castiblanco" required/>
                </div>
            </div>

            <div class="form-group">
                <label for="cedula">Número de cédula *</label>
                <input type="text" id="cedula" name="cedula"
                       value="<%= esEdicion ? HtmlUtil.escapar(usuarioEditar.getCedula()) : "" %>"
                       placeholder="Mínimo 6 dígitos" required/>
            </div>

            <div class="form-group">
                <label for="correo">Correo electrónico *</label>
                <input type="email" id="correo" name="correo"
                       value="<%= esEdicion ? HtmlUtil.escapar(usuarioEditar.getCorreo()) : "" %>"
                       placeholder="usuario@correo.com" required/>
            </div>

            <%-- La contraseña solo se solicita al crear, no al editar --%>
            <% if (!esEdicion) { %>
            <div class="form-group">
                <label for="contrasena">Contraseña * (mínimo 6 caracteres)</label>
                <input type="password" id="contrasena" name="contrasena"
                       placeholder="••••••" required/>
            </div>
            <div class="form-group">
                <label for="fechaNac">Fecha de nacimiento</label>
                <input type="date" id="fechaNac" name="fechaNac"/>
            </div>
            <% } else { %>
            <div class="alert alert-info" style="font-size:.82rem;">
                Para cambiar la contraseña, use el botón <strong>Contraseña</strong>
                en el listado de usuarios.
            </div>
            <% } %>

            <div style="display:flex; gap:12px; margin-top:20px;">
                <button type="submit" class="btn btn-primary">
                    <%= esEdicion ? "Actualizar usuario" : "Registrar usuario" %>
                </button>
                <a href="${pageContext.request.contextPath}/usuarios"
                   class="btn btn-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</div>

<footer>FocusUp © 2026 — GA7-220501096-AA2-EV02 | Daniel Castiblanco</footer>
</body>
</html>
