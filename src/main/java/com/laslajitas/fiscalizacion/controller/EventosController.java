package com.laslajitas.fiscalizacion.controller;

import com.laslajitas.fiscalizacion.enums.TipoTramite;
import com.laslajitas.fiscalizacion.entity.Tramite;
import com.laslajitas.fiscalizacion.service.TramiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/eventos")
public class EventosController {

    @Autowired
    private TramiteService tramiteService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Permisos de Eventos");
        model.addAttribute("eventos", tramiteService.findByTipo(TipoTramite.EVENTO));
        return "eventos/index";
    }

    @GetMapping("/nuevo")
    public String nuevoEvento(Model model) {
        model.addAttribute("pageTitle", "Nuevo Permiso de Evento");
        model.addAttribute("tramite", new Tramite());
        return "eventos/form";
    }

    @PostMapping("/guardar")
    public String guardarEvento(Tramite tramite) {
        tramiteService.guardarEvento(tramite);
        return "redirect:/eventos";
    }

    @GetMapping("/ver/{id}")
    public String verEvento(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Ver Permiso de Evento");
        model.addAttribute("tramite", tramiteService.findById(id));
        model.addAttribute("readonly", true);
        return "eventos/form";
    }

    @GetMapping("/editar/{id}")
    public String editarEvento(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Permiso de Evento");
        model.addAttribute("tramite", tramiteService.findById(id));
        return "eventos/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEvento(@PathVariable Long id) {
        tramiteService.eliminar(id);
        return "redirect:/eventos";
    }
}