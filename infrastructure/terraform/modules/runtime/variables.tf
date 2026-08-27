variable "name_prefix" {
  description = "Prefixo de nome dos recursos de runtime."
  type        = string
}

variable "aws_region" {
  description = "Região AWS do runtime."
  type        = string
}

variable "vpc_id" {
  description = "VPC onde ALB, ECS e RDS serão provisionados."
  type        = string
}

variable "public_subnet_ids" {
  description = "Subnets públicas usadas pelo ALB e pelos serviços ECS no ambiente dev."
  type        = list(string)
}

variable "private_subnet_ids" {
  description = "Subnets privadas usadas pelo RDS."
  type        = list(string)
}

variable "alb_security_group_id" {
  description = "Security Group do Application Load Balancer."
  type        = string
}

variable "frontend_security_group_id" {
  description = "Security Group do serviço ECS frontend."
  type        = string
}

variable "backend_security_group_id" {
  description = "Security Group do serviço ECS backend."
  type        = string
}

variable "rds_security_group_id" {
  description = "Security Group do PostgreSQL RDS."
  type        = string
}

variable "backend_repository_url" {
  description = "URL do repositório ECR do backend."
  type        = string
}

variable "frontend_repository_url" {
  description = "URL do repositório ECR do frontend."
  type        = string
}

variable "backend_image_tag" {
  description = "Tag imutável da imagem do backend no ECR."
  type        = string
}

variable "frontend_image_tag" {
  description = "Tag imutável da imagem do frontend no ECR."
  type        = string
}

variable "database_name" {
  description = "Nome lógico do banco PostgreSQL."
  type        = string
  default     = "cloudtask"
}

variable "database_username" {
  description = "Usuário master do RDS. A senha é gerenciada pelo RDS/Secrets Manager."
  type        = string
  default     = "cloudtask_admin"
}

variable "rds_engine_version" {
  description = "Major version do PostgreSQL no RDS."
  type        = string
  default     = "17"
}

variable "rds_instance_class" {
  description = "Classe da instância RDS do ambiente dev."
  type        = string
  default     = "db.t4g.micro"
}

variable "rds_allocated_storage" {
  description = "Armazenamento inicial do RDS em GiB."
  type        = number
  default     = 20
}

variable "backend_cpu" {
  description = "CPU Fargate do backend em unidades ECS."
  type        = number
  default     = 512
}

variable "backend_memory" {
  description = "Memória Fargate do backend em MiB."
  type        = number
  default     = 1024
}

variable "frontend_cpu" {
  description = "CPU Fargate do frontend em unidades ECS."
  type        = number
  default     = 256
}

variable "frontend_memory" {
  description = "Memória Fargate do frontend em MiB."
  type        = number
  default     = 512
}

variable "desired_count" {
  description = "Quantidade de tasks por serviço ECS no ambiente dev."
  type        = number
  default     = 1
}

variable "log_retention_days" {
  description = "Retenção dos logs ECS no CloudWatch."
  type        = number
  default     = 14
}

variable "tags" {
  description = "Tags comuns aplicadas aos recursos de runtime."
  type        = map(string)
  default     = {}
}
