// WebRTC 설정 및 유틸리티 함수

// WebRTC 기본 설정
export const WEBRTC_CONFIG = {
  // LiveKit 서버 설정
  server: {
    url: "wss://zmnnoory.r-e.kr/",
    apiKey: "your-api-key",
    apiSecret: "your-api-secret",
  },

  // 방 설정
  room: {
    defaultName: "guess-expression-room",
    maxParticipants: 10,
    emptyTimeout: 300, // 5분
    maxDuration: 3600, // 1시간
  },

  // 미디어 설정
  media: {
    video: {
      width: { ideal: 1280 },
      height: { ideal: 720 },
      frameRate: { ideal: 30 },
    },
    audio: {
      echoCancellation: true,
      noiseSuppression: true,
      autoGainControl: true,
    },
  },

  // 연결 설정
  connection: {
    timeout: 10000, // 10초
    retryAttempts: 3,
    retryDelay: 1000, // 1초
  },

  // 비디오 품질 설정
  videoQuality: {
    low: {
      width: 640,
      height: 480,
      frameRate: 15,
    },
    medium: {
      width: 1280,
      height: 720,
      frameRate: 30,
    },
    high: {
      width: 1920,
      height: 1080,
      frameRate: 30,
    },
  },
};

// WebRTC 설정 가져오기 함수
export function getWebRTCConfig() {
  return WEBRTC_CONFIG;
}

// 방 코드 생성 함수
export function generateRoomCode(): string {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  let result = "";
  for (let i = 0; i < 6; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

// 방 코드 유효성 검사 함수
export function validateRoomCode(code: string): boolean {
  // 6자리 알파벳 대문자와 숫자 조합
  const roomCodeRegex = /^[A-Z0-9]{6}$/;
  return roomCodeRegex.test(code);
}

// 방 이름 생성 함수
export function generateRoomName(roomCode: string): string {
  return `guess-expression-${roomCode}`;
}

// 미디어 제약 조건 생성 함수
export function createMediaConstraints(
  quality: "low" | "medium" | "high" = "medium"
) {
  const qualityConfig = WEBRTC_CONFIG.videoQuality[quality];

  return {
    video: {
      width: { ideal: qualityConfig.width },
      height: { ideal: qualityConfig.height },
      frameRate: { ideal: qualityConfig.frameRate },
    },
    audio: WEBRTC_CONFIG.media.audio,
  };
}

// 연결 상태 확인 함수
export function checkConnectionHealth(room: any): boolean {
  if (!room) return false;

  // 연결 상태 확인
  const isConnected = room.connectionState === "connected";
  const hasParticipants = room.participants.size > 0;

  return isConnected && hasParticipants;
}

// 네트워크 품질 평가 함수
export function evaluateNetworkQuality(stats: any): "good" | "fair" | "poor" {
  if (!stats) return "poor";

  // 패킷 손실률 기준
  const packetLoss = stats.packetLoss || 0;

  if (packetLoss < 2) return "good";
  if (packetLoss < 5) return "fair";
  return "poor";
}

// 자동 품질 조정 함수
export function adjustVideoQuality(
  networkQuality: "good" | "fair" | "poor"
): "low" | "medium" | "high" {
  switch (networkQuality) {
    case "good":
      return "high";
    case "fair":
      return "medium";
    case "poor":
      return "low";
    default:
      return "medium";
  }
}
