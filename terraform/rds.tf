# =============================================
# RDS PostgreSQL Configuration
# Provisions a db.t3.micro PostgreSQL instance in private subnets.
# The database is not publicly accessible - only reachable from
# the EC2 instance within the same VPC via the DB security group.
# =============================================

# Subnet group for RDS - requires subnets in at least 2 availability zones
resource "aws_db_subnet_group" "main" {
  name       = "codereview-db-subnet-group"
  subnet_ids = [aws_subnet.private_a.id, aws_subnet.private_b.id]

  tags = {
    Name = "codereview-db-subnet-group"
  }
}

# Security group for RDS - only allows PostgreSQL connections from the EC2 security group
resource "aws_security_group" "rds_sg" {
  name        = "codereview-rds-sg"
  description = "Security group for RDS PostgreSQL instance"
  vpc_id      = aws_vpc.main.id

  # Allow PostgreSQL connections only from the EC2 instance
  ingress {
    description     = "PostgreSQL access from EC2"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.ec2_sg.id]
  }

  tags = {
    Name = "codereview-rds-sg"
  }
}

# RDS PostgreSQL instance for the application database
resource "aws_db_instance" "postgres" {
  identifier             = "codereview-db"
  engine                 = "postgres"
  engine_version         = "15.17"
  instance_class         = var.db_instance_class
  allocated_storage      = 20
  max_allocated_storage  = 50
  storage_type           = "gp3"
  db_name                = "codereview_db"
  username               = var.db_username
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  publicly_accessible    = false
  skip_final_snapshot    = true
  multi_az               = false

  tags = {
    Name    = "codereview-postgres"
    Project = "DecentralisedPeerCodeReview"
  }
}
