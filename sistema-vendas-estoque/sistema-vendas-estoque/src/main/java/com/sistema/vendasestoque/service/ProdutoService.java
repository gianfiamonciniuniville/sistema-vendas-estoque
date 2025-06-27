package com.sistema.vendasestoque.service;

import com.sistema.vendasestoque.entity.Produto;
import com.sistema.vendasestoque.entity.Estoque;
import com.sistema.vendasestoque.repository.ProdutoRepository;
import com.sistema.vendasestoque.repository.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> listarAtivos() {
        return produtoRepository.findAllAtivoOrderByNome();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return produtoRepository.findByCodigoBarras(codigoBarras);
    }

    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findProdutosAtivosPorCategoria(categoria);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Produto> buscarPorTermo(String termo) {
        return produtoRepository.buscarPorTermo(termo);
    }

    public List<Produto> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {
        return produtoRepository.findByPrecoRange(precoMin, precoMax);
    }

    public List<String> listarCategorias() {
        return produtoRepository.findAllCategorias();
    }

    public List<Produto> listarProdutosComEstoqueBaixo() {
        return produtoRepository.findProdutosComEstoqueBaixo();
    }

    public Produto salvar(Produto produto) {
        validarProduto(produto);
        Produto produtoSalvo = produtoRepository.save(produto);
        
        // Criar estoque inicial se não existir
        if (!estoqueRepository.existsByProdutoProdutoId(produtoSalvo.getProdutoId())) {
            Estoque estoque = new Estoque(produtoSalvo, 0, 5); // Estoque inicial 0, mínimo 5
            estoqueRepository.save(estoque);
        }
        
        return produtoSalvo;
    }

    public Produto atualizar(Produto produto) {
        if (produto.getProdutoId() == null) {
            throw new IllegalArgumentException("ID do produto é obrigatório para atualização");
        }
        
        Optional<Produto> produtoExistente = produtoRepository.findById(produto.getProdutoId());
        if (produtoExistente.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }

        validarProduto(produto);
        return produtoRepository.save(produto);
    }

    public void excluir(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        produtoRepository.deleteById(id);
    }

    public void desativar(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produtoEntity = produto.get();
        produtoEntity.setAtivo(false);
        produtoRepository.save(produtoEntity);
    }

    public void ativar(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produtoEntity = produto.get();
        produtoEntity.setAtivo(true);
        produtoRepository.save(produtoEntity);
    }

    public boolean existeCodigoBarras(String codigoBarras) {
        return produtoRepository.existsByCodigoBarras(codigoBarras);
    }

    public boolean existeCodigoBarrasParaOutroProduto(String codigoBarras, Long produtoId) {
        Optional<Produto> produto = produtoRepository.findByCodigoBarras(codigoBarras);
        return produto.isPresent() && !produto.get().getProdutoId().equals(produtoId);
    }

    public long contarProdutosAtivos() {
        return produtoRepository.countProdutosAtivos();
    }

    private void validarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        
        if (produto.getCategoria() == null || produto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        // Verificar código de barras se fornecido
        if (produto.getCodigoBarras() != null && !produto.getCodigoBarras().trim().isEmpty()) {
            if (produto.getProdutoId() == null) {
                // Novo produto
                if (existeCodigoBarras(produto.getCodigoBarras())) {
                    throw new IllegalArgumentException("Código de barras já está em uso");
                }
            } else {
                // Produto existente
                if (existeCodigoBarrasParaOutroProduto(produto.getCodigoBarras(), produto.getProdutoId())) {
                    throw new IllegalArgumentException("Código de barras já está em uso por outro produto");
                }
            }
        }
    }
}

