# ZMNNOORY Kubernetes 배포 가이드

이 디렉토리는 AWS EC2 xlarge 인스턴스에서 Kubernetes + NGINX Ingress Controller를 통한 Spring Boot 백엔드 무중단 배포를 위한 매니페스트 파일들을 포함합니다.

## 🏗️ 아키텍처 개요

```
AWS EC2 xlarge 인스턴스
├── Docker (호스트 레벨)
│   ├── PostgreSQL (포트: 5432)
│   └── Redis (포트: 6379)
└── Kubernetes 클러스터
    ├── NGINX Ingress Controller (NodePort: 30080, 30443)
    ├── Spring Boot Pods (3개 인스턴스)
    └── ClusterIP Service (내부 로드밸런싱)

외부 접근: http://<EC2-PUBLIC-IP>:30080
```

## 📁 디렉토리 구조

```
infra/k8s/
├── 00-nginx-ingress-controller.yaml  # NGINX Ingress Controller 배포
├── 01-namespace.yaml                 # 네임스페이스 정의
├── 02-configmap.yaml                 # 환경설정 관리
├── 03-secret.yaml                    # 민감정보 관리
├── 04-deployment.yaml                # 애플리케이션 배포 (무중단 배포)
├── 05-service.yaml                   # 내부 로드밸런싱 서비스 (ClusterIP)
├── 06-hpa.yaml                      # 자동 스케일링
├── 07-ingress.yaml                  # NGINX 라우팅 설정
├── kustomization.yaml               # Kustomize 설정
├── deploy.sh                        # 배포 스크립트
└── README.md                        # 이 파일
```

## 🚀 빠른 시작

### 1. 전체 배포 실행
```bash
cd infra/k8s
./deploy.sh deploy
```

### 2. 상태 확인
```bash
./deploy.sh status
```

### 3. 로그 확인
```bash
./deploy.sh logs
```

### 4. 외부 접근
```bash
# EC2 퍼블릭 IP 확인
curl -I http://<EC2-PUBLIC-IP>:30080

# 보안 그룹에서 30080, 30443 포트 열기 필요
```

## 🔧 상세 설정

### NGINX Ingress Controller

#### 주요 특징
- **NodePort 서비스**: EC2 인스턴스의 30080(HTTP), 30443(HTTPS) 포트로 외부 접근
- **로드밸런싱**: 3개 Spring Boot Pod에 Round Robin 방식 트래픽 분산
- **이중화**: NGINX Controller 2개 인스턴스로 고가용성 확보
- **성능 최적화**: Worker 프로세스, 연결 수, 버퍼 크기 튜닝

#### 네트워크 흐름
```
외부 요청 → EC2:30080 → NGINX Ingress → ClusterIP Service → Spring Boot Pods
```

### 환경변수 관리

#### ConfigMap (02-configmap.yaml)
- Spring Boot 프로파일: `k8s`
- 데이터베이스 연결: `host.docker.internal:5432`
- Redis 연결: `host.docker.internal:6379`
- 서버 최적화 설정

#### Secret (03-secret.yaml)
- JWT 시크릿 키 (base64 인코딩 필요)
- 데이터베이스 비밀번호
- Redis 비밀번호 (필요시)

**⚠️ 중요**: 운영 환경에서는 Secret 값들을 GitLab Variables에서 가져와 교체해야 합니다.

### 무중단 배포 설정

#### Rolling Update 전략
```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxUnavailable: 1    # 최대 1개 Pod 중단
    maxSurge: 1          # 최대 1개 Pod 추가
```

#### 헬스체크
- **Startup Probe**: 애플리케이션 시작 확인 (최대 100초)
- **Liveness Probe**: 컨테이너 생존 확인 (10초마다)
- **Readiness Probe**: 트래픽 처리 준비 확인 (5초마다)

### 자동 스케일링 (HPA)

```yaml
# CPU 70% 초과 시 확장
# 메모리 80% 초과 시 확장
minReplicas: 2      # 최소 2개
maxReplicas: 10     # 최대 10개
```

## 📋 배포 명령어

### 기본 배포 명령어
```bash
# 전체 배포 (NGINX Ingress Controller 포함)
./deploy.sh deploy

# 애플리케이션 재시작
./deploy.sh restart

# Pod 수 조정
./deploy.sh scale 5

# 상태 확인
./deploy.sh status

# 실시간 로그
./deploy.sh logs

# 전체 삭제
./deploy.sh delete
```

### kubectl 직접 사용
```bash
# NGINX Ingress Controller 먼저 배포
kubectl apply -f 00-nginx-ingress-controller.yaml

# 애플리케이션 리소스 배포
kubectl apply -f 01-namespace.yaml
kubectl apply -f 02-configmap.yaml
kubectl apply -f 03-secret.yaml
kubectl apply -f 04-deployment.yaml
kubectl apply -f 05-service.yaml
kubectl apply -f 06-hpa.yaml
kubectl apply -f 07-ingress.yaml

# 전체 리소스 적용
kubectl apply -f .

# Kustomize 사용
kubectl apply -k .
```

## 🔐 GitLab CI/CD 연동

### GitLab Variables 설정 (File 타입)

1. **APPLICATION_CONFIG_K8S**
   - Type: File
   - Value: `backend/ZMNNOORY/src/main/resources/application-k8s.yml` 내용

2. **JWT_SECRET_BASE64**
   - Type: Variable (Masked)
   - Value: `echo -n "your-strong-jwt-secret" | base64`

3. **DB_PASSWORD_BASE64**
   - Type: Variable (Masked)
   - Value: `echo -n "q1w2e3r4t5" | base64`

### .gitlab-ci.yml 배포 스테이지 예시
```yaml
deploy:
  stage: deploy
  script:
    # Secret 값 업데이트
    - sed -i "s/JWT_SECRET_KEY:.*/JWT_SECRET_KEY: $JWT_SECRET_BASE64/" infra/k8s/03-secret.yaml
    - sed -i "s/DB_PASSWORD:.*/DB_PASSWORD: $DB_PASSWORD_BASE64/" infra/k8s/03-secret.yaml
    
    # 이미지 태그 업데이트
    - sed -i "s|your-registry/zmnnoory:latest|$CI_REGISTRY_IMAGE:$CI_COMMIT_SHA|" infra/k8s/04-deployment.yaml
    
    # 배포 실행
    - cd infra/k8s
    - ./deploy.sh deploy
```

## 🌐 외부 접근 설정

### EC2 보안 그룹 설정
```bash
# 인바운드 규칙 추가
- Type: Custom TCP
- Port Range: 30080
- Source: 0.0.0.0/0 (또는 필요한 IP 범위)

- Type: Custom TCP  
- Port Range: 30443
- Source: 0.0.0.0/0 (또는 필요한 IP 범위)
```

### 접근 URL
```bash
# HTTP 접근
http://<EC2-PUBLIC-IP>:30080

# HTTPS 접근 (SSL 인증서 설정 시)
https://<EC2-PUBLIC-IP>:30443

# Health Check
curl http://<EC2-PUBLIC-IP>:30080/actuator/health
```

### 도메인 연결 (선택사항)
```bash
# DNS A 레코드 설정
yourdomain.com → <EC2-PUBLIC-IP>

# 접근 시 포트 필요
http://yourdomain.com:30080
```

## 🔧 트러블슈팅

### 자주 발생하는 문제들

#### 1. NGINX Ingress Controller 시작 실패
```bash
# Ingress Controller 상태 확인
kubectl get pods -n ingress-nginx

# 로그 확인
kubectl logs -n ingress-nginx deployment/nginx-ingress-controller

# 재배포
kubectl delete -f 00-nginx-ingress-controller.yaml
kubectl apply -f 00-nginx-ingress-controller.yaml
```

#### 2. 외부 접근 불가
- EC2 보안 그룹에서 30080, 30443 포트 열기
- 방화벽(ufw) 설정 확인
- NodePort 서비스 상태 확인: `kubectl get svc -n ingress-nginx`

#### 3. Pod가 시작되지 않음
```bash
# Pod 상태 확인
kubectl get pods -n zmnnoory

# Pod 이벤트 확인
kubectl describe pod <POD-NAME> -n zmnnoory

# 로그 확인
kubectl logs <POD-NAME> -n zmnnoory
```

#### 4. 데이터베이스 연결 실패
- `host.docker.internal` 접근 가능 여부 확인
- 호스트에서 PostgreSQL/Redis 컨테이너 실행 상태 확인
- Docker 네트워크 설정 확인

## 📊 모니터링

### 기본 모니터링
```bash
# Pod 상태 실시간 확인
kubectl get pods -n zmnnoory -w

# NGINX Ingress Controller 상태
kubectl get pods -n ingress-nginx -w

# HPA 상태
kubectl get hpa -n zmnnoory -w

# 리소스 사용량
kubectl top pods -n zmnnoory
kubectl top nodes
```

### NGINX 메트릭
```bash
# NGINX Controller 메트릭
kubectl get svc -n ingress-nginx
curl http://<NODE-IP>:30080/nginx_status

# Prometheus 메트릭 (설정 시)
curl http://<NODE-IP>:30080/metrics
```

### 애플리케이션 메트릭
- Health Check: `http://<EC2-IP>:30080/actuator/health`
- Prometheus 메트릭: `http://<EC2-IP>:30080/actuator/prometheus`
- 애플리케이션 정보: `http://<EC2-IP>:30080/actuator/info`

## 🔗 관련 링크

- [NGINX Ingress Controller](https://kubernetes.github.io/ingress-nginx/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Kubernetes HPA](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)
- [Kustomize](https://kubernetes.io/docs/tasks/manage-kubernetes-objects/kustomization/) 