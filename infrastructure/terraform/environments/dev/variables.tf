variable "project_name" {
  description = "Nome base usado para identificar os recursos do CloudTask."
  type        = string
  default     = "cloudtask-enterprise"
}

variable "environment" {
  description = "Ambiente da infraestrutura."
  type        = string
  default     = "dev"
}

variable "aws_region" {
  description = "Região AWS usada pelo ambiente."
  type        = string
  default     = "sa-east-1"
}

variable "vpc_cidr" {
  description = "CIDR principal da VPC."
  type        = string
  default     = "10.20.0.0/16"
}

variable "availability_zone_count" {
  description = "Quantidade de Availability Zones utilizadas pela fundação de rede."
  type        = number
  default     = 2

  validation {
    condition     = var.availability_zone_count >= 2 && var.availability_zone_count <= 3
    error_message = "availability_zone_count deve estar entre 2 e 3."
  }
}

variable "backend_image_tag" {
  description = "Tag imutável da imagem do backend publicada no ECR."
  type        = string
  default     = "sha-5dc48f0"
}

variable "frontend_image_tag" {
  description = "Tag imutável da imagem do frontend publicada no ECR."
  type        = string
  default     = "sha-0e20d5b"
}

variable "database_name" {
  description = "Nome do banco PostgreSQL da aplicação."
  type        = string
  default     = "cloudtask"
}

variable "database_username" {
  description = "Usuário master do RDS. A senha é gerenciada pela AWS."
  type        = string
  default     = "cloudtask_admin"
}

variable "rds_engine_version" {
  description = "Major version do PostgreSQL no RDS."
  type        = string
  default     = "17"
}

variable "rds_instance_class" {
  description = "Classe da instância PostgreSQL no ambiente dev."
  type        = string
  default     = "db.t4g.micro"
}

variable "rds_allocated_storage" {
  description = "Armazenamento inicial do RDS em GiB."
  type        = number
  default     = 20
}

variable "ecs_desired_count" {
  description = "Número de tasks por serviço ECS no ambiente dev."
  type        = number
  default     = 1
}

variable "log_retention_days" {
  description = "Dias de retenção dos logs no CloudWatch."
  type        = number
  default     = 14
}

variable "tags" {
  description = "Tags adicionais aplicadas aos recursos AWS."
  type        = map(string)
  default     = {}
}
