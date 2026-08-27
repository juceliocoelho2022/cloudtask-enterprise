variable "name_prefix" {
  description = "Prefixo de nome para os recursos de rede."
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR da VPC."
  type        = string
}

variable "availability_zones" {
  description = "Availability Zones usadas pelas subnets."
  type        = list(string)
}

variable "public_subnet_cidrs" {
  description = "CIDRs das subnets públicas."
  type        = list(string)
}

variable "private_subnet_cidrs" {
  description = "CIDRs das subnets privadas."
  type        = list(string)
}

variable "tags" {
  description = "Tags comuns aplicadas aos recursos."
  type        = map(string)
  default     = {}
}
