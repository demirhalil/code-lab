resource "aws_ecs_cluster" "this" {
  name = var.cluster_name

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = merge(var.tags, {
    Name = var.cluster_name
  })
}

resource "aws_iam_role" "task_execution" {
  name = "${var.cluster_name}-task-exec-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
    }]
  })

  tags = var.tags
}

resource "aws_iam_role_policy_attachment" "task_execution_policy" {
  role       = aws_iam_role.task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

locals {
  service_map = { for svc in var.services : svc.name => svc }
}

resource "aws_lb" "this" {
  count              = length(var.services) > 0 ? 1 : 0
  name               = "${var.cluster_name}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = []
  subnets            = var.public_subnet_ids

  tags = merge(var.tags, {
    Name = "${var.cluster_name}-alb"
  })
}

resource "aws_lb_target_group" "service" {
  for_each = local.service_map

  name        = "${each.key}-tg"
  port        = each.value.container_port
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = var.vpc_id

  health_check {
    path                = try(each.value.health_check_path, "/")
    healthy_threshold   = 2
    unhealthy_threshold = 2
    timeout             = 5
    interval            = 30
    matcher             = "200-399"
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

resource "aws_lb_listener" "http" {
  count             = length(var.services) > 0 ? 1 : 0
  load_balancer_arn = aws_lb.this[0].arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Not Found"
      status_code  = "404"
    }
  }
}

resource "aws_lb_listener_rule" "service" {
  for_each = local.service_map

  listener_arn = aws_lb_listener.http[0].arn
  priority     = 100 + index(keys(local.service_map), each.key)

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.service[each.key].arn
  }

  condition {
    path_pattern {
      values = ["/${each.key}/*"]
    }
  }
}

resource "aws_ecs_task_definition" "service" {
  for_each = local.service_map

  family                   = "${each.key}-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = each.value.cpu
  memory                   = each.value.memory
  execution_role_arn       = aws_iam_role.task_execution.arn

  container_definitions = jsonencode([
    {
      name      = each.key
      image     = each.value.image
      essential = true
      portMappings = [
        {
          containerPort = each.value.container_port
          hostPort      = each.value.host_port
          protocol      = "tcp"
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/${each.key}"
          awslogs-region        = var.region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])

  runtime_platform {
    operating_system_family = "LINUX"
    cpu_architecture        = "X86_64"
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

resource "aws_service_discovery_service" "service" {
  for_each = local.service_map

  name = each.key

  dns_config {
    namespace_id = var.namespace_id
    dns_records {
      type = "A"
      ttl  = 10
    }
    routing_policy = "WEIGHTED"
  }

  health_check_custom_config {
    failure_threshold = 1
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

resource "aws_ecs_service" "service" {
  for_each = local.service_map

  name            = each.key
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.service[each.key].arn
  desired_count   = each.value.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    assign_public_ip = each.value.assign_public_ip
    security_groups  = []
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.service[each.key].arn
    container_name   = each.key
    container_port   = each.value.container_port
  }

  service_registries {
    registry_arn = aws_service_discovery_service.service[each.key].arn
  }

  lifecycle {
    ignore_changes = [desired_count]
  }

  depends_on = [
    aws_lb_listener.http
  ]

  tags = merge(var.tags, {
    Service = each.key
  })
}

output "cluster_name" {
  value = aws_ecs_cluster.this.name
}

