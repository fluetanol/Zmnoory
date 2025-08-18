// WebRTC 관련 타입 정의

// WebRTC 참가자 인터페이스
export interface WebRTCParticipant {
  identity: string;
  name: string;
  isLocal: boolean;
  videoReady: boolean;
  audioReady: boolean;
  score: number;
}

// WebRTC 로그 인터페이스
export interface WebRTCLog {
  id: number;
  message: string;
  type: "info" | "success" | "warning" | "error";
  timestamp: string;
}

// WebRTC 연결 설정 인터페이스
export interface WebRTCConnectionConfig {
  roomCode: string;
  participantName: string;
  serverUrl: string;
  token: string;
}

// WebRTC 상태 인터페이스
export interface WebRTCState {
  isConnected: boolean;
  isConnecting: boolean;
  participants: WebRTCParticipant[];
  logs: WebRTCLog[];
  error: string | null;
}

// WebRTC 이벤트 핸들러 인터페이스
export interface WebRTCEventHandlers {
  onStateChange?: (state: WebRTCState) => void;
  onLog?: (log: WebRTCLog) => void;
  onParticipantJoin?: (participant: WebRTCParticipant) => void;
  onParticipantLeave?: (participantId: string) => void;
  onTrackSubscribed?: (track: any, participant: WebRTCParticipant) => void;
  onTrackUnsubscribed?: (track: any, participant: WebRTCParticipant) => void;
}

// 비디오 참조 인터페이스
export interface VideoRefs {
  [participantId: string]: HTMLVideoElement | null;
}

// LiveKit 관련 타입들
export interface LiveKitRoom {
  connectionState: string;
  participants: Map<string, LiveKitParticipant>;
  localParticipant: LiveKitParticipant;
  disconnect(): Promise<void>;
  on(event: string, callback: Function): void;
}

export interface LiveKitParticipant {
  identity: string;
  name?: string;
  isLocal: boolean;
  isMicrophoneEnabled: boolean;
  isCameraEnabled: boolean;
  getTrack(source: any): any;
  setMicrophoneEnabled(enabled: boolean): Promise<void>;
  setCameraEnabled(enabled: boolean): Promise<void>;
  on(event: string, callback: Function): void;
}

export interface LiveKitTrack {
  kind: string;
  source: any;
  attach(element: HTMLVideoElement): void;
}

export interface LiveKitTrackPublication {
  track?: LiveKitTrack;
  kind: string;
}

export interface LiveKitRemoteTrack extends LiveKitTrack {
  // 원격 트랙 특정 속성들
}

export interface LiveKitLocalTrack extends LiveKitTrack {
  // 로컬 트랙 특정 속성들
}

// JWT 토큰 페이로드 인터페이스
export interface JwtPayload {
  room: string;
  participant: string;
  exp: number;
  iat: number;
}

// 게임 상태 관련 타입들
export interface GameState {
  stage: "preGame" | "connecting" | "Game" | "connectionFailed";
  currentEmoji: string;
  remainingTime: number;
  gameStage: "first_step" | "submit_answer" | "check_answer" | "final_step";
  selectedAnswer: string | null;
  answerChoices: AnswerChoice[];
  participants: WebRTCParticipant[];
}

export interface AnswerChoice {
  emoji: string;
  emotion: string;
  label: string;
}

export interface GameEvent {
  type: "state_change" | "answer_submit" | "participant_update";
  data: any;
}

export interface AnswerSubmission {
  participantId: string;
  emotion: string;
  timestamp: number;
}
