package com.focusup.controlador;

import com.focusup.seguridad.Sesion;
import com.focusup.servicio.EstadisticasServicio;
import com.focusup.servicio.EstadisticasServicio.Periodo;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Pantallas "Inicio" y "Estadísticas" (módulos portados de la app Android).
 * Solo lectura: el usuario sale siempre de la sesión, así nadie ve datos ajenos.
 *
 * @author Daniel Castiblanco
 * @version 3.0
 */
@Controller
public class PanelControlador {

    private final EstadisticasServicio estadisticas;

    public PanelControlador(EstadisticasServicio estadisticas) {
        this.estadisticas = estadisticas;
    }

    @GetMapping("/inicio")
    public String inicio(HttpServletRequest request, Model model) {
        Long usuarioId = Sesion.usuarioActual(request).getId();
        model.addAttribute("resumen", estadisticas.inicio(usuarioId, LocalDate.now()));
        return "inicio/inicio";
    }

    @GetMapping("/estadisticas")
    public String estadisticas(@RequestParam(required = false) String periodo,
                               HttpServletRequest request, Model model) {
        Long usuarioId = Sesion.usuarioActual(request).getId();
        Periodo p = Periodo.desde(periodo);
        model.addAttribute("r", estadisticas.estadisticas(usuarioId, p, LocalDate.now()));
        model.addAttribute("periodoActual", p.name().toLowerCase());
        return "estadisticas/estadisticas";
    }
}
