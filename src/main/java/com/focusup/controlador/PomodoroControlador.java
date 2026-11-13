package com.focusup.controlador;

import com.focusup.excepcion.ReglaNegocioException;
import com.focusup.modelo.SesionPomodoro;
import com.focusup.seguridad.Sesion;
import com.focusup.servicio.PomodoroServicio;
import com.focusup.servicio.TareaServicio;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Pomodoro: el temporizador corre en el navegador (JavaScript) y, al terminar,
 * envía la sesión por POST (con token CSRF) para guardarla.
 *
 * GET  /pomodoro            pantalla del temporizador
 * POST /pomodoro/sesiones   guarda una sesión completada (responde JSON)
 *
 * @author Daniel Castiblanco
 * @version 3.0
 */
@Controller
@RequestMapping("/pomodoro")
public class PomodoroControlador {

    private final PomodoroServicio pomodoroServicio;
    private final TareaServicio tareaServicio;

    public PomodoroControlador(PomodoroServicio pomodoroServicio, TareaServicio tareaServicio) {
        this.pomodoroServicio = pomodoroServicio;
        this.tareaServicio = tareaServicio;
    }

    @GetMapping
    public String pantalla(HttpServletRequest request, Model model) {
        Long usuarioId = Sesion.usuarioActual(request).getId();
        model.addAttribute("pendientes", tareaServicio.obtenerPendientes(usuarioId));
        model.addAttribute("ultimas", pomodoroServicio.ultimas(usuarioId, 5));
        return "pomodoro/pomodoro";
    }

    @PostMapping("/sesiones")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardar(@RequestParam int minutos,
                                                        @RequestParam(required = false) Long tareaId,
                                                        HttpServletRequest request) {
        Long usuarioId = Sesion.usuarioActual(request).getId();
        try {
            SesionPomodoro s = pomodoroServicio.registrar(usuarioId, tareaId, minutos, LocalDateTime.now());
            return ResponseEntity.ok(Map.of("ok", true, "tarea", s.getNombreTarea(), "minutos", s.getMinutos()));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("ok", false, "error", e.getMessage()));
        }
    }
}
