package com.laslajitas.fiscalizacion.controller;

import com.laslajitas.fiscalizacion.enums.TipoTramite;
import com.laslajitas.fiscalizacion.entity.Tramite;
import com.laslajitas.fiscalizacion.service.TramiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alcohol")
public class AlcoholController {

    @Autowired
    private TramiteService tramiteService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Licencias de Venta de Alcohol");
        model.addAttribute("alcoholes", tramiteService.findByTipo(TipoTramite.ALCOHOL));
        return "alcohol/index";
    }

    @GetMapping("/nueva")
    public String nuevaLicencia(Model model) {
        model.addAttribute("pageTitle", "Nueva Licencia de Alcohol");
        model.addAttribute("tramite", new Tramite());
        return "alcohol/form";
    }

    @PostMapping("/guardar")
    public String guardarLicencia(Tramite tramite) {
        tramiteService.guardarAlcohol(tramite);
        return "redirect:/alcohol";
    }

    @GetMapping("/ver/{id}")
    public String verLicencia(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Ver Licencia de Alcohol");
        model.addAttribute("tramite", tramiteService.findById(id));
        model.addAttribute("readonly", true);
        return "alcohol/form";
    }

    @GetMapping("/editar/{id}")
    public String editarLicencia(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Editar Licencia de Alcohol");
        model.addAttribute("tramite", tramiteService.findById(id));
        return "alcohol/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarLicencia(@PathVariable Long id) {
        tramiteService.eliminar(id);
        return "redirect:/alcohol";
    }
}