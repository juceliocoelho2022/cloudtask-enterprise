# Arquitetura CloudTask Enterprise

## v0.3 — Aplicação + Observabilidade

```text
[Browser]
    |
    v
[React + Nginx :5173]
    |
    | HTTP/JSON + JWT
    v
[Spring Boot REST API :8080]
    |
    | JPA / Hibernate
    v
[PostgreSQL 17 :5432]

[Spring Boot Actuator + Micrometer]
    |
    | /actuator/prometheus
    v
[Prometheus :9090]
    |
    | datasource provisionado
    v
[Grafana :3000]
```

### Métricas observadas

- taxa de requisições HTTP
- taxa de respostas 5xx
- latência HTTP p95
- heap da JVM
- uso de CPU do processo
- conexões ativas HikariCP/PostgreSQL
- uptime da aplicação

## Próxima evolução AWS

```text
Route 53
  |
ACM / HTTPS
  |
ALB
  |
ECS Service
  |
ECS Tasks
  |
RDS PostgreSQL
```

A observabilidade local de Prometheus e Grafana servirá como base para a futura estratégia de métricas e alertas no ambiente cloud.
