package com.sistema.vendasestoque.service;

import com.sistema.vendasestoque.entity.Usuario;
import com.sistema.vendasestoque.enums.PerfilUsuario;
import com.sistema.vendasestoque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> buscarPorPerfil(PerfilUsuario perfil) {
        return usuarioRepository.findByPerfil(perfil);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Usuario> buscarPorNomeOuEmail(String termo) {
        return usuarioRepository.buscarPorNomeOuEmail(termo);
    }

    public Usuario salvar(Usuario usuario) {
        validarUsuario(usuario);
        // Criptografar senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Usuario usuario) {
        if (usuario.getUsuarioId() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório para atualização");
        }
        
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getUsuarioId());
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        validarUsuario(usuario);
        
        // Se a senha foi alterada, criptografar
        Usuario usuarioAtual = usuarioExistente.get();
        if (!usuario.getSenha().equals(usuarioAtual.getSenha())) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        
        return usuarioRepository.save(usuario);
    }

    public void excluir(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public void desativar(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        
        Usuario usuarioEntity = usuario.get();
        usuarioEntity.setAtivo(false);
        usuarioRepository.save(usuarioEntity);
    }

    public void ativar(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        
        Usuario usuarioEntity = usuario.get();
        usuarioEntity.setAtivo(true);
        usuarioRepository.save(usuarioEntity);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existeEmailParaOutroUsuario(String email, Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.isPresent() && !usuario.get().getUsuarioId().equals(usuarioId);
    }

    public long contarUsuariosAtivos() {
        return usuarioRepository.countUsuariosAtivos();
    }

    public long contarPorPerfil(PerfilUsuario perfil) {
        return usuarioRepository.countByPerfilAndAtivoTrue(perfil);
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        
        if (usuario.getPerfil() == null) {
            throw new IllegalArgumentException("Perfil é obrigatório");
        }

        // Verificar se email já existe para outro usuário
        if (usuario.getUsuarioId() == null) {
            // Novo usuário
            if (existeEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("Email já está em uso");
            }
        } else {
            // Usuário existente
            if (existeEmailParaOutroUsuario(usuario.getEmail(), usuario.getUsuarioId())) {
                throw new IllegalArgumentException("Email já está em uso por outro usuário");
            }
        }
    }
}

