output "vpc_id" {
  description = "VPC ID"
  value       = aws_vpc.main.id
}

output "public_subnet_id" {
  description = "Public subnet ID"
  value       = aws_subnet.public.id
}

output "app_server_public_ip" {
  description = "Application server public IP"
  value       = aws_eip.app_server.public_ip
}

output "app_server_id" {
  description = "Application server instance ID"
  value       = aws_instance.app_server.id
}

output "ssh_command" {
  description = "SSH command to connect to the server"
  value       = "ssh -i ~/.ssh/star-keys-deployer.pem ec2-user@${aws_eip.app_server.public_ip}"
}