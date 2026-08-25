# CloudTask Enterprise

Plataforma cloud-native de gerenciamento de tarefas criada para portfólio profissional, com foco em arquitetura escalável, segurança, automação, observabilidade e práticas de produção.

## Tech Stack

### Backend

- **Java 21** — linguagem principal da API
- **Spring Boot 3.5.x** — base da aplicação backend
- **Spring Web** — construção da API REST
- **Spring Data JPA** — persistência e acesso a dados
- **Hibernate** — implementação ORM utilizada pelo JPA
- **Spring Security** — autenticação e autorização
- **JWT** — autenticação stateless baseada em token
- **Jakarta Validation** — validação dos dados de entrada
- **Lombok** — redução de código boilerplate

### Banco de Dados

- **PostgreSQL 17** — banco de dados relacional
- **Flyway** — versionamento e migração do schema

### Frontend

- **React 19** — construção da interface web
- **Vite** — build tool e ambiente de desenvolvimento frontend
- **JavaScript / JSX** — implementação dos componentes e regras da interface
- **CSS3** — estilização responsiva da aplicação
- **Nginx** — servidor web do frontend em container

### API e Documentação

- **REST / JSON** — padrão de comunicação entre frontend e backend
- **OpenAPI** — especificação da API
- **Swagger UI** — documentação e testes interativos dos endpoints
- **Postman** — collection para testes manuais da API

### DevOps e Containers

- **Docker** — containerização dos serviços
- **Docker Compose** — orquestração local de frontend, backend e PostgreSQL
- **Git** — controle de versão
- **GitHub** — hospedagem e colaboração do código
- **GitHub Actions** — pipelines de CI para backend e frontend

### Observabilidade

- **Spring Boot Actuator** — health checks, métricas e informações operacionais
- **Prometheus** — planejado para coleta de métricas
- **Grafana** — planejado para dashboards e visualização de métricas

### Cloud e Infraestrutura — Roadmap

- **AWS ECR** — registro de imagens Docker
- **AWS ECS** — execução dos containers
- **AWS RDS PostgreSQL** — banco de dados gerenciado
- **AWS Application Load Balancer** — balanceamento de carga e health checks
- **AWS Certificate Manager** — certificados TLS/HTTPS
- **AWS Route 53** — DNS
- **AWS Secrets Manager / SSM Parameter Store** — gerenciamento de segredos
- **Terraform** — Infrastructure as Code

### IA — Roadmap

- **MCP Server** — integração controlada entre agentes de IA e os recursos da plataforma

## Arquitetura inicial

```text
Browser
   |
   v
React + Nginx
   |
   | HTTP/JSON + JWT
   v
Spring Boot REST API
   |
   | JPA / Hibernate
   v
PostgreSQL
```

## Funcionalidades v0.1

- Cadastro de usuário
- Login com JWT
- CRUD de tarefas
- Filtro por status
- Prioridades de tarefas
- Validação de dados
- Tratamento global de exceções
- Health check
- Swagger/OpenAPI
- Migração de banco com Flyway
- Docker Compose
- Pipelines CI para backend e frontend

## Executar com Docker

```bash
docker compose up --build
```

Serviços:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`
- PostgreSQL: `localhost:5432`

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

- **v0.2** — JUnit, Mockito, Testcontainers e cobertura de testes
- **v0.3** — Prometheus + Grafana
- **v0.4** — Terraform
- **v0.5** — AWS ECR + ECS + RDS + ALB
- **v0.6** — CI/CD completo com deploy automatizado
- **v0.7** — MCP Server e integração com IA
- **v1.0** — observabilidade, segurança, resiliência e documentação final

## Segurança

O segredo JWT do `docker-compose.yml` é utilizado apenas para desenvolvimento.

Em produção, os segredos deverão ser armazenados em serviços como **AWS Secrets Manager** ou **AWS Systems Manager Parameter Store**, evitando credenciais sensíveis versionadas no repositório.
