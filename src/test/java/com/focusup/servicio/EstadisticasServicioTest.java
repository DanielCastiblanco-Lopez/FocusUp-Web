package com.focusup.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.focusup.modelo.SesionPomodoro;
import com.focusup.modelo.Tarea;
import com.focusup.repositorio.SesionPomodoroRepository;
import com.focusup.repositorio.TareaRepository;
import com.focusup.servicio.EstadisticasServicio.Periodo;
import com.focusup.servicio.EstadisticasServicio.Resumen;
import com.focusup.servicio.EstadisticasServicio.ResumenInicio;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Pruebas unitarias de Inicio y Estadísticas con una fecha "hoy" fija:
 * miércoles 11 de marzo de 2026 (la semana va del lunes 9 al domingo 15).
 */
class EstadisticasServicioTest {

    private static final LocalDate HOY = LocalDate.of(2026, 3, 11);
    private static final Long UID = 1L;

    private SesionPomodoroRepository sesiones;
    private TareaRepository tareas;
    private EstadisticasServicio servicio;

    @BeforeEach
    void preparar() {
        sesiones = Mockito.mock(SesionPomodoroRepository.class);
        tareas = Mockito.mock(TareaRepository.class);
        servicio = new EstadisticasServicio(sesiones, tareas);
        when(tareas.findByUsuarioIdOrderByFechaCreacionDesc(UID)).thenReturn(List.of());
        when(sesiones.findByUsuarioIdOrderByFechaHoraDesc(UID)).thenReturn(List.of());
    }

    private static SesionPomodoro sesion(LocalDate dia, int minutos) {
        return new SesionPomodoro(UID, "Libre", minutos, dia.atTime(9, 0));
    }

    @Test
    void inicio_sumaLosMinutosDeHoyYCuentaPendientes() {
        Tarea pendiente = new Tarea("A", "Alta");
        pendiente.setFechaLimite(HOY.plusDays(2));
        Tarea otra = new Tarea("B", "Baja");
        Tarea hecha = new Tarea("C", "Media");
        hecha.setCompletada(true);
        hecha.setFechaCompletada(HOY.atTime(8, 0));
        when(tareas.findByUsuarioIdOrderByFechaCreacionDesc(UID)).thenReturn(List.of(pendiente, otra, hecha));
        when(sesiones.findByUsuarioIdOrderByFechaHoraDesc(UID))
                .thenReturn(List.of(sesion(HOY, 25), sesion(HOY, 10), sesion(HOY.minusDays(1), 50)));

        ResumenInicio r = servicio.inicio(UID, HOY);

        assertEquals(2, r.pendientes());
        assertEquals(35, r.minutosHoy());
        assertEquals("A", r.proximaTarea().getTitulo());
        assertEquals(2, r.racha()); // hoy (sesión y tarea) + ayer (sesión)
    }

    @Test
    void inicio_calendarioSemanalVaDeLunesADomingoYMarcaHoy() {
        Tarea t = new Tarea("Entrega", "Alta");
        t.setFechaLimite(LocalDate.of(2026, 3, 13)); // viernes
        when(tareas.findByUsuarioIdOrderByFechaCreacionDesc(UID)).thenReturn(List.of(t));

        var semana = servicio.inicio(UID, HOY).semana();

        assertEquals(7, semana.size());
        assertEquals(LocalDate.of(2026, 3, 9), semana.get(0).fecha());
        assertTrue(semana.get(2).hoy());
        assertEquals(1, semana.get(4).tareas());
        assertEquals(0, semana.get(3).tareas());
    }

    @Test
    void inicio_sinDatos_todoEnCeroYSinProximaTarea() {
        ResumenInicio r = servicio.inicio(UID, HOY);
        assertEquals(0, r.pendientes());
        assertEquals(0, r.racha());
        assertNull(r.proximaTarea());
    }

    @Test
    void estadisticas_semanaSoloIncluyeDesdeElLunes() {
        when(sesiones.findByUsuarioIdOrderByFechaHoraDesc(UID)).thenReturn(List.of(
                sesion(HOY, 30), sesion(LocalDate.of(2026, 3, 9), 20), sesion(LocalDate.of(2026, 3, 8), 99)));

        Resumen r = servicio.estadisticas(UID, Periodo.SEMANA, HOY);

        assertEquals(50, r.minutos());
        assertEquals(2, r.sesiones());
        assertEquals(20, r.barras().get(0).minutos()); // lunes
        assertEquals(30, r.barras().get(2).minutos()); // miércoles
        assertEquals(100, r.barras().get(2).alturaPorcentaje()); // la barra más alta (mínimo de escala 30)
        assertNull(r.total());
    }

    @Test
    void estadisticas_mesYTotal() {
        when(sesiones.findByUsuarioIdOrderByFechaHoraDesc(UID)).thenReturn(List.of(
                sesion(HOY, 30), sesion(LocalDate.of(2026, 3, 1), 20), sesion(LocalDate.of(2026, 2, 20), 40)));

        assertEquals(50, servicio.estadisticas(UID, Periodo.MES, HOY).minutos());

        Resumen total = servicio.estadisticas(UID, Periodo.TOTAL, HOY);
        assertEquals(90, total.minutos());
        assertNotNull(total.total());
        assertEquals(3, total.total().diasEstudiados());
        assertEquals(30, total.total().promedioDiario());
        assertEquals(40, total.total().recordDia());
    }

    @Test
    void estadisticas_tareasCompletadasSeFiltranPorPeriodo() {
        Tarea reciente = new Tarea("R", "Alta");
        reciente.setCompletada(true);
        reciente.setFechaCompletada(HOY.atTime(7, 0));
        Tarea vieja = new Tarea("V", "Alta");
        vieja.setCompletada(true);
        vieja.setFechaCompletada(LocalDateTime.of(2026, 1, 5, 7, 0));
        when(tareas.findByUsuarioIdOrderByFechaCreacionDesc(UID)).thenReturn(List.of(reciente, vieja));

        assertEquals(1, servicio.estadisticas(UID, Periodo.SEMANA, HOY).tareasCompletadas());
        assertEquals(2, servicio.estadisticas(UID, Periodo.TOTAL, HOY).tareasCompletadas());
    }

    @Test
    void periodoDesconocido_caeEnSemana() {
        assertEquals(Periodo.SEMANA, Periodo.desde("cualquier-cosa"));
        assertEquals(Periodo.MES, Periodo.desde("MES"));
        assertEquals(Periodo.SEMANA, Periodo.desde(null));
    }
}
