# 🪟 Windows PowerShell에서 Minikube K8s 환경 구축 가이드

Windows PowerShell을 사용하여 xlarge 인스턴스와 **완전히 동일한** Kubernetes 환경을 Minikube로 구축하는 단계별 가이드입니다.

## 🛠️ 사전 준비 (Windows)

### 1. 필수 프로그램 설치 확인

```powershell
# PowerShell을 관리자 권한으로 실행 후 확인

# 1. Chocolatey 설치 확인 (패키지 매니저)
choco --version

# Chocolatey가 없다면 설치:
# Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 2. Docker Desktop 설치 확인
docker --version
docker info

# 3. Minikube 설치 확인
minikube version

# Minikube가 없다면 설치:
# choco install minikube

# 4. kubectl 설치 확인
kubectl version --client

# kubectl이 없다면 설치:
# choco install kubernetes-cli
```

### 2. 시스템 요구사항
- **OS**: Windows 10/11
- **CPU**: 최소 4코어 (권장)
- **메모리**: 최소 8GB (권장)
- **하이퍼바이저**: Hyper-V 또는 Docker Desktop
- **PowerShell**: 5.1 이상

---

## 🚀 단계별 환경 구축 (PowerShell)

### **Step 1: Minikube 클러스터 시작**

```powershell
# PowerShell을 관리자 권한으로 실행

# 1. 기존 클러스터 상태 확인
minikube status

# 2. 새 클러스터 시작 (xlarge 인스턴스 스펙에 맞게)
minikube start --cpus=4 --memory=6144 --driver=docker

# Windows에서 Hyper-V 사용 시:
# minikube start --cpus=4 --memory=6144 --driver=hyperv

# 3. 클러스터 상태 확인
minikube status

# 4. kubectl 컨텍스트 설정
kubectl config current-context
kubectl config use-context minikube
```

### **Step 2: NGINX Ingress Controller 활성화**

```powershell
# 1. Ingress 애드온 활성화 (xlarge와 동일한 NGINX 환경)
minikube addons enable ingress

# 2. 메트릭 서버 활성화 (HPA 테스트용)
minikube addons enable metrics-server

# 3. 애드온 상태 확인
minikube addons list

# 4. Ingress Controller Pod 상태 확인
kubectl get pods -n ingress-nginx

# 5. Ingress Controller 준비 대기
kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/name=ingress-nginx --timeout=300s
```

### **Step 3: 호스트 데이터베이스 서비스 시작**

```powershell
# 1. 프로젝트 루트 디렉토리로 이동
cd C:\Users\SSAFY\S13P11A302

# 2. infra 디렉토리로 이동
cd infra

# 3. PostgreSQL과 Redis 시작 (xlarge 인스턴스와 동일하게)
docker-compose up -d postgres redis

# 4. 서비스 상태 확인
docker-compose ps

# 5. 서비스 로그 확인
docker-compose logs postgres
docker-compose logs redis

# 6. 데이터베이스 연결 테스트
docker exec postgres_db pg_isready -U root -d development_db
docker exec redis-stack redis-cli ping

# 7. 서비스 준비 대기 (15초)
Start-Sleep -Seconds 15
```

### **Step 4: Spring Boot Docker 이미지 빌드**

```powershell
# 1. Minikube Docker 환경 설정 (중요!)
minikube docker-env --shell powershell | Invoke-Expression

# 환경 설정 확인
docker info

# 2. 프로젝트 루트로 돌아가기
cd C:\Users\SSAFY\S13P11A302

# 3. Docker 이미지 빌드 (시간이 걸릴 수 있음)
Write-Host "Docker 이미지 빌드 중... (5-10분 소요)" -ForegroundColor Yellow
docker build -t zmnnoory-local:latest .

# 4. 빌드 결과 확인
docker images | Select-String "zmnnoory"

# 성공 시 "zmnnoory-local latest" 이미지가 보여야 함
```

### **Step 5: Kubernetes 리소스 배포**

```powershell
# 1. Minikube용 K8s 매니페스트 디렉토리로 이동
cd C:\Users\SSAFY\S13P11A302\infra\local-test\k8s

# 2. 매니페스트 파일 존재 확인
Get-ChildItem -Name "*.yaml"

# 3. 리소스 순서대로 배포
Write-Host "Kubernetes 리소스 배포 시작..." -ForegroundColor Green

kubectl apply -f 01-namespace.yaml
Write-Host "✓ Namespace 생성 완료" -ForegroundColor Green

kubectl apply -f 02-configmap.yaml
Write-Host "✓ ConfigMap 생성 완료" -ForegroundColor Green

kubectl apply -f 03-secret.yaml
Write-Host "✓ Secret 생성 완료" -ForegroundColor Green

kubectl apply -f 04-deployment.yaml
Write-Host "✓ Deployment 생성 완료" -ForegroundColor Green

kubectl apply -f 05-service.yaml
Write-Host "✓ Service 생성 완료" -ForegroundColor Green

kubectl apply -f 06-hpa.yaml
Write-Host "✓ HPA 생성 완료" -ForegroundColor Green

kubectl apply -f 07-ingress.yaml
Write-Host "✓ Ingress 생성 완료" -ForegroundColor Green

# 4. 배포 상태 확인
Write-Host "배포 상태 확인 중..." -ForegroundColor Yellow
kubectl rollout status deployment/zmnnoory-backend -n zmnnoory --timeout=300s
```

### **Step 6: 네트워크 연결 테스트**

```powershell
# 1. Pod 준비 대기
Write-Host "Pod 준비 대기 중..." -ForegroundColor Yellow
kubectl wait --for=condition=ready pod -l app=zmnnoory-backend -n zmnnoory --timeout=300s

# 2. Pod 상태 확인
kubectl get pods -n zmnnoory

# 3. 호스트 네트워크 접근 테스트
Write-Host "호스트 네트워크 접근 테스트..." -ForegroundColor Cyan
$podName = kubectl get pods -n zmnnoory -l app=zmnnoory-backend -o jsonpath="{.items[0].metadata.name}"
kubectl exec -n zmnnoory $podName -- nslookup host.minikube.internal

# 4. PostgreSQL 연결 테스트
Write-Host "PostgreSQL 연결 테스트..." -ForegroundColor Cyan
kubectl exec -n zmnnoory $podName -- nc -zv host.minikube.internal 5432

# 5. Redis 연결 테스트
Write-Host "Redis 연결 테스트..." -ForegroundColor Cyan
kubectl exec -n zmnnoory $podName -- nc -zv host.minikube.internal 6379
```

### **Step 7: 애플리케이션 접근 및 테스트**

```powershell
# 1. Minikube IP 확인
$MINIKUBE_IP = minikube ip
Write-Host "Minikube IP: $MINIKUBE_IP" -ForegroundColor Green

# 2. 애플리케이션 시작 대기
Write-Host "애플리케이션 시작 대기 중..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# 3. Health Check 테스트
Write-Host "Health Check 테스트..." -ForegroundColor Cyan
try {
    $healthResponse = Invoke-RestMethod -Uri "http://$MINIKUBE_IP/actuator/health" -Method Get
    Write-Host "✓ Health Check 성공!" -ForegroundColor Green
    $healthResponse | ConvertTo-Json
} catch {
    Write-Host "✗ Health Check 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Spring Boot 프로파일 확인
Write-Host "Spring Boot 프로파일 확인..." -ForegroundColor Cyan
try {
    $envResponse = Invoke-RestMethod -Uri "http://$MINIKUBE_IP/actuator/env" -Method Get
    $activeProfile = $envResponse.activeProfiles
    Write-Host "활성 프로파일: $activeProfile" -ForegroundColor Green
} catch {
    Write-Host "프로파일 확인 실패: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 애플리케이션 로그 확인
Write-Host "애플리케이션 로그 확인..." -ForegroundColor Cyan
kubectl logs -n zmnnoory deployment/zmnnoory-backend --tail=20
```

---

## 🧪 Windows PowerShell 테스트 시나리오

### **1. 무중단 배포 테스트**

```powershell
# 1. 현재 Pod 상태 모니터링 시작 (새 PowerShell 창에서)
kubectl get pods -n zmnnoory -w

# 2. Rolling Update 시뮬레이션 (원래 창에서)
Write-Host "Rolling Update 시뮬레이션 시작..." -ForegroundColor Yellow
kubectl patch deployment zmnnoory-backend -n zmnnoory -p '{"spec":{"template":{"metadata":{"labels":{"version":"v2"}}}}}'

# 3. 배포 진행 상황 확인
kubectl rollout status deployment/zmnnoory-backend -n zmnnoory

# 4. 업데이트 중에도 서비스 가능한지 확인
$MINIKUBE_IP = minikube ip
for ($i = 1; $i -le 10; $i++) {
    try {
        $response = Invoke-RestMethod -Uri "http://$MINIKUBE_IP/actuator/health" -Method Get -TimeoutSec 5
        Write-Host "요청 $i : 성공" -ForegroundColor Green
    } catch {
        Write-Host "요청 $i : 실패" -ForegroundColor Red
    }
    Start-Sleep -Seconds 2
}
```

### **2. 자동 스케일링 테스트**

```powershell
# 1. HPA 상태 확인
kubectl get hpa -n zmnnoory

# 2. 부하 생성기 실행 (별도 PowerShell 창에서)
kubectl run load-generator --image=busybox --restart=Never -- /bin/sh -c "while true; do wget -q -O- http://zmnnoory-service.zmnnoory:8080/actuator/health; sleep 0.01; done"

# 3. HPA 상태 모니터링 (원래 창에서)
Write-Host "자동 스케일링 모니터링 시작..." -ForegroundColor Yellow
kubectl get hpa -n zmnnoory -w

# 4. Pod 수 변화 확인 (또 다른 창에서)
kubectl get pods -n zmnnoory -w

# 5. 부하 테스트 정리
kubectl delete pod load-generator
```

### **3. 로드밸런싱 테스트**

```powershell
# 1. Pod 수 증가
kubectl scale deployment zmnnoory-backend --replicas=3 -n zmnnoory

# 2. Pod 준비 대기
kubectl wait --for=condition=ready pod -l app=zmnnoory-backend -n zmnnoory --timeout=300s

# 3. 각 Pod의 정보 확인
kubectl get pods -n zmnnoory -o wide

# 4. 로드밸런싱 테스트
$MINIKUBE_IP = minikube ip
Write-Host "로드밸런싱 테스트 (10회 요청)..." -ForegroundColor Yellow

for ($i = 1; $i -le 10; $i++) {
    try {
        $response = Invoke-RestMethod -Uri "http://$MINIKUBE_IP/actuator/info" -Method Get
        $hostname = $response.hostname
        Write-Host "요청 $i : Pod $hostname" -ForegroundColor Green
    } catch {
        Write-Host "요청 $i : 실패" -ForegroundColor Red
    }
    Start-Sleep -Seconds 1
}
```

---

## 📊 상태 확인 및 관리 (PowerShell)

### **전체 상태 확인**

```powershell
# 1. Minikube 상태
Write-Host "=== Minikube 상태 ===" -ForegroundColor Blue
minikube status

# 2. 호스트 데이터베이스 상태
Write-Host "`n=== 호스트 데이터베이스 ===" -ForegroundColor Blue
cd C:\Users\SSAFY\S13P11A302\infra
docker-compose ps postgres redis

# 3. Kubernetes 리소스 상태
Write-Host "`n=== Kubernetes 리소스 ===" -ForegroundColor Blue
kubectl get all -n zmnnoory

# 4. Ingress 상태
Write-Host "`n=== Ingress 정보 ===" -ForegroundColor Blue
kubectl get ingress -n zmnnoory

# 5. 접근 정보
Write-Host "`n=== 접근 URL ===" -ForegroundColor Blue
$MINIKUBE_IP = minikube ip
Write-Host "애플리케이션: http://$MINIKUBE_IP" -ForegroundColor Green
Write-Host "Health Check: http://$MINIKUBE_IP/actuator/health" -ForegroundColor Green
Write-Host "Minikube 대시보드: minikube dashboard" -ForegroundColor Green
```

### **로그 확인**

```powershell
# 1. 애플리케이션 로그 (실시간)
kubectl logs -n zmnnoory deployment/zmnnoory-backend -f

# 2. 최근 로그만 확인
kubectl logs -n zmnnoory deployment/zmnnoory-backend --tail=50

# 3. 특정 Pod 로그 확인
$podName = kubectl get pods -n zmnnoory -l app=zmnnoory-backend -o jsonpath="{.items[0].metadata.name}"
kubectl logs -n zmnnoory $podName

# 4. 이전 재시작된 컨테이너 로그
kubectl logs -n zmnnoory $podName --previous
```

---

## 🧹 환경 정리 (PowerShell)

### **부분 정리**

```powershell
# 1. Kubernetes 리소스만 삭제
cd C:\Users\SSAFY\S13P11A302\infra\local-test\k8s
kubectl delete -f . --ignore-not-found=true

# 2. 호스트 데이터베이스만 정리
cd C:\Users\SSAFY\S13P11A302\infra
docker-compose down -v

# 3. Minikube 정지 (삭제하지 않음)
minikube stop
```

### **완전 정리**

```powershell
# 1. 모든 Kubernetes 리소스 삭제
kubectl delete namespace zmnnoory --ignore-not-found=true

# 2. 호스트 데이터베이스 정리
cd C:\Users\SSAFY\S13P11A302\infra
docker-compose down -v
docker system prune -f

# 3. Minikube 클러스터 완전 삭제
minikube delete

# 4. Docker 이미지 정리
docker rmi zmnnoory-local:latest --force
```

---

## 🔧 Windows 특화 트러블슈팅

### **문제 1: PowerShell 실행 정책 오류**

```powershell
# 현재 실행 정책 확인
Get-ExecutionPolicy

# 실행 정책 변경 (관리자 권한 필요)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# 또는 일시적 우회
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
```

### **문제 2: Hyper-V 충돌**

```powershell
# Docker 드라이버로 재시작
minikube delete
minikube start --driver=docker --cpus=4 --memory=6144

# Hyper-V 사용 시
minikube start --driver=hyperv --cpus=4 --memory=6144
```

### **문제 3: 방화벽 차단**

```powershell
# Windows 방화벽 상태 확인
netsh advfirewall show allprofiles

# 특정 포트 허용 (관리자 권한 필요)
netsh advfirewall firewall add rule name="Minikube" dir=in action=allow protocol=TCP localport=8080
```

### **문제 4: Docker Desktop 연결 오류**

```powershell
# Docker Desktop 재시작
Restart-Service docker

# Docker 상태 확인
docker info

# Minikube Docker 환경 재설정
minikube docker-env --shell powershell | Invoke-Expression
```

---

## ✅ Windows PowerShell 체크리스트

### **시작 전 확인**
- [ ] PowerShell 관리자 권한으로 실행
- [ ] Docker Desktop 실행 중
- [ ] Minikube 설치됨
- [ ] kubectl 설치됨
- [ ] 메모리 8GB 이상 사용 가능

### **환경 구축 확인**
- [ ] Minikube 클러스터 정상 시작
- [ ] Ingress Controller 활성화
- [ ] 호스트 DB 서비스 실행
- [ ] Docker 이미지 빌드 성공
- [ ] K8s 리소스 배포 완료

### **기능 테스트 확인**
- [ ] Health Check 응답 정상
- [ ] 데이터베이스 연결 성공
- [ ] 무중단 배포 테스트 성공
- [ ] 자동 스케일링 동작 확인

이제 **Windows PowerShell**에서 xlarge 인스턴스와 완전히 동일한 환경을 테스트할 수 있습니다! 🎉 