# CloudTask AI Client

Cliente de IA da v0.7 do CloudTask Enterprise. O serviço recebe linguagem natural, conecta-se ao CloudTask MCP Server via Streamable HTTP e disponibiliza as tools MCP para o modelo através do Spring AI `ChatClient`.

## Stack

- Java 21
- Spring Boot 4.0.8
- Spring AI 2.0.1
- MCP Client
- Streamable HTTP
- Ollama
- Qwen3 4B por padrão
- Maven
- Actuator

## Arquitetura local

```text
Usuário / HTTP
      |
      v
CloudTask AI Client :8091
Spring AI ChatClient + Ollama
      |
      v
MCP Client
      |
      v
CloudTask MCP Server :8090/mcp
      |
      v
CloudTask REST API :8080
      |
      v
PostgreSQL
```

## Pré-requisitos

1. Backend CloudTask ativo em `http://localhost:8080`.
2. MCP Server ativo em `http://127.0.0.1:8090/mcp` com um JWT válido configurado no processo do MCP Server.
3. Ollama instalado e ativo localmente.
4. Modelo com suporte a tools disponível no Ollama.

O modelo padrão é `qwen3:4b`.

```powershell
ollama pull qwen3:4b
```

Se o serviço do Ollama não estiver iniciado:

```powershell
ollama serve
```

## Executar

```powershell
cd ai-client

$env:CLOUDTASK_MCP_URL="http://127.0.0.1:8090"
$env:OLLAMA_BASE_URL="http://localhost:11434"
$env:OLLAMA_MODEL="qwen3:4b"

mvn spring-boot:run
```

O AI Client sobe em `http://127.0.0.1:8091`.

## Testar linguagem natural

### Listar tarefas

```powershell
$body = @{
  message = "Liste minhas tarefas"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "http://127.0.0.1:8091/api/v1/ai/chat" `
  -ContentType "application/json" `
  -Body $body
```

### Criar tarefa

```powershell
$body = @{
  message = "Crie uma tarefa de prioridade alta chamada Revisar documentação do CloudTask para 30 de agosto de 2026"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "http://127.0.0.1:8091/api/v1/ai/chat" `
  -ContentType "application/json" `
  -Body $body
```

## Proteção de exclusão

A tool `delete_task` não é fornecida ao modelo por padrão. Para habilitá-la em uma solicitação específica, a mensagem precisa conter explicitamente:

```text
CONFIRMAR_EXCLUSAO
```

Exemplo:

```text
CONFIRMAR_EXCLUSAO: exclua a tarefa 10
```

Esse bloqueio é aplicado em código antes de as tools serem entregues ao modelo, não apenas por instrução no prompt.

## Health

```text
http://127.0.0.1:8091/actuator/health
```

## Testes

```powershell
mvn clean verify
```

Os testes cobrem a fronteira que impede a disponibilização da tool destrutiva sem confirmação explícita.

## Segurança

O AI Client permanece local nesta fase e escuta em `127.0.0.1` por padrão. O endpoint de chat ainda não deve ser publicado na Internet antes da inclusão de autenticação, autorização, rate limiting e controles de operação adequados.

Documentação da arquitetura completa: [`../docs/mcp-ai.md`](../docs/mcp-ai.md).
