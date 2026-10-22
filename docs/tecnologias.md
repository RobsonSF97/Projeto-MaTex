# Tecnologias e recursos do Java

> Guia do que o projeto usa, com as versões e exemplos tirados do próprio código.
> Mantido a cada nova dependência ou recurso da linguagem adotado. Última atualização: 2026-10-21.

## Versões

| Tecnologia | Versão | Papel no projeto |
|------------|--------|------------------|
| Java (JDK) | 21 (LTS) | Linguagem |
| Spring Boot | 4.1.1 | Base da aplicação e autoconfiguração |
| Spring Framework | 7.0.9 | Injeção de dependência, MVC, transações |
| Spring Security | 7.1.1 | Autenticação e autorização |
| Hibernate ORM | 7.4.5 | Implementação do JPA |
| Jakarta Persistence (JPA) | 3.2 | API de mapeamento objeto-relacional |
| Hibernate Validator | 9.1.3 | Implementação do Bean Validation |
| Jakarta Validation | 3.1 | API de validação (`@NotBlank`, `@Valid`...) |
| Thymeleaf | 3.1.5 | Renderização das telas no servidor |
| Driver JDBC PostgreSQL | 42.7.13 | Conexão com o banco |
| Maven | via wrapper (`mvnw`) | Build e dependências |

As versões das bibliotecas são gerenciadas pelo Spring Boot (`spring-boot-starter-parent`). Por isso o `pom.xml` não declara versão nas dependências.

---

## O que mudou desde o Java 8 / Spring Boot 2 e aparece no projeto

### `javax.*` → `jakarta.*`
Desde o Spring Boot 3, as APIs do Java EE mudaram de pacote. A Oracle transferiu o Java EE para a Eclipse Foundation, que o renomeou para Jakarta EE.

```java
// Java 8 / Spring Boot 2
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

// Este projeto
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
```
Um tutorial antigo com `javax.*` não compila aqui. É só trocar o pacote.

### Starters modulares (Spring Boot 4)
O Spring Boot 4 dividiu a autoconfiguração em módulos menores. Alguns nomes mudaram:

| Antes | Agora |
|-------|-------|
| `spring-boot-starter-web` | `spring-boot-starter-webmvc` |
| `spring-boot-starter-test` (um só) | Um starter de teste por módulo: `spring-boot-starter-data-jpa-test`, `spring-boot-starter-webmvc-test`, `spring-boot-starter-security-test`... |

---

## Recursos do Java em uso

### `java.time.Instant` (Java 8)
Representa um **instante absoluto** na linha do tempo, em UTC. É o par natural da coluna `TIMESTAMPTZ`.

```java
// Auditoria.java
private Instant criadoEm;

void registrarCriacao() {
    this.criadoEm = Instant.now();
}
```

**Por que não `java.util.Date`:** `Date` é mutável (qualquer um com a referência altera o valor), tem uma API confusa (meses começam em 0) e não deixa claro se representa um instante ou uma data local. A API `java.time` é imutável e tem um tipo para cada conceito:

| Tipo | Representa | Exemplo no domínio |
|------|-----------|--------------------|
| `Instant` | Instante absoluto (UTC) | Momento em que a OS foi aberta |
| `LocalDate` | Data sem hora e sem fuso | Data de vencimento da garantia |
| `LocalDateTime` | Data e hora sem fuso | Raramente o tipo certo para gravar |
| `Duration` / `Period` | Intervalo de tempo / de calendário | Garantia de 90 dias |

### `String.strip()` (Java 11)
Remove espaços das pontas, **incluindo espaços Unicode**. O `trim()` remove apenas caracteres com código ≤ `U+0020`.

```java
// Usuario.java
public static String normalizarEmail(String email) {
    return email == null ? null : email.strip().toLowerCase(Locale.ROOT);
}
```

Junto com o `strip()`, outras adições do Java 11 a `String`: `isBlank()`, `lines()`, `repeat(n)`.

**Por que `Locale.ROOT`:** `toLowerCase()` sem argumento usa o idioma do servidor. Em turco, `"I".toLowerCase()` resulta em `"ı"` (i sem ponto), e o mesmo e-mail seria normalizado de forma diferente dependendo da máquina.

---

## Anotações JPA e Bean Validation em uso

| Anotação | Onde | O que faz |
|----------|------|-----------|
| `@Entity` | `Empresa`, `Usuario` | Classe mapeada para uma tabela |
| `@Id` + `@GeneratedValue(IDENTITY)` | Entidades | Chave primária gerada pelo banco. O `@Id` no **campo** define *field access*: o Hibernate lê e grava direto nos atributos, sem precisar de setters |
| `@ManyToOne(fetch = LAZY)` + `@JoinColumn` | `Usuario.empresa` | Associação N:1. Só carrega a empresa quando ela é acessada |
| `@OneToMany(mappedBy = ...)` | `Empresa.usuarios` | Lado inverso da associação. Não controla a FK |
| `@Embeddable` / `@Embedded` | `Endereco`, `Auditoria` | Objeto sem tabela própria: as colunas ficam na tabela da entidade |
| `@Enumerated(EnumType.STRING)` | `perfil`, `uf` | Grava o **nome** do enum. `ORDINAL` gravaria a posição e quebraria ao reordenar o enum |
| `@PrePersist` / `@PreUpdate` | Entidades | Métodos chamados pelo JPA antes do INSERT e do UPDATE |
| `@NotNull` | Qualquer tipo | Rejeita `null` |
| `@NotEmpty` | Texto, coleções, mapas, arrays | Rejeita `null` e tamanho 0 |
| `@NotBlank` | Somente texto | Rejeita `null`, `""` e texto só com espaços |
| `@Valid` | Campos `@Embedded` | Validação em cascata: valida as anotações **dentro** do objeto |

---

## Recursos do Java moderno ainda não usados (candidatos)

Recursos que devem aparecer conforme o projeto crescer. Ao serem usados, sobem para a seção "Recursos do Java em uso".

| Recurso | Desde | Exemplo no domínio |
|---------|-------|--------------------|
| `record` | Java 16 | DTO do formulário de autocadastro: `record AutocadastroForm(String nomeFantasia, String cnpj, ...) {}` |
| `var` | Java 10 | `var usuarios = usuarioRepository.findByEmpresaId(id);` |
| `switch` como expressão | Java 14 | `int prazoDias = switch (tipo) { case CORRETIVA -> 90; case PREVENTIVA -> 30; };` |
| Text blocks (`"""`) | Java 15 | Queries JPQL longas em várias linhas |
| Pattern matching no `instanceof` | Java 16 | `if (equipamento instanceof Notebook n) { n.getBateria(); }` |
| `Stream.toList()` | Java 16 | Substitui `.collect(Collectors.toList())` e retorna lista imutável |
| Sealed classes | Java 17 | `sealed interface Equipamento permits Computador, Notebook` |
| Pattern matching no `switch` | Java 21 | `switch (equipamento) { case Notebook n -> ...; case Computador c -> ...; }` |
| Sequenced Collections | Java 21 | `historico.getLast()` em vez de `historico.get(historico.size() - 1)` |
