#!/bin/bash

# =================================================================
# Minikube 완전 동일 환경 구축 자동화 스크립트
# xlarge 인스턴스와 동일한 Kubernetes 환경을 로컬에 구축
# =================================================================

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m'

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${PURPLE}[STEP]${NC} $1"
}

# 사용법 출력
usage() {
    echo "Minikube xlarge 인스턴스 동일 환경 구축 스크립트"
    echo ""
    echo "사용법: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  setup      - 전체 환경 구축 (권장)"
    echo "  start      - Minikube 클러스터만 시작"
    echo "  build      - Docker 이미지 빌드"
    echo "  deploy     - K8s 리소스 배포"
    echo "  test       - 환경 테스트"
    echo "  clean      - 환경 정리"
    echo "  status     - 현재 상태 확인"
    echo ""
    echo "Examples:"
    echo "  $0 setup    # 처음 시작시 전체 환경 구축"
    echo "  $0 test     # 환경 테스트"
    echo "  $0 clean    # 정리"
}

# 사전 요구사항 확인
check_prerequisites() {
    log_step "사전 요구사항 확인 중..."
    
    # Minikube 설치 확인
    if ! command -v minikube &> /dev/null; then
        log_error "Minikube가 설치되지 않았습니다."
        log_info "설치 가이드: https://minikube.sigs.k8s.io/docs/start/"
        exit 1
    fi
    
    # kubectl 설치 확인
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl이 설치되지 않았습니다."
        exit 1
    fi
    
    # Docker 설치 확인
    if ! command -v docker &> /dev/null; then
        log_error "Docker가 설치되지 않았습니다."
        exit 1
    fi
    
    # Docker 실행 확인
    if ! docker info &> /dev/null; then
        log_error "Docker가 실행되지 않았습니다. Docker Desktop을 시작하세요."
        exit 1
    fi
    
    log_success "모든 사전 요구사항이 충족되었습니다."
}

# Minikube 클러스터 시작
start_minikube() {
    log_step "Minikube 클러스터 시작 중..."
    
    # 기존 클러스터 상태 확인
    if minikube status &> /dev/null; then
        log_info "기존 Minikube 클러스터가 실행 중입니다."
    else
        log_info "새로운 Minikube 클러스터를 시작합니다..."
        # xlarge 인스턴스와 유사한 리소스로 시작
        minikube start --cpus=4 --memory=6144 --driver=docker
    fi
    
    # kubectl 컨텍스트 설정
    kubectl config use-context minikube
    
    log_success "Minikube 클러스터가 준비되었습니다."
}

# NGINX Ingress Controller 설정
setup_ingress() {
    log_step "NGINX Ingress Controller 설정 중..."
    
    # Ingress 애드온 활성화
    minikube addons enable ingress
    
    # 메트릭 서버 활성화 (HPA용)
    minikube addons enable metrics-server
    
    # Ingress Controller 준비 대기
    log_info "Ingress Controller 준비 대기 중..."
    kubectl wait --namespace ingress-nginx \
        --for=condition=ready pod \
        --selector=app.kubernetes.io/name=ingress-nginx \
        --timeout=300s
    
    log_success "NGINX Ingress Controller가 준비되었습니다."
}

# 호스트 데이터베이스 서비스 시작
start_host_databases() {
    log_step "호스트 데이터베이스 서비스 시작 중..."
    
    # 프로젝트 루트로 이동
    cd "$(dirname "$0")/../.."
    
    # PostgreSQL, Redis 시작
    cd infra
    docker-compose up -d postgres redis
    
    # 서비스 준비 대기
    log_info "데이터베이스 서비스 준비 대기 중..."
    sleep 15
    
    # 연결 테스트
    if docker exec postgres_db pg_isready -U root -d development_db &> /dev/null; then
        log_success "PostgreSQL이 준비되었습니다."
    else
        log_error "PostgreSQL 시작에 실패했습니다."
        exit 1
    fi
    
    if docker exec redis-stack redis-cli ping &> /dev/null; then
        log_success "Redis가 준비되었습니다."
    else
        log_error "Redis 시작에 실패했습니다."
        exit 1
    fi
}

# Docker 이미지 빌드
build_image() {
    log_step "Spring Boot Docker 이미지 빌드 중..."
    
    # Minikube Docker 환경 설정
    eval $(minikube docker-env)
    
    # 프로젝트 루트로 이동
    cd "$(dirname "$0")/../.."
    
    # 이미지 빌드
    log_info "Docker 이미지 빌드 중... (시간이 걸릴 수 있습니다)"
    docker build -t zmnnoory-local:latest .
    
    # 빌드 확인
    if docker images | grep -q "zmnnoory-local"; then
        log_success "Docker 이미지가 성공적으로 빌드되었습니다."
    else
        log_error "Docker 이미지 빌드에 실패했습니다."
        exit 1
    fi
}

# Kubernetes 리소스 배포
deploy_k8s() {
    log_step "Kubernetes 리소스 배포 중..."
    
    # K8s 매니페스트 디렉토리로 이동
    cd "$(dirname "$0")/k8s"
    
    # 순서대로 리소스 배포
    resources=(
        "01-namespace.yaml"
        "02-configmap.yaml"
        "03-secret.yaml"
        "04-deployment.yaml"
        "05-service.yaml"
        "06-hpa.yaml"
        "07-ingress.yaml"
    )
    
    for resource in "${resources[@]}"; do
        if [ -f "$resource" ]; then
            log_info "배포 중: $resource"
            kubectl apply -f "$resource"
        else
            log_warning "파일을 찾을 수 없습니다: $resource"
        fi
    done
    
    # 배포 상태 확인
    log_info "배포 상태 확인 중..."
    kubectl rollout status deployment/zmnnoory-backend -n zmnnoory --timeout=300s
    
    log_success "Kubernetes 리소스가 성공적으로 배포되었습니다."
}

# 네트워크 연결 테스트
test_network() {
    log_step "네트워크 연결 테스트 중..."
    
    # Pod 준비 대기
    kubectl wait --for=condition=ready pod -l app=zmnnoory-backend -n zmnnoory --timeout=300s
    
    # 호스트 네트워크 접근 테스트
    log_info "호스트 네트워크 접근 테스트..."
    if kubectl exec -n zmnnoory deployment/zmnnoory-backend -- nslookup host.minikube.internal &> /dev/null; then
        log_success "호스트 네트워크 접근 가능"
    else
        log_warning "호스트 네트워크 접근에 문제가 있을 수 있습니다."
    fi
    
    # PostgreSQL 연결 테스트
    log_info "PostgreSQL 연결 테스트..."
    if kubectl exec -n zmnnoory deployment/zmnnoory-backend -- nc -zv host.minikube.internal 5432 &> /dev/null; then
        log_success "PostgreSQL 연결 가능"
    else
        log_warning "PostgreSQL 연결에 문제가 있을 수 있습니다."
    fi
    
    # Redis 연결 테스트
    log_info "Redis 연결 테스트..."
    if kubectl exec -n zmnnoory deployment/zmnnoory-backend -- nc -zv host.minikube.internal 6379 &> /dev/null; then
        log_success "Redis 연결 가능"
    else
        log_warning "Redis 연결에 문제가 있을 수 있습니다."
    fi
}

# 애플리케이션 테스트
test_application() {
    log_step "애플리케이션 테스트 중..."
    
    # Minikube IP 확인
    MINIKUBE_IP=$(minikube ip)
    log_info "Minikube IP: $MINIKUBE_IP"
    
    # Health Check 테스트
    log_info "Health Check 테스트..."
    sleep 10  # 애플리케이션 시작 대기
    
    if curl -f "http://$MINIKUBE_IP/actuator/health" &> /dev/null; then
        log_success "Health Check 통과"
    else
        log_error "Health Check 실패"
        log_info "디버깅 정보:"
        kubectl get pods -n zmnnoory
        kubectl logs -n zmnnoory deployment/zmnnoory-backend --tail=20
        return 1
    fi
    
    # 프로파일 확인
    log_info "Spring Boot 프로파일 확인..."
    PROFILE=$(curl -s "http://$MINIKUBE_IP/actuator/env" | grep -o '"spring.profiles.active":{"value":"[^"]*"' | cut -d'"' -f6)
    if [ "$PROFILE" = "k8s" ]; then
        log_success "올바른 프로파일 사용: $PROFILE"
    else
        log_warning "예상과 다른 프로파일: $PROFILE"
    fi
}

# 상태 확인
show_status() {
    log_step "현재 환경 상태 확인"
    
    echo -e "\n${BLUE}=== Minikube 상태 ===${NC}"
    minikube status
    
    echo -e "\n${BLUE}=== 호스트 데이터베이스 ===${NC}"
    cd "$(dirname "$0")/../.."
    docker-compose -f infra/docker-compose.yml ps postgres redis
    
    echo -e "\n${BLUE}=== Kubernetes 리소스 ===${NC}"
    kubectl get all -n zmnnoory
    
    echo -e "\n${BLUE}=== Ingress 정보 ===${NC}"
    kubectl get ingress -n zmnnoory
    
    echo -e "\n${BLUE}=== 접근 URL ===${NC}"
    MINIKUBE_IP=$(minikube ip)
    echo "애플리케이션: http://$MINIKUBE_IP"
    echo "Health Check: http://$MINIKUBE_IP/actuator/health"
    echo "Minikube 대시보드: minikube dashboard"
}

# 환경 정리
clean_environment() {
    log_step "환경 정리 중..."
    
    # Kubernetes 리소스 삭제
    log_info "Kubernetes 리소스 삭제 중..."
    if [ -d "$(dirname "$0")/k8s" ]; then
        kubectl delete -f "$(dirname "$0")/k8s" --ignore-not-found=true
    fi
    
    # 호스트 데이터베이스 정리
    log_info "호스트 데이터베이스 정리 중..."
    cd "$(dirname "$0")/../.."
    docker-compose -f infra/docker-compose.yml down -v
    
    # Minikube 정리 (선택사항)
    log_warning "Minikube 클러스터를 삭제하시겠습니까? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        minikube delete
        log_success "Minikube 클러스터가 삭제되었습니다."
    else
        minikube stop
        log_info "Minikube 클러스터가 중지되었습니다."
    fi
    
    log_success "환경 정리가 완료되었습니다."
}

# 전체 환경 구축
setup_complete() {
    log_info "=== Minikube xlarge 인스턴스 동일 환경 구축 시작 ==="
    
    check_prerequisites
    start_minikube
    setup_ingress
    start_host_databases
    build_image
    deploy_k8s
    test_network
    test_application
    
    log_success "=== 환경 구축이 완료되었습니다! ==="
    
    # 접근 정보 표시
    MINIKUBE_IP=$(minikube ip)
    echo -e "\n${GREEN}🎉 xlarge 인스턴스와 동일한 환경이 준비되었습니다!${NC}"
    echo -e "\n${BLUE}접근 정보:${NC}"
    echo "  애플리케이션: http://$MINIKUBE_IP"
    echo "  Health Check: http://$MINIKUBE_IP/actuator/health"
    echo "  Minikube 대시보드: minikube dashboard"
    echo ""
    echo -e "${YELLOW}다음 단계:${NC}"
    echo "  1. 무중단 배포 테스트: kubectl patch deployment zmnnoory-backend -n zmnnoory -p '{\"spec\":{\"template\":{\"metadata\":{\"labels\":{\"version\":\"v2\"}}}}}'"
    echo "  2. 자동 스케일링 테스트: kubectl run -i --tty load-generator --rm --image=busybox --restart=Never -- /bin/sh"
    echo "  3. 상태 확인: $0 status"
}

# 메인 로직
case "${1:-}" in
    setup)
        setup_complete
        ;;
    start)
        check_prerequisites
        start_minikube
        setup_ingress
        ;;
    build)
        check_prerequisites
        build_image
        ;;
    deploy)
        deploy_k8s
        ;;
    test)
        test_network
        test_application
        ;;
    status)
        show_status
        ;;
    clean)
        clean_environment
        ;;
    *)
        usage
        exit 1
        ;;
esac 