output "frontend_repo_url" {
  value = aws_ecr_repository.repos["frontend-repository-alan"].repository_url
}

output "backend_repo_url" {
  value = aws_ecr_repository.repos["backend-repository-alan"].repository_url
}

output "public_ip" {
  value = aws_instance.myserver.public_ip
}

output "public_dns" {
  value = aws_instance.myserver.public_dns
}

output "elastic_ip" {
  value = aws_eip.alan_eip.public_ip
}

output "bucket_name" {
  value = aws_s3_bucket.docker_compose_bucket.bucket
}