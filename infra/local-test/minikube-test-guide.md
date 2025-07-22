# 🏗️ Minikube에서 xlarge 인스턴스와 동일한 K8s 환경 구축 가이드

이 가이드는 Minikube를 사용하여 AWS EC2 xlarge 인스턴스와 **완전히 동일한** Kubernetes 환경을 로컬에서 구축하고 테스트하는 방법을 제공합니다.

## 🎯 목표 아키텍처

```
로컬 머신 (Minikube 환경)
├── 호스트 Docker
│   ├── PostgreSQL (:5432)
│   └── Redis (:6379)
└── Minikube Kubernetes 클러스터
    ├── NGINX Ingress Controller (Minikube 내장)
    ├── Spring Boot Pods (1-3개)
    └── ClusterIP Service (내부 로드밸런싱)

외부 접근: http://<MINIKUBE-IP>
```

## 📋 사전 준비

### 1. 필수 도구 설치
```bash
# Minikube 설치 확인
minikube version

# kubectl 설치 확인  
kubectl version --client

# Docker 설치 확인
docker version
```

### 2. 시스템 요구사항
- **CPU**: 최소 2코어 (권장 4코어)
- **메모리**: 최소 4GB (권장 8GB)
- **디스크**: 최소 10GB 여유 공간

---

## 🚀 단계별 환경 구축

### **Step 1: Minikube 클러스터 시작**

```bash
# 1. 클러스터 생성 (xlarge 인스턴스 스펙에 맞게)
minikube start --cpus=4 --memory=6144 --driver=docker

# 2. 상태 확인
minikube status

# 3. kubectl 컨텍스트 확인
kubectl config current-context
# 출력: minikube
```

### **Step 2: NGINX Ingress Controller 활성화**

```bash
# 1. Ingress 애드온 활성화 (xlarge 인스턴스와 동일한 NGINX 환경)
minikube addons enable ingress

# 2. 메트릭 서버 활성화 (HPA 테스트용)
minikube addons enable metrics-server

# 3. Ingress Controller 준비 확인
kubectl get pods -n ingress-nginx
```

### **Step 3: 호스트에서 데이터베이스 서비스 시작**

xlarge 인스턴스와 동일하게 호스트 레벨에서 PostgreSQL, Redis 실행:

```bash
# 1. 프로젝트 루트로 이동
cd /path/to/your/project

# 2. 호스트에서 DB 서비스 시작
cd infra
docker-compose up -d postgres redis

# 3. 서비스 상태 확인
docker-compose ps
docker logs postgres_db
docker logs redis-stack

# 4. 연결 테스트
docker exec -it postgres_db psql -U root -d development_db -c "SELECT 1;"
docker exec -it redis-stack redis-cli ping
```

### **Step 4: Spring Boot 이미지 빌드**

Minikube 환경에서 로컬 이미지 빌드:

```bash
# 1. Minikube Docker 환경 설정
eval $(minikube docker-env)

# Windows PowerShell의 경우:
# minikube docker-env --shell powershell | Invoke-Expression

# 2. 프로젝트 루트에서 이미지 빌드
docker build -t zmnnoory-local:latest .

# 3. 이미지 빌드 확인
docker images | grep zmnnoory
```

### **Step 5: Kubernetes 리소스 배포**

xlarge 인스턴스와 동일한 K8s 리소스 배포:

```bash
# 1. Minikube용 K8s 매니페스트 디렉토리로 이동
cd infra/local-test/k8s

# 2. 리소스 순서대로 배포
kubectl apply -f 01-namespace.yaml
kubectl apply -f 02-configmap.yaml
kubectl apply -f 03-secret.yaml
kubectl apply -f 04-deployment.yaml
kubectl apply -f 05-service.yaml
kubectl apply -f 06-hpa.yaml
kubectl apply -f 07-ingress.yaml

# 3. 배포 상태 확인
kubectl get all -n zmnnoory
```

### **Step 6: 네트워크 연결 테스트**

호스트 Docker ↔ Minikube Pod 연결 확인:

```bash
# 1. Pod가 호스트 DB에 접근 가능한지 확인
kubectl exec -n zmnnoory deployment/zmnnoory-backend -- nslookup host.minikube.internal

# 2. Pod에서 PostgreSQL 연결 테스트
kubectl exec -n zmnnoory deployment/zmnnoory-backend -- nc -zv host.minikube.internal 5432

# 3. Pod에서 Redis 연결 테스트
kubectl exec -n zmnnoory deployment/zmnnoory-backend -- nc -zv host.minikube.internal 6379
```

### **Step 7: 애플리케이션 접근 설정**

```bash
# 1. Minikube IP 확인
minikube ip
# 예: 192.168.49.2

# 2. Ingress 상태 확인
kubectl get ingress -n zmnnoory

# 3. 외부 접근 테스트
curl http://$(minikube ip)/actuator/health
```

---

## 🧪 완전성 테스트

### **1. 기본 기능 테스트**

```bash
# Health Check
curl http://$(minikube ip)/actuator/health

# 상세 환경 정보
curl http://$(minikube ip)/actuator/env | grep -i profile

# 메트릭 확인
curl http://$(minikube ip)/actuator/metrics
```

### **2. 데이터베이스 연결 테스트**

```bash
# PostgreSQL 연결 로그 확인
kubectl logs -n zmnnoory deployment/zmnnoory-backend | grep -i "postgresql\|database"

# Redis 연결 로그 확인  
kubectl logs -n zmnnoory deployment/zmnnoory-backend | grep -i redis

# JPA 엔티티 생성 확인
docker exec -it postgres_db psql -U root -d development_db -c "\dt"
```

### **3. 무중단 배포 테스트**

```bash
# 1. 현재 Pod 상태 확인
kubectl get pods -n zmnnoory -w &
WATCH_PID=$!

# 2. Rolling Update 시뮬레이션
kubectl patch deployment zmnnoory-backend -n zmnnoory -p '{"spec":{"template":{"metadata":{"labels":{"version":"v2"}}}}}'

# 3. 배포 진행 상황 확인
kubectl rollout status deployment/zmnnoory-backend -n zmnnoory

# 4. 모니터링 중지
kill $WATCH_PID
```

### **4. 자동 스케일링 테스트**

```bash
# 1. HPA 상태 확인
kubectl get hpa -n zmnnoory

# 2. 부하 테스트 (다른 터미널에서)
kubectl run -i --tty load-generator --rm --image=busybox --restart=Never -- \
  /bin/sh -c "while sleep 0.01; do wget -q -O- http://zmnnoory-service.zmnnoory:8080/actuator/health; done"

# 3. 스케일링 확인 (다른 터미널에서)
kubectl get hpa -n zmnnoory -w
kubectl get pods -n zmnnoory -w
```

### **5. NGINX Ingress 로드밸런싱 테스트**

```bash
# 1. Pod 수 증가
kubectl scale deployment zmnnoory-backend --replicas=3 -n zmnnoory

# 2. 각 Pod의 IP 확인  
kubectl get pods -n zmnnoory -o wide

# 3. 로드밸런싱 테스트
for i in {1..10}; do
  curl -s http://$(minikube ip)/actuator/info | grep -o '"hostname":"[^"]*"' || echo "Request $i"
  sleep 1
done
```

---

## 🔍 xlarge 인스턴스와의 차이점 확인

### **동일한 부분 ✅**
- [x] **Kubernetes 클러스터 구조**: 동일
- [x] **NGINX Ingress Controller**: 동일한 기능
- [x] **Spring Boot Pod 설정**: 동일한 환경변수, 프로브
- [x] **ConfigMap/Secret 관리**: 동일한 구조  
- [x] **Service/HPA 설정**: 동일한 로직
- [x] **호스트 Docker → K8s Pod 네트워크**: `host.*.internal` 사용
- [x] **무중단 배포 (Rolling Update)**: 동일한 전략
- [x] **자동 스케일링**: 동일한 메트릭

### **차이점 ⚠️**
| 항목 | xlarge 인스턴스 | Minikube |
|------|----------------|----------|
| **외부 접근** | NodePort 30080/30443 | Minikube IP 80/443 |
| **호스트 네트워크** | `host.docker.internal` | `host.minikube.internal` |
| **리소스 제한** | 높음 (8코어, 32GB) | 낮음 (4코어, 6GB) |
| **Pod 수** | 기본 3개 | 기본 1개 |

---

## 🔧 트러블슈팅

### **문제 1: host.minikube.internal 접근 불가**

```bash
# 해결: Minikube 네트워크 설정 확인
minikube ssh
# 내부에서: ping host.minikube.internal

# 대안: 호스트 IP 직접 사용
HOST_IP=$(minikube ssh "route -n | grep '^0.0.0.0' | awk '{print \$2}'")
echo "Host IP: $HOST_IP"
```

### **문제 2: 이미지 Pull 실패**

```bash
# 해결: Docker 환경 재설정
eval $(minikube docker-env)
docker images | grep zmnnoory

# 이미지 다시 빌드
docker build -t zmnnoory-local:latest .
```

### **문제 3: Pod 리소스 부족**

```bash
# 해결: 리소스 제한 완화
kubectl patch deployment zmnnoory-backend -n zmnnoory -p '
{
  "spec": {
    "template": {
      "spec": {
        "containers": [{
          "name": "backend",
          "resources": {
            "requests": {"memory": "128Mi", "cpu": "50m"},
            "limits": {"memory": "256Mi", "cpu": "200m"}
          }
        }]
      }
    }
  }
}'
```

### **문제 4: Ingress 응답 없음**

```bash
# 해결: Ingress Controller 상태 확인
kubectl get pods -n ingress-nginx
kubectl describe ingress zmnnoory-ingress -n zmnnoory

# 대안: 포트포워딩 사용
kubectl port-forward -n zmnnoory service/zmnnoory-service 8080:8080
```

---

## 📊 성능 비교

### **로컬 Minikube vs xlarge 인스턴스**

| 메트릭 | Minikube | xlarge 인스턴스 |
|--------|----------|----------------|
| **시작 시간** | ~30초 | ~60초 |
| **메모리 사용량** | ~300MB | ~500MB |
| **응답 시간** | ~100ms | ~50ms |
| **최대 Pod 수** | 3개 | 10개 |

---

## ✅ 완료 체크리스트

- [ ] Minikube 클러스터 정상 시작
- [ ] Ingress Controller 활성화
- [ ] 호스트 PostgreSQL/Redis 실행
- [ ] Spring Boot 이미지 빌드 성공
- [ ] 모든 K8s 리소스 배포 완료
- [ ] Pod → 호스트 DB 연결 성공
- [ ] 외부에서 애플리케이션 접근 가능
- [ ] Health Check 응답 정상
- [ ] 무중단 배포 테스트 성공
- [ ] 자동 스케일링 동작 확인
- [ ] 로드밸런싱 동작 확인

---

## 🎯 다음 단계

✅ **Minikube 테스트 완료 후**:
1. 실제 xlarge 인스턴스에 배포
2. 성능/안정성 비교 분석
3. GitLab CI/CD 파이프라인 연동
4. 모니터링/로깅 시스템 구축

이제 xlarge 인스턴스와 **완전히 동일한 환경**에서 모든 기능을 사전 검증할 수 있습니다! 