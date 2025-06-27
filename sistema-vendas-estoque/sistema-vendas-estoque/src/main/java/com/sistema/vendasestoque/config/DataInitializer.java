package com.sistema.vendasestoque.config;

import com.sistema.vendasestoque.entity.*;
import com.sistema.vendasestoque.enums.PerfilUsuario;
import com.sistema.vendasestoque.enums.StatusPedido;
import com.sistema.vendasestoque.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ItemPedidoService itemPedidoService;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existem dados
        if (usuarioService.contarUsuariosAtivos() > 0) {
            return; // Dados já foram inicializados
        }

        // Criar usuários
        Usuario admin = new Usuario("Administrador", "admin@sistema.com", "123456", PerfilUsuario.ADMIN);
        usuarioService.salvar(admin);

        Usuario gerente = new Usuario("João Silva", "gerente@sistema.com", "123456", PerfilUsuario.GERENTE);
        usuarioService.salvar(gerente);

        Usuario vendedor = new Usuario("Maria Santos", "vendedor@sistema.com", "123456", PerfilUsuario.VENDEDOR);
        usuarioService.salvar(vendedor);

        // Criar clientes
        Cliente cliente1 = new Cliente("José da Silva", "jose@email.com", "(11) 99999-9999", "123.456.789-00", "Rua A, 123 - São Paulo/SP");
        clienteService.salvar(cliente1);

        Cliente cliente2 = new Cliente("Maria Oliveira", "maria@email.com", "(11) 88888-8888", "987.654.321-00", "Rua B, 456 - São Paulo/SP");
        clienteService.salvar(cliente2);

        Cliente cliente3 = new Cliente("Empresa ABC Ltda", "contato@empresaabc.com", "(11) 77777-7777", "12.345.678/0001-90", "Av. Principal, 789 - São Paulo/SP");
        clienteService.salvar(cliente3);

        // Criar produtos
        Produto produto1 = new Produto("Notebook Dell Inspiron", "Notebook Dell Inspiron 15 3000, Intel Core i5, 8GB RAM, 256GB SSD", 
                                      new BigDecimal("2500.00"), "7891234567890", "Informática");
        produtoService.salvar(produto1);

        Produto produto2 = new Produto("Mouse Logitech MX Master", "Mouse sem fio Logitech MX Master 3, sensor Darkfield, 7 botões", 
                                      new BigDecimal("350.00"), "7891234567891", "Informática");
        produtoService.salvar(produto2);

        Produto produto3 = new Produto("Teclado Mecânico Corsair", "Teclado mecânico Corsair K70, switches Cherry MX Red, RGB", 
                                      new BigDecimal("450.00"), "7891234567892", "Informática");
        produtoService.salvar(produto3);

        Produto produto4 = new Produto("Monitor LG 24''", "Monitor LG 24'' Full HD, IPS, 75Hz, FreeSync", 
                                      new BigDecimal("800.00"), "7891234567893", "Informática");
        produtoService.salvar(produto4);

        Produto produto5 = new Produto("Smartphone Samsung Galaxy", "Samsung Galaxy A54 5G, 128GB, 6GB RAM, Câmera 50MP", 
                                      new BigDecimal("1200.00"), "7891234567894", "Celulares");
        produtoService.salvar(produto5);

        Produto produto6 = new Produto("Fone de Ouvido Sony", "Fone de ouvido Sony WH-1000XM4, cancelamento de ruído, Bluetooth", 
                                      new BigDecimal("1500.00"), "7891234567895", "Áudio");
        produtoService.salvar(produto6);

        Produto produto7 = new Produto("Impressora HP LaserJet", "Impressora HP LaserJet Pro M404dn, monocromática, duplex automático", 
                                      new BigDecimal("900.00"), "7891234567896", "Impressoras");
        produtoService.salvar(produto7);

        Produto produto8 = new Produto("HD Externo Seagate 1TB", "HD Externo Seagate Expansion 1TB, USB 3.0, portátil", 
                                      new BigDecimal("250.00"), "7891234567897", "Armazenamento");
        produtoService.salvar(produto8);

        // Atualizar estoque dos produtos
        estoqueService.adicionarEstoque(produto1.getProdutoId(), 15);
        estoqueService.atualizarQuantidadeMinima(produto1.getProdutoId(), 5);

        estoqueService.adicionarEstoque(produto2.getProdutoId(), 50);
        estoqueService.atualizarQuantidadeMinima(produto2.getProdutoId(), 10);

        estoqueService.adicionarEstoque(produto3.getProdutoId(), 30);
        estoqueService.atualizarQuantidadeMinima(produto3.getProdutoId(), 8);

        estoqueService.adicionarEstoque(produto4.getProdutoId(), 25);
        estoqueService.atualizarQuantidadeMinima(produto4.getProdutoId(), 5);

        estoqueService.adicionarEstoque(produto5.getProdutoId(), 40);
        estoqueService.atualizarQuantidadeMinima(produto5.getProdutoId(), 10);

        estoqueService.adicionarEstoque(produto6.getProdutoId(), 20);
        estoqueService.atualizarQuantidadeMinima(produto6.getProdutoId(), 5);

        estoqueService.adicionarEstoque(produto7.getProdutoId(), 12);
        estoqueService.atualizarQuantidadeMinima(produto7.getProdutoId(), 3);

        estoqueService.adicionarEstoque(produto8.getProdutoId(), 35);
        estoqueService.atualizarQuantidadeMinima(produto8.getProdutoId(), 8);

        // Criar alguns pedidos de exemplo
        Pedido pedido1 = pedidoService.criarPedido(cliente1.getClienteId(), vendedor.getUsuarioId());
        pedido1.setData(LocalDate.now().minusDays(2));
        pedido1.setObservacoes("Pedido urgente - entregar até sexta-feira");
        pedidoService.atualizar(pedido1);

        // Adicionar itens ao pedido 1
        itemPedidoService.adicionarItemAoPedido(pedido1.getPedidoId(), produto1.getProdutoId(), 1);
        itemPedidoService.adicionarItemAoPedido(pedido1.getPedidoId(), produto2.getProdutoId(), 2);
        itemPedidoService.adicionarItemAoPedido(pedido1.getPedidoId(), produto4.getProdutoId(), 1);

        // Confirmar o pedido 1
        pedidoService.confirmarPedido(pedido1.getPedidoId());
        pedidoService.atualizarStatus(pedido1.getPedidoId(), StatusPedido.ENTREGUE);

        // Criar pedido 2
        Pedido pedido2 = pedidoService.criarPedido(cliente2.getClienteId(), vendedor.getUsuarioId());
        pedido2.setData(LocalDate.now().minusDays(1));
        pedido2.setObservacoes("Cliente preferencial - desconto aplicado");
        pedidoService.atualizar(pedido2);

        // Adicionar itens ao pedido 2
        itemPedidoService.adicionarItemAoPedido(pedido2.getPedidoId(), produto5.getProdutoId(), 2);
        itemPedidoService.adicionarItemAoPedido(pedido2.getPedidoId(), produto6.getProdutoId(), 1);

        // Confirmar o pedido 2
        pedidoService.confirmarPedido(pedido2.getPedidoId());
        pedidoService.atualizarStatus(pedido2.getPedidoId(), StatusPedido.EM_PREPARACAO);

        // Criar pedido 3 (pendente)
        Pedido pedido3 = pedidoService.criarPedido(cliente3.getClienteId(), gerente.getUsuarioId());
        pedido3.setData(LocalDate.now());
        pedido3.setObservacoes("Pedido corporativo - aguardando aprovação");
        pedidoService.atualizar(pedido3);

        // Adicionar itens ao pedido 3
        itemPedidoService.adicionarItemAoPedido(pedido3.getPedidoId(), produto1.getProdutoId(), 5);
        itemPedidoService.adicionarItemAoPedido(pedido3.getPedidoId(), produto3.getProdutoId(), 5);
        itemPedidoService.adicionarItemAoPedido(pedido3.getPedidoId(), produto4.getProdutoId(), 5);
        itemPedidoService.adicionarItemAoPedido(pedido3.getPedidoId(), produto7.getProdutoId(), 2);

        // Criar pedido 4 (pendente)
        Pedido pedido4 = pedidoService.criarPedido(cliente1.getClienteId(), vendedor.getUsuarioId());
        pedido4.setData(LocalDate.now());
        pedido4.setObservacoes("Segundo pedido do cliente");
        pedidoService.atualizar(pedido4);

        // Adicionar itens ao pedido 4
        itemPedidoService.adicionarItemAoPedido(pedido4.getPedidoId(), produto8.getProdutoId(), 3);
        itemPedidoService.adicionarItemAoPedido(pedido4.getPedidoId(), produto2.getProdutoId(), 1);

        System.out.println("=== Dados de inicialização criados com sucesso! ===");
        System.out.println("Usuários criados: 3 (admin, gerente, vendedor)");
        System.out.println("Clientes criados: 3");
        System.out.println("Produtos criados: 8");
        System.out.println("Pedidos criados: 4");
        System.out.println("=== Sistema pronto para uso! ===");
    }
}

