# M5-Storage API — Sistema de Gestão de Recursos

Projeto desenvolvido em Java utilizando Spring Boot, Maven, JPA/Hibernate e Oracle Database para gerenciamento de bases, setores, recursos, movimentações e alertas críticos através de operações CRUD (Create, Read, Update e Delete).

---

# Desenvolvido por

- Guilherme Cintra RM562850
- Erick de Faria Gama RM561951
- Matheus Nascimento Corregio RM563765
- Pedro Fonseca de Almeida RM563466
- Daniel Fonseca de Almeida RM563045

---

# Objetivo

A aplicação tem como objetivo auxiliar no controle operacional de recursos em bases e setores, permitindo:

- Cadastro e gerenciamento de bases e setores
- Cadastro e monitoramento de recursos por setor
- Registro de consumo e reabastecimento de recursos
- Geração automática de alertas para recursos críticos
- Dashboard em tempo real com status dos recursos e alertas por setor
- Controle de acesso por tipo de usuário (Operator e Viewer)

A API foi desenvolvida utilizando arquitetura REST e persistência em banco de dados Oracle.

---

# Tecnologias Utilizadas

- Java 21
- Spring Boot
- Maven
- Spring Data JPA
- Hibernate
- Oracle Database
- Swagger / OpenAPI
- Spring HATEOAS
- Lombok

---

# Arquitetura da Aplicação

O projeto foi desenvolvido seguindo arquitetura em camadas, separando responsabilidades para facilitar manutenção, escalabilidade e organização do código.

## Camadas da Aplicação

### Controller

Responsável por receber as requisições HTTP da API REST e retornar as respostas ao cliente.

Exemplos:
- `BaseController`
- `SetorController`
- `RecursoController`
- `MovimentacaoController`
- `AlertaController`
- `UsuarioController`

---

### Service

Responsável pelas regras de negócio da aplicação.

Exemplos:
- validação de permissões por tipo de usuário
- atualização automática de status do recurso
- geração e resolução de alertas críticos
- controle de estoque com limites mínimo e máximo

---

### Repository

Responsável pela comunicação com o banco de dados utilizando Spring Data JPA.

As interfaces Repository realizam operações como:
- salvar
- buscar
- atualizar
- deletar registros

---

### Entity

Representação das tabelas do banco de dados através de entidades JPA.

Exemplos:
- `Base`
- `Setor`
- `Recurso`
- `Movimentacao`
- `Alerta`
- `Usuario` (com herança SINGLE_TABLE: `Operator` e `Viewer`)

---

### DTO

Responsável pela transferência de dados entre cliente e API.

Utilizado para:
- cadastro
- atualização
- listagem de informações

---

### Assembler

Responsável por adicionar links HATEOAS às respostas da API, permitindo navegação entre os recursos.

---

# Estrutura do Projeto

```text
src/main/java/br/com/m5_storage

├── config
├── controller
├── dto
│   ├── alerta
│   ├── base
│   ├── movimentacao
│   ├── recurso
│   ├── setor
│   └── usuario
├── entity
│   ├── alerta
│   ├── base
│   ├── movimentacao
│   ├── recurso
│   ├── setor
│   └── usuario
├── exception
├── repository
└── service
```

---

# Modelagem Avançada

## Herança — SINGLE_TABLE

A entidade `Usuario` utiliza herança com estratégia `SINGLE_TABLE`, armazenando todos os tipos de usuário em uma única tabela `st_t_usuarios` com a coluna discriminadora `tipo_usuario`.

```
Usuario  (SINGLE_TABLE)
  ├── Operator  → pode criar, editar, deletar recursos e registrar movimentações
  └── Viewer    → apenas leitura
```

## Embedded

A entidade `Setor` utiliza `@Embeddable` para encapsular as informações de nome e descrição através da classe `SetorInfo`.

## Estrutura Hierárquica

```
Base
 └── Setor
      └── Recurso
           └── Movimentação
                    ↓
                 Alerta
```

---

# Regras de Negócio

- Recursos não podem ter quantidade negativa
- Reabastecimento não pode ultrapassar a capacidade máxima
- O mínimo deve ser menor que a capacidade máxima
- Status do recurso é calculado automaticamente: `OK`, `ATENCAO` ou `CRITICO`
- Apenas recursos marcados como críticos geram alertas
- Alertas são gerados automaticamente ao atingir o nível mínimo
- Alertas são resolvidos automaticamente ao voltar ao nível seguro
- Movimentações nunca são deletadas (histórico permanente)
- Recursos com movimentações vinculadas não podem ser deletados
- Apenas `Operator` pode criar, editar, deletar recursos e registrar movimentações
- Apenas `Operator` pode resolver alertas manualmente
- Emails de usuários são únicos no sistema

---

# Funcionalidades da API

## Bases

Permite:
- cadastrar bases
- listar bases
- buscar base por ID
- atualizar dados
- remover bases

---

## Setores

Permite:
- cadastrar setores vinculados a uma base
- listar setores
- listar setores por base
- buscar setor por ID
- atualizar dados
- remover setores

---

## Recursos

Permite:
- cadastrar recursos vinculados a um setor *(apenas Operator)*
- listar recursos
- buscar recurso por ID
- listar por setor, base ou status
- atualizar dados *(apenas Operator)*
- remover recursos *(apenas Operator)*

---

## Movimentações

Permite:
- registrar consumo ou reabastecimento *(apenas Operator)*
- listar histórico por recurso, usuário, setor ou base
- filtrar por setor e tipo de movimentação

---

## Alertas

Permite:
- listar alertas ativos (dashboard)
- listar alertas por recurso, setor ou base
- resolver alertas manualmente *(apenas Operator)*

---

## Usuários

Permite:
- cadastrar usuários (Operator ou Viewer)
- listar usuários
- listar usuários por base
- buscar usuário por ID
- atualizar dados
- remover usuários

---

# Banco de Dados

O sistema utiliza Oracle Database para persistência das informações.

As principais tabelas são:

- `st_t_bases`
- `st_t_setores`
- `st_t_usuarios`
- `st_t_recursos`
- `st_t_movimentacoes`
- `st_t_alertas`

---

# Constraints Utilizadas

## Primary Key (PK)

Responsável pela identificação única dos registros.

## Foreign Key (FK)

Responsável pelos relacionamentos entre as tabelas.

Exemplos:
- setor vinculado à base
- recurso vinculado ao setor
- movimentação vinculada ao recurso, setor e usuário
- alerta vinculado ao recurso e setor

## UNIQUE

Impede duplicidade de informações importantes.

Exemplos:
- email do usuário

## CHECK

Valida valores específicos.

Exemplos:
- `tipo_usuario IN ('VIEWER', 'OPERATOR')`

---

# Endpoints da API

## Base URL

```http
http://localhost:8080
```

## Bases
```http
GET    /bases
POST   /bases
GET    /bases/{id}
PUT    /bases/{id}
DELETE /bases/{id}
```

## Setores
```http
GET    /setores
POST   /setores
GET    /setores/{id}
GET    /setores/base/{baseId}
PUT    /setores/{id}
DELETE /setores/{id}
```

## Recursos
```http
GET    /recursos
POST   /recursos?usuarioId={id}
GET    /recursos/{id}
GET    /recursos/setor/{setorId}
GET    /recursos/base/{baseId}
GET    /recursos/status/{status}
PUT    /recursos/{id}?usuarioId={id}
DELETE /recursos/{id}?usuarioId={id}
```

## Movimentações
```http
POST   /movimentacoes
GET    /movimentacoes/recurso/{recursoId}
GET    /movimentacoes/usuario/{usuarioId}
GET    /movimentacoes/setor/{setorId}
GET    /movimentacoes/setor/{setorId}/tipo/{tipo}
GET    /movimentacoes/base/{baseId}
```

## Alertas
```http
GET    /alertas
GET    /alertas/recurso/{recursoId}
GET    /alertas/setor/{setorId}
GET    /alertas/base/{baseId}
PATCH  /alertas/{id}/resolver?usuarioId={id}
```

## Usuários
```http
GET    /usuarios
POST   /usuarios?solicitanteId={id}
GET    /usuarios/{id}
GET    /usuarios/base/{baseId}
PUT    /usuarios/{id}
DELETE /usuarios/{id}
```

---

# Swagger

A documentação completa da API pode ser acessada através do endereço:

```http
http://localhost:8080/swagger-ui/index.html
```

---

# Tratamento de Exceções

A aplicação possui tratamento padronizado de exceções para:

| Situação | Status HTTP |
|---|---|
| Registro não encontrado | 404 Not Found |
| Acesso negado (Viewer tentando escrita) | 403 Forbidden |
| Dados inválidos ou regra de negócio violada | 400 Bad Request |
| Email ou dado duplicado | 409 Conflict |
| Método HTTP não suportado | 405 Method Not Allowed |
| Tipo de conteúdo inválido | 415 Unsupported Media Type |
| Erro interno | 500 Internal Server Error |

---

# Como Executar o Projeto

## 1. Clonar o Repositório

```bash
git clone <repositorio>
```

## 2. Configurar Banco Oracle

Configurar as variáveis de ambiente ou o arquivo `application.properties`:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

## 3. Executar a Aplicação

```bash
mvn spring-boot:run
```

## 4. Acessar Swagger

```http
http://localhost:8080/swagger-ui/index.html
```

# Link Deploy
```http
https://m5-storage.onrender.com
```

# Link Vídeos

## 1. Vídeo apresentação
```http
https://www.youtube.com/watch?v=W-W-nl4zYuE
```

## 2. Vídeo pitch
```http
https://www.youtube.com/watch?v=6SMKNB_aJPI
```
