package com.sistema.vendasestoque.controller;

import com.sistema.vendasestoque.entity.Produto;
import com.sistema.vendasestoque.service.ProdutoService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listar(Model model, 
                        @RequestParam(required = false) String busca,
                        @RequestParam(required = false) String categoria,
                        @RequestParam(required = false) BigDecimal precoMin,
                        @RequestParam(required = false) BigDecimal precoMax) {
        
        if (busca != null && !busca.trim().isEmpty()) {
            model.addAttribute("produtos", produtoService.buscarPorTermo(busca));
            model.addAttribute("busca", busca);
        } else if (categoria != null && !categoria.trim().isEmpty()) {
            model.addAttribute("produtos", produtoService.buscarPorCategoria(categoria));
            model.addAttribute("categoria", categoria);
        } else if (precoMin != null && precoMax != null) {
            model.addAttribute("produtos", produtoService.buscarPorFaixaPreco(precoMin, precoMax));
            model.addAttribute("precoMin", precoMin);
            model.addAttribute("precoMax", precoMax);
        } else {
            model.addAttribute("produtos", produtoService.listarAtivos());
        }
        
        model.addAttribute("categorias", produtoService.listarCategorias());
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", produtoService.listarCategorias());
        return "produtos/form";
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Produto> produto = produtoService.buscarPorId(id);
        if (produto.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado");
            return "redirect:/produtos";
        }
        model.addAttribute("produto", produto.get());
        return "produtos/visualizar";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Produto> produto = produtoService.buscarPorId(id);
        if (produto.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado");
            return "redirect:/produtos";
        }
        model.addAttribute("produto", produto.get());
        model.addAttribute("categorias", produtoService.listarCategorias());
        return "produtos/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Produto produto, BindingResult result, 
                        Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", produtoService.listarCategorias());
            return "produtos/form";
        }

        try {
            if (produto.getProdutoId() == null) {
                produtoService.salvar(produto);
                redirectAttributes.addFlashAttribute("sucesso", "Produto criado com sucesso!");
            } else {
                produtoService.atualizar(produto);
                redirectAttributes.addFlashAttribute("sucesso", "Produto atualizado com sucesso!");
            }
            return "redirect:/produtos";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("categorias", produtoService.listarCategorias());
            return "produtos/form";
        }
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.desativar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto desativado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.ativar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto ativado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/produtos";
    }

    @GetMapping("/buscar")
    @ResponseBody
    public Object buscarPorTermo(@RequestParam String termo) {
        return produtoService.buscarPorTermo(termo);
    }
}

