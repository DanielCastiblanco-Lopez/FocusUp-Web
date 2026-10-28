package com.focusup.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase utilitaria para encriptación de contraseñas.
 * Proporciona métodos estáticos para encriptar cadenas usando el algoritmo MD5,
 * compatible con la función MD5() de MySQL.
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class EncriptadorUtil {

    /**
     * Constructor privado para evitar instanciación.
     * Esta clase solo contiene métodos estáticos.
     */
    private EncriptadorUtil() {}

    /**
     * Encripta una cadena de texto usando el algoritmo MD5.
     * El resultado es compatible con la función MD5() de MySQL.
     *
     * @param texto cadena de texto a encriptar
     * @return hash MD5 en formato hexadecimal de 32 caracteres
     * @throws RuntimeException si el algoritmo MD5 no está disponible
     */
    public static String encriptarMD5(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            BigInteger numero = new BigInteger(1, messageDigest);
            String hashHex = numero.toString(16);

            // Rellenar con ceros a la izquierda hasta completar 32 caracteres
            while (hashHex.length() < 32) {
                hashHex = "0" + hashHex;
            }
            return hashHex;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar: algoritmo MD5 no disponible.", e);
        }
    }
}
