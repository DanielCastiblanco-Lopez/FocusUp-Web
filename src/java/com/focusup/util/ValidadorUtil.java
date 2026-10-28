package com.focusup.util;

/**
 * Clase utilitaria para validación de datos de entrada del usuario.
 * Centraliza las reglas de validación para garantizar coherencia en toda la aplicación.
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class ValidadorUtil {

    /** Longitud mínima aceptada para una contraseña */
    private static final int LONGITUD_MIN_CONTRASENA = 6;

    /** Longitud mínima aceptada para una cédula */
    private static final int LONGITUD_MIN_CEDULA = 6;

    /**
     * Constructor privado para evitar instanciación.
     */
    private ValidadorUtil() {}

    /**
     * Valida que un correo electrónico tenga formato válido.
     *
     * @param correo cadena a validar
     * @return {@code true} si el formato es válido
     */
    public static boolean esCorreoValido(String correo) {
        if (correo == null || correo.isBlank()) return false;
        return correo.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Valida que una contraseña cumpla la longitud mínima requerida.
     *
     * @param contrasena cadena a validar
     * @return {@code true} si la contraseña es válida
     */
    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && contrasena.length() >= LONGITUD_MIN_CONTRASENA;
    }

    /**
     * Valida que una cédula tenga la longitud mínima y sea numérica.
     *
     * @param cedula cadena a validar
     * @return {@code true} si la cédula es válida
     */
    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || cedula.length() < LONGITUD_MIN_CEDULA) return false;
        return cedula.matches("\\d+");
    }

    /**
     * Valida que un texto no sea nulo ni esté vacío.
     *
     * @param texto cadena a validar
     * @return {@code true} si el texto tiene contenido
     */
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.isBlank();
    }
}
