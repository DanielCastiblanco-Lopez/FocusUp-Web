package com.focusup.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Pruebas unitarias de {@link HtmlUtil}: protección contra XSS en las vistas JSP.
 */
public class HtmlUtilTest {

    @Test
    public void nulo_seConvierteEnCadenaVacia() {
        assertEquals("", HtmlUtil.escapar(null));
    }

    @Test
    public void textoSinCaracteresEspeciales_quedaIgual() {
        assertEquals("Daniel Castiblanco", HtmlUtil.escapar("Daniel Castiblanco"));
        assertEquals("contraseña ñandú", HtmlUtil.escapar("contraseña ñandú"));
    }

    @Test
    public void escapaLosCincoCaracteresEspeciales() {
        assertEquals("&amp;&lt;&gt;&quot;&#39;", HtmlUtil.escapar("&<>\"'"));
    }

    @Test
    public void etiquetaScript_noQuedaEjecutable() {
        String salida = HtmlUtil.escapar("<script>alert('xss')</script>");
        assertEquals("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;", salida);
        assertFalse(salida.contains("<"));
        assertFalse(salida.contains(">"));
    }

    @Test
    public void cierreDeAtributoConEventoMalicioso_noPuedeRomperElValue() {
        // Payload típico que intenta salir de value="..." e inyectar un evento
        String salida = HtmlUtil.escapar("\" onfocus=\"alert(1)\" x=\"");
        assertFalse(salida.contains("\""));
        assertEquals("&quot; onfocus=&quot;alert(1)&quot; x=&quot;", salida);
    }

    @Test
    public void aceptaCualquierObjeto_usandoToString() {
        assertEquals("42", HtmlUtil.escapar(42));
        assertEquals("true", HtmlUtil.escapar(Boolean.TRUE));
    }

    @Test
    public void unaEntidadYaEscrita_seEscapaDeNuevoPorDiseno() {
        // La función no intenta adivinar si el texto ya estaba escapado.
        assertEquals("&amp;lt;", HtmlUtil.escapar("&lt;"));
    }
}
