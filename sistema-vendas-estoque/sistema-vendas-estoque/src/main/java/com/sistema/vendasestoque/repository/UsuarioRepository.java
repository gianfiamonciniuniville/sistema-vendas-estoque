package com.sistema.vendasestoque.repository;

import com.sistema.vendasestoque.entity.Usuario;
import com.sistema.vendasestoque.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    List<Usuario> findByAtivoTrue();

    List<Usuario> findByPerfil(PerfilUsuario perfil);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND u.perfil = :perfil")
    List<Usuario> findUsuariosAtivosPorPerfil(@Param("perfil") PerfilUsuario perfil);

    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND " +
           "(LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Usuario> buscarPorNomeOuEmail(@Param("termo") String termo);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.ativo = true")
    long countUsuariosAtivos();

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.perfil = :perfil AND u.ativo = true")
    long countByPerfilAndAtivoTrue(@Param("perfil") PerfilUsuario perfil);
}

