package com.focusup.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Pruebas de la racha de días activos (lógica pura). */
class RachaTest {

    private static final LocalDate HOY = LocalDate.of(2026, 3, 11);

    @Test
    void sinActividad_rachaCero() {
        assertEquals(0, Racha.actual(Set.of(), HOY));
        assertEquals(0, Racha.maxima(Set.of()));
    }

    @Test
    void diasConsecutivosHastaHoy_seCuentan() {
        Set<LocalDate> dias = Set.of(HOY, HOY.minusDays(1), HOY.minusDays(2));
        assertEquals(3, Racha.actual(dias, HOY));
    }

    @Test
    void siHoyNoHayActividadTodaviaPeroAyerSi_laRachaSigueViva() {
        Set<LocalDate> dias = Set.of(HOY.minusDays(1), HOY.minusDays(2));
        assertEquals(2, Racha.actual(dias, HOY));
    }

    @Test
    void siSePasoUnDiaCompleto_laRachaSeRompe() {
        Set<LocalDate> dias = Set.of(HOY.minusDays(2), HOY.minusDays(3));
        assertEquals(0, Racha.actual(dias, HOY));
    }

    @Test
    void rachaMaxima_eligeLaMasLargaDeLaHistoria() {
        Set<LocalDate> dias = Set.of(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 4),
                LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 11));
        assertEquals(4, Racha.maxima(dias));
    }
}
