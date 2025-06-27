package com.sistema.vendasestoque.controller;

import com.sistema.vendasestoque.entity.Pedido;
import com.sistema.vendasestoque.entity.ItemPedido;
import com.sistema.vendasestoque.enums.StatusPedido;
import com.sistema.vendasestoque.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ItemPedidoService itemPedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listar(Model model, 
                        @RequestParam(required = false) StatusPedido status,
                        @RequestParam(required = false) Long clienteId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        List<Pedido> pedidos;
        
        if (status != null) {
            pedidos = pedidoService.buscarPorStatus(status);
            model.addAttribute("status", status);
        } else if (clienteId != null) {
            pedidos = pedidoService.buscarPorCliente(clienteId);
            model.addAttribute("clienteId", clienteId);
        } else if (dataInicio != null && dataFim != null) {
            pedidos = pedidoService.buscarPorPeriodo(dataInicio, dataFim);
            model.addAttribute("dataInicio", dataInicio);
            model.addAttribute("dataFim", dataFim);
        } else {
            pedidos = pedidoService.listarTodos();
        }
        
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("statusList", StatusPedido.values());
        model.addAttribute("clientes", clienteService.listarTodos());
        return "pedidos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("usuarios", usuarioService.listarAtivos());
        return "pedidos/form";
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado");
            return "redirect:/pedidos";
        }
        
        List<ItemPedido> itens = itemPedidoService.buscarPorPedido(id);
        model.addAttribute("pedido", pedido.get());
        model.addAttribute("itens", itens);
        return "pedidos/visualizar";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        if (pedido.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado");
            return "redirect:/pedidos";
        }
        
        List<ItemPedido> itens = itemPedidoService.buscarPorPedido(id);
        model.addAttribute("pedido", pedido.get());
        model.addAttribute("itens", itens);
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("usuarios", usuarioService.listarAtivos());
        model.addAttribute("produtos", produtoService.listarAtivos());
        model.addAttribute("statusList", StatusPedido.values());
        return "pedidos/form";
    }

    @PostMapping
    public String salvar(@RequestParam Long clienteId, 
                        @RequestParam Long usuarioId,
                        @RequestParam(required = false) String observacoes,
                        RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.criarPedido(clienteId, usuarioId);
            if (observacoes != null && !observacoes.trim().isEmpty()) {
                pedido.setObservacoes(observacoes);
                pedidoService.atualizar(pedido);
            }
            redirectAttributes.addFlashAttribute("sucesso", "Pedido criado com sucesso!");
            return "redirect:/pedidos/" + pedido.getPedidoId() + "/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/pedidos/novo";
        }
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.confirmarPedido(id);
            redirectAttributes.addFlashAttribute("sucesso", "Pedido confirmado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cancelarPedido(id);
            redirectAttributes.addFlashAttribute("sucesso", "Pedido cancelado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/status")
    public String atualizarStatus(@PathVariable Long id, 
                                 @RequestParam StatusPedido status,
                                 RedirectAttributes redirectAttributes) {
        try {
            pedidoService.atualizarStatus(id, status);
            redirectAttributes.addFlashAttribute("sucesso", "Status atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Pedido excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/{pedidoId}/itens")
    public String adicionarItem(@PathVariable Long pedidoId,
                               @RequestParam Long produtoId,
                               @RequestParam Integer quantidade,
                               RedirectAttributes redirectAttributes) {
        try {
            itemPedidoService.adicionarItemAoPedido(pedidoId, produtoId, quantidade);
            redirectAttributes.addFlashAttribute("sucesso", "Item adicionado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/" + pedidoId + "/editar";
    }

    @PostMapping("/itens/{itemId}/excluir")
    public String excluirItem(@PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        try {
            Optional<ItemPedido> item = itemPedidoService.buscarPorId(itemId);
            if (item.isPresent()) {
                Long pedidoId = item.get().getPedido().getPedidoId();
                itemPedidoService.excluir(itemId);
                redirectAttributes.addFlashAttribute("sucesso", "Item removido com sucesso!");
                return "redirect:/pedidos/" + pedidoId + "/editar";
            } else {
                redirectAttributes.addFlashAttribute("erro", "Item não encontrado");
                return "redirect:/pedidos";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/pedidos";
        }
    }

    @GetMapping("/relatorio")
    public String relatorio(Model model,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        if (dataInicio == null) dataInicio = LocalDate.now().withDayOfMonth(1);
        if (dataFim == null) dataFim = LocalDate.now();
        
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("vendas", pedidoService.calcularVendasPorPeriodo(dataInicio, dataFim));
        model.addAttribute("ticketMedio", pedidoService.calcularTicketMedioPorPeriodo(dataInicio, dataFim));
        model.addAttribute("pedidos", pedidoService.buscarPorPeriodo(dataInicio, dataFim));
        
        return "pedidos/relatorio";
    }
}

