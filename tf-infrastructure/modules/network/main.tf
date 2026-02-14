resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr_block
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = merge(var.tags, {
    Name = "main-vpc"
  })
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = merge(var.tags, {
    Name = "main-vpc-igw"
  })
}

resource "aws_subnet" "public" {
  for_each = { for idx, cidr in var.public_subnet_cidrs : idx => cidr }

  vpc_id                  = aws_vpc.main.id
  cidr_block              = each.value
  map_public_ip_on_launch = true

  tags = merge(var.tags, {
    Name = "main-public-${each.key}"
    Tier = "public"
  })
}

resource "aws_subnet" "private" {
  for_each = { for idx, cidr in var.private_subnet_cidrs : idx => cidr }

  vpc_id     = aws_vpc.main.id
  cidr_block = each.value

  tags = merge(var.tags, {
    Name = "main-private-${each.key}"
    Tier = "private"
  })
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  tags = merge(var.tags, {
    Name = "main-public-rt"
  })
}

resource "aws_route" "public_internet" {
  route_table_id         = aws_route_table.public.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.igw.id
}

resource "aws_route_table_association" "public_assoc" {
  for_each = aws_subnet.public

  subnet_id      = each.value.id
  route_table_id = aws_route_table.public.id
}

output "vpc_id" {
  value = aws_vpc.main.id
}

output "public_subnet_ids" {
  value = [for s in aws_subnet.public : s.id]
}

output "private_subnet_ids" {
  value = [for s in aws_subnet.private : s.id]
}

