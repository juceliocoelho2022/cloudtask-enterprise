# CloudTask Enterprise — Terraform

Infraestrutura como código da v0.4 do CloudTask Enterprise.

## Escopo atual

Esta etapa cria a fundação AWS reutilizável para o ambiente `dev`:

- VPC dedicada
- 2 subnets públicas
- 2 subnets privadas
- Internet Gateway e rota pública
- Security Groups para ALB, backend/ECS e PostgreSQL/RDS
- repositórios ECR para backend e frontend
- validação automática com GitHub Actions

> A v0.4 não cria ECS, RDS ou ALB ainda. Esses recursos entram na v0.5 para evitar misturar fundação de rede com workloads e serviços que podem gerar custo.

## Estrutura

```text
infrastructure/terraform/
├── README.md
├── environments/
│   └── dev/
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

Confirme sua identidade antes de qualquer `plan` ou `apply`:

```powershell
aws sts get-caller-identity
```

## Validar localmente

```powershell
cd infrastructure\terraform
terraform fmt -check -recursive

cd environments\dev
terraform init
terraform validate
```

## Planejar

Crie seu arquivo local de variáveis a partir do exemplo:

```powershell
Copy-Item terraform.tfvars.example terraform.tfvars
terraform plan -out=tfplan
```

O arquivo `terraform.tfvars`, o state e arquivos de plano são ignorados pelo Git.

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
VPC:             10.20.0.0/16
Subnets públicas: 2 x /24
Subnets privadas: 2 x /24
Região:          sa-east-1
```

As subnets privadas não possuem NAT Gateway nesta etapa. Isso evita custo recorrente enquanto ainda não existem workloads privados. A conectividade necessária para ECS será definida na v0.5.

## Segurança

- ALB: entrada pública em 80/443
- backend: porta 8080 acessível somente a partir do Security Group do ALB
- RDS: porta 5432 acessível somente a partir do Security Group do backend
- ECR: tags imutáveis e scan de imagem no push
- nenhum segredo AWS deve ser versionado

## Estado remoto

Nesta primeira etapa o backend remoto do Terraform ainda não é criado. Antes de uso compartilhado/produção, o state deve migrar para armazenamento remoto seguro e com locking.
