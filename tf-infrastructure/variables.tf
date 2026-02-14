variable "aws_region" {
  description = "AWS region to deploy resources into."
  type        = string
  default     = "us-east-1"
}

variable "aws_access_key" {
  description = "AWS access key ID (prefer environment variables instead of hardcoding)."
  type        = string
  sensitive   = true
}

variable "aws_secret_key" {
  description = "AWS secret access key (prefer environment variables instead of hardcoding)."
  type        = string
  sensitive   = true
}

variable "vpc_cidr_block" {
  description = "CIDR block for the main VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "List of CIDR blocks for public subnets."
  type        = list(string)
  default     = ["10.0.1.0/24"]
}

variable "private_subnet_cidrs" {
  description = "List of CIDR blocks for private subnets."
  type        = list(string)
  default     = ["10.0.2.0/24"]
}

variable "cloudmap_namespace_name" {
  description = "Name of the AWS Cloud Map private DNS namespace."
  type        = string
  default     = "main.local"
}

variable "ecs_cluster_name" {
  description = "Name of the ECS cluster."
  type        = string
  default     = "main-ecs-cluster"
}

variable "ecs_services" {
  description = "List of ECS services to deploy in a generic, parameterized way."

  type = list(object({
    name              = string
    desired_count     = number
    cpu               = number
    memory            = number
    container_port    = number
    host_port         = number
    image             = string
    assign_public_ip  = bool
    health_check_path = optional(string, "/")
  }))

  default = []
}

variable "tags" {
  description = "Common tags to apply to all resources."
  type        = map(string)
  default = {
    Project = "code-lab"
    Env     = "dev"
  }
}

# 1. Fetch the authenticated account details
data "aws_caller_identity" "current" {}

# 2. Extract and print the Account ID as an output
output "deployed_to_account_id" {
  description = "The AWS Account ID where resources are being deployed"
  value       = data.aws_caller_identity.current.account_id
}

# 3. Optional: Print the ARN (shows if you are a User or a Role)
output "authenticated_user_arn" {
  value = data.aws_caller_identity.current.arn
}

