package com.focusup.servlet;

import com.focusup.dao.UsuarioDAO;
import com.focusup.modelo.Usuario;
import com.focusup.util.ValidadorUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet principal del módulo de usuarios. Gestiona las operaciones CRUD
 * usando el parámetro "accion" para determinar qué operación ejecutar.
 *
 * Rutas GET:
 *   /usuarios              → lista todos los usuarios
 *   /usuarios?accion=nuevo → muestra formulario de registro
 *   /usuarios?accion=editar&id=X → muestra formulario de edición
 *   /usuarios?accion=eliminar&id=X → desactiva el usuario
 *
 * Rutas POST:
 *   /usuarios?accion=guardar   → inserta o actualiza usuario
 *   /usuarios?accion=contrasena → actualiza contraseña
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class UsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ── Método GET ────────────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar autenticación
        if (!estaAutenticado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar"   -> listarUsuarios(request, response);
            case "nuevo"    -> mostrarFormularioNuevo(request, response);
            case "editar"   -> mostrarFormularioEditar(request, response);
            case "eliminar" -> eliminarUsuario(request, response);
            default         -> listarUsuarios(request, response);
        }
    }

    // ── Método POST ───────────────────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!estaAutenticado(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) accion = "";

        switch (accion) {
            case "guardar"     -> guardarUsuario(request, response);
            case "contrasena"  -> actualizarContrasena(request, response);
            default            -> listarUsuarios(request, response);
        }
    }

    // ── Operaciones GET ───────────────────────────────────────────────────────

    private void listarUsuarios(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            List<Usuario> lista = usuarioDAO.consultarTodos();
            req.setAttribute("listaUsuarios", lista);
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios/listar.jsp").forward(req, res);
        } catch (SQLException e) {
            log("Error al consultar usuarios", e);
            req.setAttribute("error", "No fue posible consultar los usuarios en este momento.");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, res);
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("titulo", "Registrar nuevo usuario");
        req.setAttribute("accion", "guardar");
        req.getRequestDispatcher("/WEB-INF/jsp/usuarios/formulario.jsp").forward(req, res);
    }

    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Usuario usuario = usuarioDAO.consultarPorId(id);
            if (usuario == null) {
                req.setAttribute("error", "Usuario no encontrado.");
                listarUsuarios(req, res);
                return;
            }
            req.setAttribute("usuario", usuario);
            req.setAttribute("titulo", "Editar usuario");
            req.setAttribute("accion", "guardar");
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios/formulario.jsp").forward(req, res);
        } catch (NumberFormatException e) {
            res.sendRedirect(req.getContextPath() + "/usuarios");
        } catch (SQLException e) {
            log("Error al consultar un usuario", e);
            req.setAttribute("error", "No fue posible consultar el usuario.");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, res);
        }
    }

    private void eliminarUsuario(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            boolean resultado = usuarioDAO.eliminarUsuario(id);
            String msg = resultado ? "exito=Usuario desactivado correctamente."
                                   : "error=No se encontró el usuario.";
            res.sendRedirect(req.getContextPath() + "/usuarios?" + msg);
        } catch (NumberFormatException | SQLException e) {
            log("Error al desactivar usuario", e);
            res.sendRedirect(req.getContextPath() + "/usuarios?error=Error+al+eliminar.");
        }
    }

    // ── Operaciones POST ──────────────────────────────────────────────────────

    private void guardarUsuario(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String idParam    = req.getParameter("id");
        String nombres    = req.getParameter("nombres");
        String apellidos  = req.getParameter("apellidos");
        String cedula     = req.getParameter("cedula");
        String correo     = req.getParameter("correo");
        String contrasena = req.getParameter("contrasena");
        String fechaNac   = req.getParameter("fechaNac");

        // Validaciones
        if (!ValidadorUtil.esTextoValido(nombres) || !ValidadorUtil.esTextoValido(apellidos)) {
            req.setAttribute("error", "Nombres y apellidos son obligatorios.");
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios/formulario.jsp").forward(req, res);
            return;
        }
        if (!ValidadorUtil.esCorreoValido(correo)) {
            req.setAttribute("error", "El correo electrónico no tiene un formato válido.");
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios/formulario.jsp").forward(req, res);
            return;
        }
        if (!ValidadorUtil.esCedulaValida(cedula)) {
            req.setAttribute("error", "La cédula debe tener mínimo 6 dígitos numéricos.");
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios/formulario.jsp").forward(req, res);
            return;
        }

        try {
            boolean esNuevo = (idParam == null || idParam.isBlank());

            if (esNuevo) {
                // ── INSERTAR ──
                if (!ValidadorUtil.esContrasenaValida(contrasena)) {
                    req.setAttribute("error", "La contraseña debe tener mínimo 6 caracteres.");
                    req.getRequestDispatcher("/WEB-INF/jsp/usuarios/formulario.jsp").forward(req, res);
                    return;
                }
                Usuario nuevo = new Usuario();
                nuevo.setNombres(nombres);
                nuevo.setApellidos(apellidos);
                nuevo.setCedula(cedula);
                nuevo.setCorreo(correo);
                nuevo.setContrasena(contrasena);
                if (fechaNac != null && !fechaNac.isBlank()) {
                    nuevo.setFechaNacimiento(java.time.LocalDate.parse(fechaNac));
                }
                usuarioDAO.registrarUsuario(nuevo);
                res.sendRedirect(req.getContextPath() + "/usuarios?exito=Usuario+registrado+exitosamente.");

            } else {
                // ── ACTUALIZAR ──
                int id = Integer.parseInt(idParam);
                Usuario existente = usuarioDAO.consultarPorId(id);
                if (existente == null) {
                    res.sendRedirect(req.getContextPath() + "/usuarios?error=Usuario+no+encontrado.");
                    return;
                }
                existente.setNombres(nombres);
                existente.setApellidos(apellidos);
                existente.setCedula(cedula);
                existente.setCorreo(correo);
                usuarioDAO.actualizarUsuario(existente);
                res.sendRedirect(req.getContextPath() + "/usuarios?exito=Usuario+actualizado+exitosamente.");
            }

        } catch (SQLException e) {
            log("Error al guardar usuario", e);
            req.setAttribute("error", "No fue posible guardar el usuario. Verifique los datos e intente de nuevo.");
            req.getRequestDispatcher("/WEB-INF/jsp/usuarios/formulario.jsp").forward(req, res);
        }
    }

    private void actualizarContrasena(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            int    id             = Integer.parseInt(req.getParameter("id"));
            String nuevaContrasena = req.getParameter("nuevaContrasena");

            if (!ValidadorUtil.esContrasenaValida(nuevaContrasena)) {
                res.sendRedirect(req.getContextPath()
                    + "/usuarios?error=La+contraseña+debe+tener+mínimo+6+caracteres.");
                return;
            }
            usuarioDAO.actualizarContrasena(id, nuevaContrasena);
            res.sendRedirect(req.getContextPath() + "/usuarios?exito=Contraseña+actualizada.");
        } catch (NumberFormatException | SQLException e) {
            log("Error al actualizar contraseña", e);
            res.sendRedirect(req.getContextPath() + "/usuarios?error=Error+al+actualizar+contraseña.");
        }
    }

    // ── Auxiliar ──────────────────────────────────────────────────────────────

    private boolean estaAutenticado(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion != null && sesion.getAttribute("usuarioActivo") != null;
    }
}
