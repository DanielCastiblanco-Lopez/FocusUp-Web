package com.focusup.servicio;

import java.time.LocalDate;
import java.util.Set;

/**
 * Cálculo de la racha de días activos (lógica pura, sin base de datos, para
 * poder probarla fácil). Un día es "activo" si el usuario completó una tarea
 * o terminó una sesión de Pomodoro ese día: la misma regla de la app Android.
 *
 * @author Daniel Castiblanco
 * @version 3.0
 */
public final class Racha {

    private Racha() {}

    /**
     * Días consecutivos activos que terminan hoy. Si hoy todavía no hay
     * actividad, la racha no se rompe hasta que pase el día: se cuenta desde ayer.
     */
    public static int actual(Set<LocalDate> diasActivos, LocalDate hoy) {
        LocalDate dia = diasActivos.contains(hoy) ? hoy : hoy.minusDays(1);
        int racha = 0;
        while (diasActivos.contains(dia)) {
            racha++;
            dia = dia.minusDays(1);
        }
        return racha;
    }

    /** La racha consecutiva más larga de toda la historia. */
    public static int maxima(Set<LocalDate> diasActivos) {
        int mejor = 0;
        for (LocalDate dia : diasActivos) {
            if (diasActivos.contains(dia.minusDays(1))) {
                continue; // no es el inicio de una racha
            }
            int largo = 0;
            LocalDate d = dia;
            while (diasActivos.contains(d)) {
                largo++;
                d = d.plusDays(1);
            }
            mejor = Math.max(mejor, largo);
        }
        return mejor;
    }
}
