locals {
  name_prefix = "${var.project_name}-${var.environment}"

  common_tags = merge(
    {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "Terraform"
      Repository  = "cloudtask-enterprise"
    },
    var.tags
  )
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = local.common_tags
  }
}
