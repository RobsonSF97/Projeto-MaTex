# Decisões técnicas (ADRs)

Registro das decisões de arquitetura do projeto. Cada decisão registra o contexto, o que foi decidido e as consequências, inclusive as negativas.

Uma decisão aceita não é apagada: se mudar, cria-se uma nova ADR que a substitui.

---

## ADR-001: Multi-tenancy por coluna discriminadora

**Status:** Aceita · 2026-10-09

**Contexto:** Várias assistências técnicas usam o mesmo sistema e os dados de cada uma precisam ficar isolados.

**Alternativas consideradas:**
- Banco por empresa: isolamento máximo, mas operação complexa demais para o estágio do projeto.
- Schema por empresa: migrações e testes mais complexos.
- Coluna `empresa_id` nas tabelas de negócio.

**Decisão:** Coluna discriminadora `empresa_id` em um schema único.

**Consequências:**
- ✅ É simples de operar, migrar e testar.
- ⚠️ Esquecer o filtro por `empresa_id` em uma consulta vaza dados entre empresas.
- ⚠️ O `empresa_id` deve vir **sempre do usuário autenticado**, nunca da requisição. Caso contrário, abre uma falha de IDOR.

---

## ADR-002: Telas server-side com Thymeleaf e autenticação por sessão

**Status:** Aceita · 2026-10-09

**Contexto:** A aplicação precisa de telas agora. Uma API REST para integrações simples é desejada no futuro.

**Decisão:** Telas renderizadas no servidor com Thymeleaf, com autenticação por sessão (form login do Spring Security).

**Consequências:**
- ✅ Uma única aplicação para desenvolver e publicar.
- ✅ O Spring Security entrega form login, sessão e proteção CSRF quase prontos.
- ⚠️ Quando a API REST chegar, ela vai precisar de outra forma de autenticação (provavelmente token), configurada separadamente.

---

## ADR-003: E-mail único global

**Status:** Aceita · 2026-10-09

**Contexto:** Usuários fazem login com e-mail e senha.

**Decisão:** O e-mail é único em todo o sistema, e não apenas dentro de cada empresa.

**Consequências:**
- ✅ O login precisa só de e-mail e senha, sem campo de "código da empresa".
- ⚠️ Uma pessoa não pode trabalhar em duas assistências com o mesmo e-mail. Se isso virar requisito, a evolução é uma tabela de vínculo N:N entre usuário e empresa.

---

## ADR-004: Dono da empresa é um perfil ADMIN

**Status:** Aceita · 2026-10-09

**Contexto:** O dono que se autocadastra administra a empresa e cadastra funcionários.

**Alternativas consideradas:** Uma coluna `empresa.dono_id`, que gera dependência circular entre empresa e usuário.

**Decisão:** "Ser dono" é representado pelo perfil `ADMIN` do usuário. Os perfis são um enum (`ADMIN`, `TECNICO`, `ATENDENTE`).

**Consequências:**
- ✅ Não há FK circular, e o insert do autocadastro é simples.
- ✅ A empresa pode ter mais de um administrador sem mudar o schema.
- ⚠️ Perfis customizáveis pelo cliente exigiriam migrar o enum para uma tabela de perfis e permissões. Isso só se justifica se virar requisito.

---

## ADR-005: Tabelas criadas manualmente, sem geração pelo Hibernate

**Status:** Aceita · 2026-10-09

**Contexto:** O schema do banco é parte importante do aprendizado e deve ser desenhado de forma consciente.

**Decisão:** As tabelas são criadas manualmente no PostgreSQL. O Hibernate roda com `ddl-auto=validate`: ele só verifica se as entidades batem com o banco e nunca altera o schema.

**Consequências:**
- ✅ Controle total sobre tipos, constraints e índices.
- ✅ Uma divergência entre entidade e tabela faz a aplicação falhar ao subir, em vez de falhar em produção.
- ⚠️ Os scripts SQL precisam ser versionados no repositório. Caso contrário, ninguém consegue recriar o banco. A evolução natural é adotar uma ferramenta de migração (Flyway).

**Convenção dos scripts:**
- Local: `src/main/resources/db/migration/`, o local padrão do Flyway.
- Nome: `V<versão>__<descrição>.sql`, com **dois** underscores. Exemplo: `V2__cria_tabela_cliente.sql`.
- Os scripts rodam em ordem de versão, cada um uma única vez.
- **Um script já aplicado nunca é editado.** Toda mudança vira um novo script com a próxima versão.

---

## ADR-006: CNPJ alfanumérico armazenado sem máscara

**Status:** Aceita · 2026-10-12

**Contexto:** A partir de julho de 2026, a Receita Federal passou a emitir CNPJs alfanuméricos: as 12 primeiras posições aceitam letras e números, e os 2 dígitos verificadores continuam numéricos.

**Decisão:** O CNPJ é armazenado em `VARCHAR(14)`, sem máscara e em letras maiúsculas, com `CHECK` validando o formato `^[0-9A-Z]{12}[0-9]{2}$`. A tela exibe e aceita o CNPJ com máscara, e a aplicação remove a formatação antes de salvar.

**Consequências:**
- ✅ Atende assistências abertas antes e depois da mudança.
- ✅ Buscas por CNPJ são feitas por igualdade simples, sem lidar com formatação.
- ⚠️ O cálculo dos dígitos verificadores precisa seguir a regra nova, que converte letras em valores numéricos.
- ⚠️ Nunca usar tipo numérico: perderia letras e zeros à esquerda.
- ⚠️ O `@CNPJ` do Hibernate Validator (9.1.3) valida apenas CNPJ numérico por padrão. O formato alfanumérico exige `@CNPJ(format = CNPJ.Format.ALPHANUMERIC)`, que está marcado como `@Incubating` (a API pode mudar). A validação dos dígitos verificadores ainda será implementada.

---

## ADR-007: Auditoria por callbacks do ciclo de vida JPA

**Status:** Aceita · 2026-10-21

**Contexto:** Todas as entidades registram `criado_em` e `atualizado_em`. Definir essas datas manualmente nos services espalha a regra e permite esquecimentos. Esquecer o `atualizado_em` não gera erro: o dado fica errado em silêncio.

**Alternativas consideradas:**
- Definir as datas manualmente no service antes do `save`.
- `@CreationTimestamp` e `@UpdateTimestamp`, específicos do Hibernate. O `@UpdateTimestamp` preenche o campo já no INSERT.
- `@PrePersist` e `@PreUpdate`, do padrão JPA.

**Decisão:** As datas ficam em um `@Embeddable Auditoria`, sem setters públicos, com os métodos `registrarCriacao()` e `registrarAtualizacao()`. Cada entidade os chama nos seus callbacks `@PrePersist` e `@PreUpdate`. A coluna `criado_em` é mapeada com `updatable = false`.

**Consequências:**
- ✅ Nenhum service precisa lembrar de definir as datas.
- ✅ `atualizado_em` permanece `null` até a primeira alteração real.
- ✅ Usa apenas JPA padrão, sem anotações específicas do Hibernate.
- ⚠️ Os dois callbacks se repetem em cada entidade. Na terceira entidade auditada, extrair para um `@EntityListeners` compartilhado.
- ⚠️ `criado_por` e `atualizado_por` ficam para depois. Avaliar o Spring Data JPA Auditing (`@CreatedBy` + `AuditorAware`) quando o login existir.

---

## ADR-008: Normalização do e-mail na entidade

**Status:** Aceita · 2026-10-21

**Contexto:** `Joao@Email.com` e `joao@email.com` são a mesma caixa postal, mas passam como valores diferentes pelo `UNIQUE` do banco. Normalizar em cada service espalha a regra e permite esquecimentos.

**Decisão:** O `Usuario` normaliza o e-mail (`strip()` + `toLowerCase(Locale.ROOT)`) em todos os seus pontos de entrada: o construtor e o método de alteração de e-mail. A regra fica no método estático `Usuario.normalizarEmail`, reutilizado também nas buscas por e-mail, como no login.

**Consequências:**
- ✅ É impossível salvar um e-mail fora do padrão pela aplicação.
- ✅ A mesma regra é usada para gravar e para buscar.
- ⚠️ Inserts manuais no banco não passam pela entidade. Se isso virar um risco, adicionar `CHECK (email = lower(email))` em uma migração.
- ⚠️ Evolução possível: um Value Object `Email` (record) que se normaliza ao ser criado.
