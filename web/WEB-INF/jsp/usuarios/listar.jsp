<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.focusup.modelo.Usuario, com.focusup.util.ValidadorUtil, java.util.List, com.focusup.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>FocusUp — Usuarios</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"/>
</head>
<body>

<%-- Obtener usuario de sesión --%>
<%
    com.focusup.modelo.Usuario usuarioSesion =
        (com.focusup.modelo.Usuario) session.getAttribute("usuarioActivo");
%>

<header class="topbar">
    <a href="${pageContext.request.contextPath}/usuarios" class="brand">FocusUp</a>
    <nav>
        <span style="color:#BDC3C7; font-size:.85rem;">
            Hola, <%= usuarioSesion != null ? HtmlUtil.escapar(usuarioSesion.getNombres()) : "Usuario" %> |
        </span>
        <a href="${pageContext.request.contextPath}/logout">Cerrar sesión</a>
    </nav>
</header>

<div class="container">

    <%-- Mensajes de resultado de operaciones --%>
    <%
        String exito = request.getParameter("exito");
        String error = request.getParameter("error");
        if (exito != null) { %>
            <div class="alert alert-success"><%= HtmlUtil.escapar(exito) %></div>
    <% } else if (error != null) { %>
            <div class="alert alert-error"><%= HtmlUtil.escapar(error) %></div>
    <% } %>

    <div class="card">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
            <h2 style="margin:0; border:none; padding:0;">Gestión de Usuarios</h2>
            <%--
                Enlace GET → UsuarioServlet.doGet() con accion=nuevo
                Abre el formulario de registro desde el panel de administración
            --%>
            <a href="${pageContext.request.contextPath}/usuarios?accion=nuevo"
               class="btn btn-primary btn-sm">+ Nuevo usuario</a>
        </div>

        <%
            List<Usuario> lista = (List<Usuario>) request.getAttribute("listaUsuarios");
            if (lista == null || lista.isEmpty()) {
        %>
            <div class="alert alert-info">No hay usuarios registrados en el sistema.</div>
        <% } else { %>
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre completo</th>
                            <th>Cédula</th>
                            <th>Correo</th>
                            <th>Fecha registro</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                    <% for (Usuario u : lista) { %>
                        <tr>
                            <td><%= u.getId() %></td>
                            <td><%= HtmlUtil.escapar(u.getNombreCompleto()) %></td>
                            <td><%= HtmlUtil.escapar(u.getCedula()) %></td>
                            <td><%= HtmlUtil.escapar(u.getCorreo()) %></td>
                            <td>
                                <%= u.getFechaRegistro() != null
                                    ? u.getFechaRegistro().toLocalDate().toString()
                                    : "—" %>
                            </td>
                            <td>
                                <%-- Editar: GET → accion=editar&id=X --%>
                                <a href="${pageContext.request.contextPath}/usuarios?accion=editar&id=<%= u.getId() %>"
                                   class="btn btn-secondary btn-sm">Editar</a>

                                <%-- Cambiar contraseña: formulario pequeño POST --%>
                                <button type="button"
                                        class="btn btn-secondary btn-sm"
                                        data-id="<%= u.getId() %>"
                                        data-nombre="<%= HtmlUtil.escapar(u.getNombreCompleto()) %>"
                                        onclick="mostrarModalContrasena(this)">
                                    Contraseña
                                </button>

                                <%-- Eliminar: GET → accion=eliminar&id=X con confirmación JS --%>
                                <a href="${pageContext.request.contextPath}/usuarios?accion=eliminar&id=<%= u.getId() %>"
                                   class="btn btn-danger btn-sm"
                                   data-nombre="<%= HtmlUtil.escapar(u.getNombreCompleto()) %>"
                                   onclick="return confirm('¿Desactivar a ' + this.dataset.nombre + '?')">
                                   Desactivar
                                </a>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <p style="color:#7F8C8D; font-size:.78rem; margin-top:10px;">
                Total: <%= lista.size() %> usuario(s) activo(s)
            </p>
        <% } %>
    </div>
</div>

<%-- Modal cambiar contraseña (POST → UsuarioServlet.doPost() accion=contrasena) --%>
<div id="modalContrasena" style="display:none; position:fixed; inset:0;
     background:rgba(0,0,0,.45); z-index:100; align-items:center; justify-content:center;">
    <div class="card" style="max-width:380px; width:90%; margin:0;">
        <h2>Cambiar contraseña</h2>
        <p id="modalNombreUsuario" style="color:#7F8C8D; margin-bottom:16px; font-size:.88rem;"></p>

        <form action="${pageContext.request.contextPath}/usuarios" method="post">
            <input type="hidden" name="accion" value="contrasena"/>
            <input type="hidden" name="id" id="modalUserId"/>

            <div class="form-group">
                <label for="nuevaContrasena">Nueva contraseña (mín. 6 caracteres)</label>
                <input type="password" id="nuevaContrasena" name="nuevaContrasena"
                       placeholder="••••••" required/>
            </div>

            <div style="display:flex; gap:10px; margin-top:8px;">
                <button type="submit" class="btn btn-primary">Guardar</button>
                <button type="button" class="btn btn-secondary"
                        onclick="cerrarModal()">Cancelar</button>
            </div>
        </form>
    </div>
</div>

<footer>FocusUp © 2026 — GA7-220501096-AA2-EV02 | Daniel Castiblanco</footer>

<script>
function mostrarModalContrasena(boton) {
    document.getElementById('modalUserId').value = boton.dataset.id;
    document.getElementById('modalNombreUsuario').textContent = 'Usuario: ' + boton.dataset.nombre;
    document.getElementById('modalContrasena').style.display = 'flex';
}
function cerrarModal() {
    document.getElementById('modalContrasena').style.display = 'none';
    document.getElementById('nuevaContrasena').value = '';
}
</script>
</body>
</html>
