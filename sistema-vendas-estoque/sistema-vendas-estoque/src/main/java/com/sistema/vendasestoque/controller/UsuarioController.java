package com.sistema.vendasestoque.controller;

import com.sistema.vendasestoque.entity.Usuario;
import com.sistema.vendasestoque.enums.PerfilUsuario;
import com.sistema.vendasestoque.service.UsuarioService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String busca) {
        if (busca != null && !busca.trim().isEmpty()) {
            model.addAttribute("usuarios", usuarioService.buscarPorNomeOuEmail(busca));
            model.addAttribute("busca", busca);
        } else {
            model.addAttribute("usuarios", usuarioService.listarAtivos());
        }
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfis", PerfilUsuario.values());
        return "usuarios/form";
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario.get());
        return "usuarios/visualizar";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", usuario.get());
        model.addAttribute("perfis", PerfilUsuario.values());
        return "usuarios/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Usuario usuario, BindingResult result, 
                        Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", PerfilUsuario.values());
            return "usuarios/form";
        }

        try {
            if (usuario.getUsuarioId() == null) {
                usuarioService.salvar(usuario);
                redirectAttributes.addFlashAttribute("sucesso", "Usuário criado com sucesso!");
            } else {
                usuarioService.atualizar(usuario);
                redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso!");
            }
            return "redirect:/usuarios";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("perfis", PerfilUsuario.values());
            return "usuarios/form";
        }
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.desativar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário desativado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.ativar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário ativado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}

