export interface GameManifest {
  /** 프론트 카탈로그의 고유 키(폴더명과 같게) */
  slug: string;

  /** 기본 메타(서버 값이 있으면 UI에선 서버 값 우선 사용 가능) */
  title: string;
  description?: string;
  thumbnail?: string; // new URL(...).href 권장

  /** 태그/분류 */
  tags?: string[];

  /** 실행 전 요구 조건(권한/환경) */
  requires?: {
    camera?: boolean;
    microphone?: boolean;
    webgl?: boolean;   // three.js 등
    webrtc?: boolean;  // p2p/회의류
  };

  /** 런타임 힌트(레이아웃/모드 등) */
  runtime?: {
    isMultiplayer?: boolean;
    maxPlayers?: number;
  };

  /** UI 힌트 */
  ui?: {
    aspectRatio?: string;       // '16/9' | '4/3' | '1/1' ...
    allowFullscreen?: boolean;  // 전체화면 버튼 노출 여부
    theme?: 'dark' | 'light';
  };

  /** 조작법/안내문 */
  instructions?: string[];

  /** 호환성 */
  compatibility?: {
    minAppVersion?: string;           // 호스트 앱 최소 버전
    supportedBrowsers?: string[];     // 'Chrome >= 110' 식으로
    mobile?: 'required' | 'ok' | 'not-recommended' | 'unsupported';
  };

  /** 게임 설정(선택): 호스트에서 옵션 패널 만들어줄 때 사용 */
  settings?: {
    defaults: Record<string, unknown>;
    // 간단한 유효성 함수(선택)
    validate?: (s: Record<string, unknown>) => boolean | string;
  };
}
