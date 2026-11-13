package com.focusup.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Una sesión de Pomodoro COMPLETADA (equivale a PomodoroSession de la app Android).
 * Pertenece siempre a un usuario. El nombre de la tarea se guarda como copia
 * (si luego se borra o renombra la tarea, el historial no cambia).
 *
 * @author Daniel Castiblanco
 * @version 3.0
 */
@Entity
@Table(name = "sesiones_pomodoro")
public class SesionPomodoro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "nombre_tarea", nullable = false, length = 200)
    private String nombreTarea;

    @Column(nullable = false)
    private int minutos;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    public SesionPomodoro() {}

    public SesionPomodoro(Long usuarioId, String nombreTarea, int minutos, LocalDateTime fechaHora) {
        this.usuarioId = usuarioId;
        this.nombreTarea = nombreTarea;
        this.minutos = minutos;
        this.fechaHora = fechaHora;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getNombreTarea() { return nombreTarea; }
    public int getMinutos() { return minutos; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}
