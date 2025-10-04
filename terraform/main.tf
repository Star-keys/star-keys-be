terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  cloud {
    organization = "star-keys-backend"

    workspaces {
      name = "star-keys-be"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# VPC
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name        = "${var.project_name}-vpc"
    Environment = var.environment
  }
}

# Internet Gateway
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name        = "${var.project_name}-igw"
    Environment = var.environment
  }
}

# Public Subnet
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_cidr
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true

  tags = {
    Name        = "${var.project_name}-public-subnet"
    Environment = var.environment
  }
}

# Route Table
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name        = "${var.project_name}-public-rt"
    Environment = var.environment
  }
}

# Route Table Association
resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

# Security Group for Application Server
resource "aws_security_group" "app_server" {
  name        = "${var.project_name}-app-sg"
  description = "Security group for Spring Boot application"
  vpc_id      = aws_vpc.main.id

  # SSH
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # TODO: Restrict to your IP
  }

  # Spring Boot Application
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # PostgreSQL (from app server only)
  ingress {
    from_port = 5432
    to_port   = 5432
    protocol  = "tcp"
    self      = true
  }

  # Outbound
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-app-sg"
    Environment = var.environment
  }
}

# Key Pair
resource "aws_key_pair" "deployer" {
  key_name   = "${var.project_name}-deployer-key"
  public_key = var.ssh_public_key

  tags = {
    Name        = "${var.project_name}-key"
    Environment = var.environment
  }
}

# EC2 Instance
resource "aws_instance" "app_server" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  key_name              = aws_key_pair.deployer.key_name
  vpc_security_group_ids = [aws_security_group.app_server.id]
  subnet_id             = aws_subnet.public.id

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
  }

  user_data = <<-EOF
              #!/bin/bash
              # Update system
              yum update -y

              # Install Java 21
              yum install -y java-21-amazon-corretto

              # Install PostgreSQL
              amazon-linux-extras install postgresql14 -y
              yum install -y postgresql-server postgresql-contrib

              # Initialize and start PostgreSQL
              postgresql-setup initdb
              systemctl start postgresql
              systemctl enable postgresql

              # Configure PostgreSQL
              sudo -u postgres psql -c "CREATE USER starkeys WITH PASSWORD 'secret';"
              sudo -u postgres psql -c "CREATE DATABASE starkeys OWNER starkeys;"

              # Allow local connections
              echo "host    all             all             127.0.0.1/32            md5" >> /var/lib/pgsql/data/pg_hba.conf
              systemctl restart postgresql

              # Create app directory
              mkdir -p /opt/starkeys
              useradd -r -s /bin/false starkeys
              chown starkeys:starkeys /opt/starkeys
              EOF

  tags = {
    Name        = "${var.project_name}-server"
    Environment = var.environment
    Application = "starkeys-backend"
  }
}

# Elastic IP
resource "aws_eip" "app_server" {
  instance = aws_instance.app_server.id
  domain   = "vpc"

  tags = {
    Name        = "${var.project_name}-eip"
    Environment = var.environment
  }
}