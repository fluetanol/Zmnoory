# Minikube 로컬 Kubernetes 테스트 가이드

Minikube를 사용하여 로컬에서 xlarge 인스턴스와 동일한 Kubernetes 환경을 테스트할 수 있습니다.

## 🛠️ 사전 준비

### 1. Minikube 설치
```bash
# Windows (Chocolatey)
choco install minikube

# Windows (직접 다운로드)
# https://minikube.sigs.k8s.io/docs/start/ 에서 다운로드

# macOS
brew install minikube

# Linux
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube
```

### 2. kubectl 설치
```bash
# Windows (Chocolatey)
choco install kubernetes-cli

# macOS
brew install kubectl

# Linux
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install kubectl /usr/local/bin/kubectl
```

### 3. Docker Desktop 설치 (선택사항)
- Windows/macOS: Docker Desktop 설치
- Linux: Docker Engine 설치

## 🚀 Minikube 클러스터 시작

### 1. 클러스터 생성
```bash
# 리소스가 충분한 환경으로 시작
minikube start --cpus=4 --memory=6144 --driver=docker

# 또는 하이퍼바이저 사용 (Windows)
minikube start --cpus=4 --memory=6144 --driver=hyperv
```

### 2. 애드온 활성화
```bash
# NGINX Ingress Controller 활성화
minikube addons enable ingress

# 대시보드 활성화 (선택사항)
minikube addons enable dashboard

# 메트릭 서버 활성화 (HPA 사용을 위해)
minikube addons enable metrics-server
```

### 3. kubectl 컨텍스트 설정
```bash
# Minikube 컨텍스트 확인
kubectl config current-context

# 컨텍스트가 minikube가 아니라면 변경
kubectl config use-context minikube
```

## 🐳 로컬용 Docker 이미지 빌드

### 1. Minikube Docker 환경 사용
```bash
# Minikube의 Docker 데몬 사용
eval $(minikube docker-env)

# 또는 Windows PowerShell
minikube docker-env --shell powershell | Invoke-Expression
```

### 2. Spring Boot 이미지 빌드
```bash
# 프로젝트 루트에서
docker build -t zmnnoory-local:latest .
```

## 📝 로컬용 Kubernetes 매니페스트 수정

### 1. 로컬 테스트용 설정 생성
```bash
# 로컬용 디렉토리 생성
mkdir -p infra/local-test/k8s
cp infra/k8s/* infra/local-test/k8s/
```

### 2. 이미지명 수정
로컬 이미지를 사용하도록 Deployment 수정:
```yaml
# infra/local-test/k8s/04-deployment.yaml
containers:
- name: backend
  image: zmnnoory-local:latest
  imagePullPolicy: Never  # 로컬 이미지 사용
```

### 3. DB 연결 설정 수정 (Option A: 호스트 DB 사용)
```yaml
# infra/local-test/k8s/02-configmap.yaml
data:
  # 호스트의 Docker 컨테이너 접근
  DB_HOST: "host.minikube.internal"  # Minikube용
  REDIS_HOST: "host.minikube.internal"
```

### 4. DB 연결 설정 수정 (Option B: K8s 내부 DB 사용)
별도의 PostgreSQL/Redis를 Kubernetes 내부에 배포

## 🎯 배포 및 테스트

### 1. 호스트에서 DB 시작 (Option A)
```bash
# 프로젝트 루트에서
cd infra
docker-compose up -d
```

### 2. Kubernetes 애플리케이션 배포
```bash
cd infra/local-test/k8s
kubectl apply -f .

# 또는 스크립트 사용 (수정된 버전)
./deploy.sh deploy
```

### 3. 포트포워딩으로 접근
```bash
# Ingress Controller 포트포워딩
kubectl port-forward -n ingress-nginx service/ingress-nginx-controller 8080:80

# 또는 직접 서비스 포트포워딩
kubectl port-forward -n zmnnoory service/zmnnoory-service 8080:8080
```

### 4. 애플리케이션 테스트
```bash
# Health Check
curl http://localhost:8080/actuator/health

# API 테스트
curl http://localhost:8080/api/your-endpoint
```

## 🔧 유용한 Minikube 명령어

### 클러스터 관리
```bash
# 클러스터 상태 확인
minikube status

# 클러스터 중지
minikube stop

# 클러스터 삭제
minikube delete

# 대시보드 열기
minikube dashboard

# 서비스 URL 확인
minikube service list
```

### 리소스 모니터링
```bash
# 노드 리소스 사용량
kubectl top nodes

# Pod 리소스 사용량
kubectl top pods -A

# 로그 확인
kubectl logs -n zmnnoory deployment/zmnnoory-backend
```

## 🎛️ 설정 최적화

### 리소스 제한 완화 (로컬용)
```yaml
# 04-deployment.yaml
resources:
  requests:
    memory: "256Mi"  # 원본: 512Mi
    cpu: "100m"      # 원본: 250m
  limits:
    memory: "512Mi"  # 원본: 1Gi
    cpu: "250m"      # 원본: 500m
```

### Replica 수 감소
```yaml
# 04-deployment.yaml
replicas: 1  # 원본: 3

# 06-hpa.yaml
minReplicas: 1  # 원본: 2
maxReplicas: 3  # 원본: 10
```

## 🔍 트러블슈팅

### Docker 이미지 문제
```bash
# Minikube Docker 환경 확인
minikube docker-env

# 이미지 목록 확인
docker images | grep zmnnoory

# 이미지 다시 빌드
docker build -t zmnnoory-local:latest .
```

### DB 연결 문제
```bash
# 호스트 네트워크 확인
minikube ssh
# 내부에서: ping host.minikube.internal

# 포트 확인
netstat -tlnp | grep 5432
```

### 포트포워딩 문제
```bash
# 서비스 상태 확인
kubectl get svc -A

# Ingress Controller 상태
kubectl get pods -n ingress-nginx

# 포트포워딩 다시 시도
kubectl port-forward -n zmnnoory service/zmnnoory-service 8080:8080
``` 