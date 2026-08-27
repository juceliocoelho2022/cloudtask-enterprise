output "backend_repository_url" {
  description = "URL do repositório ECR do backend."
  value       = aws_ecr_repository.this["backend"].repository_url
}

output "frontend_repository_url" {
  description = "URL do repositório ECR do frontend."
  value       = aws_ecr_repository.this["frontend"].repository_url
}

output "backend_repository_arn" {
  description = "ARN do repositório ECR do backend."
  value       = aws_ecr_repository.this["backend"].arn
}

output "frontend_repository_arn" {
  description = "ARN do repositório ECR do frontend."
  value       = aws_ecr_repository.this["frontend"].arn
}
