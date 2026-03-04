# =============================================
# EC2 Instance Configuration
# Provisions a t3.micro Ubuntu 22.04 instance running the Spring Boot backend.
# Includes user data script to install Java 17 and configure systemd service.
# Security group allows HTTP (8080) and SSH (22) access.
# =============================================

# Security group for the EC2 instance
resource "aws_security_group" "ec2_sg" {
  name        = "codereview-ec2-sg"
  description = "Security group for the backend EC2 instance"
  vpc_id      = aws_vpc.main.id

  # Allow SSH access for deployment via CI/CD pipeline
  ingress {
    description = "SSH access for deployment"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow HTTP access on port 8080 for the Spring Boot application
  ingress {
    description = "Spring Boot application port"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all outbound traffic for package installation and API calls
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "codereview-ec2-sg"
  }
}

# EC2 instance running the Spring Boot backend application
resource "aws_instance" "backend" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  key_name               = var.key_pair_name
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]

  # User data script runs on first boot to set up Java and the application service
  user_data = <<-EOF
              #!/bin/bash
              # Update system packages
              sudo apt update -y
              # Install Java 17 (OpenJDK) for running Spring Boot
              sudo apt install -y openjdk-17-jdk-headless
              # Create application directory
              mkdir -p /home/ubuntu/app
              # Create systemd service file for the Spring Boot application
              cat > /etc/systemd/system/codereview.service <<'SERVICE'
              [Unit]
              Description=Code Review Platform Backend
              After=network.target
              [Service]
              User=ubuntu
              ExecStart=/usr/bin/java -jar /home/ubuntu/app/codereview-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
              Restart=always
              RestartSec=10
              [Install]
              WantedBy=multi-user.target
              SERVICE
              # Enable the service to start on boot
              sudo systemctl daemon-reload
              sudo systemctl enable codereview
              EOF

  tags = {
    Name    = "codereview-backend"
    Project = "DecentralisedPeerCodeReview"
  }
}

# Elastic IP for a static public IP that persists across EC2 restarts
resource "aws_eip" "backend" {
  instance = aws_instance.backend.id
  domain   = "vpc"

  tags = {
    Name = "codereview-backend-eip"
  }
}
