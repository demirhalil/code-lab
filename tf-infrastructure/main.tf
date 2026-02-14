terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
}

module "network" {
  source = "./modules/network"

  vpc_cidr_block = var.vpc_cidr_block

  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs

  tags = var.tags
}

module "service_discovery" {
  source = "./modules/service_discovery"

  namespace_name = var.cloudmap_namespace_name
  vpc_id         = module.network.vpc_id

  tags = var.tags
}

# module "ecs" {
#   source = "./modules/ecs"
#
#   vpc_id             = module.network.vpc_id
#   private_subnet_ids = module.network.private_subnet_ids
#   public_subnet_ids  = module.network.public_subnet_ids
#   namespace_id = module.service_discovery.namespace_id
#   cluster_name = var.ecs_cluster_name
#
#   # Generic service definition list – you can add entries here for more services.
#   services = var.ecs_services
#
#   region = var.aws_region
#   tags = var.tags
# }

output "vpc_id" {
  value = module.network.vpc_id
}

output "public_subnet_ids" {
  value = module.network.public_subnet_ids
}

output "private_subnet_ids" {
  value = module.network.private_subnet_ids
}
#
# output "ecs_cluster_name" {
#   value = module.ecs.cluster_name
# }

output "cloudmap_namespace_id" {
  value = module.service_discovery.namespace_id
}


