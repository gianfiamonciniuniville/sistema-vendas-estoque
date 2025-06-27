package com.sistema.vendasestoque.enums;

public enum PerfilUsuario {
    ADMIN("Administrador"),
    VENDEDOR("Vendedor"),
    GERENTE("Gerente");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

