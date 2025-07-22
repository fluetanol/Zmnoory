#!/bin/bash

# =================================================================
# ZMNNOORY Kubernetes 배포 스크립트
# =================================================================

set -e  # 에러 발생 시 스크립트 중단

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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
    echo "사용법: $0 [command] [options]"
    echo ""
    echo "Commands:"
    echo "  deploy    - 애플리케이션 배포"
    echo "  delete    - 애플리케이션 삭제"
    echo "  restart   - 애플리케이션 재시작"
    echo "  status    - 배포 상태 확인"
    echo "  logs      - 로그 확인"
    echo "  scale     - Pod 수 조정"
    echo ""
    echo "Examples:"
    echo "  $0 deploy"
    echo "  $0 scale 5"
    echo "  $0 logs"
}

# kubectl 명령어 존재 확인
check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl이 설치되지 않았습니다."
        exit 1
    fi
}

# 클러스터 연결 확인
check_cluster() {
    if ! kubectl cluster-info &> /dev/null; then
        log_error "Kubernetes 클러스터에 연결할 수 없습니다."
        log_info "kubeconfig 설정을 확인하세요."
        exit 1
    fi
}

# 네임스페이스 생성 확인
ensure_namespace() {
    log_info "네임스페이스 확인 중..."
    if ! kubectl get namespace zmnnoory &> /dev/null; then
        log_info "네임스페이스 'zmnnoory' 생성 중..."
        kubectl apply -f 01-namespace.yaml
    fi
    log_success "네임스페이스 준비 완료"
}

# 배포 함수
deploy() {
    log_info "ZMNNOORY 애플리케이션 배포 시작..."
    
    check_kubectl
    check_cluster
    
    # NGINX Ingress Controller 먼저 배포
    log_info "NGINX Ingress Controller 배포 중..."
    kubectl apply -f 00-nginx-ingress-controller.yaml
    
    # Ingress Controller가 준비될 때까지 대기
    log_info "NGINX Ingress Controller 준비 대기 중..."
    kubectl wait --namespace ingress-nginx \
        --for=condition=ready pod \
        --selector=app.kubernetes.io/name=ingress-nginx \
        --timeout=300s
    
    ensure_namespace
    
    # ConfigMap과 Secret 먼저 적용
    log_info "ConfigMap 적용 중..."
    kubectl apply -f 02-configmap.yaml
    
    log_info "Secret 적용 중..."
    kubectl apply -f 03-secret.yaml
    
    # Deployment 적용
    log_info "Deployment 적용 중..."
    kubectl apply -f 04-deployment.yaml
    
    # Service 적용
    log_info "Service 적용 중..."
    kubectl apply -f 05-service.yaml
    
    # HPA 적용
    log_info "HPA 적용 중..."
    kubectl apply -f 06-hpa.yaml
    
    # Ingress 적용
    log_info "Ingress 적용 중..."
    kubectl apply -f 07-ingress.yaml
    
    # 배포 상태 확인
    log_info "배포 상태 확인 중..."
    kubectl rollout status deployment/zmnnoory-backend -n zmnnoory --timeout=300s
    
    log_success "배포 완료!"
    show_status
}

# 삭제 함수
delete() {
    log_warning "ZMNNOORY 애플리케이션을 삭제하시겠습니까? (y/N)"
    read -r response
    
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        log_info "애플리케이션 삭제 중..."
        kubectl delete -f . --ignore-not-found=true
        log_success "삭제 완료"
    else
        log_info "삭제 취소됨"
    fi
}

# 재시작 함수
restart() {
    log_info "애플리케이션 재시작 중..."
    kubectl rollout restart deployment/zmnnoory-backend -n zmnnoory
    kubectl rollout status deployment/zmnnoory-backend -n zmnnoory --timeout=300s
    log_success "재시작 완료"
}

# 상태 확인 함수
show_status() {
    log_info "=== ZMNNOORY 애플리케이션 상태 ==="
    
    echo -e "\n${BLUE}Pods:${NC}"
    kubectl get pods -n zmnnoory -l app=zmnnoory-backend
    
    echo -e "\n${BLUE}Services:${NC}"
    kubectl get svc -n zmnnoory
    
    echo -e "\n${BLUE}HPA:${NC}"
    kubectl get hpa -n zmnnoory
    
    echo -e "\n${BLUE}Ingress:${NC}"
    kubectl get ingress -n zmnnoory 2>/dev/null || echo "Ingress 없음"
    
    # 외부 접근 URL 표시
    echo -e "\n${BLUE}외부 접근 정보:${NC}"
    
    # NodePort로 외부 접근
    echo "NGINX Ingress Controller NodePort 정보:"
    kubectl get svc ingress-nginx -n ingress-nginx
    
    # 노드 IP 확인
    NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="ExternalIP")].address}' 2>/dev/null)
    if [[ -z "$NODE_IP" ]]; then
        NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}' 2>/dev/null)
    fi
    
    if [[ -n "$NODE_IP" ]]; then
        echo -e "\n${GREEN}애플리케이션 접근 URL:${NC}"
        echo "HTTP:  http://$NODE_IP:30080"
        echo "HTTPS: https://$NODE_IP:30443"
        echo ""
        echo "EC2 보안 그룹에서 30080, 30443 포트를 열어야 합니다."
    else
        echo "노드 IP를 찾을 수 없습니다. 다음 명령어로 확인하세요:"
        echo "kubectl get nodes -o wide"
    fi
}

# 로그 확인 함수
show_logs() {
    POD_COUNT=$(kubectl get pods -n zmnnoory -l app=zmnnoory-backend --no-headers | wc -l)
    
    if [[ $POD_COUNT -eq 0 ]]; then
        log_error "실행 중인 Pod가 없습니다."
        exit 1
    elif [[ $POD_COUNT -eq 1 ]]; then
        POD_NAME=$(kubectl get pods -n zmnnoory -l app=zmnnoory-backend -o jsonpath='{.items[0].metadata.name}')
        log_info "Pod $POD_NAME 로그:"
        kubectl logs -f $POD_NAME -n zmnnoory
    else
        log_info "여러 Pod가 실행 중입니다. 선택하세요:"
        kubectl get pods -n zmnnoory -l app=zmnnoory-backend
        echo -n "Pod 이름을 입력하세요: "
        read -r POD_NAME
        kubectl logs -f $POD_NAME -n zmnnoory
    fi
}

# 스케일 조정 함수
scale() {
    if [[ -z "$1" ]]; then
        log_error "replica 수를 지정하세요."
        echo "예: $0 scale 5"
        exit 1
    fi
    
    log_info "Pod 수를 $1개로 조정 중..."
    kubectl scale deployment zmnnoory-backend --replicas=$1 -n zmnnoory
    kubectl rollout status deployment/zmnnoory-backend -n zmnnoory --timeout=300s
    log_success "스케일 조정 완료"
}

# 메인 로직
case "${1:-}" in
    deploy)
        deploy "$2"
        ;;
    delete)
        delete
        ;;
    restart)
        restart
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    scale)
        scale "$2"
        ;;
    *)
        usage
        exit 1
        ;;
esac 