package com.focusup.dao;

import com.focusup.conexion.ConexionBD;
import com.focusup.modelo.Usuario;
import com.focusup.util.EncriptadorUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del patrón DAO para la entidad {@link Usuario}.
 * Contiene todas las operaciones CRUD usando JDBC y MySQL.
 *
 * Operaciones implementadas:
 *  - Inserción   : registrarUsuario()
 *  - Consulta    : consultarTodos(), consultarPorId(), consultarPorCorreo()
 *  - Actualización: actualizarUsuario()
 *  - Eliminación : eliminarUsuario() (eliminación lógica)
 *  - Login       : iniciarSesion()
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class UsuarioDAO implements IUsuarioDAO {

    // ── Sentencias SQL parametrizadas (PreparedStatement) ────────────────────
    private static final String SQL_INSERTAR =
        "INSERT INTO usuarios (nombres, apellidos, cedula, correo, contrasena, fecha_nac) "
      + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_CONSULTAR_TODOS =
        "SELECT id, nombres, apellidos, cedula, correo, fecha_nac, fecha_registro, activo "
      + "FROM usuarios WHERE activo = 1 ORDER BY nombres";

    private static final String SQL_CONSULTAR_POR_ID =
        "SELECT id, nombres, apellidos, cedula, correo, fecha_nac, fecha_registro, activo "
      + "FROM usuarios WHERE id = ?";

    private static final String SQL_CONSULTAR_POR_CORREO =
        "SELECT id, nombres, apellidos, cedula, correo, fecha_nac, fecha_registro, activo "
      + "FROM usuarios WHERE correo = ?";

    private static final String SQL_ACTUALIZAR =
        "UPDATE usuarios SET nombres = ?, apellidos = ?, cedula = ?, correo = ? WHERE id = ?";

    private static final String SQL_ACTUALIZAR_CONTRASENA =
        "UPDATE usuarios SET contrasena = ? WHERE id = ?";

    private static final String SQL_ELIMINAR =
        "UPDATE usuarios SET activo = 0 WHERE id = ?";

    private static final String SQL_LOGIN =
        "SELECT id, nombres, apellidos, cedula, correo, fecha_nac, fecha_registro, activo "
      + "FROM usuarios WHERE correo = ? AND contrasena = ? AND activo = 1";

    // ── INSERCIÓN ─────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * La contraseña se encripta con MD5 antes de guardarse en la base de datos.
     */
    @Override
    public boolean registrarUsuario(Usuario usuario) throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();

        try (PreparedStatement ps = conexion.prepareStatement(SQL_INSERTAR,
                                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombres());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getCedula());
            ps.setString(4, usuario.getCorreo());
            ps.setString(5, EncriptadorUtil.encriptarMD5(usuario.getContrasena()));
            ps.setDate(6, usuario.getFechaNacimiento() != null
                          ? Date.valueOf(usuario.getFechaNacimiento()) : null);

            int filasAfectadas = ps.executeUpdate();

            // Recuperar el ID generado automáticamente por MySQL
            if (filasAfectadas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        usuario.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // ── CONSULTA ──────────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public List<Usuario> consultarTodos() throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();
        List<Usuario> listaUsuarios = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(SQL_CONSULTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                listaUsuarios.add(mapearResultSet(rs));
            }
        }
        return listaUsuarios;
    }

    /** {@inheritDoc} */
    @Override
    public Usuario consultarPorId(int id) throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();

        try (PreparedStatement ps = conexion.prepareStatement(SQL_CONSULTAR_POR_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Usuario consultarPorCorreo(String correo) throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();

        try (PreparedStatement ps = conexion.prepareStatement(SQL_CONSULTAR_POR_CORREO)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    // ── ACTUALIZACIÓN ─────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean actualizarUsuario(Usuario usuario) throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();

        try (PreparedStatement ps = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, usuario.getNombres());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getCedula());
            ps.setString(4, usuario.getCorreo());
            ps.setInt(5, usuario.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza únicamente la contraseña de un usuario.
     * La nueva contraseña se encripta con MD5 antes de guardarse.
     *
     * @param idUsuario      identificador del usuario
     * @param nuevaContrasena nueva contraseña en texto plano
     * @return {@code true} si la actualización fue exitosa
     * @throws SQLException si ocurre un error en la operación
     */
    public boolean actualizarContrasena(int idUsuario, String nuevaContrasena)
            throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();

        try (PreparedStatement ps = conexion.prepareStatement(SQL_ACTUALIZAR_CONTRASENA)) {
            ps.setString(1, EncriptadorUtil.encriptarMD5(nuevaContrasena));
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    // ── ELIMINACIÓN LÓGICA ────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public boolean eliminarUsuario(int id) throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();

        try (PreparedStatement ps = conexion.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── INICIO DE SESIÓN ──────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public Usuario iniciarSesion(String correo, String contrasena) throws SQLException {
        Connection conexion = ConexionBD.obtenerConexion();

        try (PreparedStatement ps = conexion.prepareStatement(SQL_LOGIN)) {
            ps.setString(1, correo);
            ps.setString(2, EncriptadorUtil.encriptarMD5(contrasena));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    // ── MÉTODO AUXILIAR: mapear ResultSet → Usuario ───────────────────────────

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Usuario}.
     *
     * @param rs ResultSet posicionado en la fila a mapear
     * @return objeto {@link Usuario} con los datos de la fila
     * @throws SQLException si ocurre un error al leer las columnas
     */
    private Usuario mapearResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidos(rs.getString("apellidos"));
        usuario.setCedula(rs.getString("cedula"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setActivo(rs.getBoolean("activo"));

        Date fechaNac = rs.getDate("fecha_nac");
        if (fechaNac != null) {
            usuario.setFechaNacimiento(fechaNac.toLocalDate());
        }

        Timestamp fechaReg = rs.getTimestamp("fecha_registro");
        if (fechaReg != null) {
            usuario.setFechaRegistro(fechaReg.toLocalDateTime());
        }

        return usuario;
    }
}
