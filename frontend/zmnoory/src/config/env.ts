// guess-expression 환경 설정 (Vite 환경변수 기반)

// 사용 가능한 변수
// - VITE_GAME_SERVER_URL: 예) http://localhost:8080, https://zmnnoory.r-e.kr/game
// - VITE_LIVEKIT_URL: 예) wss://localhost:7880, wss://zmnnoory.r-e.kr/webrtc/livekit

function getEnv(key: string): string | undefined {
  try {
    // @ts-ignore
    return import.meta.env?.[key];
  } catch {
    return undefined;
  }
}

function inferProtocolFromHost(host: string): { http: string; ws: string } {
  const isLocal = /^(localhost|127\.0\.0\.1|\[::1\])(?::\d+)?$/i.test(host);
  return {
    http: isLocal ? 'http' : 'https',
    ws: isLocal ? 'ws' : 'wss',
  };
}

export function buildGameServerUrlFromHost(hostWithOptionalPath: string): string {
  // hostWithOptionalPath 예) localhost:8080, zmnnoory.r-e.kr, zmnnoory.r-e.kr/game
  if (/^https?:\/\//i.test(hostWithOptionalPath)) return hostWithOptionalPath;
  const { http } = inferProtocolFromHost(hostWithOptionalPath.split('/')[0] || hostWithOptionalPath);
  return `${http}://${hostWithOptionalPath}`;
}

export function buildLiveKitUrlFromHost(hostWithOptionalPath: string): string {
  if (/^wss?:\/\//i.test(hostWithOptionalPath)) return hostWithOptionalPath;
  const { ws } = inferProtocolFromHost(hostWithOptionalPath.split('/')[0] || hostWithOptionalPath);
  return `${ws}://${hostWithOptionalPath}`;
}

// 기본값은 EC2 환경(프로덕션) 값으로 두고, 개발자는 .env.local 로 오버라이드
const DEFAULT_GAME_SERVER_URL = 'https://zmnnoory.r-e.kr/game';
const DEFAULT_LIVEKIT_URL = 'wss://zmnnoory.r-e.kr/webrtc/livekit';

export const GAME_SERVER_URL: string =
  getEnv('VITE_GAME_SERVER_URL') || DEFAULT_GAME_SERVER_URL;

export const LIVEKIT_URL: string =
  getEnv('VITE_LIVEKIT_URL') || DEFAULT_LIVEKIT_URL;


