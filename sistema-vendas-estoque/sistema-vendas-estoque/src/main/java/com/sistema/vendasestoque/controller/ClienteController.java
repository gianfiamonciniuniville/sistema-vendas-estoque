package com.sistema.vendasestoque.controller;

import com.sistema.vendasestoque.entity.Cliente;
import com.sistema.vendasestoque.service.ClienteService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String busca) {
        if (busca != null && !busca.trim().isEmpty()) {
            model.addAttribute("clientes", clienteService.buscarPorTermo(busca));
            model.addAttribute("busca", busca);
        } else {
            model.addAttribute("clientes", clienteService.listarTodos());
        }
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);
        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado");
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cliente.get());
        return "clientes/visualizar";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cliente> cliente = clienteService.buscarPorId(id);
        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado");
            return "redirect:/clientes";
        }
        model.addAttribute("cliente", cliente.get());
        return "clientes/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Cliente cliente, BindingResult result, 
                        Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "clientes/form";
        }

        try {
            if (cliente.getClienteId() == null) {
                clienteService.salvar(cliente);
                redirectAttributes.addFlashAttribute("sucesso", "Cliente criado com sucesso!");
            } else {
                clienteService.atualizar(cliente);
                redirectAttributes.addFlashAttribute("sucesso", "Cliente atualizado com sucesso!");
            }
            return "redirect:/clientes";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "clientes/form";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public Object buscarPorTermo(@RequestParam String termo) {
        return clienteService.buscarPorTermo(termo);
    }
}

