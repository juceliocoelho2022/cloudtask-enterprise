module "github_oidc" {
  source = "../../modules/github-oidc"

  name_prefix       = local.name_prefix
  aws_region        = var.aws_region
  github_repository = var.github_repository
  github_branch     = var.github_deploy_branch

  create_oidc_provider       = var.create_github_oidc_provider
  existing_oidc_provider_arn = var.existing_github_oidc_provider_arn

  ecr_repository_arns = [
    module.ecr.backend_repository_arn,
    module.ecr.frontend_repository_arn
  ]

  ecs_cluster_name = module.runtime.ecs_cluster_name
  ecs_service_names = [
    module.runtime.backend_service_name,
    module.runtime.frontend_service_name
  ]

  ecs_task_role_arns = [
    module.runtime.ecs_execution_role_arn,
    module.runtime.ecs_task_role_arn
  ]

  tags = local.common_tags
}
