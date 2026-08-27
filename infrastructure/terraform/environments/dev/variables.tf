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

variable "tags" {
  description = "Tags adicionais aplicadas aos recursos AWS."
  type        = map(string)
  default     = {}
}
