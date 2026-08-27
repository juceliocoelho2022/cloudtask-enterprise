variable "backend_repository_name" {
  description = "Nome do repositório ECR do backend."
  type        = string
}

variable "frontend_repository_name" {
  description = "Nome do repositório ECR do frontend."
  type        = string
}

variable "tags" {
  description = "Tags comuns aplicadas aos recursos."
  type        = map(string)
  default     = {}
}
