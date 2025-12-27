

# Desafio Banco Aggregator API

Solução para o desafio técnico de agregação de contas bancarias e saldos.
O objetivo é expor uma API que consolida dados vindos de duas bases de dados distintas (Base A: Dados cadastrais, Base B: Saldos financeiros), simulando um ambiente de microserviços desacoplado.

## Tecnologias

- Java 17
- Spring Boot 3.1+
- H2 Database (em memória)
- Lombok
- Actuator / Micrometer

## Arquitetura e Decisões Técnicas

Como o requisito pedia bases distintas, Configurei dois `DataSource` separados (`accountdb` e `balancedb`). Isso garante que não existe integridade referencial a nivel de banco de dados, forçando a integridade ser tratada na aplicação.

### Estrutura de Pastas
Tudo relacionado a agregação está no pacote `feature/accountaggregator`. Os dominios puros ficam em `domain`. Isso facilita caso precisemos extrair essa feature para um serviço separado no futuro.

### Performance
Para evitar gargalo de varias querys dfiz isso:
1. Busco todas as contas na Base A.
2. Coleto os IDs e faço uma unica busca na Base B (`WHERE account_id IN (...)`).
3. Crio um `Map<Long, Balance>` em memoria.
4. Faço o merge dos dados iterando a lista apenas uma vez.

Isso mantem a complexidade baixa e o tempo de resposta rapido mesmo com muitos registros.

## Como rodar

O projeto usa Maven. Para subir a aplicação:

```bash
mvn spring-boot:run

```

A aplicação roda na porta `8080`.

### Carga inicial de dados

Criei uma classe `DataInitializer` que popula o banco automaticamente ao iniciar com varios registros de teste (inspirados na Familia Dinossauro) para facilitar a validação da soma dos saldos.

## Endpoints

A documentação da API pode ser acessada via Swagger (se o springdoc estiver habilitado) ou chamando diretamente:

**Listar contas (Agregação):**
`GET /api/v1/accounts`

**Criar conta:**
`POST /api/v1/accounts`
Payload:

```json
{
  "accountNumber": "12345",
  "holderName": "Nome do Cliente",
  "type": "CORRENTE"
}

```

## Observabilidade

Adicionei logs com traceId configurado para rastreamento. Tambem configurei metricas de tempo de execução no endpoint de listagem via `@Timed`.
Para ver as metricas, acesse o actuator em `/actuator/metrics/aggregation.time` (precisa fazer ao menos uma requisição antes para a metrica aparecer).

## Configuração Local

O `application.properties` já está configurado para subir dois bancos H2 em memória. Se precisar ver as tabelas, o console do H2 fica em `/h2-console`.
Lembre-se de alterar a JDBC URL para conectar na base certa:

* Contas: `jdbc:h2:mem:accountdb`
* Saldos: `jdbc:h2:mem:balancedb`

Qualquer problema na execução verificar as variaveis de ambiente do java ou a versão do maven.

```
