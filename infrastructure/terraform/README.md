# CloudTask Enterprise — Terraform

Infraestrutura como código do CloudTask Enterprise.

## Escopo atual

A fundação AWS do ambiente `dev` inclui:

- VPC dedicada
- 2 subnets públicas
- 2 subnets privadas
- Internet Gateway e rota pública
- Security Groups para ALB, backend/ECS e PostgreSQL/RDS
- repositórios ECR para backend e frontend
- backend remoto S3 para o Terraform state
- state locking nativo do backend S3
- validação automática com GitHub Actions

> A v0.4 cria a fundação de rede, segurança e registry. A v0.4.1 adiciona o state remoto. ECS, RDS e ALB entram na v0.5 para evitar misturar fundação de rede com workloads e serviços que podem gerar custo recorrente.

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
    ├── security-groups/
    └── vpc/
```

## Pré-requisitos

- Terraform >= 1.9
- AWS CLI configurada
- credenciais AWS com permissão para os recursos utilizados
- bucket S3 privado com versionamento habilitado para o remote state

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

A migração inicial do state local para S3 foi executada manualmente com:

```powershell
terraform init -migrate-state -backend-config="backend.hcl"
```

Após a migração, `terraform plan` deve permanecer sem alterações quando a infraestrutura real estiver consistente com o código.

## Validar localmente

```powershell
cd infrastructure\terraform
terraform fmt -check -recursive

cd environments\dev
terraform init -backend-config="backend.hcl"
terraform validate
```

## Planejar

Crie seu arquivo local de variáveis a partir do exemplo:

```powershell
Copy-Item terraform.tfvars.example terraform.tfvars
terraform plan -out=tfplan
```

O arquivo `terraform.tfvars`, o state, o `backend.hcl` real e arquivos de plano são ignorados pelo Git.

## Aplicar

Somente aplique quando a conta, região e impacto de custos estiverem revisados:

```powershell
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

As subnets privadas não possuem NAT Gateway nesta etapa. Isso evita custo recorrente enquanto ainda não existem workloads privados. A conectividade necessária para ECS será definida na v0.5.

## Segurança

- ALB: entrada pública em 80/443
- backend: porta 8080 acessível somente a partir do Security Group do ALB
- RDS: porta 5432 acessível somente a partir do Security Group do backend
- ECR: tags imutáveis e scan de imagem no push
- bucket de state com bloqueio de acesso público e versionamento
- `backend.hcl`, `*.tfstate`, `*.tfstate.*` e `*.tfplan` não são versionados
- nenhum segredo AWS deve ser versionado
