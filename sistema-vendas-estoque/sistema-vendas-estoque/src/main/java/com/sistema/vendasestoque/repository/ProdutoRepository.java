package com.sistema.vendasestoque.repository;

import com.sistema.vendasestoque.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigoBarras(String codigoBarras);

    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "p.codigoBarras LIKE CONCAT('%', :termo, '%'))")
    List<Produto> buscarPorTermo(@Param("termo") String termo);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.categoria = :categoria")
    List<Produto> findProdutosAtivosPorCategoria(@Param("categoria") String categoria);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.preco BETWEEN :precoMin AND :precoMax")
    List<Produto> findByPrecoRange(@Param("precoMin") BigDecimal precoMin, 
                                   @Param("precoMax") BigDecimal precoMax);

    @Query("SELECT DISTINCT p.categoria FROM Produto p WHERE p.ativo = true ORDER BY p.categoria")
    List<String> findAllCategorias();

    boolean existsByCodigoBarras(String codigoBarras);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.ativo = true")
    long countProdutosAtivos();

    @Query("SELECT p FROM Produto p WHERE p.ativo = true ORDER BY p.nome ASC")
    List<Produto> findAllAtivoOrderByNome();

    @Query("SELECT p FROM Produto p JOIN p.estoque e WHERE e.quantidadeAtual <= e.quantidadeMinima")
    List<Produto> findProdutosComEstoqueBaixo();
}

