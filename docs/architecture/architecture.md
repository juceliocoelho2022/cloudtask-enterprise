# Arquitetura v0.1

```text
[Browser]
    |
    v
[React + Nginx]
    |
    | HTTP/JSON + JWT
    v
[Spring Boot REST API]
    |
    | JPA
    v
[PostgreSQL]
```

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
