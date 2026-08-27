output "application_url" {
  description = "URL HTTP pública do CloudTask via ALB."
  value       = local.application_url
}

output "alb_dns_name" {
  description = "DNS público do Application Load Balancer."
  value       = aws_lb.this.dns_name
}

output "ecs_cluster_name" {
  description = "Nome do cluster ECS."
  value       = aws_ecs_cluster.this.name
}

output "frontend_service_name" {
  description = "Nome do serviço ECS frontend."
  value       = aws_ecs_service.frontend.name
}

output "backend_service_name" {
  description = "Nome do serviço ECS backend."
  value       = aws_ecs_service.backend.name
}

output "ecs_execution_role_arn" {
  description = "ARN da IAM execution role usada pelas tasks ECS."
  value       = aws_iam_role.ecs_execution.arn
}

output "ecs_task_role_arn" {
  description = "ARN da IAM task role usada pelas tasks ECS."
  value       = aws_iam_role.ecs_task.arn
}

output "rds_endpoint" {
  description = "Endpoint DNS do PostgreSQL RDS."
  value       = aws_db_instance.postgres.address
}

output "rds_secret_arn" {
  description = "ARN do segredo gerenciado pelo RDS com usuário e senha master."
  value       = local.rds_secret_arn
  sensitive   = true
}
