package com.sistema.vendasestoque.repository;

import com.sistema.vendasestoque.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "c.cpfCnpj LIKE CONCAT('%', :termo, '%') OR " +
           "c.telefone LIKE CONCAT('%', :termo, '%')")
    List<Cliente> buscarPorTermo(@Param("termo") String termo);

    @Query("SELECT c FROM Cliente c ORDER BY c.nome ASC")
    List<Cliente> findAllOrderByNome();

    boolean existsByCpfCnpj(String cpfCnpj);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(c) FROM Cliente c")
    long countTotalClientes();

    @Query("SELECT c FROM Cliente c WHERE c.clienteId IN " +
           "(SELECT p.cliente.clienteId FROM Pedido p GROUP BY p.cliente.clienteId " +
           "ORDER BY COUNT(p) DESC)")
    List<Cliente> findClientesComMaisPedidos();
}

