# 인프라 구성 가이드

## 📋 개요

이 프로젝트는 AWS 단일 서버 환경에서 Spring Boot 애플리케이션을 배포하기 위한 CI/CD 파이프라인을 구성합니다.

### 기술 스택
- **클라우드**: AWS (VPC, EC2, EIP)
- **IaC**: Terraform (로컬)
- **배포 자동화**: Ansible
- **CI/CD**: GitHub Actions
- **런타임**: Java 21 (Amazon Corretto)

## 🏗️ 인프라 아키텍처

```
┌─────────────────────────────────────────────┐
│              AWS VPC (10.0.0.0/16)          │
│                                             │
│  ┌────────────────────────────────────┐    │
│  │  Public Subnet (10.0.1.0/24)       │    │
│  │                                     │    │
│  │  ┌──────────────────────────────┐  │    │
│  │  │   EC2 Instance (t3.small)    │  │    │
│  │  │                              │  │    │
│  │  │  - Java 21 (Corretto)        │  │    │
│  │  │  - Spring Boot App (8080)    │  │    │
│  │  │                              │  │    │
│  │  │  Elastic IP                  │  │    │
│  │  └──────────────────────────────┘  │    │
│  │                                     │    │
│  └────────────────────────────────────┘    │
│                                             │
│  Internet Gateway                           │
└─────────────────────────────────────────────┘
```

## 🚀 초기 설정

### 1. AWS CLI 설정

```bash
# AWS CLI 설치 (macOS)
brew install awscli

# AWS 자격 증명 설정
aws configure
# AWS Access Key ID 입력
# AWS Secret Access Key 입력
# Default region: ap-northeast-2
# Default output format: json
```

### 2. SSH 키 페어 생성

```bash
# SSH 키 페어 생성
ssh-keygen -t rsa -b 4096 -f ~/.ssh/star-keys-deployer -C "star-keys-deployer"

# Public Key 확인
cat ~/.ssh/star-keys-deployer.pub
```

### 3. Terraform 변수 설정

`terraform/terraform.tfvars` 파일 생성:

```hcl
ssh_public_key = "ssh-rsa AAAA... (위에서 복사한 public key)"
```

⚠️ **중요**: `terraform.tfvars` 파일은 민감한 정보를 포함하므로 `.gitignore`에 추가되어 있습니다.

### 4. GitHub Secrets 설정

GitHub Repository → Settings → Secrets and variables → Actions에 추가:

| Secret Name | 설명 | 값 |
|------------|------|---|
| `AWS_ACCESS_KEY_ID` | AWS Access Key | IAM 사용자 키 |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Key | IAM 사용자 시크릿 키 |
| `SSH_PRIVATE_KEY` | EC2 접속용 Private Key | `cat ~/.ssh/star-keys-deployer` |
| `SSH_PUBLIC_KEY` | EC2 접속용 Public Key | `cat ~/.ssh/star-keys-deployer.pub` |

## 🛠️ 인프라 배포

### Terraform으로 인프라 생성

```bash
cd terraform

# 초기화
terraform init

# 계획 확인
terraform plan

# 인프라 생성 (승인 필요)
terraform apply

# 또는 자동 승인
terraform apply -auto-approve
```

생성되는 리소스:
- ✅ VPC (10.0.0.0/16)
- ✅ Public Subnet (10.0.1.0/24)
- ✅ Internet Gateway
- ✅ Route Table
- ✅ Security Group (SSH 22, HTTP 8080)
- ✅ EC2 Instance (t3.small, Amazon Linux 2023)
- ✅ Elastic IP
- ✅ Java 21 (Amazon Corretto) 설치

### 배포 후 확인

```bash
# Output 확인
terraform output

# SSH 접속 테스트
ssh -i ~/.ssh/star-keys-deployer ec2-user@<ELASTIC_IP>
```

## 🔄 CI/CD 파이프라인

### GitHub Actions Workflow

**트리거 조건**:
- `main` 브랜치에 push
- Pull Request 생성

**파이프라인 단계**:

1. **Test** (모든 이벤트)
   - 코드 체크아웃
   - JDK 21 설정
   - Gradle 테스트 실행
   - 테스트 리포트 업로드

2. **Build** (`main` push만)
   - JAR 파일 빌드
   - Artifact 업로드

3. **Deploy** (`main` push만)
   - JAR 다운로드
   - AWS 자격 증명 설정
   - Ansible 설치
   - Ansible Playbook 실행
   - 애플리케이션 배포

## 📦 Ansible 배포 프로세스

### 배포 스크립트 구성

**`ansible/playbooks/deploy.yml`**:
1. Java 21 설치 확인
2. 애플리케이션 디렉토리 생성
3. 기존 서비스 중지
4. JAR 파일 복사
5. systemd 서비스 파일 생성
6. 애플리케이션 시작

### 수동 배포

```bash
# JAR 파일 빌드
./gradlew bootJar

# EC2 인스턴스에 수동 배포
ansible-playbook \
  -i ansible/inventory/aws_ec2.yml \
  ansible/playbooks/deploy.yml \
  --private-key=~/.ssh/star-keys-deployer \
  -e "jar_file=build/libs/be-0.0.1-SNAPSHOT.jar"
```

## 🔐 보안 고려사항

### 현재 설정
- ⚠️ SSH 포트가 모든 IP에 열려있음 (0.0.0.0/0)
- ⚠️ HTTP 포트 8080이 모든 IP에 열려있음

### 프로덕션 권장사항

1. **SSH 접근 제한**:
   ```hcl
   # terraform/main.tf에서 수정
   ingress {
     from_port   = 22
     to_port     = 22
     protocol    = "tcp"
     cidr_blocks = ["YOUR_OFFICE_IP/32"]  # 특정 IP로 제한
   }
   ```

2. **HTTPS 설정**:
   - ALB + ACM으로 SSL/TLS 인증서 적용
   - 또는 Let's Encrypt + Nginx 리버스 프록시

3. **시크릿 관리**:
   - AWS Secrets Manager 또는 Parameter Store 사용
   - 환경 변수로 민감 정보 주입

4. **데이터베이스**:
   - 외부 데이터베이스(RDS) 사용 권장
   - 자동 백업 설정
   - Multi-AZ 구성

## 📊 모니터링

### 애플리케이션 로그 확인

```bash
# SSH 접속
ssh -i ~/.ssh/star-keys-deployer ec2-user@<ELASTIC_IP>

# 서비스 상태 확인
sudo systemctl status starkeys-backend

# 로그 확인
sudo journalctl -u starkeys-backend -f

# 최근 100줄 로그
sudo journalctl -u starkeys-backend -n 100
```

### 애플리케이션 확인

```bash
# 로컬에서 접속 테스트
curl http://<ELASTIC_IP>:8080

# 서버에서 포트 확인
ss -tlnp | grep 8080
```

## 🧹 리소스 정리

```bash
cd terraform

# 인프라 삭제
terraform destroy

# 확인 후 'yes' 입력
```

## 📝 체크리스트

### 배포 전 확인사항
- [ ] AWS CLI 설치 및 자격 증명 설정 완료
- [ ] Terraform 설치 완료
- [ ] SSH 키 페어 생성 완료
- [ ] `terraform/terraform.tfvars` 파일 생성 완료
- [ ] GitHub Secrets 등록 완료

### 배포 후 확인사항
- [ ] EC2 인스턴스 실행 중
- [ ] Elastic IP 할당 완료
- [ ] SSH 접속 가능
- [ ] Java 21 설치 확인 (`java -version`)
- [ ] 애플리케이션 정상 동작
- [ ] 포트 8080 리스닝 확인

## 🆘 트러블슈팅

### 1. Ansible이 EC2를 찾지 못함
```bash
# AWS 자격 증명 확인
aws sts get-caller-identity

# EC2 태그 확인
aws ec2 describe-instances --filters "Name=tag:Application,Values=starkeys-backend"
```

### 2. 애플리케이션 시작 실패
```bash
# 로그 확인
sudo journalctl -u starkeys-backend -n 100

# Java 버전 확인
java -version

# JAR 파일 확인
ls -lh /opt/starkeys/application.jar
```

### 3. GitHub Actions 배포 실패
- Secrets 설정 확인
- SSH 키 권한 확인 (600)
- Ansible 인벤토리 동적 확인

## 📚 추가 리소스

- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Ansible AWS Guide](https://docs.ansible.com/ansible/latest/collections/amazon/aws/index.html)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)