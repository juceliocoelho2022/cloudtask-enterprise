# CloudTask Enterprise v0.7 — MCP + IA

## Objetivo

A v0.7 adiciona uma camada de integração com IA sem acoplar o runtime principal do CloudTask a um provedor de modelo específico.

O primeiro incremento é um **MCP Server independente**, responsável por expor operações do domínio CloudTask como ferramentas padronizadas do Model Context Protocol.

## Decisão de arquitetura

O backend principal permanece em Java 21 + Spring Boot 3.5.x. O novo `mcp-server` é um serviço separado, usando Java 21, Spring Boot 4.0.x e Spring AI 2.0.x.

Essa separação evita uma migração forçada do backend estável apenas para habilitar recursos de IA e cria uma fronteira clara entre:

- domínio e persistência do CloudTask;
- protocolo MCP;
- futuro cliente de IA/agente.

## Fluxo planejado

```text
AI Assistant / MCP Client
        |
        v
CloudTask MCP Server :8090
Streamable HTTP /mcp
        |
        v
CloudTask REST API :8080
JWT Bearer Token
        |
        v
PostgreSQL
```

No próximo incremento, um cliente de IA poderá consumir as ferramentas do MCP Server e executar operações do CloudTask de forma controlada.

## Ferramentas do primeiro incremento

| Tool | Tipo | Comportamento |
| --- | --- | --- |
| `list_tasks` | leitura | Lista tarefas e aceita filtro opcional por status |
| `create_task` | escrita | Cria tarefa com defaults seguros para status e prioridade |
| `update_task_status` | escrita idempotente | Atualiza somente o status e preserva os demais campos |
| `delete_task` | destrutiva | Remove uma tarefa existente |

As tools reutilizam a API REST já existente em `/api/v1/tasks`, preservando o escopo do usuário através de um JWT configurado por ambiente.

## Segurança

O endpoint HTTP do MCP não deve ser exposto publicamente sem autenticação/autorização própria.

Por padrão, o serviço escuta somente em `127.0.0.1`. O acesso ao backend exige `CLOUDTASK_API_TOKEN`; nenhum token é versionado no Git.

Antes de publicar o MCP Server na AWS, a v0.7 deverá adicionar uma fronteira de segurança específica para o endpoint `/mcp`.

## Configuração local

```powershell
$env:CLOUDTASK_API_URL="http://localhost:8080"
$env:CLOUDTASK_API_TOKEN="SEU_JWT_LOCAL"

cd mcp-server
mvn spring-boot:run
```

Endpoints locais:

- MCP: `http://127.0.0.1:8090/mcp`
- Health: `http://127.0.0.1:8090/actuator/health`

## Fases da v0.7

### Fase A — MCP Server

- [x] serviço independente
- [x] Streamable HTTP
- [x] tools CloudTask
- [x] integração via JWT com a REST API
- [x] testes unitários das tools
- [x] CI dedicado

### Fase B — Integração com IA

- [ ] MCP Client
- [ ] ChatClient / modelo configurável por ambiente
- [ ] execução de tools pelo modelo
- [ ] endpoint de chat para o frontend
- [ ] proteção contra operações destrutivas sem confirmação

### Fase C — Cloud

- [ ] container ECR do MCP Server
- [ ] ECS/Fargate
- [ ] roteamento dedicado no ALB ou endpoint privado
- [ ] autenticação/autorização do MCP remoto
- [ ] observabilidade e métricas
- [ ] pipeline de deploy

## Critérios para concluir a v0.7

A versão só será marcada como concluída quando um fluxo real de IA conseguir descobrir e executar tools do CloudTask via MCP, com testes, segurança mínima, CI e validação ponta a ponta.
