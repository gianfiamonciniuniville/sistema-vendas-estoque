package com.sistema.vendasestoque.repository;

import com.sistema.vendasestoque.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedidoPedidoId(Long pedidoId);

    List<ItemPedido> findByProdutoProdutoId(Long produtoId);

    @Query("SELECT ip FROM ItemPedido ip WHERE ip.pedido.pedidoId = :pedidoId ORDER BY ip.dataCriacao")
    List<ItemPedido> findByPedidoOrderByDataCriacao(@Param("pedidoId") Long pedidoId);

    @Query("SELECT ip FROM ItemPedido ip WHERE ip.produto.produtoId = :produtoId " +
           "AND ip.pedido.data BETWEEN :dataInicio AND :dataFim")
    List<ItemPedido> findByProdutoAndPeriodo(@Param("produtoId") Long produtoId,
                                             @Param("dataInicio") LocalDate dataInicio,
                                             @Param("dataFim") LocalDate dataFim);

    @Query("SELECT SUM(ip.quantidade) FROM ItemPedido ip WHERE ip.produto.produtoId = :produtoId " +
           "AND ip.pedido.data BETWEEN :dataInicio AND :dataFim " +
           "AND ip.pedido.status IN ('CONFIRMADO', 'EM_PREPARACAO', 'ENVIADO', 'ENTREGUE')")
    Integer calcularQuantidadeVendidaPorProdutoEPeriodo(@Param("produtoId") Long produtoId,
                                                        @Param("dataInicio") LocalDate dataInicio,
                                                        @Param("dataFim") LocalDate dataFim);

    @Query("SELECT ip.produto.produtoId, ip.produto.nome, SUM(ip.quantidade) as totalVendido " +
           "FROM ItemPedido ip WHERE ip.pedido.data BETWEEN :dataInicio AND :dataFim " +
           "AND ip.pedido.status IN ('CONFIRMADO', 'EM_PREPARACAO', 'ENVIADO', 'ENTREGUE') " +
           "GROUP BY ip.produto.produtoId, ip.produto.nome " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProdutosMaisVendidosPorPeriodo(@Param("dataInicio") LocalDate dataInicio,
                                                      @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COUNT(ip) FROM ItemPedido ip WHERE ip.pedido.pedidoId = :pedidoId")
    long countItensPorPedido(@Param("pedidoId") Long pedidoId);

    @Query("SELECT SUM(ip.subtotal) FROM ItemPedido ip WHERE ip.pedido.pedidoId = :pedidoId")
    java.math.BigDecimal calcularTotalPorPedido(@Param("pedidoId") Long pedidoId);

    void deleteByPedidoPedidoId(Long pedidoId);
}

