package com.sistema.vendasestoque.service;

import com.sistema.vendasestoque.entity.Estoque;
import com.sistema.vendasestoque.entity.Produto;
import com.sistema.vendasestoque.repository.EstoqueRepository;
import com.sistema.vendasestoque.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Estoque> listarTodos() {
        return estoqueRepository.findAllByProdutoAtivoOrderByNome();
    }

    public Optional<Estoque> buscarPorId(Long id) {
        return estoqueRepository.findById(id);
    }

    public Optional<Estoque> buscarPorProdutoId(Long produtoId) {
        return estoqueRepository.findByProdutoProdutoId(produtoId);
    }

    public List<Estoque> listarEstoqueBaixo() {
        return estoqueRepository.findEstoqueBaixo();
    }

    public List<Estoque> listarEstoqueZerado() {
        return estoqueRepository.findEstoqueZerado();
    }

    public List<Estoque> buscarPorCategoria(String categoria) {
        return estoqueRepository.findByProdutoCategoria(categoria);
    }

    public List<Estoque> buscarPorProduto(String termo) {
        return estoqueRepository.buscarPorProduto(termo);
    }

    public Estoque salvar(Estoque estoque) {
        validarEstoque(estoque);
        return estoqueRepository.save(estoque);
    }

    public Estoque atualizar(Estoque estoque) {
        if (estoque.getId() == null) {
            throw new IllegalArgumentException("ID do estoque é obrigatório para atualização");
        }
        
        Optional<Estoque> estoqueExistente = estoqueRepository.findById(estoque.getId());
        if (estoqueExistente.isEmpty()) {
            throw new IllegalArgumentException("Estoque não encontrado");
        }

        validarEstoque(estoque);
        return estoqueRepository.save(estoque);
    }

    public Estoque criarEstoqueParaProduto(Long produtoId, Integer quantidadeInicial, Integer quantidadeMinima) {
        Optional<Produto> produto = produtoRepository.findById(produtoId);
        if (produto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }

        if (estoqueRepository.existsByProdutoProdutoId(produtoId)) {
            throw new IllegalArgumentException("Produto já possui estoque cadastrado");
        }

        Estoque estoque = new Estoque(produto.get(), quantidadeInicial, quantidadeMinima);
        return estoqueRepository.save(estoque);
    }

    public boolean adicionarEstoque(Long produtoId, Integer quantidade) {
        Optional<Estoque> estoqueOpt = estoqueRepository.findByProdutoProdutoId(produtoId);
        if (estoqueOpt.isEmpty()) {
            throw new IllegalArgumentException("Estoque não encontrado para o produto");
        }

        Estoque estoque = estoqueOpt.get();
        estoque.adicionarEstoque(quantidade);
        estoqueRepository.save(estoque);
        return true;
    }

    public boolean removerEstoque(Long produtoId, Integer quantidade) {
        Optional<Estoque> estoqueOpt = estoqueRepository.findByProdutoProdutoId(produtoId);
        if (estoqueOpt.isEmpty()) {
            throw new IllegalArgumentException("Estoque não encontrado para o produto");
        }

        Estoque estoque = estoqueOpt.get();
        boolean sucesso = estoque.removerEstoque(quantidade);
        if (sucesso) {
            estoqueRepository.save(estoque);
        }
        return sucesso;
    }

    public boolean verificarDisponibilidade(Long produtoId, Integer quantidade) {
        Optional<Estoque> estoque = estoqueRepository.findByProdutoProdutoId(produtoId);
        return estoque.isPresent() && estoque.get().temEstoqueSuficiente(quantidade);
    }

    public void atualizarQuantidadeMinima(Long produtoId, Integer novaQuantidadeMinima) {
        Optional<Estoque> estoqueOpt = estoqueRepository.findByProdutoProdutoId(produtoId);
        if (estoqueOpt.isEmpty()) {
            throw new IllegalArgumentException("Estoque não encontrado para o produto");
        }

        Estoque estoque = estoqueOpt.get();
        estoque.setQuantidadeMinima(novaQuantidadeMinima);
        estoqueRepository.save(estoque);
    }

    public long contarEstoqueBaixo() {
        return estoqueRepository.countEstoqueBaixo();
    }

    public long contarEstoqueZerado() {
        return estoqueRepository.countEstoqueZerado();
    }

    public Long calcularTotalItensEmEstoque() {
        Long total = estoqueRepository.calcularTotalItensEmEstoque();
        return total != null ? total : 0L;
    }

    private void validarEstoque(Estoque estoque) {
        if (estoque.getProduto() == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        
        if (estoque.getQuantidadeAtual() == null || estoque.getQuantidadeAtual() < 0) {
            throw new IllegalArgumentException("Quantidade atual não pode ser negativa");
        }
        
        if (estoque.getQuantidadeMinima() == null || estoque.getQuantidadeMinima() < 0) {
            throw new IllegalArgumentException("Quantidade mínima não pode ser negativa");
        }
    }
}

