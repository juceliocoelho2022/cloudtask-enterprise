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
  description = "Security Group do ALB."
  value       = module.security_groups.alb_security_group_id
}

output "frontend_security_group_id" {
  description = "Security Group do frontend/ECS."
  value       = module.security_groups.frontend_security_group_id
}

output "backend_security_group_id" {
  description = "Security Group do backend/ECS."
  value       = module.security_groups.backend_security_group_id
}

output "rds_security_group_id" {
  description = "Security Group do PostgreSQL/RDS."
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

output "application_url" {
  description = "URL pública HTTP da aplicação via ALB."
  value       = module.runtime.application_url
}

output "alb_dns_name" {
  description = "DNS público do ALB."
  value       = module.runtime.alb_dns_name
}

output "ecs_cluster_name" {
  description = "Nome do cluster ECS/Fargate."
  value       = module.runtime.ecs_cluster_name
}

output "backend_service_name" {
  description = "Nome do serviço ECS backend."
  value       = module.runtime.backend_service_name
}

output "frontend_service_name" {
  description = "Nome do serviço ECS frontend."
  value       = module.runtime.frontend_service_name
}

output "rds_endpoint" {
  description = "Endpoint privado do PostgreSQL RDS."
  value       = module.runtime.rds_endpoint
}
