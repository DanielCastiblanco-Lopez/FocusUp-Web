<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ page import="com.focusup.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>FocusUp — Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"/>
</head>
<body>
<div class="container" style="max-width:480px; margin-top:80px; text-align:center;">
    <div class="card">
        <div style="font-size:3rem; margin-bottom:12px;">⚠️</div>
        <h2>Algo salió mal</h2>
        <p style="color:#7F8C8D; margin:12px 0 24px;">
            <%= request.getAttribute("error") != null
                ? HtmlUtil.escapar(request.getAttribute("error"))
                : "La página solicitada no existe o ocurrió un error inesperado." %>
        </p>
        <a href="${pageContext.request.contextPath}/usuarios" class="btn btn-primary">
            Volver al inicio
        </a>
    </div>
</div>
<footer>FocusUp © 2026 — GA7-220501096-AA2-EV02 | Daniel Castiblanco</footer>
</body>
</html>
