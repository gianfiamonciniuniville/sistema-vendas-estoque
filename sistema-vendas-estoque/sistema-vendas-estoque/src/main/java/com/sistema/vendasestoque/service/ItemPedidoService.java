package com.sistema.vendasestoque.service;

import com.sistema.vendasestoque.entity.ItemPedido;
import com.sistema.vendasestoque.entity.Pedido;
import com.sistema.vendasestoque.entity.Produto;
import com.sistema.vendasestoque.repository.ItemPedidoRepository;
import com.sistema.vendasestoque.repository.PedidoRepository;
import com.sistema.vendasestoque.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemPedidoService {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<ItemPedido> listarTodos() {
        return itemPedidoRepository.findAll();
    }

    public Optional<ItemPedido> buscarPorId(Long id) {
        return itemPedidoRepository.findById(id);
    }

    public List<ItemPedido> buscarPorPedido(Long pedidoId) {
        return itemPedidoRepository.findByPedidoOrderByDataCriacao(pedidoId);
    }

    public List<ItemPedido> buscarPorProduto(Long produtoId) {
        return itemPedidoRepository.findByProdutoProdutoId(produtoId);
    }

    public List<ItemPedido> buscarPorProdutoEPeriodo(Long produtoId, LocalDate dataInicio, LocalDate dataFim) {
        return itemPedidoRepository.findByProdutoAndPeriodo(produtoId, dataInicio, dataFim);
    }

    public ItemPedido adicionarItemAoPedido(Long pedidoId, Long produtoId, Integer quantidade) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        Optional<Produto> produtoOpt = produtoRepository.findById(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }

        Pedido pedido = pedidoOpt.get();
        Produto produto = produtoOpt.get();

        if (!produto.getAtivo()) {
            throw new IllegalArgumentException("Produto não está ativo");
        }

        // Verificar se já existe item para este produto no pedido
        List<ItemPedido> itensExistentes = itemPedidoRepository.findByPedidoPedidoId(pedidoId);
        Optional<ItemPedido> itemExistente = itensExistentes.stream()
                .filter(item -> item.getProduto().getProdutoId().equals(produtoId))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Atualizar quantidade do item existente
            ItemPedido item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
            ItemPedido itemSalvo = itemPedidoRepository.save(item);
            atualizarTotalPedido(pedidoId);
            return itemSalvo;
        } else {
            // Criar novo item
            ItemPedido novoItem = new ItemPedido(pedido, produto, quantidade, produto.getPreco());
            ItemPedido itemSalvo = itemPedidoRepository.save(novoItem);
            atualizarTotalPedido(pedidoId);
            return itemSalvo;
        }
    }

    public ItemPedido salvar(ItemPedido itemPedido) {
        validarItemPedido(itemPedido);
        ItemPedido itemSalvo = itemPedidoRepository.save(itemPedido);
        atualizarTotalPedido(itemPedido.getPedido().getPedidoId());
        return itemSalvo;
    }

    public ItemPedido atualizar(ItemPedido itemPedido) {
        if (itemPedido.getId() == null) {
            throw new IllegalArgumentException("ID do item é obrigatório para atualização");
        }
        
        Optional<ItemPedido> itemExistente = itemPedidoRepository.findById(itemPedido.getId());
        if (itemExistente.isEmpty()) {
            throw new IllegalArgumentException("Item do pedido não encontrado");
        }

        validarItemPedido(itemPedido);
        ItemPedido itemSalvo = itemPedidoRepository.save(itemPedido);
        atualizarTotalPedido(itemPedido.getPedido().getPedidoId());
        return itemSalvo;
    }

    public void excluir(Long id) {
        Optional<ItemPedido> item = itemPedidoRepository.findById(id);
        if (item.isEmpty()) {
            throw new IllegalArgumentException("Item do pedido não encontrado");
        }

        Long pedidoId = item.get().getPedido().getPedidoId();
        itemPedidoRepository.deleteById(id);
        atualizarTotalPedido(pedidoId);
    }

    public void excluirTodosDosPedido(Long pedidoId) {
        itemPedidoRepository.deleteByPedidoPedidoId(pedidoId);
        atualizarTotalPedido(pedidoId);
    }

    public ItemPedido atualizarQuantidade(Long itemId, Integer novaQuantidade) {
        Optional<ItemPedido> itemOpt = itemPedidoRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item do pedido não encontrado");
        }

        ItemPedido item = itemOpt.get();
        item.setQuantidade(novaQuantidade);
        ItemPedido itemSalvo = itemPedidoRepository.save(item);
        atualizarTotalPedido(item.getPedido().getPedidoId());
        return itemSalvo;
    }

    public Integer calcularQuantidadeVendidaPorProdutoEPeriodo(Long produtoId, LocalDate dataInicio, LocalDate dataFim) {
        Integer quantidade = itemPedidoRepository.calcularQuantidadeVendidaPorProdutoEPeriodo(produtoId, dataInicio, dataFim);
        return quantidade != null ? quantidade : 0;
    }

    public List<Object[]> obterProdutosMaisVendidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return itemPedidoRepository.findProdutosMaisVendidosPorPeriodo(dataInicio, dataFim);
    }

    public long contarItensPorPedido(Long pedidoId) {
        return itemPedidoRepository.countItensPorPedido(pedidoId);
    }

    public BigDecimal calcularTotalPorPedido(Long pedidoId) {
        BigDecimal total = itemPedidoRepository.calcularTotalPorPedido(pedidoId);
        return total != null ? total : BigDecimal.ZERO;
    }

    private void atualizarTotalPedido(Long pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            BigDecimal novoTotal = calcularTotalPorPedido(pedidoId);
            pedido.setValorTotal(novoTotal);
            pedidoRepository.save(pedido);
        }
    }

    private void validarItemPedido(ItemPedido itemPedido) {
        if (itemPedido.getPedido() == null) {
            throw new IllegalArgumentException("Pedido é obrigatório");
        }
        
        if (itemPedido.getProduto() == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        
        if (itemPedido.getQuantidade() == null || itemPedido.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        if (itemPedido.getPrecoUnitario() == null || itemPedido.getPrecoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço unitário deve ser maior que zero");
        }
    }
}

