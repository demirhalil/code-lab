resource "aws_service_discovery_private_dns_namespace" "this" {
  name        = var.namespace_name
  description = "Private DNS namespace for service discovery"
  vpc         = var.vpc_id

  tags = merge(var.tags, {
    Name = var.namespace_name
  })
}

output "namespace_id" {
  value = aws_service_discovery_private_dns_namespace.this.id
}

