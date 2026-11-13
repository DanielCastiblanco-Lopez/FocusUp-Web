package com.focusup.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.focusup.excepcion.ReglaNegocioException;
import com.focusup.modelo.SesionPomodoro;
import com.focusup.modelo.Tarea;
import com.focusup.repositorio.SesionPomodoroRepository;
import com.focusup.repositorio.TareaRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/** Pruebas unitarias del registro de sesiones de Pomodoro (con repositorios simulados). */
class PomodoroServicioTest {

    private SesionPomodoroRepository sesiones;
    private TareaRepository tareas;
    private PomodoroServicio servicio;
    private final LocalDateTime ahora = LocalDateTime.of(2026, 3, 11, 10, 0);

    @BeforeEach
    void preparar() {
        sesiones = Mockito.mock(SesionPomodoroRepository.class);
        tareas = Mockito.mock(TareaRepository.class);
        when(sesiones.save(any(SesionPomodoro.class))).thenAnswer(i -> i.getArgument(0));
        servicio = new PomodoroServicio(sesiones, tareas);
    }

    @Test
    void sesionConTareaPropia_guardaElTituloDeLaTarea() {
        Tarea t = new Tarea("Estudiar Java", "Alta");
        when(tareas.findByIdAndUsuarioId(5L, 1L)).thenReturn(Optional.of(t));

        SesionPomodoro s = servicio.registrar(1L, 5L, 25, ahora);

        assertEquals("Estudiar Java", s.getNombreTarea());
        assertEquals(1L, s.getUsuarioId());
        assertEquals(25, s.getMinutos());
    }

    @Test
    void tareaAjena_seTrataComoSesionLibre() {
        when(tareas.findByIdAndUsuarioId(5L, 1L)).thenReturn(Optional.empty());

        SesionPomodoro s = servicio.registrar(1L, 5L, 25, ahora);

        assertEquals(PomodoroServicio.SESION_LIBRE, s.getNombreTarea());
    }

    @Test
    void tareaYaCompletada_seTrataComoSesionLibre() {
        Tarea t = new Tarea("Vieja", "Baja");
        t.setCompletada(true);
        when(tareas.findByIdAndUsuarioId(5L, 1L)).thenReturn(Optional.of(t));

        assertEquals(PomodoroServicio.SESION_LIBRE, servicio.registrar(1L, 5L, 25, ahora).getNombreTarea());
    }

    @Test
    void duracionFueraDeRango_seRechaza() {
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(1L, null, 0, ahora));
        assertThrows(ReglaNegocioException.class, () -> servicio.registrar(1L, null, 181, ahora));
        verify(sesiones, never()).save(any());
    }
}
