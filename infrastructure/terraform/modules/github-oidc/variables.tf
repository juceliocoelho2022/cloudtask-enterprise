variable "name_prefix" {
  description = "Prefixo usado nos recursos IAM do pipeline."
  type        = string
}

variable "aws_region" {
  description = "Região AWS do ambiente."
  type        = string
}

variable "github_repository" {
  description = "Repositório GitHub no formato owner/repository autorizado a assumir a role."
  type        = string
}

variable "github_branch" {
  description = "Branch autorizada a realizar deploy via OIDC."
  type        = string
  default     = "main"
}

variable "create_oidc_provider" {
  description = "Cria o provider OIDC do GitHub na conta AWS. Desative se ele já existir e informe existing_oidc_provider_arn."
  type        = bool
  default     = true
}

variable "existing_oidc_provider_arn" {
  description = "ARN de um provider OIDC do GitHub já existente na conta AWS."
  type        = string
  default     = null
  nullable    = true

  validation {
    condition     = var.create_oidc_provider || var.existing_oidc_provider_arn != null
    error_message = "existing_oidc_provider_arn deve ser informado quando create_oidc_provider = false."
  }
}

variable "ecr_repository_arns" {
  description = "ARNs dos repositórios ECR autorizados para push do pipeline."
  type        = set(string)
}

variable "ecs_cluster_name" {
  description = "Nome do cluster ECS que receberá os deploys."
  type        = string
}

variable "ecs_service_names" {
  description = "Nomes dos serviços ECS autorizados para deploy."
  type        = set(string)
}

variable "ecs_task_role_arns" {
  description = "IAM roles que o GitHub Actions pode repassar ao registrar novas task definitions."
  type        = set(string)
}

variable "tags" {
  description = "Tags aplicadas aos recursos IAM."
  type        = map(string)
  default     = {}
}
