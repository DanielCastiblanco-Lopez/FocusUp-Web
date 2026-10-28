package com.focusup.servlet;

import com.focusup.dao.UsuarioDAO;
import com.focusup.modelo.Usuario;
import com.focusup.util.ValidadorUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Servlet público para el registro de nuevos usuarios desde el formulario web.
 *
 * GET  /registro → Muestra el formulario de registro (registro.jsp)
 * POST /registro → Procesa los datos y crea el usuario en la base de datos
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class RegistroServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Método GET: muestra el formulario de registro. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(request, response);
    }

    /** Método POST: valida y guarda el nuevo usuario. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombres    = request.getParameter("nombres");
        String apellidos  = request.getParameter("apellidos");
        String cedula     = request.getParameter("cedula");
        String correo     = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String fechaNac   = request.getParameter("fechaNac");

        // ── Validaciones ──────────────────────────────────────────────────────
        if (!ValidadorUtil.esTextoValido(nombres) || !ValidadorUtil.esTextoValido(apellidos)) {
            request.setAttribute("error", "Nombres y apellidos son obligatorios.");
            reenviarFormulario(request, response);
            return;
        }
        if (!ValidadorUtil.esCedulaValida(cedula)) {
            request.setAttribute("error", "La cédula debe tener mínimo 6 dígitos numéricos.");
            reenviarFormulario(request, response);
            return;
        }
        if (!ValidadorUtil.esCorreoValido(correo)) {
            request.setAttribute("error", "El formato del correo electrónico no es válido.");
            reenviarFormulario(request, response);
            return;
        }
        if (!ValidadorUtil.esContrasenaValida(contrasena)) {
            request.setAttribute("error", "La contraseña debe tener mínimo 6 caracteres.");
            reenviarFormulario(request, response);
            return;
        }

        try {
            // Verificar que el correo no esté ya registrado
            if (usuarioDAO.consultarPorCorreo(correo) != null) {
                request.setAttribute("error", "El correo " + correo + " ya está registrado.");
                reenviarFormulario(request, response);
                return;
            }

            // Crear y registrar el nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombres(nombres.trim());
            nuevoUsuario.setApellidos(apellidos.trim());
            nuevoUsuario.setCedula(cedula.trim());
            nuevoUsuario.setCorreo(correo.trim());
            nuevoUsuario.setContrasena(contrasena);
            if (fechaNac != null && !fechaNac.isBlank()) {
                nuevoUsuario.setFechaNacimiento(LocalDate.parse(fechaNac));
            }

            boolean registrado = usuarioDAO.registrarUsuario(nuevoUsuario);

            if (registrado) {
                response.sendRedirect(request.getContextPath()
                    + "/login?exito=Registro+exitoso.+Ya+puedes+iniciar+sesión.");
            } else {
                request.setAttribute("error", "No se pudo completar el registro. Intenta de nuevo.");
                reenviarFormulario(request, response);
            }

        } catch (SQLException e) {
            log("Error de base de datos al registrar usuario", e);
            request.setAttribute("error", "No fue posible completar el registro en este momento. Intente de nuevo más tarde.");
            reenviarFormulario(request, response);
        }
    }

    private void reenviarFormulario(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Preservar datos ingresados para no tener que re-digitarlos
        req.getRequestDispatcher("/WEB-INF/jsp/registro.jsp").forward(req, res);
    }
}
