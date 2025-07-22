#!/bin/bash

# =================================================================
# ZMNNOORY 로컬 테스트 스크립트
# =================================================================

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

# 사용법 출력
usage() {
    echo "사용법: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  docker     - Docker Compose로 테스트"
    echo "  minikube   - Minikube로 테스트"
    echo "  local      - 로컬 Spring Boot + Docker DB"
    echo "  clean      - 모든 테스트 환경 정리"
    echo "  test       - API 테스트 실행"
    echo ""
}

# Docker 설치 확인
check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker가 설치되지 않았습니다."
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        log_error "Docker가 실행되지 않았습니다."
        exit 1
    fi
}

# Docker Compose 테스트
test_docker_compose() {
    log_info "Docker Compose 테스트 시작..."
    
    check_docker
    
    cd "$(dirname "$0")"
    
    # Docker Compose 실행
    log_info "서비스 시작 중..."
    docker-compose -f docker-compose-test.yml up -d
    
    # 헬스체크 대기
    log_info "서비스 준비 대기 중..."
    sleep 30
    
    # 애플리케이션 상태 확인
    log_info "애플리케이션 상태 확인 중..."
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        log_success "애플리케이션이 정상적으로 시작되었습니다!"
        log_info "접근 URL:"
        echo "  - 직접 접근: http://localhost:8080"
        echo "  - NGINX를 통한 접근: http://localhost:80"
        echo "  - Health Check: http://localhost:8080/actuator/health"
        echo "  - Redis UI: http://localhost:8001"
    else
        log_error "애플리케이션 시작에 실패했습니다."
        log_info "로그 확인:"
        docker-compose -f docker-compose-test.yml logs zmnnoory-app
        exit 1
    fi
}

# Minikube 테스트
test_minikube() {
    log_info "Minikube 테스트 시작..."
    
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
    
    # Minikube 상태 확인
    if ! minikube status > /dev/null 2>&1; then
        log_info "Minikube 클러스터 시작 중..."
        minikube start --cpus=2 --memory=4096
    fi
    
    # Ingress 애드온 활성화
    log_info "NGINX Ingress Controller 활성화 중..."
    minikube addons enable ingress
    
    # 메트릭 서버 활성화
    minikube addons enable metrics-server
    
    # Docker 환경 설정
    log_info "Docker 이미지 빌드 중..."
    eval $(minikube docker-env)
    
    cd "$(dirname "$0")/../.."
    docker build -t zmnnoory-local:latest .
    
    # Kubernetes 배포
    cd infra/local-test/k8s
    log_info "Kubernetes 리소스 배포 중..."
    kubectl apply -f .
    
    # 배포 상태 확인
    log_info "배포 상태 확인 중..."
    kubectl rollout status deployment/zmnnoory-backend -n zmnnoory --timeout=300s
    
    # 포트포워딩
    log_info "포트포워딩 설정 중..."
    kubectl port-forward -n zmnnoory service/zmnnoory-service 8080:8080 &
    PORT_FORWARD_PID=$!
    
    sleep 10
    
    # 애플리케이션 테스트
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        log_success "Minikube 환경에서 애플리케이션이 정상 작동합니다!"
        log_info "접근 URL: http://localhost:8080"
        log_info "포트포워딩 중지: kill $PORT_FORWARD_PID"
    else
        log_error "애플리케이션 접근에 실패했습니다."
        kill $PORT_FORWARD_PID 2>/dev/null || true
        exit 1
    fi
}

# 로컬 Spring Boot 테스트
test_local_springboot() {
    log_info "로컬 Spring Boot + Docker DB 테스트..."
    
    check_docker
    
    cd "$(dirname "$0")/.."
    
    # DB만 시작
    log_info "데이터베이스 서비스 시작 중..."
    docker-compose up -d postgres redis
    
    # DB 준비 대기
    log_info "데이터베이스 준비 대기 중..."
    sleep 15
    
    log_success "데이터베이스가 준비되었습니다!"
    log_info "이제 IDE에서 Spring Boot 애플리케이션을 실행하세요."
    log_info "프로파일: local"
    log_info "DB 연결: localhost:5432"
    log_info "Redis 연결: localhost:6379"
}

# API 테스트
test_api() {
    log_info "API 테스트 실행 중..."
    
    # 기본 URL 설정
    BASE_URL=${1:-"http://localhost:8080"}
    
    # Health Check
    log_info "Health Check 테스트..."
    if curl -f "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        log_success "✓ Health Check 통과"
    else
        log_error "✗ Health Check 실패"
        return 1
    fi
    
    # API 엔드포인트 테스트 (실제 엔드포인트에 맞게 수정 필요)
    log_info "API 엔드포인트 테스트..."
    
    # 예시: Member API 테스트
    # if curl -f "$BASE_URL/api/members" > /dev/null 2>&1; then
    #     log_success "✓ Members API 통과"
    # else
    #     log_warning "⚠ Members API 응답 없음 (정상일 수 있음)"
    # fi
    
    log_success "기본 API 테스트 완료"
}

# 정리 함수
clean_all() {
    log_info "테스트 환경 정리 중..."
    
    # Docker Compose 정리
    cd "$(dirname "$0")"
    if [ -f "docker-compose-test.yml" ]; then
        docker-compose -f docker-compose-test.yml down -v
    fi
    
    cd ..
    if [ -f "docker-compose.yml" ]; then
        docker-compose down -v
    fi
    
    # Minikube 정리 (선택사항)
    # minikube delete
    
    log_success "정리 완료"
}

# 메인 로직
case "${1:-}" in
    docker)
        test_docker_compose
        ;;
    minikube)
        test_minikube
        ;;
    local)
        test_local_springboot
        ;;
    test)
        test_api "$2"
        ;;
    clean)
        clean_all
        ;;
    *)
        usage
        exit 1
        ;;
esac 