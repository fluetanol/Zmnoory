# Guess Expression - WebRTC 통신환경

이 모듈은 직접 WebRTC를 구현하여 실시간 통신환경을 구축한 감정 표현 맞추기 게임입니다.

## 주요 기능

### 1. WebRTC 연결 관리
- **직접 WebRTC 구현**: LiveKit 대신 직접 WebRTC API 사용
- **시그널링 서버**: 자체 시그널링 서버로 연결 관리
- **방 관리**: 방 코드 기반 참가자 연결
- **역할 구분**: 호스트와 참여자 역할 분리

### 2. 실시간 비디오 통신
- **로컬 비디오**: 사용자 카메라 스트림 표시
- **원격 비디오**: 다른 참가자들의 비디오 스트림 표시
- **자동 연결**: 참가자 입장/퇴장 시 자동 비디오 연결/해제

### 3. 게임 이벤트 동기화
- **데이터 패킷**: 실시간 게임 상태 동기화
- **이벤트 타입**:
  - `game_start`: 게임 시작
  - `answer_submit`: 답변 제출
  - `game_end`: 게임 종료
  - `emoji_change`: 이모지 변경

## 파일 구조

```
modules/
├── WebRTCManager.ts      # WebRTC 연결 및 이벤트 관리
├── useWebRTC.ts          # Vue 컴포지션 API 훅
├── types.ts              # TypeScript 타입 정의
└── webrtcConfig.ts       # WebRTC 설정

components/
├── ConnectionStatusBar.vue   # 연결 상태 표시
├── ConnectionLoading.vue     # 연결 중 로딩
└── ConnectionFailed.vue      # 연결 실패 처리

GuessExpressionView.vue       # 메인 게임 뷰
signaling-server.js           # 시그널링 서버
```

## 설치 및 실행

### 1. 의존성 설치
```bash
npm install
```

### 2. 시그널링 서버 실행
```bash
npm run signaling
```

### 3. 프론트엔드 개발 서버 실행
```bash
npm run dev
```

## 사용법

### 1. 방 참가
```typescript
const { connect, isConnected, participants } = useWebRTC();

// 방에 연결
await connect(roomCode, nickname, role);
```

### 2. 게임 이벤트 전송
```typescript
const { sendGameStart, sendAnswerSubmit, sendEmojiChange } = useWebRTC();

// 게임 시작 이벤트
await sendGameStart();

// 답변 제출
await sendAnswerSubmit(answer, correctAnswer);

// 이모지 변경
await sendEmojiChange(emoji);
```

### 3. 비디오 연결
```vue
<template>
  <!-- 로컬 비디오 -->
  <video ref="localVideo" autoplay playsinline muted></video>
  
  <!-- 원격 비디오들 -->
  <video 
    v-for="participant in participants" 
    :key="participant.identity"
    :ref="(el) => setVideoRef(participant.identity, el)"
    autoplay 
    playsinline
  ></video>
</template>

<script setup>
const { setLocalVideoRef, setVideoRef } = useWebRTC();

// 로컬 비디오 설정
setLocalVideoRef(localVideo.value);

// 원격 비디오 설정
const setRemoteVideoRef = (identity, el) => setVideoRef(identity, el);
</script>
```

## 설정

### 시그널링 서버
- **URL**: `ws://localhost:3001`
- **포트**: 3001 (환경변수 PORT로 변경 가능)

### STUN 서버
- Google STUN 서버 사용
- `stun:stun.l.google.com:19302`
- `stun:stun1.l.google.com:19302`
- `stun:stun2.l.google.com:19302`

## WebRTC 연결 과정

1. **시그널링 서버 연결**: WebSocket으로 시그널링 서버에 연결
2. **방 참가**: 방 코드와 참가자 정보를 서버에 전송
3. **피어 연결 생성**: 다른 참가자들과의 RTCPeerConnection 생성
4. **미디어 스트림 교환**: Offer/Answer 교환으로 미디어 스트림 연결
5. **ICE 후보 교환**: NAT 통과를 위한 ICE 후보 교환
6. **연결 완료**: P2P 연결이 완료되어 실시간 통신 시작

## 게임 플로우

1. **방 참가**: 방 코드와 닉네임 입력
2. **WebRTC 연결**: 시그널링 서버를 통한 P2P 연결
3. **비디오 스트림**: 카메라/마이크 권한 요청
4. **게임 시작**: 호스트가 게임 시작 이벤트 전송
5. **이모지 표시**: 호스트가 이모지 변경 이벤트 전송
6. **답변 제출**: 참가자들이 답변 선택 및 제출
7. **결과 공개**: 정답 및 결과 표시
8. **다음 라운드**: 반복

## 에러 처리

- **연결 실패**: 시그널링 서버 연결 상태 모니터링
- **권한 거부**: 카메라/마이크 권한 안내
- **네트워크 오류**: ICE 연결 상태 모니터링 및 재연결 시도

## 성능 최적화

- **P2P 연결**: 중앙 서버를 거치지 않는 직접 연결
- **STUN 서버**: NAT 통과 지원
- **효율적인 시그널링**: 필요한 정보만 교환

## 개발 참고사항

- TypeScript 타입 안전성 보장
- Vue 3 Composition API 사용
- 반응형 상태 관리
- 이벤트 기반 아키텍처
- 직접 WebRTC API 사용으로 더 나은 제어 가능

## 문제 해결

### 시그널링 서버 연결 실패
1. 시그널링 서버가 실행 중인지 확인
2. 포트 3001이 사용 가능한지 확인
3. 방화벽 설정 확인

### 비디오 연결 실패
1. 카메라/마이크 권한 확인
2. STUN 서버 연결 상태 확인
3. 네트워크 환경 확인

### 게임 이벤트 동기화 실패
1. 시그널링 서버 연결 상태 확인
2. 방 코드가 올바른지 확인
3. 참가자 역할 설정 확인
