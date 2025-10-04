# 인프라 구성 가이드

## 📋 개요

이 프로젝트는 AWS 단일 서버 환경에서 Spring Boot 애플리케이션을 배포하기 위한 CI/CD 파이프라인을 구성합니다.

### 기술 스택
- **클라우드**: AWS (VPC, EC2, EIP)
- **IaC**: Terraform Cloud
- **배포 자동화**: Ansible
- **CI/CD**: GitHub Actions
- **데이터베이스**: PostgreSQL (EC2 내부)

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
│  │  │  - Java 21                   │  │    │
│  │  │  - Spring Boot App (8080)    │  │    │
│  │  │  - PostgreSQL (5432)         │  │    │
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

### 1. Terraform Cloud 설정

1. [Terraform Cloud](https://app.terraform.io/) 계정 생성
2. Organization 생성
3. Workspace 생성: `star-keys-be`
4. `terraform/main.tf`에서 organization 이름 수정:
   ```hcl
   cloud {
     organization = "YOUR_TF_CLOUD_ORG"  # 본인의 organization으로 변경
     workspaces {
       name = "star-keys-be"
     }
   }
   ```

### 2. AWS 자격 증명 설정

Terraform Cloud Workspace에 환경 변수 추가:
- `AWS_ACCESS_KEY_ID`: AWS Access Key
- `AWS_SECRET_ACCESS_KEY`: AWS Secret Key (Sensitive로 설정)

### 3. SSH 키 페어 생성

```bash
# SSH 키 페어 생성
ssh-keygen -t rsa -b 4096 -f ~/.ssh/star-keys-deployer -C "star-keys-deployer"

# Public Key 복사
cat ~/.ssh/star-keys-deployer.pub
```

Terraform Cloud Workspace에 변수 추가:
- Key: `ssh_public_key`
- Value: (위에서 복사한 public key)
- Sensitive: ✓

### 4. GitHub Secrets 설정

GitHub Repository → Settings → Secrets and variables → Actions에 추가:

| Secret Name | 설명 | 값 |
|------------|------|---|
| `AWS_ACCESS_KEY_ID` | AWS Access Key | IAM 사용자 키 |
| `AWS_SECRET_ACCESS_KEY` | AWS Secret Key | IAM 사용자 시크릿 키 |
| `SSH_PRIVATE_KEY` | EC2 접속용 Private Key | `cat ~/.ssh/star-keys-deployer` |
| `TF_API_TOKEN` | Terraform Cloud API Token | Terraform Cloud에서 생성 |

## 🛠️ 인프라 배포

### Terraform으로 인프라 생성

```bash
cd terraform

# Terraform Cloud 로그인
terraform login

# 초기화
terraform init

# 계획 확인
terraform plan

# 인프라 생성
terraform apply
```

생성되는 리소스:
- ✅ VPC (10.0.0.0/16)
- ✅ Public Subnet (10.0.1.0/24)
- ✅ Internet Gateway
- ✅ Route Table
- ✅ Security Group (SSH, HTTP 8080, PostgreSQL 5432)
- ✅ EC2 Instance (t3.small, Amazon Linux 2023)
- ✅ Elastic IP
- ✅ Java 21 설치
- ✅ PostgreSQL 설치 및 설정

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
1. 애플리케이션 디렉토리 생성
2. 기존 서비스 중지
3. JAR 파일 복사
4. systemd 서비스 파일 생성
5. 애플리케이션 시작
6. 헬스 체크

### 수동 배포

```bash
# EC2 인스턴스에 수동 배포
ansible-playbook \
  -i ansible/inventory/aws_ec2.yml \
  ansible/playbooks/deploy.yml \
  --private-key=~/.ssh/star-keys-deployer \
  -e "jar_file=build/libs/be-0.0.1-SNAPSHOT.jar"
```

## 🔐 보안 고려사항

### 현재 설정
- ⚠️ SSH 포트가 모든 IP에 열려있음
- ⚠️ PostgreSQL 비밀번호가 코드에 하드코딩됨

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

2. **시크릿 관리**:
   - AWS Secrets Manager 사용
   - 환경 변수로 민감 정보 주입

3. **HTTPS 설정**:
   - ALB + ACM으로 SSL/TLS 인증서 적용
   - 또는 Let's Encrypt + Nginx 리버스 프록시

4. **데이터베이스**:
   - RDS PostgreSQL 사용 권장
   - 백업 설정
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

### 헬스 체크

```bash
# 로컬에서
curl http://<ELASTIC_IP>:8080/actuator/health

# 서버에서
curl http://localhost:8080/actuator/health
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
- [ ] Terraform Cloud organization 설정 완료
- [ ] AWS 자격 증명 설정 완료
- [ ] SSH 키 페어 생성 완료
- [ ] GitHub Secrets 등록 완료
- [ ] `terraform/main.tf`에서 organization 이름 수정

### 배포 후 확인사항
- [ ] EC2 인스턴스 실행 중
- [ ] Elastic IP 할당 완료
- [ ] SSH 접속 가능
- [ ] PostgreSQL 실행 중
- [ ] 애플리케이션 정상 동작
- [ ] 헬스 체크 응답 정상

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

# PostgreSQL 상태 확인
sudo systemctl status postgresql

# 데이터베이스 연결 테스트
sudo -u postgres psql -c "\l"
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