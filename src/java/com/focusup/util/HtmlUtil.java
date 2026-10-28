package com.focusup.util;

/**
 * Clase utilitaria para escapar texto antes de escribirlo en una página HTML.
 *
 * Los JSP del proyecto imprimen datos que vienen del usuario (nombres, correo,
 * parámetros de la URL). Si esos datos se escriben sin escapar, un valor como
 * {@code <script>...</script>} se ejecutaría en el navegador de quien vea la
 * página (vulnerabilidad XSS). Todo dato dinámico debe pasar por
 * {@link #escapar(Object)} antes de imprimirse con {@code <%= %>}.
 *
 * @author  Daniel Castiblanco
 * @version 1.0
 */
public class HtmlUtil {

    /**
     * Constructor privado para evitar instanciación.
     * Esta clase solo contiene métodos estáticos.
     */
    private HtmlUtil() {}

    /**
     * Reemplaza los caracteres con significado especial en HTML por sus entidades.
     * Es seguro tanto para contenido de etiquetas como para valores de atributos
     * escritos entre comillas dobles o simples.
     *
     * @param valor objeto a escapar (se usa su {@code toString()}); puede ser {@code null}
     * @return texto escapado; cadena vacía si {@code valor} es {@code null}
     */
    public static String escapar(Object valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.toString();
        StringBuilder resultado = new StringBuilder(texto.length() + 16);
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '&'  -> resultado.append("&amp;");
                case '<'  -> resultado.append("&lt;");
                case '>'  -> resultado.append("&gt;");
                case '"'  -> resultado.append("&quot;");
                case '\'' -> resultado.append("&#39;");
                default   -> resultado.append(c);
            }
        }
        return resultado.toString();
    }
}
