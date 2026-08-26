# CloudTask Enterprise — Guia Visual de Uso

Este guia mostra o fluxo principal do projeto, desde o acesso à aplicação até a observabilidade e cobertura de testes.

> Nota: algumas telas desta pasta são mockups conceituais de interface usados para demonstrar a evolução visual planejada do produto. Os fluxos funcionais validados atualmente são autenticação JWT, CRUD de tarefas, Swagger/OpenAPI, Prometheus, Grafana, testes automatizados e JaCoCo.

## 1. Acessar a aplicação

Com a stack em execução, abra:

```text
http://localhost:5173
```

![Tela de login](screenshots/login.png)

Use uma conta já cadastrada ou crie uma nova.

## 2. Criar uma conta

A partir da tela de login, acesse o cadastro.

![Tela de cadastro](screenshots/register.png)

O backend atual registra usuário com nome, e-mail e senha por meio de:

```http
POST /api/v1/auth/register
```

## 3. Dashboard

Após autenticação, o usuário acessa a área principal de tarefas.

![Dashboard](screenshots/dashboard.png)

A aplicação atual permite visualizar tarefas e acompanhar o status do CRUD.

## 4. Criar uma tarefa

Use a ação de nova tarefa para informar título, descrição, status, prioridade e prazo.

![Criar tarefa](screenshots/create-task.png)

Endpoint principal:

```http
POST /api/v1/tasks
Authorization: Bearer <JWT>
```

## 5. Explorar a API com Swagger

Abra:

```text
http://localhost:8080/swagger-ui.html
```

![Swagger / OpenAPI](screenshots/swagger.png)

A documentação permite inspecionar os contratos REST e testar os endpoints disponíveis.

## 6. Validar o Prometheus

Abra:

```text
http://localhost:9090/targets
```

![Prometheus Targets](screenshots/prometheus-targets.png)

O target principal esperado é `cloudtask-api` com estado `UP`, coletando:

```text
http://backend:8080/actuator/prometheus
```

## 7. Abrir o Grafana

Abra:

```text
http://localhost:3000
```

Credenciais locais:

```text
Usuário: admin
Senha: cloudtask
```

Depois acesse:

```text
Dashboards → CloudTask → CloudTask Enterprise — Overview
```

![Dashboard Grafana](screenshots/grafana-dashboard.png)

O dashboard acompanha métricas HTTP, JVM, CPU, HikariCP, latência e uptime.

## 8. Executar testes e gerar cobertura

No backend:

```bash
cd backend
mvn clean verify
```

O projeto utiliza JUnit 5, Mockito, Testcontainers e JaCoCo.

Relatório local:

```text
backend/target/site/jacoco/index.html
```

![Cobertura JaCoCo](screenshots/jacoco-coverage.png)

## 9. Subir toda a stack

Na raiz do projeto:

```bash
docker compose up --build -d
```

Serviços locais:

| Serviço | Endereço |
|---|---|
| Frontend | `http://localhost:5173` |
| Backend | `http://localhost:8080` |
| Swagger | `http://localhost:8080/swagger-ui.html` |
| Actuator Health | `http://localhost:8080/actuator/health` |
| Métricas | `http://localhost:8080/actuator/prometheus` |
| Prometheus | `http://localhost:9090` |
| Grafana | `http://localhost:3000` |
| PostgreSQL | `localhost:5432` |

## Fluxo técnico

```text
Browser
  ↓
React + Nginx
  ↓ HTTP/JSON + JWT
Spring Boot REST API
  ↓ JPA/Hibernate
PostgreSQL

Spring Boot Actuator + Micrometer
  ↓
Prometheus
  ↓
Grafana
```

Voltar para o [README principal](../README.md).
