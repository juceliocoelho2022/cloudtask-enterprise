# CloudTask MCP Server

Serviço MCP da v0.7 do CloudTask Enterprise.

## Stack

- Java 21
- Spring Boot 4.0.x
- Spring AI 2.0.x
- Model Context Protocol
- Streamable HTTP
- Maven
- Actuator

## Executar

Com o backend CloudTask ativo em `http://localhost:8080`, obtenha um JWT válido e configure:

```powershell
$env:CLOUDTASK_API_URL="http://localhost:8080"
$env:CLOUDTASK_API_TOKEN="SEU_JWT_LOCAL"
mvn spring-boot:run
```

O MCP Server sobe em `http://127.0.0.1:8090` e expõe o protocolo em `/mcp`.

## Testes

```powershell
mvn clean verify
```

## Docker

```powershell
docker build -t cloudtask-mcp-server .
docker run --rm `
  -p 127.0.0.1:8090:8090 `
  -e CLOUDTASK_API_URL=http://host.docker.internal:8080 `
  -e CLOUDTASK_API_TOKEN=$env:CLOUDTASK_API_TOKEN `
  cloudtask-mcp-server
```

> O transporte HTTP MCP não deve ser publicado para a Internet sem uma camada de autenticação/autorização. O bind local e o mapeamento Docker acima limitam o primeiro incremento ao ambiente de desenvolvimento.

Documentação completa: [`../docs/mcp-ai.md`](../docs/mcp-ai.md).
