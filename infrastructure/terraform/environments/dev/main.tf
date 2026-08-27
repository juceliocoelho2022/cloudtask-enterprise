data "aws_availability_zones" "available" {
  state = "available"
}

locals {
  availability_zones = slice(
    data.aws_availability_zones.available.names,
    0,
    var.availability_zone_count
  )

  public_subnet_cidrs = [
    for index in range(var.availability_zone_count) :
    cidrsubnet(var.vpc_cidr, 8, index)
  ]

  private_subnet_cidrs = [
    for index in range(var.availability_zone_count) :
    cidrsubnet(var.vpc_cidr, 8, index + 10)
  ]
}

module "vpc" {
  source = "../../modules/vpc"

  name_prefix          = local.name_prefix
  vpc_cidr             = var.vpc_cidr
  availability_zones   = local.availability_zones
  public_subnet_cidrs  = local.public_subnet_cidrs
  private_subnet_cidrs = local.private_subnet_cidrs
  tags                 = local.common_tags
}

module "security_groups" {
  source = "../../modules/security-groups"

  name_prefix = local.name_prefix
  vpc_id      = module.vpc.vpc_id
  tags        = local.common_tags
}

module "ecr" {
  source = "../../modules/ecr"

  backend_repository_name  = "${local.name_prefix}-backend"
  frontend_repository_name = "${local.name_prefix}-frontend"
  tags                     = local.common_tags
}

module "runtime" {
  source = "../../modules/runtime"

  name_prefix = local.name_prefix
  aws_region  = var.aws_region
  vpc_id      = module.vpc.vpc_id

  public_subnet_ids  = module.vpc.public_subnet_ids
  private_subnet_ids = module.vpc.private_subnet_ids

  alb_security_group_id      = module.security_groups.alb_security_group_id
  frontend_security_group_id = module.security_groups.frontend_security_group_id
  backend_security_group_id  = module.security_groups.backend_security_group_id
  rds_security_group_id      = module.security_groups.rds_security_group_id

  backend_repository_url  = module.ecr.backend_repository_url
  frontend_repository_url = module.ecr.frontend_repository_url
  backend_image_tag        = var.backend_image_tag
  frontend_image_tag       = var.frontend_image_tag

  database_name        = var.database_name
  database_username    = var.database_username
  rds_engine_version   = var.rds_engine_version
  rds_instance_class   = var.rds_instance_class
  rds_allocated_storage = var.rds_allocated_storage

  desired_count      = var.ecs_desired_count
  log_retention_days = var.log_retention_days

  tags = local.common_tags
}
