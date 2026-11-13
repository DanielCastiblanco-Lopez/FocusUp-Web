package com.focusup.repositorio;

import com.focusup.modelo.SesionPomodoro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de sesiones de Pomodoro. Todas las consultas filtran por usuario.
 *
 * @author Daniel Castiblanco
 * @version 3.0
 */
@Repository
public interface SesionPomodoroRepository extends JpaRepository<SesionPomodoro, Long> {

    List<SesionPomodoro> findByUsuarioIdOrderByFechaHoraDesc(Long usuarioId);
}
