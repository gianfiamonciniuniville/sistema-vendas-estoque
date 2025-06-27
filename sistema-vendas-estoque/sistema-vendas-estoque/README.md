# Sistema de Controle de Vendas e Estoque

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-blue)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.0-purple)
![H2](https://img.shields.io/badge/H2-Database-lightgrey)

## 📋 Sobre o Projeto

Sistema web completo para controle de vendas e estoque desenvolvido em Java com Spring Boot e Thymeleaf. Oferece uma interface moderna e intuitiva para gestão empresarial.

## ✨ Funcionalidades

### 🏠 Dashboard
- Indicadores de vendas em tempo real
- Alertas de estoque baixo
- Estatísticas de pedidos e produtos
- Ações rápidas para operações comuns

### 📦 Gestão de Produtos
- Cadastro completo de produtos
- Controle de categorias
- Códigos de barras
- Status ativo/inativo

### 📊 Controle de Estoque
- Monitoramento de quantidades
- Alertas automáticos de estoque baixo
- Movimentações de entrada e saída
- Relatórios de estoque

### 🛒 Gestão de Vendas
- Criação e acompanhamento de pedidos
- Controle de status (Pendente, Confirmado, Em Preparação, Entregue)
- Cálculo automático de valores
- Histórico de vendas

### 👥 Gestão de Clientes
- Cadastro de pessoas físicas e jurídicas
- Informações de contato completas
- Histórico de pedidos

### 👤 Gestão de Usuários
- Sistema de perfis (Admin, Gerente, Vendedor)
- Controle de permissões
- Gerenciamento de status

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Web** - Desenvolvimento web
- **Hibernate** - ORM
- **H2 Database** - Banco de dados em memória

### Frontend
- **Thymeleaf** - Template engine
- **Bootstrap 5.3.0** - Framework CSS
- **jQuery 3.7.0** - Biblioteca JavaScript
- **Font Awesome** - Ícones
- **CSS3/HTML5** - Tecnologias web

### Ferramentas
- **Maven** - Gerenciamento de dependências
- **Spring Boot DevTools** - Desenvolvimento ágil

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- IDE de sua preferência

## 🔧 Instalação e Execução

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd sistema-vendas-estoque
   ```

2. **Compile o projeto**
   ```bash
   mvn clean compile
   ```

3. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse o sistema**
   ```
   http://localhost:8080
   ```

## 📊 Dados de Exemplo

O sistema inclui dados iniciais para demonstração:

### Usuários
- **Admin**: admin@sistema.com (senha: 123456)
- **Gerente**: gerente@sistema.com (senha: 123456)
- **Vendedor**: vendedor@sistema.com (senha: 123456)

### Produtos
- 8 produtos de exemplo em diferentes categorias
- Estoque configurado com quantidades adequadas

### Clientes
- 3 clientes de exemplo (pessoa física e jurídica)

### Pedidos
- 4 pedidos em diferentes status para demonstração

## 🏗️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/sistema/vendasestoque/
│   │   ├── config/          # Configurações
│   │   ├── controller/      # Controladores REST
│   │   ├── entity/          # Entidades JPA
│   │   ├── enums/           # Enumerações
│   │   ├── repository/      # Repositórios
│   │   ├── service/         # Serviços de negócio
│   │   └── VendasEstoqueApplication.java
│   └── resources/
│       ├── static/          # CSS, JS, imagens
│       ├── templates/       # Templates Thymeleaf
│       └── application.properties
└── test/                    # Testes unitários
```

## 🎯 Funcionalidades Principais

### Dashboard Interativo
- Cards com indicadores principais
- Gráficos de status de pedidos e estoque
- Ações rápidas para operações comuns

### Interface Responsiva
- Design adaptável para desktop e mobile
- Navegação intuitiva
- Feedback visual para ações do usuário

### Controle de Estoque Inteligente
- Alertas automáticos para estoque baixo
- Controle de quantidades mínimas
- Histórico de movimentações

### Sistema de Vendas Completo
- Fluxo completo de pedidos
- Cálculos automáticos
- Controle de status

## 🔒 Segurança

- Validação de dados no servidor
- Proteção contra SQL Injection
- Controle de acesso por perfis
- Validação de CPF/CNPJ

## 📱 Interface do Usuário

### Características da UI
- **Design Moderno**: Interface limpa e profissional
- **Responsiva**: Funciona em desktop, tablet e mobile
- **Intuitiva**: Navegação fácil e lógica
- **Acessível**: Ícones e cores que facilitam o uso

### Componentes Principais
- Cards informativos no dashboard
- Tabelas responsivas para listagens
- Formulários validados
- Modais para ações rápidas
- Alertas e notificações

## 🛠️ Configuração para Produção

### Banco de Dados MySQL
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vendasestoque
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

### Configurações de Segurança
- Implementar Spring Security
- Configurar HTTPS
- Definir políticas de senha
- Configurar backup automático

## 📈 Melhorias Futuras

- [ ] Sistema de relatórios avançados
- [ ] Integração com APIs de pagamento
- [ ] Módulo de compras e fornecedores
- [ ] Aplicativo mobile
- [ ] Sistema de notificações por email
- [ ] Integração com código de barras
- [ ] Módulo financeiro
- [ ] API REST completa

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 📞 Suporte

Para suporte e dúvidas:
- Abra uma issue no GitHub
- Consulte a documentação completa em `DOCUMENTACAO.md`

## 🎉 Agradecimentos

- Spring Boot Team pela excelente framework
- Bootstrap Team pelo framework CSS
- Thymeleaf Team pelo template engine
- Font Awesome pelos ícones

---

**Desenvolvido com ❤️ usando Spring Boot e Thymeleaf**

