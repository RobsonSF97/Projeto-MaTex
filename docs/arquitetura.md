# Arquitetura

> Legenda: linhas contínuas = existente/planejado para a fase atual · linhas tracejadas = evolução futura

## Visão geral

```mermaid
flowchart LR
    U[Navegador] -->|HTTP + sessão| SEC[Spring Security<br/>filtros de autenticação]
    API[Clientes da API REST] -.->|futuro| SEC
    SEC --> C[Controllers<br/>Spring MVC]
    C --> V[Views<br/>Thymeleaf]
    C --> S[Services<br/>regras de negócio e transações]
    S --> R[Repositories<br/>Spring Data JPA]
    R --> DB[(PostgreSQL)]
    S -.->|futuro| MQ[[RabbitMQ]]
```

## Responsabilidades das camadas

| Camada       | Responsabilidade                                                        | Não deve                                   |
|--------------|-------------------------------------------------------------------------|--------------------------------------------|
| Controller   | Receber a requisição, validar o formulário, escolher a view/redirect    | Conter regra de negócio ou abrir transação |
| Service      | Regras de negócio, orquestração e limites de transação                  | Conhecer HTTP, views ou sessão             |
| Repository   | Acesso a dados                                                          | Conter regra de negócio                    |
| View         | Exibir dados                                                            | Tomar decisões de negócio                  |

## Multi-tenancy

Cada assistência técnica (Empresa) é um *tenant*. O isolamento é feito por **coluna discriminadora** (`empresa_id`) nas tabelas de negócio.

```mermaid
flowchart TB
    subgraph Banco[PostgreSQL - schema único]
        direction LR
        A[Dados da Empresa A<br/>empresa_id = 1]
        B[Dados da Empresa B<br/>empresa_id = 2]
    end
    UA[Usuário da Empresa A] -->|empresa_id vem da sessão| A
    UB[Usuário da Empresa B] -->|empresa_id vem da sessão| B
    UA -. bloqueado .-x B
```

**Regra de segurança:** o `empresa_id` usado nas consultas vem **sempre do usuário autenticado**, nunca da URL, do formulário ou do corpo da requisição. Ver [ADR-001](decisoes.md#adr-001--multi-tenancy-por-coluna-discriminadora).
