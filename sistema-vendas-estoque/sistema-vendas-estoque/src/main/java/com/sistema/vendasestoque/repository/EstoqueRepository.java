package com.sistema.vendasestoque.repository;

import com.sistema.vendasestoque.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProdutoProdutoId(Long produtoId);

    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual <= e.quantidadeMinima")
    List<Estoque> findEstoqueBaixo();

    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual = 0")
    List<Estoque> findEstoqueZerado();

    @Query("SELECT e FROM Estoque e WHERE e.produto.ativo = true ORDER BY e.produto.nome")
    List<Estoque> findAllByProdutoAtivoOrderByNome();

    @Query("SELECT e FROM Estoque e WHERE e.produto.categoria = :categoria AND e.produto.ativo = true")
    List<Estoque> findByProdutoCategoria(@Param("categoria") String categoria);

    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual >= :quantidade AND e.produto.ativo = true")
    List<Estoque> findByQuantidadeAtualGreaterThanEqual(@Param("quantidade") Integer quantidade);

    @Query("SELECT COUNT(e) FROM Estoque e WHERE e.quantidadeAtual <= e.quantidadeMinima")
    long countEstoqueBaixo();

    @Query("SELECT COUNT(e) FROM Estoque e WHERE e.quantidadeAtual = 0")
    long countEstoqueZerado();

    @Query("SELECT SUM(e.quantidadeAtual) FROM Estoque e WHERE e.produto.ativo = true")
    Long calcularTotalItensEmEstoque();

    @Query("SELECT e FROM Estoque e WHERE e.produto.ativo = true AND " +
           "(LOWER(e.produto.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "e.produto.codigoBarras LIKE CONCAT('%', :termo, '%'))")
    List<Estoque> buscarPorProduto(@Param("termo") String termo);

    boolean existsByProdutoProdutoId(Long produtoId);
}

