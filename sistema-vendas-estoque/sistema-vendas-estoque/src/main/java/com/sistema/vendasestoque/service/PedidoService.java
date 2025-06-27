package com.sistema.vendasestoque.service;

import com.sistema.vendasestoque.entity.Pedido;
import com.sistema.vendasestoque.entity.ItemPedido;
import com.sistema.vendasestoque.entity.Cliente;
import com.sistema.vendasestoque.entity.Usuario;
import com.sistema.vendasestoque.enums.StatusPedido;
import com.sistema.vendasestoque.repository.PedidoRepository;
import com.sistema.vendasestoque.repository.ItemPedidoRepository;
import com.sistema.vendasestoque.repository.ClienteRepository;
import com.sistema.vendasestoque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstoqueService estoqueService;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllOrderByDataDesc();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatusOrderByDataDesc(status);
    }

    public List<Pedido> buscarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteOrderByDataDesc(clienteId);
    }

    public List<Pedido> buscarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioUsuarioId(usuarioId);
    }

    public List<Pedido> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return pedidoRepository.findByDataBetween(dataInicio, dataFim);
    }

    public List<Pedido> buscarPorPeriodoEStatus(LocalDate dataInicio, LocalDate dataFim, StatusPedido status) {
        return pedidoRepository.findByDataBetweenAndStatus(dataInicio, dataFim, status);
    }

    public List<Pedido> buscarPedidosRecentes(int dias) {
        LocalDate dataInicio = LocalDate.now().minusDays(dias);
        return pedidoRepository.findPedidosRecentes(dataInicio);
    }

    public Pedido criarPedido(Long clienteId, Long usuarioId) {
        Optional<Cliente> cliente = clienteRepository.findById(clienteId);
        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }

        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        Pedido pedido = new Pedido(cliente.get(), usuario.get());
        return pedidoRepository.save(pedido);
    }

    public Pedido salvar(Pedido pedido) {
        validarPedido(pedido);
        return pedidoRepository.save(pedido);
    }

    public Pedido atualizar(Pedido pedido) {
        if (pedido.getPedidoId() == null) {
            throw new IllegalArgumentException("ID do pedido é obrigatório para atualização");
        }
        
        Optional<Pedido> pedidoExistente = pedidoRepository.findById(pedido.getPedidoId());
        if (pedidoExistente.isEmpty()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        validarPedido(pedido);
        return pedidoRepository.save(pedido);
    }

    public void excluir(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isEmpty()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        Pedido pedidoEntity = pedido.get();
        if (pedidoEntity.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalArgumentException("Apenas pedidos pendentes podem ser excluídos");
        }

        pedidoRepository.deleteById(id);
    }

    public Pedido confirmarPedido(Long pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new IllegalArgumentException("Apenas pedidos pendentes podem ser confirmados");
        }

        // Verificar disponibilidade de estoque para todos os itens
        List<ItemPedido> itens = itemPedidoRepository.findByPedidoPedidoId(pedidoId);
        for (ItemPedido item : itens) {
            if (!estoqueService.verificarDisponibilidade(item.getProduto().getProdutoId(), item.getQuantidade())) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + item.getProduto().getNome());
            }
        }

        // Remover do estoque
        for (ItemPedido item : itens) {
            estoqueService.removerEstoque(item.getProduto().getProdutoId(), item.getQuantidade());
        }

        pedido.setStatus(StatusPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);
    }

    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    public Pedido cancelarPedido(Long pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new IllegalArgumentException("Pedidos entregues não podem ser cancelados");
        }

        // Se o pedido estava confirmado, devolver ao estoque
        if (pedido.getStatus() == StatusPedido.CONFIRMADO || 
            pedido.getStatus() == StatusPedido.EM_PREPARACAO || 
            pedido.getStatus() == StatusPedido.ENVIADO) {
            
            List<ItemPedido> itens = itemPedidoRepository.findByPedidoPedidoId(pedidoId);
            for (ItemPedido item : itens) {
                estoqueService.adicionarEstoque(item.getProduto().getProdutoId(), item.getQuantidade());
            }
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    public BigDecimal calcularVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal vendas = pedidoRepository.calcularVendasPorPeriodo(dataInicio, dataFim);
        return vendas != null ? vendas : BigDecimal.ZERO;
    }

    public BigDecimal calcularTicketMedioPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal ticketMedio = pedidoRepository.calcularTicketMedioPorPeriodo(dataInicio, dataFim);
        return ticketMedio != null ? ticketMedio : BigDecimal.ZERO;
    }

    public long contarPedidosPorData(LocalDate data) {
        return pedidoRepository.countPedidosPorData(data);
    }

    public long contarPorStatus(StatusPedido status) {
        return pedidoRepository.countByStatus(status);
    }

    private void validarPedido(Pedido pedido) {
        if (pedido.getCliente() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }
        
        if (pedido.getUsuario() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }
        
        if (pedido.getData() == null) {
            throw new IllegalArgumentException("Data é obrigatória");
        }
        
        if (pedido.getStatus() == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }
    }
}

