variable "aws_region" {
  type    = string
  default = "us-west-2"
}

variable "db_name" {
  type    = string
  default = "prueba_tecnica"
}

variable "db_username" {
  type = string
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "allowed_cidr" {
  type        = string
  description = "CIDR que puede acceder a 5432 (p.ej. TU.IP/32)"
}
