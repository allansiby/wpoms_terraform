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

# Create a Security Group in the default VPC
resource "aws_security_group" "alan_sg" {
  name        = "alan-terraform-sg"
  description = "Allow SSH access"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
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


data "aws_ami" "al2023" {
  most_recent = true

  owners = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

resource "aws_key_pair" "alan_key" {
  key_name   = "alan-key"
  public_key = file("${path.module}/alan-key.pub")
}

resource "aws_instance" "myserver" {
  ami                         = data.aws_ami.al2023.id
  instance_type               = "t3.micro"
  key_name                    = aws_key_pair.alan_key.key_name
  associate_public_ip_address = true
  vpc_security_group_ids      = [aws_security_group.alan_sg.id]

  user_data = file("${path.module}/user_data.sh")

  tags = {
    Name = "alan-terraform"
  }  
}