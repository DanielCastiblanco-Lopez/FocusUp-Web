package com.focusup.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Pruebas unitarias de {@link ValidadorUtil}.
 * Corren en la JVM, sin servidor ni base de datos.
 */
public class ValidadorUtilTest {

    // ── Correo ────────────────────────────────────────────────────────────────

    @Test
    public void correoConFormatoValido_esAceptado() {
        assertTrue(ValidadorUtil.esCorreoValido("usuario@correo.com"));
        assertTrue(ValidadorUtil.esCorreoValido("ana.perez+focus@dominio.co"));
        assertTrue(ValidadorUtil.esCorreoValido("a_b-c@sub.dominio.edu.co"));
    }

    @Test
    public void correoNuloOVacio_esRechazado() {
        assertFalse(ValidadorUtil.esCorreoValido(null));
        assertFalse(ValidadorUtil.esCorreoValido(""));
        assertFalse(ValidadorUtil.esCorreoValido("   "));
    }

    @Test
    public void correoSinArrobaODominioIncompleto_esRechazado() {
        assertFalse(ValidadorUtil.esCorreoValido("usuario.correo.com"));
        assertFalse(ValidadorUtil.esCorreoValido("usuario@"));
        assertFalse(ValidadorUtil.esCorreoValido("@correo.com"));
        assertFalse(ValidadorUtil.esCorreoValido("usuario@correo"));
        assertFalse(ValidadorUtil.esCorreoValido("usuario@correo.c"));
    }

    @Test
    public void correoConEspaciosOCaracteresPeligrosos_esRechazado() {
        assertFalse(ValidadorUtil.esCorreoValido("us uario@correo.com"));
        assertFalse(ValidadorUtil.esCorreoValido("<script>@correo.com"));
        assertFalse(ValidadorUtil.esCorreoValido("a@b.com' OR '1'='1"));
    }

    // ── Contraseña ────────────────────────────────────────────────────────────

    @Test
    public void contrasenaConSeisOMasCaracteres_esAceptada() {
        assertTrue(ValidadorUtil.esContrasenaValida("123456"));
        assertTrue(ValidadorUtil.esContrasenaValida("unaClaveMasLarga!"));
    }

    @Test
    public void contrasenaNulaOCorta_esRechazada() {
        assertFalse(ValidadorUtil.esContrasenaValida(null));
        assertFalse(ValidadorUtil.esContrasenaValida(""));
        assertFalse(ValidadorUtil.esContrasenaValida("12345"));
    }

    // ── Cédula ────────────────────────────────────────────────────────────────

    @Test
    public void cedulaNumericaDeSeisOMasDigitos_esAceptada() {
        assertTrue(ValidadorUtil.esCedulaValida("123456"));
        assertTrue(ValidadorUtil.esCedulaValida("1012345678"));
    }

    @Test
    public void cedulaNulaOCorta_esRechazada() {
        assertFalse(ValidadorUtil.esCedulaValida(null));
        assertFalse(ValidadorUtil.esCedulaValida(""));
        assertFalse(ValidadorUtil.esCedulaValida("12345"));
    }

    @Test
    public void cedulaConLetrasSimbolosOEspacios_esRechazada() {
        assertFalse(ValidadorUtil.esCedulaValida("12345a"));
        assertFalse(ValidadorUtil.esCedulaValida("123-456"));
        assertFalse(ValidadorUtil.esCedulaValida("123 456"));
    }

    // ── Texto obligatorio ─────────────────────────────────────────────────────

    @Test
    public void textoConContenido_esAceptado() {
        assertTrue(ValidadorUtil.esTextoValido("Daniel"));
        assertTrue(ValidadorUtil.esTextoValido("  con espacios  "));
    }

    @Test
    public void textoNuloVacioOSoloEspacios_esRechazado() {
        assertFalse(ValidadorUtil.esTextoValido(null));
        assertFalse(ValidadorUtil.esTextoValido(""));
        assertFalse(ValidadorUtil.esTextoValido("    "));
    }
}
