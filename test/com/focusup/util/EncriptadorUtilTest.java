package com.focusup.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Pruebas unitarias de {@link EncriptadorUtil}.
 * Los valores esperados son vectores MD5 conocidos, calculados de forma
 * independiente a la clase (MySQL: SELECT MD5('texto')).
 */
public class EncriptadorUtilTest {

    @Test
    public void md5DeCadenaVacia_coincideConElVectorConocido() {
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", EncriptadorUtil.encriptarMD5(""));
    }

    @Test
    public void md5DeTextosConocidos_coincideConElVectorConocido() {
        assertEquals("21232f297a57a5a743894a0e4a801fc3", EncriptadorUtil.encriptarMD5("admin"));
        assertEquals("e10adc3949ba59abbe56e057f20f883e", EncriptadorUtil.encriptarMD5("123456"));
        assertEquals("774b6167e1907a23ddbe5d8fe286cac0", EncriptadorUtil.encriptarMD5("FocusUp2026"));
    }

    @Test
    public void hashQueEmpiezaEnCero_conservaLosTreintaYDosCaracteres() {
        // MD5("a") = 0cc175b9... : sin el relleno con ceros a la izquierda
        // BigInteger.toString(16) devolveria solo 31 caracteres.
        String hash = EncriptadorUtil.encriptarMD5("a");
        assertEquals(32, hash.length());
        assertEquals("0cc175b9c0f1b6a831c399e269772661", hash);
    }

    @Test
    public void textoConTildesYEnie_seCodificaEnUtf8() {
        assertEquals("4c882dcb24bcb1bc225391a602feca7c", EncriptadorUtil.encriptarMD5("contraseña"));
    }

    @Test
    public void mismaEntrada_produceSiempreElMismoHash() {
        assertEquals(EncriptadorUtil.encriptarMD5("miClave1"), EncriptadorUtil.encriptarMD5("miClave1"));
    }

    @Test
    public void entradasDistintas_produceHashesDistintos() {
        assertNotEquals(EncriptadorUtil.encriptarMD5("miClave1"), EncriptadorUtil.encriptarMD5("miClave2"));
    }

    @Test
    public void elHashEsHexadecimalMinusculaYNuncaIgualAlTextoPlano() {
        String hash = EncriptadorUtil.encriptarMD5("superSecreta");
        assertNotNull(hash);
        assertTrue(hash.matches("[0-9a-f]{32}"));
        assertNotEquals("superSecreta", hash);
    }

    @Test
    public void textoNulo_lanzaNullPointerException() {
        assertThrows(NullPointerException.class, () -> EncriptadorUtil.encriptarMD5(null));
    }
}
