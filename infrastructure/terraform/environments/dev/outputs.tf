output "vpc_id" {
  description = "ID da VPC do CloudTask."
  value       = module.vpc.vpc_id
}

output "public_subnet_ids" {
  description = "IDs das subnets públicas."
  value       = module.vpc.public_subnet_ids
}

output "private_subnet_ids" {
  description = "IDs das subnets privadas."
  value       = module.vpc.private_subnet_ids
}

output "alb_security_group_id" {
  description = "Security Group reservado para o ALB."
  value       = module.security_groups.alb_security_group_id
}

output "backend_security_group_id" {
  description = "Security Group reservado para o backend/ECS."
  value       = module.security_groups.backend_security_group_id
}

output "rds_security_group_id" {
  description = "Security Group reservado para PostgreSQL/RDS."
  value       = module.security_groups.rds_security_group_id
}

output "backend_ecr_repository_url" {
  description = "URL do repositório ECR do backend."
  value       = module.ecr.backend_repository_url
}

output "frontend_ecr_repository_url" {
  description = "URL do repositório ECR do frontend."
  value       = module.ecr.frontend_repository_url
}
