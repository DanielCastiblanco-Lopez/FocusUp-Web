package com.focusup.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet que gestiona el cierre de sesión del usuario.
 *
 * GET /logout → Invalida la sesión HTTP activa y redirige al login
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class LogoutServlet extends HttpServlet {

    /**
     * Método GET: destruye la sesión activa y redirige al formulario de login.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login?mensaje=sesion_cerrada");
    }
}
