# Terraform AWS Infrastructure (VPC + Cloud Map + ECS)

This folder contains a reusable Terraform setup for:

- **AWS VPC** (`main-vpc`) with **one public** and **one private** subnet (configurable).
- **AWS Cloud Map** private DNS namespace for **service discovery**.
- **AWS ECS (Fargate)** cluster and **generic ECS services** that you can parameterize per application.

> **IMPORTANT – Credentials:**  
> Do **not** commit AWS access keys or secret keys to git. Use environment variables or a secure secrets store instead.

## Structure

- `main.tf` – root module wiring submodules together.
- `variables.tf` – input variables (region, VPC CIDRs, ECS services, etc.).
- `modules/network` – VPC, subnets, route tables, internet gateway.
- `modules/service_discovery` – AWS Cloud Map private DNS namespace.
- `modules/ecs` – ECS cluster, ALB, generic ECS services, Cloud Map services.

## Providing AWS credentials safely

Recommended options:

- **Environment variables**:

  ```bash
  export AWS_ACCESS_KEY_ID="AKIAYE..."
  export AWS_SECRET_ACCESS_KEY="yk68..."
  ```

- Or pass via Terraform variables (for local-only testing) **without committing them**:

  ```bash
  export TF_VAR_aws_access_key="AKIAYE..."
  export TF_VAR_aws_secret_key="yk68..."
  ```

Then Terraform picks them up via `var.aws_access_key` and `var.aws_secret_key`.

## Example: defining services

In a `terraform.tfvars` file (not committed if it contains secrets), you can declare multiple services:

```hcl
aws_region = "eu-central-1"

ecs_services = [
  {
    name              = "service-a"
    desired_count     = 2
    cpu               = 256
    memory            = 512
    container_port    = 8080
    host_port         = 8080
    image             = "123456789012.dkr.ecr.eu-central-1.amazonaws.com/service-a:latest"
    assign_public_ip  = false
    health_check_path = "/health"
  },
  {
    name              = "service-b"
    desired_count     = 1
    cpu               = 256
    memory            = 512
    container_port    = 8080
    host_port         = 8080
    image             = "123456789012.dkr.ecr.eu-central-1.amazonaws.com/service-b:latest"
    assign_public_ip  = false
    health_check_path = "/health"
  }
]
```

Each new service is just another object in the `ecs_services` list.

## Basic usage

From the `tf-infrastructure` directory:

```bash
terraform init
terraform plan
terraform apply
```

This will:

- Create `main-vpc` with one public and one private subnet (by default).
- Create a Cloud Map private DNS namespace (default: `main.local`).
- Create an ECS cluster and one ECS service per entry in `ecs_services`, with ALB + path-based routing and Cloud Map service discovery.

