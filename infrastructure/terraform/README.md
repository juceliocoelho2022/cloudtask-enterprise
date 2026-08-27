# CloudTask Enterprise — Terraform

Infraestrutura como código do CloudTask Enterprise.

## Escopo atual

A fundação AWS do ambiente `dev` inclui:

- VPC dedicada
- 2 subnets públicas
- 2 subnets privadas
- Internet Gateway e rota pública
- Security Groups em camadas para ALB, frontend/ECS, backend/ECS e PostgreSQL/RDS
- repositórios ECR para backend e frontend
- backend remoto S3 para o Terraform state
- state locking nativo do backend S3
- validação automática com GitHub Actions

A v0.5 adiciona o runtime AWS ao código Terraform:

- Application Load Balancer público
- roteamento por path para frontend e backend
- ECS/Fargate para frontend Nginx e backend Spring Boot
- PostgreSQL RDS privado
- credenciais master do RDS gerenciadas pela AWS Secrets Manager
- JWT secret gerado e entregue ao backend via Secrets Manager
- CloudWatch Logs para os dois serviços ECS
- IAM execution role e task role dedicadas

> O código da v0.5 deve ser validado com `terraform fmt`, `terraform init`, `terraform validate` e `terraform plan` antes de qualquer `apply`.

## Arquitetura v0.5

```text
Internet
   |
   v
Application Load Balancer :80
   |
   +-- /* ----------------------> Frontend ECS/Fargate :80
   |
   +-- /api/* ------------------> Backend ECS/Fargate :8080
   +-- /oauth2/* ---------------> Backend ECS/Fargate :8080
   +-- /login/oauth2/* ---------> Backend ECS/Fargate :8080
   +-- /actuator/* -------------> Backend ECS/Fargate :8080
                                      |
                                      v
                               PostgreSQL RDS :5432
                               (subnets privadas)
```

No ambiente `dev`, as tasks ECS usam subnets públicas com `assign_public_ip = true`. Essa decisão evita um NAT Gateway durante a fase de portfólio e permite acesso ao ECR, CloudWatch e provedores OAuth. O Security Group continua restringindo entrada das tasks exclusivamente ao ALB. O RDS permanece privado.

## Estrutura

```text
infrastructure/terraform/
├── README.md
├── environments/
│   └── dev/
│       ├── backend.tf
│       ├── backend.hcl.example
│       ├── main.tf
│       ├── outputs.tf
│       ├── providers.tf
│       ├── terraform.tfvars.example
│       ├── variables.tf
│       └── versions.tf
└── modules/
    ├── ecr/
    ├── runtime/
    ├── security-groups/
    └── vpc/
```

## Pré-requisitos

- Terraform >= 1.9
- AWS CLI configurada
- Docker Desktop para build/push das imagens
- credenciais AWS com permissão para os recursos utilizados
- bucket S3 privado com versionamento habilitado para o remote state
- imagens backend e frontend publicadas nos repositórios ECR

Confirme sua identidade antes de qualquer `plan` ou `apply`:

```powershell
aws sts get-caller-identity
```

## Backend remoto S3

O arquivo versionado `backend.tf` declara um backend S3 com configuração parcial:

```hcl
terraform {
  backend "s3" {}
}
```

Copie o exemplo local e informe o nome do bucket criado para o ambiente:

```powershell
Copy-Item backend.hcl.example backend.hcl
```

Exemplo de configuração:

```hcl
bucket       = "SEU_BUCKET_TERRAFORM_STATE"
key          = "cloudtask-enterprise/dev/terraform.tfstate"
region       = "sa-east-1"
encrypt      = true
use_lockfile = true
```

O `backend.hcl` real é local e ignorado pelo Git. Não versione credenciais, state, arquivos de plano ou configurações privadas do backend.

Para inicializar um clone novo usando o backend remoto:

```powershell
terraform init -backend-config="backend.hcl"
```

## Imagens ECR

O runtime usa tags imutáveis para manter rastreabilidade entre Git e ECS. O ambiente `dev` recebe as tags por variáveis:

```hcl
backend_image_tag  = "sha-..."
frontend_image_tag = "sha-..."
```

Não use `latest` neste projeto.

## Validar localmente

```powershell
cd infrastructure\terraform
terraform fmt -recursive
terraform fmt -check -recursive

cd environments\dev
terraform init -backend-config="backend.hcl"
terraform validate
```

Ao adicionar um novo provider, o `terraform init` pode atualizar `.terraform.lock.hcl`. O lock file deve ser revisado e versionado; credenciais e state nunca devem ser versionados.

## Planejar

Crie seu arquivo local de variáveis a partir do exemplo quando necessário:

```powershell
Copy-Item terraform.tfvars.example terraform.tfvars
terraform plan
```

O arquivo `terraform.tfvars`, o state, o `backend.hcl` real e arquivos de plano são ignorados pelo Git.

Antes de aplicar, revise especialmente recursos com cobrança recorrente:

- Application Load Balancer
- ECS/Fargate
- RDS PostgreSQL
- endereços IPv4 públicos das tasks
- Secrets Manager
- CloudWatch Logs

## Aplicar

Somente aplique quando a conta, região, plano Terraform e impacto de custos estiverem revisados:

```powershell
terraform plan -out=tfplan
terraform apply tfplan
```

Para remover recursos criados em ambiente de estudo:

```powershell
terraform destroy
```

## Rede

O ambiente `dev` usa por padrão:

```text
VPC:              10.20.0.0/16
Subnets públicas: 2 x /24
Subnets privadas: 2 x /24
Região:           sa-east-1
```

As subnets privadas continuam sem NAT Gateway nesta etapa. O RDS fica nas subnets privadas. As tasks ECS ficam nas subnets públicas com IP público, mas aceitam tráfego de entrada somente do Security Group do ALB.

## Segurança

- ALB: entrada pública em 80/443; a v0.5 inicial cria listener HTTP em 80
- frontend: porta 80 acessível somente a partir do Security Group do ALB
- backend: porta 8080 acessível somente a partir do Security Group do ALB
- RDS: porta 5432 acessível somente a partir do Security Group do backend
- RDS não é publicamente acessível
- senha master do RDS é gerenciada pelo RDS/Secrets Manager
- JWT secret é entregue à task ECS via Secrets Manager
- ECR: tags imutáveis e scan de imagem no push
- bucket de state com bloqueio de acesso público e versionamento
- `backend.hcl`, `*.tfstate`, `*.tfstate.*` e `*.tfplan` não são versionados
- nenhum segredo AWS deve ser versionado

## OAuth no primeiro deploy AWS

O runtime inicial não ativa o profile `oauth`. Primeiro obtenha o DNS do ALB e valide login por e-mail/senha. Depois configure os callback URLs do Google e GitHub com o domínio AWS/custom domain e injete os Client IDs/Secrets pela AWS Secrets Manager antes de habilitar `SPRING_PROFILES_ACTIVE=oauth`.
