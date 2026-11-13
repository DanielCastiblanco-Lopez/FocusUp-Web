package com.focusup.servicio;

import com.focusup.excepcion.ReglaNegocioException;
import com.focusup.modelo.SesionPomodoro;
import com.focusup.modelo.Tarea;
import com.focusup.repositorio.SesionPomodoroRepository;
import com.focusup.repositorio.TareaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registro de sesiones de Pomodoro. El navegador cuenta el tiempo, pero el
 * servidor valida todo lo que recibe: rango de minutos, que la tarea sea del
 * usuario de la sesión y que siga pendiente.
 *
 * @author Daniel Castiblanco
 * @version 3.0
 */
@Service
@Transactional
public class PomodoroServicio {

    public static final String SESION_LIBRE = "Sesión libre";
    public static final int MIN_MINUTOS = 1;
    public static final int MAX_MINUTOS = 180;

    private final SesionPomodoroRepository sesiones;
    private final TareaRepository tareas;

    public PomodoroServicio(SesionPomodoroRepository sesiones, TareaRepository tareas) {
        this.sesiones = sesiones;
        this.tareas = tareas;
    }

    /** @param tareaId opcional; si es null (o no es del usuario) la sesión es "libre" */
    public SesionPomodoro registrar(Long usuarioId, Long tareaId, int minutos, LocalDateTime ahora) {
        if (minutos < MIN_MINUTOS || minutos > MAX_MINUTOS) {
            throw new ReglaNegocioException(
                    "La duración debe estar entre " + MIN_MINUTOS + " y " + MAX_MINUTOS + " minutos");
        }
        String nombre = SESION_LIBRE;
        if (tareaId != null) {
            // findByIdAndUsuarioId: una tarea ajena simplemente "no existe" para este usuario.
            nombre = tareas.findByIdAndUsuarioId(tareaId, usuarioId)
                    .filter(t -> !t.isCompletada())
                    .map(Tarea::getTitulo)
                    .orElse(SESION_LIBRE);
        }
        return sesiones.save(new SesionPomodoro(usuarioId, nombre, minutos, ahora));
    }

    @Transactional(readOnly = true)
    public List<SesionPomodoro> ultimas(Long usuarioId, int cantidad) {
        return sesiones.findByUsuarioIdOrderByFechaHoraDesc(usuarioId).stream().limit(cantidad).toList();
    }
}
