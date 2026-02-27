package com.laslajitas.fiscalizacion.controller;

import com.laslajitas.fiscalizacion.entity.Usuario;
import com.laslajitas.fiscalizacion.enums.Rol;
import com.laslajitas.fiscalizacion.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ─── Listar ────────────────────────────────────────────────────────────────

    @GetMapping
    public String index(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("pageTitle", "Gestión de Usuarios");
        return "usuarios/index";
    }

    // ─── Formulario nuevo ──────────────────────────────────────────────────────

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        model.addAttribute("pageTitle", "Nuevo Usuario");
        return "usuarios/form";
    }

    // ─── Formulario editar ─────────────────────────────────────────────────────

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("pageTitle", "Editar Usuario");
        return "usuarios/form";
    }

    // ─── Guardar (nuevo o edición) ─────────────────────────────────────────────

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          @RequestParam(required = false) String rawPassword,
                          RedirectAttributes redirectAttributes) {
        try {
            if (usuario.getId() == null) {
                // NUEVO usuario: delega toda la lógica al service
                usuarioService.guardarNuevo(usuario, rawPassword);
            } else {
                // EDICIÓN: delega toda la lógica al service
                usuarioService.actualizar(usuario, rawPassword);
            }
            redirectAttributes.addFlashAttribute("success", "Usuario guardado correctamente.");
            return "redirect:/usuarios";

        } catch (IllegalArgumentException e) {
            // El service lanzó una excepción de negocio (ej: username ya existe)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return usuario.getId() == null
                    ? "redirect:/usuarios/nuevo"
                    : "redirect:/usuarios/editar/" + usuario.getId();
        }
    }

    // ─── Eliminar ──────────────────────────────────────────────────────────────

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}