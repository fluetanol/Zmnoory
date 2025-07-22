# =================================================================
# Minikube xlarge 인스턴스 동일 환경 구축 PowerShell 스크립트
# Windows 환경에서 Kubernetes 환경을 자동으로 구축
# =================================================================

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("setup", "start", "build", "deploy", "test", "status", "clean")]
    [string]$Command = "help"
)

# 색상 설정
function Write-ColorText {
    param(
        [string]$Text,
        [string]$Color = "White"
    )
    
    switch ($Color) {
        "Red" { Write-Host $Text -ForegroundColor Red }
        "Green" { Write-Host $Text -ForegroundColor Green }
        "Yellow" { Write-Host $Text -ForegroundColor Yellow }
        "Blue" { Write-Host $Text -ForegroundColor Blue }
        "Cyan" { Write-Host $Text -ForegroundColor Cyan }
        "Magenta" { Write-Host $Text -ForegroundColor Magenta }
        default { Write-Host $Text -ForegroundColor White }
    }
}

# 로그 함수
function Write-Info { param([string]$Message) Write-ColorText "[INFO] $Message" "Blue" }
function Write-Success { param([string]$Message) Write-ColorText "[SUCCESS] $Message" "Green" }
function Write-Warning { param([string]$Message) Write-ColorText "[WARNING] $Message" "Yellow" }
function Write-Error { param([string]$Message) Write-ColorText "[ERROR] $Message" "Red" }
function Write-Step { param([string]$Message) Write-ColorText "[STEP] $Message" "Magenta" }

# 사용법 출력
function Show-Usage {
    Write-Host "Minikube xlarge 인스턴스 동일 환경 구축 PowerShell 스크립트" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "사용법: .\start-minikube.ps1 -Command <command>" -ForegroundColor White
    Write-Host ""
    Write-Host "Commands:" -ForegroundColor Yellow
    Write-Host "  setup      - 전체 환경 구축 (권장)" -ForegroundColor White
    Write-Host "  start      - Minikube 클러스터만 시작" -ForegroundColor White
    Write-Host "  build      - Docker 이미지 빌드" -ForegroundColor White
    Write-Host "  deploy     - K8s 리소스 배포" -ForegroundColor White
    Write-Host "  test       - 환경 테스트" -ForegroundColor White
    Write-Host "  status     - 현재 상태 확인" -ForegroundColor White
    Write-Host "  clean      - 환경 정리" -ForegroundColor White
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Yellow
    Write-Host "  .\start-minikube.ps1 -Command setup    # 전체 환경 구축" -ForegroundColor Green
    Write-Host "  .\start-minikube.ps1 -Command test     # 환경 테스트" -ForegroundColor Green
    Write-Host "  .\start-minikube.ps1 -Command clean    # 정리" -ForegroundColor Green
}

# 사전 요구사항 확인
function Test-Prerequisites {
    Write-Step "사전 요구사항 확인 중..."
    
    # PowerShell 버전 확인
    if ($PSVersionTable.PSVersion.Major -lt 5) {
        Write-Error "PowerShell 5.0 이상이 필요합니다. 현재 버전: $($PSVersionTable.PSVersion)"
        exit 1
    }
    
    # 관리자 권한 확인
    if (-NOT ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
        Write-Warning "관리자 권한으로 실행하는 것을 권장합니다."
    }
    
    # Minikube 설치 확인
    try {
        $null = Get-Command minikube -ErrorAction Stop
        Write-Info "✓ Minikube 설치 확인됨"
    }
    catch {
        Write-Error "Minikube가 설치되지 않았습니다."
        Write-Info "설치 가이드: https://minikube.sigs.k8s.io/docs/start/"
        exit 1
    }
    
    # kubectl 설치 확인
    try {
        $null = Get-Command kubectl -ErrorAction Stop
        Write-Info "✓ kubectl 설치 확인됨"
    }
    catch {
        Write-Error "kubectl이 설치되지 않았습니다."
        Write-Info "설치: choco install kubernetes-cli"
        exit 1
    }
    
    # Docker 설치 및 실행 확인
    try {
        $null = Get-Command docker -ErrorAction Stop
        $dockerInfo = docker info 2>$null
        if ($LASTEXITCODE -ne 0) {
            throw "Docker가 실행되지 않았습니다."
        }
        Write-Info "✓ Docker 실행 확인됨"
    }
    catch {
        Write-Error "Docker가 설치되지 않았거나 실행되지 않았습니다."
        Write-Info "Docker Desktop을 시작하세요."
        exit 1
    }
    
    Write-Success "모든 사전 요구사항이 충족되었습니다."
}

# Minikube 클러스터 시작
function Start-MinikubeCluster {
    Write-Step "Minikube 클러스터 시작 중..."
    
    # 기존 클러스터 상태 확인
    $minikubeStatus = minikube status 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Info "기존 Minikube 클러스터가 실행 중입니다."
    }
    else {
        Write-Info "새로운 Minikube 클러스터를 시작합니다..."
        # xlarge 인스턴스와 유사한 리소스로 시작
        minikube start --cpus=4 --memory=6144 --driver=docker
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Minikube 클러스터 시작에 실패했습니다."
            exit 1
        }
    }
    
    # kubectl 컨텍스트 설정
    kubectl config use-context minikube
    
    Write-Success "Minikube 클러스터가 준비되었습니다."
}

# NGINX Ingress Controller 설정
function Setup-IngressController {
    Write-Step "NGINX Ingress Controller 설정 중..."
    
    # Ingress 애드온 활성화
    Write-Info "Ingress 애드온 활성화 중..."
    minikube addons enable ingress
    
    # 메트릭 서버 활성화 (HPA용)
    Write-Info "메트릭 서버 활성화 중..."
    minikube addons enable metrics-server
    
    # Ingress Controller 준비 대기
    Write-Info "Ingress Controller 준비 대기 중..."
    kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/name=ingress-nginx --timeout=300s
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "NGINX Ingress Controller가 준비되었습니다."
    }
    else {
        Write-Warning "Ingress Controller 준비에 시간이 걸리고 있습니다."
    }
}

# 호스트 데이터베이스 서비스 시작
function Start-HostDatabases {
    Write-Step "호스트 데이터베이스 서비스 시작 중..."
    
    # 프로젝트 루트로 이동
    $projectRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
    Set-Location $projectRoot
    
    # infra 디렉토리로 이동
    Set-Location "infra"
    
    # PostgreSQL, Redis 시작
    Write-Info "PostgreSQL과 Redis 시작 중..."
    docker-compose up -d postgres redis
    
    # 서비스 준비 대기
    Write-Info "데이터베이스 서비스 준비 대기 중..."
    Start-Sleep -Seconds 15
    
    # 연결 테스트
    $postgresReady = docker exec postgres_db pg_isready -U root -d development_db 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "PostgreSQL이 준비되었습니다."
    }
    else {
        Write-Error "PostgreSQL 시작에 실패했습니다."
        exit 1
    }
    
    $redisReady = docker exec redis-stack redis-cli ping 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Redis가 준비되었습니다."
    }
    else {
        Write-Error "Redis 시작에 실패했습니다."
        exit 1
    }
}

# Docker 이미지 빌드
function Build-DockerImage {
    Write-Step "Spring Boot Docker 이미지 빌드 중..."
    
    # Minikube Docker 환경 설정
    Write-Info "Minikube Docker 환경 설정 중..."
    minikube docker-env --shell powershell | Invoke-Expression
    
    # 프로젝트 루트로 이동
    $projectRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
    Set-Location $projectRoot
    
    # 이미지 빌드
    Write-Info "Docker 이미지 빌드 중... (시간이 걸릴 수 있습니다)"
    docker build -t zmnnoory-local:latest .
    
    # 빌드 확인
    $imageExists = docker images | Select-String "zmnnoory-local"
    if ($imageExists) {
        Write-Success "Docker 이미지가 성공적으로 빌드되었습니다."
    }
    else {
        Write-Error "Docker 이미지 빌드에 실패했습니다."
        exit 1
    }
}

# Kubernetes 리소스 배포
function Deploy-KubernetesResources {
    Write-Step "Kubernetes 리소스 배포 중..."
    
    # K8s 매니페스트 디렉토리로 이동
    $k8sPath = Join-Path $PSScriptRoot "k8s"
    Set-Location $k8sPath
    
    # 순서대로 리소스 배포
    $resources = @(
        "01-namespace.yaml",
        "02-configmap.yaml", 
        "03-secret.yaml",
        "04-deployment.yaml",
        "05-service.yaml",
        "06-hpa.yaml",
        "07-ingress.yaml"
    )
    
    foreach ($resource in $resources) {
        if (Test-Path $resource) {
            Write-Info "배포 중: $resource"
            kubectl apply -f $resource
        }
        else {
            Write-Warning "파일을 찾을 수 없습니다: $resource"
        }
    }
    
    # 배포 상태 확인
    Write-Info "배포 상태 확인 중..."
    kubectl rollout status deployment/zmnnoory-backend -n zmnnoory --timeout=300s
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Kubernetes 리소스가 성공적으로 배포되었습니다."
    }
    else {
        Write-Warning "배포 상태 확인에 시간이 걸리고 있습니다."
    }
}

# 네트워크 연결 테스트
function Test-NetworkConnectivity {
    Write-Step "네트워크 연결 테스트 중..."
    
    # Pod 준비 대기
    Write-Info "Pod 준비 대기 중..."
    kubectl wait --for=condition=ready pod -l app=zmnnoory-backend -n zmnnoory --timeout=300s
    
    # Pod 이름 가져오기
    $podName = kubectl get pods -n zmnnoory -l app=zmnnoory-backend -o jsonpath="{.items[0].metadata.name}" 2>$null
    if (-not $podName) {
        Write-Error "실행 중인 Pod를 찾을 수 없습니다."
        return
    }
    
    # 호스트 네트워크 접근 테스트
    Write-Info "호스트 네트워크 접근 테스트..."
    $hostLookup = kubectl exec -n zmnnoory $podName -- nslookup host.minikube.internal 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "호스트 네트워크 접근 가능"
    }
    else {
        Write-Warning "호스트 네트워크 접근에 문제가 있을 수 있습니다."
    }
    
    # PostgreSQL 연결 테스트
    Write-Info "PostgreSQL 연결 테스트..."
    $pgTest = kubectl exec -n zmnnoory $podName -- nc -zv host.minikube.internal 5432 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "PostgreSQL 연결 가능"
    }
    else {
        Write-Warning "PostgreSQL 연결에 문제가 있을 수 있습니다."
    }
    
    # Redis 연결 테스트
    Write-Info "Redis 연결 테스트..."
    $redisTest = kubectl exec -n zmnnoory $podName -- nc -zv host.minikube.internal 6379 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Redis 연결 가능"
    }
    else {
        Write-Warning "Redis 연결에 문제가 있을 수 있습니다."
    }
}

# 애플리케이션 테스트
function Test-Application {
    Write-Step "애플리케이션 테스트 중..."
    
    # Minikube IP 확인
    $minikubeIP = minikube ip
    Write-Info "Minikube IP: $minikubeIP"
    
    # 애플리케이션 시작 대기
    Write-Info "애플리케이션 시작 대기 중..."
    Start-Sleep -Seconds 30
    
    # Health Check 테스트
    Write-Info "Health Check 테스트..."
    try {
        $healthResponse = Invoke-RestMethod -Uri "http://$minikubeIP/actuator/health" -Method Get -TimeoutSec 10
        Write-Success "Health Check 통과"
        Write-Info "Health Status: $($healthResponse.status)"
    }
    catch {
        Write-Error "Health Check 실패: $($_.Exception.Message)"
        
        # 디버깅 정보 출력
        Write-Info "디버깅 정보:"
        kubectl get pods -n zmnnoory
        kubectl logs -n zmnnoory deployment/zmnnoory-backend --tail=20
        return $false
    }
    
    # 프로파일 확인
    Write-Info "Spring Boot 프로파일 확인..."
    try {
        $envResponse = Invoke-RestMethod -Uri "http://$minikubeIP/actuator/env" -Method Get -TimeoutSec 10
        $activeProfile = $envResponse.activeProfiles
        if ($activeProfile -contains "k8s") {
            Write-Success "올바른 프로파일 사용: k8s"
        }
        else {
            Write-Warning "예상과 다른 프로파일: $activeProfile"
        }
    }
    catch {
        Write-Warning "프로파일 확인 실패: $($_.Exception.Message)"
    }
    
    return $true
}

# 상태 확인
function Show-Status {
    Write-Step "현재 환경 상태 확인"
    
    Write-ColorText "`n=== Minikube 상태 ===" "Blue"
    minikube status
    
    Write-ColorText "`n=== 호스트 데이터베이스 ===" "Blue"
    $projectRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
    Set-Location "$projectRoot\infra"
    docker-compose ps postgres redis
    
    Write-ColorText "`n=== Kubernetes 리소스 ===" "Blue"
    kubectl get all -n zmnnoory
    
    Write-ColorText "`n=== Ingress 정보 ===" "Blue"
    kubectl get ingress -n zmnnoory
    
    Write-ColorText "`n=== 접근 URL ===" "Blue"
    $minikubeIP = minikube ip
    Write-Success "애플리케이션: http://$minikubeIP"
    Write-Success "Health Check: http://$minikubeIP/actuator/health"
    Write-Success "Minikube 대시보드: minikube dashboard"
}

# 환경 정리
function Clear-Environment {
    Write-Step "환경 정리 중..."
    
    # Kubernetes 리소스 삭제
    Write-Info "Kubernetes 리소스 삭제 중..."
    $k8sPath = Join-Path $PSScriptRoot "k8s"
    if (Test-Path $k8sPath) {
        kubectl delete -f $k8sPath --ignore-not-found=true
    }
    
    # 호스트 데이터베이스 정리
    Write-Info "호스트 데이터베이스 정리 중..."
    $projectRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
    Set-Location "$projectRoot\infra"
    docker-compose down -v
    
    # Minikube 정리 옵션
    $response = Read-Host "Minikube 클러스터를 삭제하시겠습니까? (y/N)"
    if ($response -match "^[Yy]") {
        Write-Info "Minikube 클러스터 삭제 중..."
        minikube delete
        Write-Success "Minikube 클러스터가 삭제되었습니다."
    }
    else {
        Write-Info "Minikube 클러스터 중지 중..."
        minikube stop
        Write-Info "Minikube 클러스터가 중지되었습니다."
    }
    
    Write-Success "환경 정리가 완료되었습니다."
}

# 전체 환경 구축
function Setup-CompleteEnvironment {
    Write-Info "=== Minikube xlarge 인스턴스 동일 환경 구축 시작 ==="
    
    Test-Prerequisites
    Start-MinikubeCluster
    Setup-IngressController
    Start-HostDatabases
    Build-DockerImage
    Deploy-KubernetesResources
    Test-NetworkConnectivity
    $testResult = Test-Application
    
    if ($testResult) {
        Write-Success "=== 환경 구축이 완료되었습니다! ==="
        
        # 접근 정보 표시
        $minikubeIP = minikube ip
        Write-ColorText "`n🎉 xlarge 인스턴스와 동일한 환경이 준비되었습니다!" "Green"
        Write-ColorText "`n접근 정보:" "Blue"
        Write-Host "  애플리케이션: http://$minikubeIP"
        Write-Host "  Health Check: http://$minikubeIP/actuator/health"
        Write-Host "  Minikube 대시보드: minikube dashboard"
        Write-ColorText "`n다음 단계:" "Yellow"
        Write-Host "  1. 무중단 배포 테스트: kubectl patch deployment zmnnoory-backend -n zmnnoory -p '{""spec"":{""template"":{""metadata"":{""labels"":{""version"":""v2""}}}}}}'"
        Write-Host "  2. 자동 스케일링 테스트: kubectl run load-generator --image=busybox --restart=Never"
        Write-Host "  3. 상태 확인: .\start-minikube.ps1 -Command status"
    }
    else {
        Write-Error "환경 구축 중 문제가 발생했습니다."
        exit 1
    }
}

# 메인 실행 로직
switch ($Command) {
    "setup" { Setup-CompleteEnvironment }
    "start" { Test-Prerequisites; Start-MinikubeCluster; Setup-IngressController }
    "build" { Test-Prerequisites; Build-DockerImage }
    "deploy" { Deploy-KubernetesResources }
    "test" { Test-NetworkConnectivity; Test-Application }
    "status" { Show-Status }
    "clean" { Clear-Environment }
    default { Show-Usage; exit 1 }
} 