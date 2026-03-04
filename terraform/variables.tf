# =============================================
# Input Variables for Terraform Configuration
# These variables allow customisation of the infrastructure
# without modifying the main configuration files.
# =============================================

variable "aws_region" {
  description = "AWS region where resources will be provisioned"
  type        = string
  default     = "eu-west-1"
}

variable "instance_type" {
  description = "EC2 instance type for the backend server"
  type        = string
  default     = "t3.micro"
}

variable "ami_id" {
  description = "Amazon Machine Image ID for the EC2 instance (Ubuntu 22.04 LTS)"
  type        = string
  default     = "ami-0c1c30571d2dae5c9"
}

variable "key_pair_name" {
  description = "Name of the EC2 key pair for SSH access"
  type        = string
  default     = "codereview-keypair"
}

variable "db_instance_class" {
  description = "RDS instance class for PostgreSQL database"
  type        = string
  default     = "db.t3.micro"
}

variable "db_username" {
  description = "Database master username"
  type        = string
  default     = "postgres"
  sensitive   = true
}

variable "db_password" {
  description = "Database master password"
  type        = string
  default     = "postgres"
  sensitive   = true
}

variable "s3_bucket_name" {
  description = "Name of the S3 bucket for frontend hosting"
  type        = string
  default     = "codereview-frontend-platform"
}
