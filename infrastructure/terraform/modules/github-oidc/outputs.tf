output "role_arn" {
  description = "ARN da role assumida pelo GitHub Actions via OIDC."
  value       = aws_iam_role.github_actions_deploy.arn
}

output "oidc_provider_arn" {
  description = "ARN do provider OIDC usado pelo GitHub Actions."
  value       = local.oidc_provider_arn
}
