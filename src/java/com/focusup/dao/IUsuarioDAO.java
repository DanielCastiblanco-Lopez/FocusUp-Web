package com.focusup.dao;

import com.focusup.modelo.Usuario;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz que define el contrato de operaciones CRUD
 * para la entidad {@link Usuario} en la base de datos.
 *
 * Patrón de diseño: DAO (Data Access Object)
 * Todas las operaciones de acceso a datos deben implementarse
 * a través de esta interfaz para garantizar la separación de capas.
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public interface IUsuarioDAO {

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param usuario objeto con los datos del nuevo usuario
     * @return {@code true} si el registro fue exitoso
     * @throws SQLException si ocurre un error en la operación
     */
    boolean registrarUsuario(Usuario usuario) throws SQLException;

    /**
     * Consulta todos los usuarios registrados en el sistema.
     *
     * @return lista de objetos {@link Usuario}; lista vacía si no hay registros
     * @throws SQLException si ocurre un error en la consulta
     */
    List<Usuario> consultarTodos() throws SQLException;

    /**
     * Busca un usuario por su identificador único.
     *
     * @param id identificador del usuario
     * @return objeto {@link Usuario} encontrado, o {@code null} si no existe
     * @throws SQLException si ocurre un error en la consulta
     */
    Usuario consultarPorId(int id) throws SQLException;

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param correo correo electrónico registrado
     * @return objeto {@link Usuario} encontrado, o {@code null} si no existe
     * @throws SQLException si ocurre un error en la consulta
     */
    Usuario consultarPorCorreo(String correo) throws SQLException;

    /**
     * Actualiza los datos personales de un usuario existente.
     *
     * @param usuario objeto con los datos actualizados (debe incluir el id)
     * @return {@code true} si la actualización fue exitosa
     * @throws SQLException si ocurre un error en la operación
     */
    boolean actualizarUsuario(Usuario usuario) throws SQLException;

    /**
     * Elimina lógicamente un usuario (lo marca como inactivo).
     * No elimina el registro físicamente de la base de datos.
     *
     * @param id identificador del usuario a desactivar
     * @return {@code true} si la operación fue exitosa
     * @throws SQLException si ocurre un error en la operación
     */
    boolean eliminarUsuario(int id) throws SQLException;

    /**
     * Verifica las credenciales de un usuario para el inicio de sesión.
     *
     * @param correo     correo electrónico ingresado
     * @param contrasena contraseña ingresada (en texto plano)
     * @return objeto {@link Usuario} si las credenciales son válidas, o {@code null}
     * @throws SQLException si ocurre un error en la consulta
     */
    Usuario iniciarSesion(String correo, String contrasena) throws SQLException;
}
