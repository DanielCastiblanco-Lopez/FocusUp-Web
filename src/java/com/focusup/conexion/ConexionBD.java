package com.focusup.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión a la base de datos MySQL.
 * Implementa el patrón Singleton para garantizar una única instancia de conexión.
 *
 * Proyecto : FocusUp - Aplicación de Productividad Personal
 * Módulo   : Gestión de Usuarios
 * Evidencia: GA7-220501096-AA2-EV01
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class ConexionBD {

    // ── Constantes de configuración de la base de datos ──────────────────────
    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";
    private static final String URL      = "jdbc:mysql://localhost:3306/focusup_web"
                                         + "?useSSL=false&serverTimezone=America/Bogota";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "FocusUp!";  // Ajustar según instalación local

    /** Segundos máximos que se espera la respuesta del servidor al validar la conexión */
    private static final int SEGUNDOS_VALIDACION = 2;

    /** Instancia única de la conexión (patrón Singleton) */
    private static Connection instancia = null;

    /**
     * Constructor privado para evitar instanciación directa desde otras clases.
     */
    private ConexionBD() {}

    /**
     * Retorna la instancia activa de la conexión a la base de datos.
     * Si no existe o está cerrada, establece una nueva conexión.
     *
     * @return objeto {@link Connection} activo hacia focusup_web
     * @throws SQLException si el driver no está disponible o la conexión falla
     */
    public static synchronized Connection obtenerConexion() throws SQLException {
        try {
            // isValid() consulta al servidor; isClosed() solo mira el estado local y no
            // detecta una conexión que MySQL cerró por inactividad (wait_timeout).
            if (instancia == null || !instancia.isValid(SEGUNDOS_VALIDACION)) {
                cerrarConexion();
                Class.forName(DRIVER);
                instancia = DriverManager.getConnection(URL, USUARIO, PASSWORD);
                System.out.println("[ConexionBD] Conexión establecida exitosamente.");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL no encontrado. Verifique el JAR en /lib: "
                                   + e.getMessage());
        }
        return instancia;
    }

    /**
     * Cierra la conexión activa a la base de datos de forma segura.
     */
    public static synchronized void cerrarConexion() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
                System.out.println("[ConexionBD] Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("[ConexionBD] Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
