# CloudTask Enterprise — Skills Matrix

Este documento traduz as decisões técnicas do projeto em competências profissionais verificáveis no repositório. O objetivo é facilitar a leitura por recrutadores, tech leads e engenheiros durante uma avaliação de portfólio.

## Engenharia de Software

| Competência | Como é demonstrada no projeto |
| --- | --- |
| Arquitetura em camadas | Separação entre controllers, services, repositories, DTOs, domínio e configuração |
| API REST | Endpoints de autenticação e CRUD de tarefas com JSON e validação |
| Tratamento de erros | `GlobalExceptionHandler` com respostas consistentes para cenários 400, 401, 404, 409 e 500 |
| Gestão de configuração | Variáveis de ambiente, profiles Spring e configuração por ambiente |
| Evolução incremental | Roadmap versionado de v0.1 a v1.0 com entregas técnicas independentes |
| Documentação técnica | README, OpenAPI/Swagger, Postman e documentação específica de OAuth, Terraform e CI/CD |

## Backend Java

| Competência | Tecnologias / evidências |
| --- | --- |
| Java moderno | Java 21 |
| Framework backend | Spring Boot 3.5.x |
| Persistência | Spring Data JPA, Hibernate, PostgreSQL 17 |
| Migrações | Flyway |
| Pool de conexões | HikariCP |
| Segurança | Spring Security, JWT próprio da aplicação |
| OAuth2 | Spring Security OAuth2 Client, Google OAuth 2.0 e GitHub OAuth App |
| Validação | Jakarta Validation |
| Observabilidade | Spring Boot Actuator + Micrometer |

## Testes e Qualidade

| Competência | Tecnologias / evidências |
| --- | --- |
| Testes unitários | JUnit 5, Mockito, AssertJ |
| Testes de integração | Spring Boot + Testcontainers + PostgreSQL real |
| Cobertura | JaCoCo |
| Integração contínua | GitHub Actions executando build e testes |
| Reprodutibilidade | Docker, lock files e infraestrutura como código |

## Frontend

| Competência | Tecnologias / evidências |
| --- | --- |
| SPA | React 19 |
| Tooling | Vite |
| Integração REST | Cliente HTTP consumindo a API Spring Boot |
| Autenticação | Fluxos JWT e callback OAuth2 |
| Containerização | Build multi-stage e Nginx |
| Deploy cloud | Frontend executando em ECS/Fargate atrás do ALB |

## Cloud AWS

| Competência | Serviços / práticas |
| --- | --- |
| Networking | VPC, subnets públicas/privadas, route tables e Internet Gateway |
| Segurança de rede | Security Groups em camadas: ALB → frontend/backend → RDS |
| Containers | Amazon ECR + Amazon ECS/Fargate |
| Banco gerenciado | Amazon RDS PostgreSQL 17 privado |
| Load balancing | Application Load Balancer com roteamento por path |
| Secrets | AWS Secrets Manager |
| Logging | Amazon CloudWatch Logs |
| Estado Terraform | Amazon S3 com versionamento e locking nativo |
| Identidade CI/CD | GitHub OIDC para credenciais temporárias na AWS |

## DevOps e CI/CD

| Competência | Como é demonstrada |
| --- | --- |
| Docker | Imagens separadas para backend e frontend |
| Image tagging | Tags imutáveis baseadas em Git SHA |
| Registry | Push para Amazon ECR |
| CI | Quality gates para backend, frontend e Terraform |
| CD | Pipeline preparado para rolling deploy no ECS |
| Segurança do pipeline | OIDC em vez de Access Keys permanentes |
| IaC | Terraform modular por ambiente |
| Validação | `terraform fmt`, `init`, `validate`, `plan` e state remoto |

## Observabilidade

| Competência | Tecnologias / evidências |
| --- | --- |
| Health checks | `/actuator/health` integrado ao ALB |
| Métricas | Micrometer + Prometheus |
| Dashboards | Grafana |
| Logs cloud | CloudWatch Logs |
| Sinais de runtime | JVM, HTTP, CPU, HikariCP e uptime |

## Palavras-chave profissionais

Java 21 · Spring Boot · Spring Security · JWT · OAuth2 · REST API · Spring Data JPA · Hibernate · PostgreSQL · Flyway · HikariCP · React · Vite · JavaScript · Nginx · Docker · Docker Compose · JUnit 5 · Mockito · Testcontainers · JaCoCo · OpenAPI · Swagger · Postman · Maven · Git · GitHub · GitHub Actions · CI/CD · Cloud Native · AWS · VPC · ECS · Fargate · ECR · RDS · Application Load Balancer · Secrets Manager · CloudWatch · Terraform · Infrastructure as Code · S3 Remote State · OIDC · Prometheus · Grafana · Micrometer · Observability · DevOps · Backend Development · Full Stack Development · Software Engineering

## Leitura recomendada para avaliação técnica

1. `README.md` — visão geral, arquitetura, stack e roadmap.
2. `backend/` — API, segurança, persistência e testes.
3. `frontend/` — SPA React e integração com autenticação/API.
4. `infrastructure/terraform/` — infraestrutura AWS modular.
5. `.github/workflows/` — CI e evolução para CD automatizado.
6. `docs/cicd-aws.md` — desenho do pipeline AWS com OIDC.
7. `docs/oauth-login.md` — autenticação social.
8. `observability/` — Prometheus e Grafana.
