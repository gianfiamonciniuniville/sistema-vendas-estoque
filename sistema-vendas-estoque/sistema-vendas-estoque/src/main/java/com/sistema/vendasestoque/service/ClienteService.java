package com.sistema.vendasestoque.service;

import com.sistema.vendasestoque.entity.Cliente;
import com.sistema.vendasestoque.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAllOrderByNome();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorCpfCnpj(String cpfCnpj) {
        return clienteRepository.findByCpfCnpj(cpfCnpj);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Cliente> buscarPorTermo(String termo) {
        return clienteRepository.buscarPorTermo(termo);
    }

    public List<Cliente> listarClientesComMaisPedidos() {
        return clienteRepository.findClientesComMaisPedidos();
    }

    public Cliente salvar(Cliente cliente) {
        validarCliente(cliente);
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Cliente cliente) {
        if (cliente.getClienteId() == null) {
            throw new IllegalArgumentException("ID do cliente é obrigatório para atualização");
        }
        
        Optional<Cliente> clienteExistente = clienteRepository.findById(cliente.getClienteId());
        if (clienteExistente.isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }

        validarCliente(cliente);
        return clienteRepository.save(cliente);
    }

    public void excluir(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }

    public boolean existeCpfCnpj(String cpfCnpj) {
        return clienteRepository.existsByCpfCnpj(cpfCnpj);
    }

    public boolean existeEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public boolean existeCpfCnpjParaOutroCliente(String cpfCnpj, Long clienteId) {
        Optional<Cliente> cliente = clienteRepository.findByCpfCnpj(cpfCnpj);
        return cliente.isPresent() && !cliente.get().getClienteId().equals(clienteId);
    }

    public boolean existeEmailParaOutroCliente(String email, Long clienteId) {
        Optional<Cliente> cliente = clienteRepository.findByEmail(email);
        return cliente.isPresent() && !cliente.get().getClienteId().equals(clienteId);
    }

    public long contarTotalClientes() {
        return clienteRepository.countTotalClientes();
    }

    private void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (cliente.getCpfCnpj() == null || cliente.getCpfCnpj().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF/CNPJ é obrigatório");
        }

        // Validar formato básico do CPF/CNPJ
        String cpfCnpj = cliente.getCpfCnpj().replaceAll("[^0-9]", "");
        if (cpfCnpj.length() != 11 && cpfCnpj.length() != 14) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos ou CNPJ deve ter 14 dígitos");
        }

        // Verificar se CPF/CNPJ já existe para outro cliente
        if (cliente.getClienteId() == null) {
            // Novo cliente
            if (existeCpfCnpj(cliente.getCpfCnpj())) {
                throw new IllegalArgumentException("CPF/CNPJ já está em uso");
            }
        } else {
            // Cliente existente
            if (existeCpfCnpjParaOutroCliente(cliente.getCpfCnpj(), cliente.getClienteId())) {
                throw new IllegalArgumentException("CPF/CNPJ já está em uso por outro cliente");
            }
        }

        // Verificar email se fornecido
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            if (cliente.getClienteId() == null) {
                // Novo cliente
                if (existeEmail(cliente.getEmail())) {
                    throw new IllegalArgumentException("Email já está em uso");
                }
            } else {
                // Cliente existente
                if (existeEmailParaOutroCliente(cliente.getEmail(), cliente.getClienteId())) {
                    throw new IllegalArgumentException("Email já está em uso por outro cliente");
                }
            }
        }
    }
}

