package com.focusup.servlet;

import com.focusup.dao.UsuarioDAO;
import com.focusup.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet que gestiona el inicio de sesión de usuarios en FocusUp.
 *
 * GET  /login → Muestra el formulario de inicio de sesión (login.jsp)
 * POST /login → Procesa las credenciales y crea la sesión HTTP si son válidas
 *
 * Evidencia: GA7-220501096-AA2-EV02
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Método GET: muestra el formulario de inicio de sesión.
     * Si el usuario ya tiene sesión activa, redirige al listado de usuarios.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion != null && sesion.getAttribute("usuarioActivo") != null) {
            response.sendRedirect(request.getContextPath() + "/usuarios");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    /**
     * Método POST: valida las credenciales enviadas desde el formulario.
     * Si son correctas, crea la sesión y redirige al panel principal.
     * Si son incorrectas, reenvía al formulario con mensaje de error.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String correo    = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // Validación básica de campos vacíos
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            request.setAttribute("error", "Por favor ingrese su correo y contraseña.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.iniciarSesion(correo.trim(), contrasena);

            if (usuario != null) {
                // Crear sesión HTTP con los datos del usuario autenticado
                HttpSession sesion = request.getSession(true);
                request.changeSessionId(); // id nuevo tras autenticar: evita la fijación de sesión
                sesion.setAttribute("usuarioActivo", usuario);
                sesion.setMaxInactiveInterval(30 * 60); // 30 minutos

                response.sendRedirect(request.getContextPath() + "/usuarios");
            } else {
                request.setAttribute("error", "Correo o contraseña incorrectos.");
                request.setAttribute("correo", correo);
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            log("Error de base de datos al iniciar sesión", e);
            request.setAttribute("error", "No fue posible iniciar sesión en este momento. Intente de nuevo más tarde.");
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}
