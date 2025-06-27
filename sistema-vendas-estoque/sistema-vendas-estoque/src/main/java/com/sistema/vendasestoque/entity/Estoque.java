package com.sistema.vendasestoque.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Produto é obrigatório")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    @NotNull(message = "Quantidade atual é obrigatória")
    @Min(value = 0, message = "Quantidade atual não pode ser negativa")
    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual = 0;

    @NotNull(message = "Quantidade mínima é obrigatória")
    @Min(value = 0, message = "Quantidade mínima não pode ser negativa")
    @Column(name = "quantidade_minima", nullable = false)
    private Integer quantidadeMinima = 0;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Construtores
    public Estoque() {
    }

    public Estoque(Produto produto, Integer quantidadeAtual, Integer quantidadeMinima) {
        this.produto = produto;
        this.quantidadeAtual = quantidadeAtual;
        this.quantidadeMinima = quantidadeMinima;
    }

    // Métodos de ciclo de vida
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Métodos de negócio
    public boolean isEstoqueBaixo() {
        return quantidadeAtual <= quantidadeMinima;
    }

    public boolean temEstoqueSuficiente(Integer quantidade) {
        return quantidadeAtual >= quantidade;
    }

    public void adicionarEstoque(Integer quantidade) {
        if (quantidade > 0) {
            this.quantidadeAtual += quantidade;
        }
    }

    public boolean removerEstoque(Integer quantidade) {
        if (quantidade > 0 && temEstoqueSuficiente(quantidade)) {
            this.quantidadeAtual -= quantidade;
            return true;
        }
        return false;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(Integer quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    @Override
    public String toString() {
        return "Estoque{" +
                "id=" + id +
                ", quantidadeAtual=" + quantidadeAtual +
                ", quantidadeMinima=" + quantidadeMinima +
                ", estoqueBaixo=" + isEstoqueBaixo() +
                '}';
    }
}

