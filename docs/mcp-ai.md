# CloudTask Enterprise v0.7 — MCP + IA

## Objetivo

A v0.7 adiciona uma camada de integração com IA sem acoplar o runtime principal do CloudTask a um provedor de modelo específico.

A implementação separa três responsabilidades:

- domínio, autenticação e persistência continuam no backend principal;
- o `mcp-server` expõe operações CloudTask como tools padronizadas;
- o `ai-client` recebe linguagem natural, disponibiliza as tools MCP ao modelo e coordena o tool calling.

## Decisão de arquitetura

O backend principal permanece em Java 21 + Spring Boot 3.5.x. Os serviços de integração com IA usam Java 21, Spring Boot 4.0.x e Spring AI 2.0.x de forma independente.

Essa separação evita uma migração forçada do backend estável apenas para habilitar recursos de IA e cria fronteiras claras entre domínio, protocolo e modelo.

O primeiro provider de modelo da Fase B é **Ollama**, permitindo validação local sem depender de uma API paga. O `ChatClient` e o MCP mantêm o domínio desacoplado do provider e permitem adicionar outros modelos posteriormente.

## Arquitetura local da v0.7

```text
Usuário / HTTP
      |
      v
CloudTask AI Client :8091
Spring AI ChatClient
Ollama / qwen3:4b
      |
      v
Spring AI MCP Client
Streamable HTTP
      |
      v
CloudTask MCP Server :8090/mcp
      |
      v
CloudTask REST API :8080
JWT Bearer Token
      |
      v
PostgreSQL
```

## Tools CloudTask

| Tool | Tipo | Comportamento |
| --- | --- | --- |
| `list_tasks` | leitura | Lista tarefas e aceita filtro opcional por status |
| `create_task` | escrita | Cria tarefa com defaults seguros para status e prioridade |
| `update_task_status` | escrita idempotente | Atualiza somente o status e preserva os demais campos |
| `delete_task` | destrutiva | Remove uma tarefa existente |

As tools reutilizam a API REST já existente em `/api/v1/tasks`, preservando o escopo do usuário através de um JWT configurado por ambiente no processo do MCP Server.

## Validação real do MCP Server

O transporte MCP foi validado localmente com o MCP Inspector usando Streamable HTTP.

Validações já executadas:

- descoberta das 4 tools com `tools/list`;
- `list_tasks` retornando resposta válida;
- `create_task` criando uma tarefa real no backend;
- nova chamada de `list_tasks` retornando a tarefa persistida;
- `delete_task` removendo a tarefa e a listagem final retornando vazia.

A validação manual específica de `update_task_status` permanece como checagem adicional antes de encerrar a v0.7.

## Segurança

### MCP Server

O endpoint HTTP do MCP não deve ser exposto publicamente sem autenticação/autorização própria.

Por padrão, o serviço escuta somente em `127.0.0.1`. O acesso ao backend exige `CLOUDTASK_API_TOKEN`; nenhum token é versionado no Git.

### AI Client

O `ai-client` também escuta em `127.0.0.1` por padrão e ainda não deve ser publicado na Internet.

A tool destrutiva `delete_task` possui uma proteção adicional em código: ela é removida do conjunto de tools entregue ao modelo, exceto quando a mensagem contém explicitamente o token:

```text
CONFIRMAR_EXCLUSAO
```

Exemplo:

```text
CONFIRMAR_EXCLUSAO: exclua a tarefa 10
```

Essa proteção não depende apenas do prompt do modelo.

Antes de qualquer implantação remota serão necessários autenticação/autorização, rate limiting e uma política explícita para operações destrutivas.

## Configuração local — MCP Server

```powershell
$env:CLOUDTASK_API_URL="http://localhost:8080"
$env:CLOUDTASK_API_TOKEN="SEU_JWT_LOCAL"

cd mcp-server
mvn spring-boot:run
```

Endpoints locais:

- MCP: `http://127.0.0.1:8090/mcp`
- Health: `http://127.0.0.1:8090/actuator/health`

## Configuração local — AI Client

Instale/inicie o Ollama e disponibilize um modelo com suporte a tools:

```powershell
ollama pull qwen3:4b
```

Depois:

```powershell
cd ai-client

$env:CLOUDTASK_MCP_URL="http://127.0.0.1:8090"
$env:OLLAMA_BASE_URL="http://localhost:11434"
$env:OLLAMA_MODEL="qwen3:4b"

mvn spring-boot:run
```

Endpoints locais:

- Chat: `POST http://127.0.0.1:8091/api/v1/ai/chat`
- Health: `http://127.0.0.1:8091/actuator/health`

Exemplo:

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

## Fases da v0.7

### Fase A — MCP Server

- [x] serviço independente
- [x] Streamable HTTP
- [x] tools CloudTask
- [x] integração via JWT com a REST API
- [x] testes unitários das tools
- [x] CI dedicado
- [x] descoberta real das tools via MCP Inspector
- [x] validação real de listagem, criação e exclusão
- [ ] validação manual final de `update_task_status`

### Fase B — Integração com IA

- [x] serviço independente `ai-client`
- [x] Spring AI MCP Client
- [x] Streamable HTTP para `mcp-server`
- [x] ChatClient com modelo local Ollama configurável por ambiente
- [x] execução de tools MCP disponibilizada ao modelo
- [x] endpoint `POST /api/v1/ai/chat`
- [x] data atual e timezone fornecidos ao modelo para interpretar datas relativas
- [x] proteção em código contra exclusão sem confirmação explícita
- [x] testes unitários da barreira de operação destrutiva
- [x] CI dedicado do AI Client
- [ ] validação ponta a ponta por linguagem natural

### Fase C — Cloud

- [ ] definir estratégia de provider/modelo para o ambiente remoto
- [ ] container ECR do MCP Server e AI Client
- [ ] ECS/Fargate
- [ ] roteamento dedicado no ALB ou endpoints privados
- [ ] autenticação/autorização do MCP remoto e endpoint de IA
- [ ] observabilidade e métricas
- [ ] pipeline de deploy

## Critérios para concluir a v0.7

A versão só será marcada como concluída quando um fluxo real de IA conseguir descobrir e executar tools do CloudTask via MCP, com testes, segurança mínima, CI e validação ponta a ponta.
