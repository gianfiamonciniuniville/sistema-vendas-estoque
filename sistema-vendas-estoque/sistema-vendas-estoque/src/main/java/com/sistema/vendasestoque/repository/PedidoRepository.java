package com.sistema.vendasestoque.repository;

import com.sistema.vendasestoque.entity.Pedido;
import com.sistema.vendasestoque.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findByClienteClienteId(Long clienteId);

    List<Pedido> findByUsuarioUsuarioId(Long usuarioId);

    List<Pedido> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);

    @Query("SELECT p FROM Pedido p WHERE p.data = :data ORDER BY p.dataCriacao DESC")
    List<Pedido> findByDataOrderByDataCriacaoDesc(@Param("data") LocalDate data);

    @Query("SELECT p FROM Pedido p WHERE p.status = :status ORDER BY p.data DESC, p.dataCriacao DESC")
    List<Pedido> findByStatusOrderByDataDesc(@Param("status") StatusPedido status);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.clienteId = :clienteId ORDER BY p.data DESC")
    List<Pedido> findByClienteOrderByDataDesc(@Param("clienteId") Long clienteId);

    @Query("SELECT p FROM Pedido p WHERE p.data BETWEEN :dataInicio AND :dataFim " +
           "AND p.status = :status ORDER BY p.data DESC")
    List<Pedido> findByDataBetweenAndStatus(@Param("dataInicio") LocalDate dataInicio,
                                            @Param("dataFim") LocalDate dataFim,
                                            @Param("status") StatusPedido status);

    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.data BETWEEN :dataInicio AND :dataFim " +
           "AND p.status IN ('CONFIRMADO', 'EM_PREPARACAO', 'ENVIADO', 'ENTREGUE')")
    BigDecimal calcularVendasPorPeriodo(@Param("dataInicio") LocalDate dataInicio,
                                        @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.data = :data")
    long countPedidosPorData(@Param("data") LocalDate data);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.status = :status")
    long countByStatus(@Param("status") StatusPedido status);

    @Query("SELECT p FROM Pedido p ORDER BY p.data DESC, p.dataCriacao DESC")
    List<Pedido> findAllOrderByDataDesc();

    @Query("SELECT p FROM Pedido p WHERE p.data >= :dataInicio ORDER BY p.valorTotal DESC")
    List<Pedido> findPedidosRecentes(@Param("dataInicio") LocalDate dataInicio);

    @Query("SELECT AVG(p.valorTotal) FROM Pedido p WHERE p.data BETWEEN :dataInicio AND :dataFim " +
           "AND p.status IN ('CONFIRMADO', 'EM_PREPARACAO', 'ENVIADO', 'ENTREGUE')")
    BigDecimal calcularTicketMedioPorPeriodo(@Param("dataInicio") LocalDate dataInicio,
                                             @Param("dataFim") LocalDate dataFim);
}

