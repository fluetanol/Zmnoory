# 🏠 ZMNNOORY 로컬 테스트 환경 가이드

xlarge 인스턴스 배포 전에 로컬에서 다양한 방법으로 테스트할 수 있는 환경을 제공합니다.

## 🎯 테스트 옵션 비교

| 방법 | 복잡도 | 유사도 | 용도 | 시간 |
|------|--------|--------|------|------|
| **Docker Compose** | ⭐ | ⭐⭐ | 빠른 통합 테스트 | ~5분 |
| **로컬 Spring Boot + Docker DB** | ⭐ | ⭐ | 개발 중 테스트 | ~2분 |
| **Minikube** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | 완전한 K8s 테스트 | ~15분 |

## 🚀 빠른 시작

### 자동화 스크립트 사용
```bash
# 실행 권한 부여
chmod +x infra/local-test/test-script.sh

# Docker Compose 테스트
./infra/local-test/test-script.sh docker

# 로컬 Spring Boot + Docker DB
./infra/local-test/test-script.sh local

# Minikube 테스트
./infra/local-test/test-script.sh minikube

# API 테스트
./infra/local-test/test-script.sh test

# 정리
./infra/local-test/test-script.sh clean
```

---

## 📦 Option 1: Docker Compose (권장)

**장점**: 빠르고 간단, 전체 스택 테스트 가능
**단점**: Kubernetes 환경과 다름

### 실행
```bash
cd infra/local-test
docker-compose -f docker-compose-test.yml up -d
```

### 접근
- **애플리케이션**: http://localhost:8080
- **NGINX 로드밸런서**: http://localhost:80
- **Redis UI**: http://localhost:8001
- **Health Check**: http://localhost:8080/actuator/health

### 확인
```bash
# 서비스 상태
docker-compose -f docker-compose-test.yml ps

# 로그 확인
docker-compose -f docker-compose-test.yml logs zmnnoory-app

# 정리
docker-compose -f docker-compose-test.yml down -v
```

---

## 💻 Option 2: 로컬 Spring Boot + Docker DB

**장점**: 가장 빠름, IDE 디버깅 가능
**단점**: 컨테이너 환경과 다름

### 1. DB 서비스만 시작
```bash
cd infra
docker-compose up -d postgres redis
```

### 2. IDE에서 Spring Boot 실행
- **프로파일**: `local`
- **환경변수** (필요시):
  ```
  SPRING_PROFILES_ACTIVE=local
  DB_HOST=localhost
  REDIS_HOST=localhost
  ```

### 3. 접근
- **애플리케이션**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health

---

## ☸️ Option 3: Minikube (완전한 K8s 환경)

**장점**: 실제 환경과 가장 유사
**단점**: 설치/설정 복잡, 리소스 사용량 많음

### 사전 준비
1. [Minikube 설치](minikube-setup.md#사전-준비)
2. kubectl 설치
3. Docker Desktop 설치

### 실행
```bash
# 클러스터 시작
minikube start --cpus=2 --memory=4096

# Ingress Controller 활성화
minikube addons enable ingress
minikube addons enable metrics-server

# 이미지 빌드 (Minikube Docker 환경에서)
eval $(minikube docker-env)
docker build -t zmnnoory-local:latest .

# K8s 리소스 배포
cd infra/local-test/k8s
kubectl apply -f .

# 포트포워딩으로 접근
kubectl port-forward -n zmnnoory service/zmnnoory-service 8080:8080
```

### 접근
- **애플리케이션**: http://localhost:8080 (포트포워딩)
- **대시보드**: `minikube dashboard`

---

## 🧪 테스트 시나리오

### 1. 기본 기능 테스트
```bash
# Health Check
curl http://localhost:8080/actuator/health

# 환경 정보
curl http://localhost:8080/actuator/env

# 메트릭
curl http://localhost:8080/actuator/metrics
```

### 2. 데이터베이스 연결 테스트
```bash
# PostgreSQL 연결 확인
docker exec -it test_postgres_db psql -U root -d development_db -c "\dt"

# Redis 연결 확인
docker exec -it test_redis_stack redis-cli ping
```

### 3. API 엔드포인트 테스트
```bash
# Member API 예시 (실제 엔드포인트에 맞게 수정)
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'

curl http://localhost:8080/api/members
```

### 4. 로드밸런싱 테스트 (Docker Compose)
```bash
# NGINX를 통한 접근
curl http://localhost:80/actuator/health

# 직접 접근과 비교
curl http://localhost:8080/actuator/health
```

---

## 🔧 설정 커스터마이징

### 환경변수 오버라이드
```bash
# Docker Compose
export DB_PASSWORD=custom_password
docker-compose -f docker-compose-test.yml up -d

# 로컬 Spring Boot
export SPRING_PROFILES_ACTIVE=local
export JWT_SECRET_KEY=custom_secret
./gradlew bootRun
```

### 포트 변경
```yaml
# docker-compose-test.yml
services:
  zmnnoory-app:
    ports:
      - "9090:8080"  # 포트 충돌 시 변경
```

### 리소스 제한 (Minikube)
```yaml
# k8s/04-deployment.yaml
resources:
  requests:
    memory: "256Mi"
    cpu: "100m"
  limits:
    memory: "512Mi"
    cpu: "250m"
```

---

## 🔍 트러블슈팅

### Docker 관련 문제
```bash
# Docker 상태 확인
docker info

# 포트 충돌 확인
netstat -tlnp | grep 8080

# 이미지 재빌드
docker-compose -f docker-compose-test.yml build --no-cache
```

### 데이터베이스 연결 문제
```bash
# PostgreSQL 로그 확인
docker logs test_postgres_db

# 연결 테스트
telnet localhost 5432
```

### Minikube 문제
```bash
# 클러스터 상태
minikube status

# 클러스터 재시작
minikube stop && minikube start

# 리소스 부족 시
minikube start --cpus=1 --memory=2048
```

### Spring Boot 문제
```bash
# 프로파일 확인
curl http://localhost:8080/actuator/env | grep "activeProfiles"

# 로그 레벨 변경
curl -X POST http://localhost:8080/actuator/loggers/com.gradation.zmnnoory \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel":"DEBUG"}'
```

---

## 📋 체크리스트

### 테스트 전 확인사항
- [ ] Docker Desktop 실행됨
- [ ] 포트 8080, 5432, 6379 사용 가능
- [ ] 충분한 메모리 (최소 4GB)

### Docker Compose 테스트
- [ ] 서비스 모두 healthy 상태
- [ ] 애플리케이션 응답 확인
- [ ] 데이터베이스 연결 확인
- [ ] Redis 연결 확인

### Minikube 테스트
- [ ] 클러스터 정상 시작
- [ ] Ingress Controller 활성화
- [ ] Pod 모두 Running 상태
- [ ] 포트포워딩 정상 작동

### 성능 확인
- [ ] 응답 시간 2초 이내
- [ ] 메모리 사용량 500MB 이내
- [ ] CPU 사용량 50% 이내

---

## 🎓 추가 리소스

- [Minikube 상세 가이드](minikube-setup.md)
- [Docker Compose 설정](docker-compose-test.yml)
- [테스트 자동화 스크립트](test-script.sh)
- [Spring Boot 로컬 설정](../backend/ZMNNOORY/src/main/resources/application-local.yml) 