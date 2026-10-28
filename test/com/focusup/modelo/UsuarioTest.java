package com.focusup.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import org.junit.Test;

/**
 * Pruebas unitarias de la entidad {@link Usuario}.
 */
public class UsuarioTest {

    private Usuario crearUsuarioDePrueba() {
        return new Usuario("Daniel", "Castiblanco", "1012345678",
                           "daniel@correo.com", "claveSecreta1", LocalDate.of(2000, 5, 17));
    }

    @Test
    public void constructorCompleto_asignaLosCampos() {
        Usuario u = crearUsuarioDePrueba();
        assertEquals("Daniel", u.getNombres());
        assertEquals("Castiblanco", u.getApellidos());
        assertEquals("1012345678", u.getCedula());
        assertEquals("daniel@correo.com", u.getCorreo());
        assertEquals(LocalDate.of(2000, 5, 17), u.getFechaNacimiento());
    }

    @Test
    public void usuarioNuevo_quedaActivoYSinIdNiFechaDeRegistro() {
        Usuario u = crearUsuarioDePrueba();
        assertTrue(u.isActivo());
        assertEquals(0, u.getId());
        assertNull(u.getFechaRegistro());
    }

    @Test
    public void constructorVacio_dejaLosCamposSinInicializar() {
        Usuario u = new Usuario();
        assertNull(u.getNombres());
        assertNull(u.getCorreo());
        assertFalse(u.isActivo());
    }

    @Test
    public void nombreCompleto_concatenaNombresYApellidos() {
        assertEquals("Daniel Castiblanco", crearUsuarioDePrueba().getNombreCompleto());
    }

    @Test
    public void setters_actualizanLosValores() {
        Usuario u = new Usuario();
        u.setId(7);
        u.setNombres("Ana");
        u.setActivo(true);
        assertEquals(7, u.getId());
        assertEquals("Ana", u.getNombres());
        assertTrue(u.isActivo());
    }

    @Test
    public void toString_nuncaIncluyeLaContrasena() {
        Usuario u = crearUsuarioDePrueba();
        String texto = u.toString();
        assertFalse(texto.contains("claveSecreta1"));
        assertTrue(texto.contains("daniel@correo.com"));
    }
}
