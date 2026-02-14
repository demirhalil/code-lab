variable "namespace_name" {
  description = "Name of the Cloud Map private DNS namespace."
  type        = string
}

variable "vpc_id" {
  description = "VPC ID to which the namespace will be associated."
  type        = string
}

variable "tags" {
  description = "Common tags to apply."
  type        = map(string)
  default     = {}
}

