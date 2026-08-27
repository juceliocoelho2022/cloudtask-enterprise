output "github_actions_role_arn" {
  description = "ARN da IAM role usada pelo GitHub Actions para publicar imagens e atualizar os serviços ECS."
  value       = module.github_oidc.role_arn
}
