# =============================================
# Terraform Main Configuration - Backend Infrastructure
# Provisions AWS VPC, subnets, internet gateway, and route tables
# for deploying the Decentralised Peer Code Review Platform backend.
# =============================================

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# AWS Provider configuration - region set via variable
provider "aws" {
  region = var.aws_region
}

# VPC for isolating the application network
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name    = "codereview-vpc"
    Project = "DecentralisedPeerCodeReview"
  }
}

# Public subnet for EC2 instance (accessible from the internet)
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true

  tags = {
    Name = "codereview-public-subnet"
  }
}

# Private subnet for RDS database (not directly accessible from internet)
resource "aws_subnet" "private_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.2.0/24"
  availability_zone = "${var.aws_region}a"

  tags = {
    Name = "codereview-private-subnet-a"
  }
}

# Second private subnet in different AZ (required for RDS subnet group)
resource "aws_subnet" "private_b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.3.0/24"
  availability_zone = "${var.aws_region}b"

  tags = {
    Name = "codereview-private-subnet-b"
  }
}

# Internet gateway for public subnet internet access
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "codereview-igw"
  }
}

# Route table for public subnet - routes internet traffic through IGW
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "codereview-public-rt"
  }
}

# Associate the public route table with the public subnet
resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}
