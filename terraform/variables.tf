variable "repositories" {
  description = "ECR repositories"
  type        = list(string)

  default = [
    "frontend-repository-alan",
    "backend-repository-alan"
  ]
}