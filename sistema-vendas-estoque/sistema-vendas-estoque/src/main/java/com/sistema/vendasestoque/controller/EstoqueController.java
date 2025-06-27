package com.sistema.vendasestoque.controller;

import com.sistema.vendasestoque.entity.Estoque;
import com.sistema.vendasestoque.service.EstoqueService;
import com.sistema.vendasestoque.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String busca) {
        if (busca != null && !busca.trim().isEmpty()) {
            model.addAttribute("estoques", estoqueService.buscarPorProduto(busca));
            model.addAttribute("busca", busca);
        } else {
            model.addAttribute("estoques", estoqueService.listarTodos());
        }
        return "estoque/lista";
    }

    @GetMapping("/baixo")
    public String estoqueBaixo(Model model) {
        model.addAttribute("estoques", estoqueService.listarEstoqueBaixo());
        model.addAttribute("titulo", "Produtos com Estoque Baixo");
        return "estoque/lista";
    }

    @GetMapping("/zerado")
    public String estoqueZerado(Model model) {
        model.addAttribute("estoques", estoqueService.listarEstoqueZerado());
        model.addAttribute("titulo", "Produtos com Estoque Zerado");
        return "estoque/lista";
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Estoque> estoque = estoqueService.buscarPorId(id);
        if (estoque.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Estoque não encontrado");
            return "redirect:/estoque";
        }
        model.addAttribute("estoque", estoque.get());
        return "estoque/visualizar";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Estoque> estoque = estoqueService.buscarPorId(id);
        if (estoque.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Estoque não encontrado");
            return "redirect:/estoque";
        }
        model.addAttribute("estoque", estoque.get());
        return "estoque/form";
    }

    @PostMapping("/{id}/atualizar")
    public String atualizar(@PathVariable Long id, 
                           @RequestParam Integer quantidadeAtual,
                           @RequestParam Integer quantidadeMinima,
                           RedirectAttributes redirectAttributes) {
        try {
            Optional<Estoque> estoqueOpt = estoqueService.buscarPorId(id);
            if (estoqueOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Estoque não encontrado");
                return "redirect:/estoque";
            }

            Estoque estoque = estoqueOpt.get();
            estoque.setQuantidadeAtual(quantidadeAtual);
            estoque.setQuantidadeMinima(quantidadeMinima);
            
            estoqueService.atualizar(estoque);
            redirectAttributes.addFlashAttribute("sucesso", "Estoque atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/estoque";
    }

    @PostMapping("/{produtoId}/adicionar")
    public String adicionarEstoque(@PathVariable Long produtoId, 
                                  @RequestParam Integer quantidade,
                                  RedirectAttributes redirectAttributes) {
        try {
            estoqueService.adicionarEstoque(produtoId, quantidade);
            redirectAttributes.addFlashAttribute("sucesso", "Estoque adicionado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/estoque";
    }

    @PostMapping("/{produtoId}/remover")
    public String removerEstoque(@PathVariable Long produtoId, 
                                @RequestParam Integer quantidade,
                                RedirectAttributes redirectAttributes) {
        try {
            boolean sucesso = estoqueService.removerEstoque(produtoId, quantidade);
            if (sucesso) {
                redirectAttributes.addFlashAttribute("sucesso", "Estoque removido com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Estoque insuficiente para remoção");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/estoque";
    }

    @GetMapping("/movimentacao/{produtoId}")
    public String movimentacao(@PathVariable Long produtoId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Estoque> estoque = estoqueService.buscarPorProdutoId(produtoId);
        if (estoque.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Estoque não encontrado");
            return "redirect:/estoque";
        }
        model.addAttribute("estoque", estoque.get());
        return "estoque/movimentacao";
    }

    @GetMapping("/relatorio")
    public String relatorio(Model model) {
        model.addAttribute("totalItens", estoqueService.calcularTotalItensEmEstoque());
        model.addAttribute("estoqueBaixo", estoqueService.contarEstoqueBaixo());
        model.addAttribute("estoqueZerado", estoqueService.contarEstoqueZerado());
        model.addAttribute("estoques", estoqueService.listarTodos());
        return "estoque/relatorio";
    }
}

