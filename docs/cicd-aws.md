# CloudTask Enterprise — CI/CD AWS v0.6

A v0.6 prepara deploy automatizado do CloudTask Enterprise no Amazon ECS usando GitHub Actions e autenticação OIDC, sem armazenar Access Key e Secret Access Key permanentes no GitHub.

## Fluxo

```text
push/merge em main
      |
      v
Quality Gates
├── Maven clean verify
└── React/Vite build
      |
      v
GitHub OIDC
      |
      v
AWS IAM Role temporária
      |
      v
Docker build
├── backend
└── frontend
      |
      v
Amazon ECR
      |
      v
Nova revisão das ECS Task Definitions
      |
      v
ECS rolling deployment
      |
      v
services-stable
      |
      v
ALB health validation
├── frontend HTTP 200
└── /actuator/health = UP + db UP
```

## Segurança

O workflow usa `permissions: id-token: write` somente para solicitar um token OIDC do GitHub. A IAM trust policy restringe o acesso ao repositório CloudTask Enterprise e à branch `main`.

Como o repositório usa o formato imutável atual de subject OIDC do GitHub, a trust policy inclui os IDs imutáveis do owner e do repositório, além dos nomes e da branch. Isso evita falhas de `sts:AssumeRoleWithWebIdentity` causadas pelo formato legado de `sub` e reduz risco em cenários de rename ou namespace reuse.

A role de deploy recebe somente as permissões necessárias para:

- autenticar e publicar imagens nos dois repositórios ECR do projeto;
- ler e atualizar os dois serviços ECS;
- registrar novas revisões das task definitions;
- executar `iam:PassRole` somente para as IAM roles das tasks do CloudTask;
- consultar o ALB para validar o endpoint após o deploy.

Nenhuma AWS Access Key permanente deve ser criada ou armazenada no GitHub.

## Estratégia de ownership Terraform x CI/CD

O Terraform continua responsável por VPC, Security Groups, ECR, ALB, RDS, IAM, ECS cluster, serviços e task definitions base.

O CI/CD passa a controlar as revisões de task definition usadas pelos serviços ECS durante cada deploy. Por isso os recursos `aws_ecs_service` ignoram drift apenas no atributo `task_definition`. As demais configurações continuam sob controle do Terraform.

## Primeira ativação

A configuração deve ser validada e aplicada manualmente uma única vez antes do primeiro deploy automatizado:

```powershell
cd C:\Projetos\cloudtask-enterprise\infrastructure\terraform
terraform fmt -recursive
terraform fmt -check -recursive

cd environments\dev
terraform init -backend-config="backend.hcl"
terraform validate
terraform plan
```

Revise o plano antes do `terraform apply`. A alteração esperada cria o provider OIDC do GitHub, uma IAM role e sua policy de deploy, além de adicionar o controle de drift das task definitions.

Se a conta AWS já possuir `token.actions.githubusercontent.com` como OIDC provider, não crie outro. Configure:

```hcl
create_github_oidc_provider       = false
existing_github_oidc_provider_arn = "ARN_DO_PROVIDER_EXISTENTE"
```

Depois do apply, obtenha:

```powershell
terraform output -raw github_actions_role_arn
```

No GitHub, crie a Repository Variable:

```text
AWS_ROLE_ARN=<valor do output acima>
```

O ARN da role não é uma senha, mas não é necessário colocá-lo diretamente no workflow.

## Deploy

O workflow está em:

```text
.github/workflows/deploy-aws.yml
```

Ele executa automaticamente em mudanças de backend, frontend ou no próprio workflow que chegam à `main`, e também permite execução manual por `workflow_dispatch`.

As imagens usam tags imutáveis no formato:

```text
sha-<git-short-sha>-run<attempt>
```

Isso permite reexecutar um workflow sem tentar sobrescrever uma tag imutável já existente no ECR.

## Validação pós-deploy

O pipeline só conclui com sucesso após:

1. os dois serviços ECS atingirem estado estável;
2. `runningCount == desiredCount` e `pendingCount == 0`;
3. o frontend responder pelo ALB;
4. `/actuator/health` retornar `UP`;
5. o componente PostgreSQL do health check retornar `UP`.

## Rollback

Cada deploy registra uma nova revisão de task definition. Em caso de falha, o ECS mantém histórico das revisões anteriores, permitindo apontar o serviço para uma revisão estável enquanto a causa é investigada.

A v0.6 não executa `terraform apply` automaticamente. Mudanças de infraestrutura continuam exigindo revisão explícita do plano Terraform.
