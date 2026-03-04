# =============================================
# Terraform Outputs
# Displays the public URLs and connection details after provisioning.
# These values are needed for CI/CD configuration and application access.
# =============================================

output "ec2_public_ip" {
  description = "Static Elastic IP address of the backend EC2 instance"
  value       = aws_eip.backend.public_ip
}

output "ec2_public_dns" {
  description = "Public DNS name of the Elastic IP"
  value       = aws_eip.backend.public_dns
}

output "rds_endpoint" {
  description = "RDS PostgreSQL connection endpoint"
  value       = aws_db_instance.postgres.endpoint
}

output "rds_port" {
  description = "RDS PostgreSQL connection port"
  value       = aws_db_instance.postgres.port
}

output "s3_website_url" {
  description = "S3 static website URL for the frontend"
  value       = aws_s3_bucket_website_configuration.frontend.website_endpoint
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket hosting the frontend"
  value       = aws_s3_bucket.frontend.bucket
}
