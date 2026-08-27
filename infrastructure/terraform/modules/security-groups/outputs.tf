output "alb_security_group_id" {
  description = "ID do Security Group do ALB."
  value       = aws_security_group.alb.id
}

output "backend_security_group_id" {
  description = "ID do Security Group do backend/ECS."
  value       = aws_security_group.backend.id
}

output "rds_security_group_id" {
  description = "ID do Security Group do PostgreSQL/RDS."
  value       = aws_security_group.rds.id
}
