# CloudTask Enterprise

Plataforma cloud-native de gerenciamento de tarefas criada para portfólio profissional.

## Stack

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Flyway
- OpenAPI / Swagger
- Actuator
- React 19 + Vite
- Docker / Docker Compose
- GitHub Actions

## Arquitetura inicial

```text
React
  |
  v
Spring Boot REST API
  |
  v
PostgreSQL
```

## Funcionalidades v0.1

- Cadastro de usuário
- Login com JWT
- CRUD de tarefas
- Filtro por status
- Validação de dados
- Tratamento global de exceções
- Health check
- Swagger/OpenAPI
- Migração de banco com Flyway
- Docker Compose
- Pipeline CI inicial

## Executar com Docker

```bash
docker compose up --build
```

Serviços:

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health
- PostgreSQL: localhost:5432

## Usuário de demonstração

Crie um usuário pela tela de registro ou via API:

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Jucelio",
  "email": "jucelio@example.com",
  "password": "Senha@123"
}
```

## Roadmap

- v0.2: testes de integração + cobertura
- v0.3: Prometheus + Grafana
- v0.4: Terraform
- v0.5: AWS ECR + ECS + RDS + ALB
- v0.6: CI/CD completo
- v0.7: MCP Server
- v1.0: observabilidade, segurança e documentação final

## Segurança

O segredo JWT do `docker-compose.yml` é apenas para desenvolvimento.
Em produção use AWS Secrets Manager / SSM Parameter Store.
