# Modelo de dados

> Status: 🟡 **planejado**. As tabelas ainda não foram criadas no banco.
> Atualize este diagrama sempre que o schema mudar.

## Diagrama entidade-relacionamento

```mermaid
erDiagram
    EMPRESA ||--|{ USUARIO : "possui"

    EMPRESA {
        bigint id PK
        varchar nome_fantasia "NOT NULL"
        varchar razao_social
        varchar cnpj UK "NOT NULL"
        varchar telefone
        boolean ativa "NOT NULL"
        timestamp criado_em "NOT NULL"
    }

    USUARIO {
        bigint id PK
        bigint empresa_id FK "NOT NULL"
        varchar nome "NOT NULL"
        varchar email UK "NOT NULL"
        varchar senha_hash "NOT NULL"
        varchar perfil "NOT NULL - ADMIN, TECNICO, ATENDENTE"
        boolean ativo "NOT NULL"
        timestamp criado_em "NOT NULL"
    }
```

## Regras de integridade

| Regra | Onde é garantida |
|-------|------------------|
| CNPJ único no sistema | `UNIQUE` no banco + validação no service |
| E-mail único no sistema (não por empresa) | `UNIQUE` no banco + validação no service |
| Toda empresa nasce com pelo menos um ADMIN | Transação do autocadastro |
| Todo usuário pertence a exatamente uma empresa | `empresa_id NOT NULL` + FK |
| Senha nunca armazenada em texto puro | Hash BCrypt no service |

Decisões relacionadas: [ADR-003](decisoes.md#adr-003--e-mail-único-global) e [ADR-004](decisoes.md#adr-004--dono-da-empresa-é-um-perfil-admin).
