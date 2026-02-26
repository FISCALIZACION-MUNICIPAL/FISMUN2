package com.laslajitas.fiscalizacion.controller;

import com.laslajitas.fiscalizacion.enums.TipoTramite;
import com.laslajitas.fiscalizacion.repository.TramiteRepository;
import com.laslajitas.fiscalizacion.entity.Tramite;
import com.laslajitas.fiscalizacion.enums.EstadoTramite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/habilitaciones")
public class HabilitacionesController {

    @Autowired
    private TramiteRepository tramiteRepository;

    @Autowired
    private com.laslajitas.fiscalizacion.service.TramiteService tramiteService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Habilitaciones Comerciales");
        // Filtrar solo habilitaciones comerciales
        model.addAttribute("habilitaciones", tramiteRepository.findAll().stream()
                .filter(t -> t.getTipo() == TipoTramite.COMERCIAL)
                .collect(Collectors.toList()));
        return "habilitaciones/index";
    }

    @GetMapping("/nueva")
    public String nuevaHabilitacion(Model model) {
        model.addAttribute("pageTitle", "Nueva Habilitación");
        model.addAttribute("tramite", new Tramite());
        return "habilitaciones/form";
    }

    @PostMapping("/guardar")
    public String guardarHabilitacion(Tramite tramite) {
        tramite.setTipo(TipoTramite.COMERCIAL);

        // If editing an existing record, preserve the original fecha
        if (tramite.getId() != null) {
            Tramite existingTramite = tramiteRepository.findById(tramite.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid tramite Id:" + tramite.getId()));
            tramite.setFecha(existingTramite.getFecha());
        } else {
            // For new records, set defaults
            if (tramite.getEstado() == null) {
                tramite.setEstado(EstadoTramite.INSPECCION_PENDIENTE);
            }
            tramite.setFecha(LocalDate.now());
        }

        tramiteRepository.save(tramite);
        tramiteService.verificarYCrearNotificacion(tramite);
        return "redirect:/habilitaciones";
    }

    @GetMapping("/ver/{id}")
    public String verHabilitacion(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Ver Habilitación");
        model.addAttribute("tramite", tramiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tramite Id:" + id)));
        model.addAttribute("readonly", true);
        return "habilitaciones/form";
    }

    @GetMapping("/editar/{id}")
    public String editarHabilitacion(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Habilitación");
        model.addAttribute("tramite", tramiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tramite Id:" + id)));
        return "habilitaciones/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarHabilitacion(@PathVariable Long id) {
        Tramite tramite = tramiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid tramite Id:" + id));
        tramiteRepository.delete(tramite);
        return "redirect:/habilitaciones";
    }
}
