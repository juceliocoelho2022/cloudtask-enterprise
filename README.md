# CloudTask Enterprise

Plataforma cloud-native de gerenciamento de tarefas criada para portfólio profissional, com foco em arquitetura escalável, segurança, automação, qualidade, observabilidade e práticas de produção.

## Tecnologias / GitHub Topics

[**java**](https://github.com/topics/java "Topic: java") · [**spring-boot**](https://github.com/topics/spring-boot "Topic: spring-boot") · [**spring-security**](https://github.com/topics/spring-security "Topic: spring-security") · [**jwt**](https://github.com/topics/jwt "Topic: jwt") · [**rest-api**](https://github.com/topics/rest-api "Topic: rest-api") · [**react**](https://github.com/topics/react "Topic: react") · [**vite**](https://github.com/topics/vite "Topic: vite") · [**postgresql**](https://github.com/topics/postgresql "Topic: postgresql") · [**flyway**](https://github.com/topics/flyway "Topic: flyway") · [**docker**](https://github.com/topics/docker "Topic: docker") · [**docker-compose**](https://github.com/topics/docker-compose "Topic: docker-compose") · [**prometheus**](https://github.com/topics/prometheus "Topic: prometheus") · [**grafana**](https://github.com/topics/grafana "Topic: grafana") · [**micrometer**](https://github.com/topics/micrometer "Topic: micrometer") · [**junit5**](https://github.com/topics/junit5 "Topic: junit5") · [**mockito**](https://github.com/topics/mockito "Topic: mockito") · [**testcontainers**](https://github.com/topics/testcontainers "Topic: testcontainers") · [**jacoco**](https://github.com/topics/jacoco "Topic: jacoco") · [**github-actions**](https://github.com/topics/github-actions "Topic: github-actions") · [**maven**](https://github.com/topics/maven "Topic: maven") · [**nginx**](https://github.com/topics/nginx "Topic: nginx") · [**cloud-native**](https://github.com/topics/cloud-native "Topic: cloud-native") · [**terraform**](https://github.com/topics/terraform "Topic: terraform") · [**aws**](https://github.com/topics/aws "Topic: aws") · [**amazon-ecr**](https://github.com/topics/amazon-ecr "Topic: amazon-ecr") · [**amazon-ecs**](https://github.com/topics/amazon-ecs "Topic: amazon-ecs") · [**aws-fargate**](https://github.com/topics/aws-fargate "Topic: aws-fargate") · [**amazon-rds**](https://github.com/topics/amazon-rds "Topic: amazon-rds")

> A versão atual já possui runtime AWS implantado com Application Load Balancer, ECS/Fargate, PostgreSQL RDS, ECR, Secrets Manager, CloudWatch Logs e Terraform state remoto em S3.

## Tech Stack

### Backend

- **Java 21**
- **Spring Boot 3.5.x**
- **Spring Web / REST**
- **Spring Data JPA + Hibernate**
- **Spring Security + JWT**
- **Spring Security OAuth2 Client**
- **Google OAuth 2.0**
- **GitHub OAuth App**
- **Jakarta Validation**
- **Lombok**
- **Spring Boot Actuator**
- **Micrometer**

### Banco de Dados

- **PostgreSQL 17**
- **Flyway**
- **HikariCP**
- **Amazon RDS PostgreSQL** no ambiente AWS

### Frontend

- **React 19**
- **Vite**
- **JavaScript / JSX**
- **CSS3**
- **Nginx**

### API e Documentação

- **REST / JSON**
- **OpenAPI 3**
- **Swagger UI / Springdoc**
- **Postman**

### Qualidade e Testes

- **JUnit 5**
- **Mockito**
- **Testcontainers**
- **AssertJ**
- **JaCoCo**
- testes de serviços, tratamento de exceções e integração ponta a ponta com PostgreSQL real

### DevOps e Containers

- **Docker**
- **Docker Compose**
- **Git / GitHub**
- **GitHub Actions**
- imagens backend e frontend versionadas por Git SHA
- **Amazon ECR** com tags imutáveis

### Observabilidade

- **Spring Boot Actuator**
- **Micrometer Prometheus Registry**
- **Prometheus 3.14.0**
- **Grafana 13.2.0**
- **Amazon CloudWatch Logs** para as tasks ECS
- dashboard provisionado automaticamente no ambiente local
- métricas HTTP, JVM, CPU, HikariCP e uptime

### Cloud e Infraestrutura

- **Terraform** com módulos reutilizáveis e ambiente `dev`
- **AWS** na região `sa-east-1`
- **Amazon VPC** com duas subnets públicas e duas privadas em duas Availability Zones
- **Internet Gateway**, route tables e associações de rota
- **Application Load Balancer** com roteamento por path
- **Amazon ECS / Fargate** para frontend e backend
- **Amazon RDS PostgreSQL 17** privado
- **Security Groups** em camadas: ALB → frontend/backend → PostgreSQL
- **Amazon ECR** para backend e frontend
- **AWS Secrets Manager** para JWT e credencial master do RDS
- **CloudWatch Logs** para frontend e backend
- **Terraform remote state em Amazon S3** com locking nativo
- tags de imagem imutáveis, scan on push e lifecycle policy no ECR
- CI de Terraform com `fmt`, `init -backend=false` e `validate`

### Cloud — Próximas etapas

- **CI/CD completo** com build, push ECR e deploy ECS automatizado
- **AWS Certificate Manager / HTTPS**
- **Route 53** e domínio customizado
- hardening de rede e observabilidade para ambiente de produção
- ativação segura do OAuth social no domínio cloud

### IA — Roadmap

- **MCP Server** para integração controlada entre agentes de IA e recursos da plataforma

## Arquitetura atual

```text
Internet
   |
   v
Application Load Balancer :80
   |
   +-- /* ----------------------> Frontend React + Nginx
   |                              ECS / Fargate :80
   |
   +-- /api/* ------------------> Backend Spring Boot
   +-- /oauth2/* ---------------> ECS / Fargate :8080
   +-- /login/oauth2/* --------->
   +-- /actuator/* ------------->
                                      |
                                      v
                               PostgreSQL 17 / RDS
                               subnets privadas

Amazon ECR
├── backend image
└── frontend image

AWS Secrets Manager
├── JWT signing secret
└── RDS master credentials

CloudWatch Logs
├── /backend
└── /frontend

Terraform
└── remote state em S3 + lockfile
```

No ambiente `dev`, as tasks ECS usam subnets públicas com IP público para evitar NAT Gateway nesta fase. A entrada permanece restrita pelo Security Group do ALB. O RDS permanece privado.

## Demonstração visual

> Algumas imagens abaixo são mockups conceituais usados para demonstrar a evolução visual planejada do produto. Os fluxos funcionais já validados são autenticação JWT, login social com Google e GitHub, CRUD de tarefas, Swagger/OpenAPI, Prometheus, Grafana, testes automatizados e JaCoCo.

### Login

![CloudTask Login](docs/screenshots/login.png)

### Cadastro

![CloudTask Register](docs/screenshots/register.png)

### Dashboard

![CloudTask Dashboard](docs/screenshots/dashboard.png)

### Criar tarefa

![Criar tarefa](docs/screenshots/create-task.png)

### Swagger / OpenAPI

![Swagger](docs/screenshots/swagger.png)

### Prometheus

![Prometheus Targets](docs/screenshots/prometheus-targets.png)

### Grafana

![Grafana Dashboard](docs/screenshots/grafana-dashboard.png)

### Cobertura de testes

![JaCoCo Coverage](docs/screenshots/jacoco-coverage.png)

Para o passo a passo completo, consulte o [Guia Visual de Uso](docs/usage-guide.md).

## Funcionalidades v0.1

- cadastro de usuário
- login com JWT
- CRUD de tarefas
- filtro por status
- prioridades e prazo
- validação de dados
- tratamento global de exceções
- Swagger/OpenAPI
- Flyway
- Docker Compose
- CI de backend e frontend

## Qualidade v0.2

A v0.2 adiciona testes automatizados e cobertura de código:

- testes unitários de `AuthService`
- testes unitários de `TaskService`
- testes do `GlobalExceptionHandler`
- cenários 400, 401, 404, 409 e 500
- teste de integração com Spring Boot + Testcontainers + PostgreSQL 17
- fluxo integrado de cadastro, login JWT e CRUD de tarefas
- relatório JaCoCo gerado por `mvn clean verify`
- artifact JaCoCo publicado pelo GitHub Actions
- compatibilidade local do Testcontainers com Docker Desktop 29 via `docker-java.properties`

Para executar:

```bash
cd backend
mvn clean verify
```

> Testcontainers exige Docker disponível no ambiente.

## Observabilidade v0.3

A v0.3 adiciona uma stack de observabilidade local reproduzível:

- endpoint Prometheus em `/actuator/prometheus`
- coleta automática pelo Prometheus a cada 10 segundos
- datasource Prometheus provisionado automaticamente no Grafana
- dashboard `CloudTask Enterprise — Overview` provisionado por arquivo
- taxa de requisições
- taxa de erros HTTP 5xx
- latência HTTP p95
- heap da JVM
- CPU do processo
- conexões ativas HikariCP
- uptime da aplicação

## Infraestrutura AWS v0.4

A v0.4 transforma a fundação cloud do projeto em infraestrutura como código e a provisiona na AWS:

- Terraform organizado por ambiente e módulos reutilizáveis
- ambiente `dev` na região `sa-east-1`
- VPC `10.20.0.0/16`
- duas subnets públicas e duas privadas em duas Availability Zones
- Internet Gateway e roteamento público
- Security Groups com acesso em camadas
- ECR para backend e frontend com tags imutáveis, scan on push e lifecycle policy
- `.terraform.lock.hcl` versionado para reprodutibilidade dos providers
- state, `tfvars` e planos Terraform protegidos pelo `.gitignore`
- GitHub Actions executando validação da configuração Terraform
- `terraform plan` inicial validado com **27 recursos a criar**
- infraestrutura provisionada na AWS e validada posteriormente com **`No changes. Your infrastructure matches the configuration.`**
- nenhuma execução automática de `terraform apply` no CI

## Remote State v0.4.1

A v0.4.1 migra o Terraform state para um backend remoto S3:

- backend S3 configurado de forma parcial
- `backend.hcl` real mantido fora do Git
- state remoto versionado no bucket S3
- locking nativo com `use_lockfile = true`
- backup do state criado antes da migração
- migração validada sem recriação de recursos

## Runtime AWS v0.5

A v0.5 implanta o runtime da aplicação na AWS:

- build e push das imagens backend e frontend no ECR
- tags de imagem baseadas em Git SHA
- Application Load Balancer público
- roteamento por path para frontend e backend
- frontend React + Nginx em ECS/Fargate
- backend Java 21 + Spring Boot em ECS/Fargate
- PostgreSQL 17 em RDS privado
- Secrets Manager para JWT e credenciais do banco
- CloudWatch Logs para os dois serviços
- Security Groups específicos para ALB, frontend, backend e RDS
- tasks ECS com `desired_count = 1`

Validação real do deploy:

- frontend ECS: **ACTIVE**, `1/1` task em execução
- backend ECS: **ACTIVE**, `1/1` task em execução
- ALB respondeu **HTTP 200** no frontend
- `/actuator/health` respondeu **UP**
- componente `db` do health check respondeu **UP**, confirmando conectividade backend → RDS
- apply final concluído sem alterações ou destruições de recursos existentes

## Autenticação social

O CloudTask suporta login com **Google** e **GitHub** usando Spring Security OAuth2 Client.

Após a autenticação no provedor, o backend:

1. recebe o callback OAuth2;
2. localiza ou cria a conta do usuário;
3. emite um JWT próprio do CloudTask;
4. redireciona o navegador de volta ao frontend autenticado.

As credenciais OAuth ficam somente no `.env` local, que é ignorado pelo Git. Consulte o guia completo em [docs/oauth-login.md](docs/oauth-login.md).

No primeiro deploy AWS, o profile `oauth` permanece desativado até que os callbacks do Google e GitHub sejam configurados para o domínio cloud e os respectivos secrets sejam armazenados com segurança.

## Executar toda a plataforma localmente

Para autenticação social, copie `.env.example` para `.env` e preencha os Client IDs e Client Secrets reais antes de subir a stack.

```bash
docker compose up --build -d
```

Serviços:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`
- Métricas Prometheus da API: `http://localhost:8080/actuator/prometheus`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- PostgreSQL no host: `localhost:5433`
- PostgreSQL na rede Docker: `postgres:5432`

### Grafana local

```text
Usuário: admin
Senha: cloudtask
```

No Grafana, abra a pasta **CloudTask** e o dashboard **CloudTask Enterprise — Overview**.

Para gerar tráfego e alimentar os gráficos, use normalmente o frontend, Swagger ou Postman.

## Usuário de demonstração

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

- **v0.1 ✅** — JWT, CRUD, PostgreSQL, React e Docker
- **v0.2 ✅** — JUnit, Mockito, Testcontainers e JaCoCo
- **v0.3 ✅** — Micrometer, Prometheus e Grafana
- **Social Login ✅** — Google OAuth 2.0 + GitHub OAuth App + JWT próprio
- **v0.4 ✅** — Terraform + AWS VPC + subnets + Security Groups + ECR
- **v0.4.1 ✅** — remote Terraform state em S3 + locking nativo
- **v0.5 ✅** — build/push ECR + ECS/Fargate + RDS + ALB + Secrets Manager + CloudWatch Logs
- **v0.6** — CI/CD completo com deploy automatizado
- **v0.7** — MCP Server e integração com IA
- **v1.0** — segurança, resiliência, alertas e documentação final

## Segurança

O segredo JWT e a senha do Grafana definidos no `docker-compose.yml` são exclusivos do ambiente local de desenvolvimento.

Os Client Secrets de Google e GitHub ficam somente no arquivo `.env` local e não devem ser versionados. Se um secret for exposto, ele deve ser rotacionado no provedor.

No ambiente AWS, o JWT secret e as credenciais master do RDS são entregues às tasks por meio do AWS Secrets Manager. O RDS não é publicamente acessível e aceita conexões somente a partir do Security Group do backend.

Arquivos de state e planos Terraform não são versionados. O `.terraform.lock.hcl` é versionado para garantir consistência das versões e checksums dos providers. O state remoto fica no S3 com locking nativo.

O endpoint `/actuator/prometheus`, embora liberado para a stack local e roteado no ambiente `dev`, deve ser protegido por rede privada, autenticação ou política equivalente antes de um ambiente de produção público.
