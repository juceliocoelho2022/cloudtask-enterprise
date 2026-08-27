variable "name_prefix" {
  description = "Prefixo de nome para os Security Groups."
  type        = string
}

variable "vpc_id" {
  description = "ID da VPC onde os Security Groups serão criados."
  type        = string
}

variable "tags" {
  description = "Tags comuns aplicadas aos recursos."
  type        = map(string)
  default     = {}
}
