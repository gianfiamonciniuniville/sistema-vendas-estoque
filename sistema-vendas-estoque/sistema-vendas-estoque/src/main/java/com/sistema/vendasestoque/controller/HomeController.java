package com.sistema.vendasestoque.controller;

import com.sistema.vendasestoque.service.*;
import com.sistema.vendasestoque.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private EstoqueService estoqueService;

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estatísticas gerais
        long totalUsuarios = usuarioService.contarUsuariosAtivos();
        long totalClientes = clienteService.contarTotalClientes();
        long totalProdutos = produtoService.contarProdutosAtivos();
        
        // Estatísticas de pedidos
        long pedidosPendentes = pedidoService.contarPorStatus(StatusPedido.PENDENTE);
        long pedidosConfirmados = pedidoService.contarPorStatus(StatusPedido.CONFIRMADO);
        long pedidosEntregues = pedidoService.contarPorStatus(StatusPedido.ENTREGUE);
        
        // Estatísticas de estoque
        long estoqueBaixo = estoqueService.contarEstoqueBaixo();
        long estoqueZerado = estoqueService.contarEstoqueZerado();
        Long totalItensEstoque = estoqueService.calcularTotalItensEmEstoque();
        
        // Vendas do mês atual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate fimMes = LocalDate.now();
        BigDecimal vendasMes = pedidoService.calcularVendasPorPeriodo(inicioMes, fimMes);
        BigDecimal ticketMedio = pedidoService.calcularTicketMedioPorPeriodo(inicioMes, fimMes);
        
        // Vendas de hoje
        long pedidosHoje = pedidoService.contarPedidosPorData(LocalDate.now());
        BigDecimal vendasHoje = pedidoService.calcularVendasPorPeriodo(LocalDate.now(), LocalDate.now());

        // Adicionar ao modelo
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("totalProdutos", totalProdutos);
        model.addAttribute("pedidosPendentes", pedidosPendentes);
        model.addAttribute("pedidosConfirmados", pedidosConfirmados);
        model.addAttribute("pedidosEntregues", pedidosEntregues);
        model.addAttribute("estoqueBaixo", estoqueBaixo);
        model.addAttribute("estoqueZerado", estoqueZerado);
        model.addAttribute("totalItensEstoque", totalItensEstoque);
        model.addAttribute("vendasMes", vendasMes);
        model.addAttribute("ticketMedio", ticketMedio);
        model.addAttribute("pedidosHoje", pedidosHoje);
        model.addAttribute("vendasHoje", vendasHoje);
        
        return "dashboard-simple";
    }
}

