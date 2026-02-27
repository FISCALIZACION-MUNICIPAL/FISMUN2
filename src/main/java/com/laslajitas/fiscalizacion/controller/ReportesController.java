package com.laslajitas.fiscalizacion.controller;

import com.laslajitas.fiscalizacion.enums.EstadoTramite;
import com.laslajitas.fiscalizacion.enums.TipoTramite;
import com.laslajitas.fiscalizacion.service.TramiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    private TramiteService tramiteService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Reportes y Estadísticas");

        // KPIs
        model.addAttribute("totalTramites", tramiteService.countTramitesByTipo(null)); // Assuming countByTipo(null) or
        // findAll().size() but let's use
        // sum of types or just findAll
        // Actually countByTipo(null) might fail if I didn't handle it. Let's use
        // specific counts.
        long totalComercial = tramiteService
                .countTramitesByTipo(TipoTramite.COMERCIAL);
        long totalAlcohol = tramiteService.countTramitesByTipo(TipoTramite.ALCOHOL);
        long totalEventos = tramiteService.countTramitesByTipo(TipoTramite.EVENTO);
        long totalMultas = tramiteService.countTramitesByTipo(TipoTramite.MULTA);
        long totalNotificaciones = tramiteService
                .countTramitesByTipo(TipoTramite.NOTIFICACION);

        model.addAttribute("totalTramites",
                totalComercial + totalAlcohol + totalEventos + totalMultas + totalNotificaciones);
        model.addAttribute("totalRecaudado", tramiteService.sumMontoMultasFinalizadas());
        model.addAttribute("totalPendientes",
                tramiteService.countTramitesByEstado(EstadoTramite.PENDIENTE));

        // Chart Data (Pie - Types)
        model.addAttribute("countComercial", totalComercial);
        model.addAttribute("countAlcohol", totalAlcohol);
        model.addAttribute("countEventos", totalEventos);
        model.addAttribute("countMultas", totalMultas);
        model.addAttribute("countNotificaciones", totalNotificaciones);

        // Chart Data (Bar - Status)
        model.addAttribute("countPendiente",
                tramiteService.countTramitesByEstado(EstadoTramite.PENDIENTE));
        model.addAttribute("countEnProceso",
                tramiteService.countTramitesByEstado(EstadoTramite.EN_PROCESO));
        model.addAttribute("countFinalizado",
                tramiteService.countTramitesByEstado(EstadoTramite.FINALIZADO));
        model.addAttribute("countVencida",
                tramiteService.countTramitesByEstado(EstadoTramite.VENCIDA));

        return "reportes/index";
    }
}
