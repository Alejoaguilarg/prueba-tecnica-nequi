# VPC por defecto
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default_subnets" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Subnet group para RDS
resource "aws_db_subnet_group" "rds" {
  name       = "franchises-rds-subnets"
  subnet_ids = data.aws_subnets.default_subnets.ids
}

# SG: permite 5432 solo desde tu CIDR
resource "aws_security_group" "rds_sg" {
  name        = "franchises-rds-sg"
  description = "Allow Postgres from allowed CIDR"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [var.allowed_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Instancia RDS (demo)
resource "aws_db_instance" "postgres" {
  identifier              = "franchises-postgres"
  engine                  = "postgres"
  engine_version          = "16.3"
  instance_class          = "db.t4g.micro"
  allocated_storage       = 20

  db_name                 = var.db_name
  username                = var.db_username
  password                = var.db_password

  publicly_accessible     = true              # demo; en prod -> false
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  db_subnet_group_name    = aws_db_subnet_group.rds.name

  skip_final_snapshot     = true
  deletion_protection     = false
  backup_retention_period = 0
}
