variable "cluster_name" {
  description = "Name of the ECS cluster."
  type        = string
}

variable "vpc_id" {
  description = "VPC ID for ECS and load balancer resources."
  type        = string
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs for ECS tasks."
  type        = list(string)
}

variable "public_subnet_ids" {
  description = "List of public subnet IDs for load balancer."
  type        = list(string)
}

variable "namespace_id" {
  description = "Cloud Map namespace ID for service discovery."
  type        = string
}

variable "services" {
  description = "List of ECS services to deploy."

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
}

variable "region" {
  description = "AWS region (for logs, etc.)."
  type        = string
}

variable "tags" {
  description = "Common tags to apply."
  type        = map(string)
  default     = {}
}

