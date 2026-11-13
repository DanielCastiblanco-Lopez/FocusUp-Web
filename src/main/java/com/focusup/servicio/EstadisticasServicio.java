package com.focusup.servicio;

import com.focusup.modelo.SesionPomodoro;
import com.focusup.modelo.Tarea;
import com.focusup.repositorio.SesionPomodoroRepository;
import com.focusup.repositorio.TareaRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Calcula lo que muestran Inicio y Estadísticas (equivalente a PomodoroStorage,
 * StreakStorage y TaskStorage de la app Android). Todo se calcula en memoria a
 * partir de las sesiones y tareas DEL USUARIO que se pide; "hoy" llega como
 * parámetro para poder probar con fechas fijas.
 *
 * @author Daniel Castiblanco
 * @version 3.0
 */
@Service
@Transactional(readOnly = true)
public class EstadisticasServicio {

    public enum Periodo {
        SEMANA("Semana"), MES("Mes"), TOTAL("Total");

        private final String etiqueta;

        Periodo(String etiqueta) { this.etiqueta = etiqueta; }

        public String etiqueta() { return etiqueta; }

        /** Convierte el parámetro de la URL; cualquier valor raro cae en SEMANA. */
        public static Periodo desde(String texto) {
            if (texto != null) {
                for (Periodo p : values()) {
                    if (p.name().equalsIgnoreCase(texto)) {
                        return p;
                    }
                }
            }
            return SEMANA;
        }
    }

    /** Un día de la semana en el calendario de Inicio. */
    public record DiaSemana(String etiqueta, LocalDate fecha, int tareas, boolean hoy) {}

    /** Una barra del gráfico semanal. */
    public record BarraDia(String etiqueta, int minutos, int alturaPorcentaje, boolean hoy) {}

    public record ResumenInicio(int pendientes, int minutosHoy, int racha, Tarea proximaTarea,
                                List<DiaSemana> semana) {}

    public record ResumenTotal(LocalDate usandoDesde, int diasEstudiados, int rachaMaxima,
                               int promedioDiario, int mejorSemana, int recordDia) {}

    public record Resumen(Periodo periodo, int tareasCompletadas, int minutos, int sesiones,
                          List<BarraDia> barras, List<SesionPomodoro> ultimasSesiones,
                          int rachaActual, int rachaMaxima, ResumenTotal total) {}

    private static final String[] DIAS = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};

    private final SesionPomodoroRepository sesiones;
    private final TareaRepository tareas;

    public EstadisticasServicio(SesionPomodoroRepository sesiones, TareaRepository tareas) {
        this.sesiones = sesiones;
        this.tareas = tareas;
    }

    public ResumenInicio inicio(Long usuarioId, LocalDate hoy) {
        List<SesionPomodoro> ses = sesiones.findByUsuarioIdOrderByFechaHoraDesc(usuarioId);
        List<Tarea> todas = tareas.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);

        int minutosHoy = ses.stream().filter(s -> s.getFechaHora().toLocalDate().equals(hoy))
                .mapToInt(SesionPomodoro::getMinutos).sum();
        int pendientes = (int) todas.stream().filter(t -> !t.isCompletada()).count();

        // Próxima tarea: la pendiente con fecha límite más cercana (las vencidas salen primero).
        Tarea proxima = todas.stream()
                .filter(t -> !t.isCompletada() && t.getFechaLimite() != null)
                .min((a, b) -> a.getFechaLimite().compareTo(b.getFechaLimite()))
                .orElse(null);

        LocalDate lunes = inicioSemana(hoy);
        List<DiaSemana> semana = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate dia = lunes.plusDays(i);
            int cuantas = (int) todas.stream().filter(t -> dia.equals(t.getFechaLimite())).count();
            semana.add(new DiaSemana(DIAS[i], dia, cuantas, dia.equals(hoy)));
        }
        return new ResumenInicio(pendientes, minutosHoy, Racha.actual(diasActivos(ses, todas), hoy),
                proxima, semana);
    }

    public Resumen estadisticas(Long usuarioId, Periodo periodo, LocalDate hoy) {
        List<SesionPomodoro> todasSes = sesiones.findByUsuarioIdOrderByFechaHoraDesc(usuarioId);
        List<Tarea> todasTareas = tareas.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);

        LocalDate desde = switch (periodo) {
            case SEMANA -> inicioSemana(hoy);
            case MES -> hoy.withDayOfMonth(1);
            case TOTAL -> LocalDate.MIN;
        };
        List<SesionPomodoro> delPeriodo = todasSes.stream()
                .filter(s -> !s.getFechaHora().toLocalDate().isBefore(desde)).toList();
        int tareasCompletadas = (int) todasTareas.stream()
                .filter(t -> t.isCompletada() && t.getFechaCompletada() != null
                        && !t.getFechaCompletada().toLocalDate().isBefore(desde))
                .count();
        int minutos = delPeriodo.stream().mapToInt(SesionPomodoro::getMinutos).sum();

        Set<LocalDate> activos = diasActivos(todasSes, todasTareas);
        return new Resumen(periodo, tareasCompletadas, minutos, delPeriodo.size(),
                barrasSemana(todasSes, hoy), delPeriodo.stream().limit(5).toList(),
                Racha.actual(activos, hoy), Racha.maxima(activos),
                periodo == Periodo.TOTAL ? resumenTotal(usuarioId, todasSes, todasTareas, activos) : null);
    }

    private ResumenTotal resumenTotal(Long usuarioId, List<SesionPomodoro> ses, List<Tarea> tar,
                                      Set<LocalDate> activos) {
        Map<LocalDate, Integer> porDia = minutosPorDia(ses);
        int total = ses.stream().mapToInt(SesionPomodoro::getMinutos).sum();
        int diasEstudiados = porDia.size();
        int promedio = diasEstudiados == 0 ? 0 : Math.round((float) total / diasEstudiados);
        int record = porDia.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        Map<LocalDate, Integer> porSemana = new LinkedHashMap<>();
        porDia.forEach((dia, min) -> porSemana.merge(inicioSemana(dia), min, Integer::sum));
        int mejorSemana = porSemana.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        LocalDate desde = tar.stream().map(t -> t.getFechaCreacion().toLocalDate()).min(LocalDate::compareTo)
                .orElse(null);
        LocalDate primeraSesion = ses.stream().map(s -> s.getFechaHora().toLocalDate())
                .min(LocalDate::compareTo).orElse(null);
        if (desde == null || (primeraSesion != null && primeraSesion.isBefore(desde))) {
            desde = primeraSesion;
        }
        return new ResumenTotal(desde, diasEstudiados, Racha.maxima(activos), promedio, mejorSemana, record);
    }

    private List<BarraDia> barrasSemana(List<SesionPomodoro> ses, LocalDate hoy) {
        LocalDate lunes = inicioSemana(hoy);
        Map<LocalDate, Integer> porDia = minutosPorDia(ses);
        int[] min = new int[7];
        for (int i = 0; i < 7; i++) {
            min[i] = porDia.getOrDefault(lunes.plusDays(i), 0);
        }
        int maximo = Math.max(30, java.util.Arrays.stream(min).max().orElse(0)); // mínimo 30, como en Android
        List<BarraDia> barras = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int alto = Math.max(4, Math.round(100f * min[i] / maximo));
            barras.add(new BarraDia(DIAS[i], min[i], alto, lunes.plusDays(i).equals(hoy)));
        }
        return barras;
    }

    private static Map<LocalDate, Integer> minutosPorDia(List<SesionPomodoro> ses) {
        Map<LocalDate, Integer> porDia = new LinkedHashMap<>();
        for (SesionPomodoro s : ses) {
            porDia.merge(s.getFechaHora().toLocalDate(), s.getMinutos(), Integer::sum);
        }
        return porDia;
    }

    private static Set<LocalDate> diasActivos(List<SesionPomodoro> ses, List<Tarea> tar) {
        Set<LocalDate> dias = new HashSet<>();
        ses.forEach(s -> dias.add(s.getFechaHora().toLocalDate()));
        tar.stream().filter(t -> t.isCompletada() && t.getFechaCompletada() != null)
                .forEach(t -> dias.add(t.getFechaCompletada().toLocalDate()));
        return dias;
    }

    static LocalDate inicioSemana(LocalDate dia) {
        return dia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
