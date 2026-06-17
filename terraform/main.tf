terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region = "ap-south-1"
}

# ECR Repositories
resource "aws_ecr_repository" "repos" {
  for_each = toset(var.repositories)

  name         = each.value
  force_delete = true
}

# Get the default VPC
data "aws_vpc" "default" {
  default = true
}

# Security Group
resource "aws_security_group" "alan_sg" {
  name        = "alan-terraform-sg"
  description = "Allow SSH, Frontend, Backend, HTTP and HTTPS"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Frontend"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Backend"
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "alan-terraform-sg"
  }
}

# Get latest Amazon Linux 2023 AMI
data "aws_ami" "al2023" {
  most_recent = true

  owners = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

# Key Pair
resource "aws_key_pair" "alan_key" {
  key_name   = "alan-key"
  public_key = file("${path.module}/alan-key.pub")
}

# EC2 Instance
resource "aws_instance" "myserver" {
  ami                         = data.aws_ami.al2023.id
  instance_type               = "t3.micro"
  key_name                    = aws_key_pair.alan_key.key_name
  associate_public_ip_address = true
  vpc_security_group_ids      = [aws_security_group.alan_sg.id]
  iam_instance_profile        = aws_iam_instance_profile.ec2_profile_alan.name

  user_data = file("${path.module}/user_data.sh")

  root_block_device { 
    volume_size = 30 
    volume_type = "gp3" 
  }

  tags = {
    Name = "alan-terraform"
  }
}

# Elastic IP
resource "aws_eip" "alan_eip" {
  domain = "vpc"

  tags = {
    Name = "alan-eip"
  }
}

# Associate Elastic IP with EC2
resource "aws_eip_association" "alan_eip_assoc" {
  instance_id   = aws_instance.myserver.id
  allocation_id = aws_eip.alan_eip.id
}

#S3 Bucket
resource "aws_s3_bucket" "docker_compose_bucket" {
  bucket = "alan-devops-files"

  tags = {
    Name = "docker-compose-storage"
  }
}