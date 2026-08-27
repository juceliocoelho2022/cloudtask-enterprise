variable "github_repository" {
  description = "Repositório GitHub autorizado a assumir a role de deploy via OIDC."
  type        = string
  default     = "juceliocoelho2022/cloudtask-enterprise"
}

variable "github_owner_id" {
  description = "ID imutável do owner do repositório GitHub usado no subject OIDC."
  type        = string
  default     = "104524218"
}

variable "github_repository_id" {
  description = "ID imutável do repositório GitHub usado no subject OIDC."
  type        = string
  default     = "1346717256"
}

variable "github_deploy_branch" {
  description = "Branch autorizada a realizar deploy no ambiente dev."
  type        = string
  default     = "main"
}

variable "create_github_oidc_provider" {
  description = "Cria o provider OIDC do GitHub na conta AWS. Defina false se já existir um provider compartilhado."
  type        = bool
  default     = true
}

variable "existing_github_oidc_provider_arn" {
  description = "ARN do provider OIDC do GitHub existente quando create_github_oidc_provider = false."
  type        = string
  default     = null
  nullable    = true
}
