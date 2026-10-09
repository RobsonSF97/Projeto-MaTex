# MaTex

Sistema de gestão para assistências técnicas de informática. Cada assistência se cadastra no sistema, administra seus próprios funcionários e gerencia a manutenção de microcomputadores dos seus clientes, com os dados de cada empresa isolados das demais.

## 🚀 TECNOLOGIAS

- **Java 21** – Linguagem principal
- **Spring Boot 4** – Framework base da aplicação
- **Spring MVC** – Controllers e tratamento das requisições HTTP
- **Thymeleaf** – Renderização das telas no servidor
- **Spring Security** – Autenticação e autorização
- **Spring Data JPA / Hibernate** – Persistência e acesso aos dados
- **Bean Validation (Jakarta Validation)** – Validação dos dados de entrada
- **PostgreSQL** – Banco de dados relacional
- **Maven** – Gerenciamento de dependências e build
- **JUnit 5 / Mockito** – Testes automatizados
- **Git / GitHub** – Controle de versão e hospedagem do código

## 📐 DOCUMENTAÇÃO

A documentação visual evolui junto com o projeto:

- [Arquitetura](docs/arquitetura.md) – Visão geral das camadas e componentes
- [Modelo de dados](docs/modelo-de-dados.md) – Diagrama entidade-relacionamento
- [Fluxos](docs/fluxos.md) – Diagramas de sequência das funcionalidades
- [Decisões técnicas](docs/decisoes.md) – Registro das decisões de arquitetura (ADRs)

## 📋 FUNCIONALIDADES

### Implementadas

_Nenhuma ainda._

### Planejadas

- [ ] Autocadastro da assistência técnica (empresa + administrador)
- [ ] Login
- [ ] Cadastro de funcionários pelo administrador

## ▶️ COMO EXECUTAR

Pré-requisitos: Java 21 e PostgreSQL com o banco `matex` criado.

Variáveis de ambiente:

| Variável      | Obrigatória | Padrão                                     |
|---------------|-------------|--------------------------------------------|
| `DB_URL`      | Não         | `jdbc:postgresql://localhost:5432/matex`   |
| `DB_USERNAME` | Não         | `postgres`                                 |
| `DB_PASSWORD` | Sim         | –                                          |

```bash
./mvnw spring-boot:run
```

## 👨‍💻 VERSÕES

| Versão | Data       | Alterações |
|--------|------------|------------|
| 0.0.1  | 2026-10-09 | - Estrutura inicial do projeto (Spring Boot 4, Java 21)<br>- Configuração do PostgreSQL e Thymeleaf<br>- Documentação visual inicial (arquitetura, modelo de dados, fluxos e decisões) |
