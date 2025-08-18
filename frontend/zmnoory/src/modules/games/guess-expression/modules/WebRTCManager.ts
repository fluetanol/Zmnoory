import { Room, RoomEvent, Participant, Track, RemoteTrack } from 'livekit-client';
import { GAME_SERVER_URL as GAME_SERVER_URL_ENV } from '@/config/env';

const GAME_SERVER_URL = GAME_SERVER_URL_ENV;

export class WebRTCManager {
  private room: Room | null = null;
  private videoRefs: Map<string, HTMLVideoElement> = new Map();
  private localVideoRef: HTMLVideoElement | null = null;
  private state: any = {
    isConnected: false,
    isConnecting: false,
    participants: [],
    logs: [],
    error: null,
  };
  private eventHandlers: any = {};
  // 개발용: 서버에서 토큰 발급만 사용하며, 별도 상태 저장 불필요

  constructor() {
    this.setupDefaultEventHandlers();
  }

  async connect(
    roomCode: string,
    participantName: string,
    // role은 내부에서 사용하지 않으므로 제거
  ): Promise<void> {
    try {
      this.updateState({ isConnecting: true, error: null });
      // 개발용: 클라이언트에 룸/참가자 상태 저장하지 않음

      // 서버에서 LiveKit 토큰과 URL 조회 시도
      let token: string | null = null;
      let serverUrl: string = undefined as unknown as string; // 토큰 응답에서 우선 사용

      try {
        const resp = await fetch(`${GAME_SERVER_URL}/rooms/${encodeURIComponent(roomCode)}/token/${encodeURIComponent(participantName)}`);
        if (resp.ok) {
          const data = await resp.json();
          token = data.token;
          serverUrl = data.livekitUrl || serverUrl;
          this.addLog('서버 발급 LiveKit 토큰 사용', 'info');
        } else {
          this.addLog('서버 토큰 조회 실패, 로컬 토큰 생성으로 대체', 'warning');
        }
      } catch (_err) {
        this.addLog('서버 토큰 조회 오류, 로컬 토큰 생성으로 대체', 'warning');
      }

      // 서버 토큰이 없으면 중단 (보안상 클라이언트 토큰 생성 금지)
      if (!token) {
        throw new Error('LiveKit 토큰을 가져오지 못했습니다. 게임 서버 연결 상태를 확인하세요.');
      }

      // Room 생성 및 연결
      this.room = new Room({
        adaptiveStream: true,
        dynacast: true,
        publishDefaults: {
          simulcast: true,
        },
      });

      // Room 이벤트 설정
      this.setupRoomEvents();

      // LiveKit 서버에 연결
      // serverUrl이 비어있으면 환경 기본값으로 대체
      const livekitUrl = serverUrl || (await import('@/config/env')).LIVEKIT_URL;
      await this.room.connect(livekitUrl, token, {
        autoSubscribe: true
        // 중요: LiveKit이 제공하는 ICE 서버(턴 포함)를 사용하도록 rtcConfig를 지정하지 않습니다.
      });

      // 로컬 미디어 시작
      await this.startLocalMedia();

      // 기존 참가자들 초기화
      this.initializeExistingParticipants();

      this.updateState({
        isConnected: true,
        isConnecting: false,
        error: null,
      });

      this.addLog("LiveKit 서버 연결 성공", "success");
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : String(error);
      this.updateState({
        isConnected: false,
        isConnecting: false,
        error: message,
      });
      this.addLog(`LiveKit 연결 실패: ${message}`, "error");
      throw error;
    }
  }

  async disconnect(): Promise<void> {
    if (this.room) {
      this.room.disconnect();
      this.room = null;
    }

    // 비디오 참조 정리
    this.videoRefs.clear();
    this.localVideoRef = null;

    this.updateState({
      isConnected: false,
      isConnecting: false,
      participants: [],
      error: null,
    });

    this.addLog("LiveKit 연결 해제됨", "info");
  }

  async toggleAudio(): Promise<void> {
    if (!this.room) return;

    const localParticipant = this.room.localParticipant;
    if (localParticipant.isMicrophoneEnabled) {
      await localParticipant.setMicrophoneEnabled(false);
      this.addLog("마이크 비활성화", "info");
    } else {
      await localParticipant.setMicrophoneEnabled(true);
      this.addLog("마이크 활성화", "info");
    }
  }

  async toggleVideo(): Promise<void> {
    if (!this.room) return;

    const localParticipant = this.room.localParticipant;
    if (localParticipant.isCameraEnabled) {
      await localParticipant.setCameraEnabled(false);
      this.addLog("카메라 비활성화", "info");
    } else {
      await localParticipant.setCameraEnabled(true);
      this.addLog("카메라 활성화", "info");
    }
  }

  // 데이터 패킷 전송 (게임 상태 동기화용)
  async sendDataPacket(data: any): Promise<void> {
    if (!this.room) return;

    try {
      const dataBuffer = new TextEncoder().encode(JSON.stringify(data));
      await this.room.localParticipant.publishData(dataBuffer);
      this.addLog("데이터 패킷 전송됨", "info");
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : String(error);
      this.addLog(`데이터 패킷 전송 실패: ${message}`, "error");
    }
  }

  setVideoRef(
    participantId: string,
    videoElement: HTMLVideoElement | null
  ): void {
    if (videoElement) {
      this.videoRefs.set(participantId, videoElement);
      // 렌더 루프 직후에 기존 트랙을 붙여 후발 입장자도 즉시 보이도록 함
      Promise.resolve().then(() => this.attachExistingVideoForParticipant(participantId));
    } else {
      this.videoRefs.delete(participantId);
    }
  }

  setLocalVideoRef(videoElement: HTMLVideoElement | null): void {
    this.localVideoRef = videoElement;
    this.tryAttachLocalVideo();
  }

  getState(): any {
    return { ...this.state };
  }

  clearLogs(): void {
    this.updateState({ logs: [] });
  }

  // 클라이언트에서 토큰 생성은 금지합니다. 반드시 서버에서 발급받아야 합니다.

  private setupRoomEvents(): void {
    if (!this.room) return;

    this.room.on(RoomEvent.ParticipantConnected, (participant: Participant) => {
      this.addLog(`${participant.identity} 참가자가 연결됨`, "info");
      this.ensureParticipantEntry(participant);
      this.setupParticipantEvents(participant);
      // 후발 입장자의 기존 트랙을 즉시 붙이기
      this.attachExistingVideoForParticipant(participant.identity);
    });

    this.room.on(RoomEvent.ParticipantDisconnected, (participant: Participant) => {
      this.addLog(`${participant.identity} 참가자가 연결 해제됨`, "info");
      this.removeParticipant(participant);
    });

    this.room.on(RoomEvent.DataReceived, (payload: Uint8Array, participant?: any) => {
      try {
        const data = JSON.parse(new TextDecoder().decode(payload));
        this.addLog(`${participant?.identity || 'Unknown'}로부터 데이터 수신: ${JSON.stringify(data)}`, "info");
        this.handleGameEvent(data, participant?.identity || 'Unknown');
      } catch (error) {
        console.error("데이터 패킷 파싱 실패:", error);
      }
    });
  }

  private setupParticipantEvents(participant: Participant): void {
    participant.on('trackSubscribed', (track: RemoteTrack) => {
      this.attachRemoteVideoElement(participant.identity, track);
    });

    participant.on('trackUnsubscribed', (_track: RemoteTrack) => {
      this.disconnectParticipantVideo(participant.identity);
    });

    // 후발 입장 시 이미 publish된 트랙을 즉시 붙여주기 위한 보완
    const tryAttach = () => this.attachExistingVideoForParticipant(participant.identity);
    participant.on('trackPublished', tryAttach as any);
    participant.on('trackMuted', tryAttach as any);
    participant.on('trackUnmuted', tryAttach as any);
  }

  private ensureParticipantEntry(participant: Participant): void {
    const existingParticipant = this.state.participants.find((p: any) => p.identity === participant.identity);
    if (!existingParticipant) {
      const newParticipant = {
        identity: participant.identity,
        name: participant.identity,
        isLocal: participant.isLocal,
        videoReady: false,
        audioReady: false,
        score: 0,
      };
      this.updateState({
        participants: [...this.state.participants, newParticipant],
      });
    }
  }

  private removeParticipant(participant: Participant): void {
    this.updateState({
      participants: this.state.participants.filter((p: any) => p.identity !== participant.identity),
    });
    this.disconnectParticipantVideo(participant.identity);
  }

  private initializeExistingParticipants(): void {
    if (!this.room) return;

    this.room.remoteParticipants.forEach((participant) => {
      this.ensureParticipantEntry(participant as unknown as Participant);
      this.setupParticipantEvents(participant as unknown as Participant);
      this.attachExistingVideoForParticipant(participant.identity);
    });
  }

  private attachExistingVideoForParticipant(participantId: string): void {
    if (!this.room) return;

    const participant = this.room.remoteParticipants.get(participantId);
    const videoElement = this.videoRefs.get(participantId) || null;
    if (participant && videoElement) {
      const cameraPub = Array.from(participant.videoTrackPublications.values())[0];
      const videoTrack = cameraPub?.videoTrack;
      if (videoTrack) {
        videoTrack.attach(videoElement);
        this.updateParticipantVideoStatus(participantId, true);
        this.addLog(`${participantId} 기존 원격 비디오 연결됨`, "success");
      }
    }
  }

  private attachRemoteVideoElement(participantId: string, track: RemoteTrack): void {
    const videoElement = this.videoRefs.get(participantId);
    if (videoElement && track.kind === Track.Kind.Video) {
      track.attach(videoElement);
      this.updateParticipantVideoStatus(participantId, true);
      this.addLog(`${participantId} 원격 비디오 연결됨`, "success");
    }
  }

  private disconnectParticipantVideo(participantId: string): void {
    const videoElement = this.videoRefs.get(participantId);
    if (videoElement) {
      videoElement.srcObject = null;
      this.updateParticipantVideoStatus(participantId, false);
    }
  }

  private async startLocalMedia(): Promise<void> {
    if (!this.room) return;

    try {
      await this.room.localParticipant.enableCameraAndMicrophone();
      this.addLog("로컬 미디어 활성화됨", "success");
      // 카메라가 활성화된 직후 로컬 비디오 엘리먼트에 트랙을 붙인다
      this.tryAttachLocalVideo();
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : String(error);
      this.addLog(`로컬 미디어 활성화 실패: ${message}`, "error");
      throw error;
    }
  }

  private tryAttachLocalVideo(): void {
    if (!this.localVideoRef || !this.room) return;

    const localParticipant = this.room.localParticipant;
    const publications = Array.from(localParticipant.videoTrackPublications.values());
    const pub = publications[0];
    const videoTrack = pub?.videoTrack;
    if (videoTrack) videoTrack.attach(this.localVideoRef);
  }

  private handleGameEvent(data: any, senderId: string): void {
    switch (data.type) {
      case 'game_start':
        this.addLog(`게임 시작 - ${senderId}`, "info");
        break;
      case 'answer_submit':
        this.addLog(`${senderId} 답변 제출: ${data.answer}`, "info");
        break;
      case 'set_round':
        // 호스트가 데이터채널로 브로드캐스트한 라운드 정보는 즉시 서버로도 전달하여 동기화
        this.forwardSetRoundToServer(data).catch(() => {});
        this.addLog(`라운드 설정 수신 - ${senderId}`, 'info');
        break;
      case 'game_end':
        this.addLog(`게임 종료 - ${senderId}`, "info");
        break;
      default:
        this.addLog(`알 수 없는 게임 이벤트: ${data.type}`, "warning");
    }
  }

  private async forwardSetRoundToServer(payload: { roomCode: string; currentEmoji: string; answerChoices: any[] }) {
    try {
      const res = await fetch(`${GAME_SERVER_URL}/rooms/${encodeURIComponent(payload.roomCode)}`);
      if (!res.ok) return; // 방 존재 확인
    } catch { /* ignore */ }
    try {
      // 서버 WebSocket으로도 set_round 이벤트를 보내야 하지만, 현재는 HTTP API가 없어
      // 데이터채널을 통해 전달된 payload를 클라이언트가 서버 WebSocket으로 재전송하도록 GameServerManager에 위임하는 것이 바람직함.
      // 본 매니저에서는 로그만 남김.
      this.addLog('set_round 이벤트는 GameServerManager를 통해 서버에 전달되어야 합니다.', 'warning');
    } catch { /* ignore */ }
  }

  private addLog(
    message: string,
    type: "info" | "success" | "warning" | "error" = "info"
  ): void {
    const log = {
      id: Date.now(),
      message,
      type,
      timestamp: new Date().toISOString(),
    };

    this.updateState({
      logs: [...this.state.logs, log],
    });

    if (this.eventHandlers.onLog) {
      this.eventHandlers.onLog(log);
    }
  }

  private updateState(newState: any): void {
    this.state = { ...this.state, ...newState };

    if (this.eventHandlers.onStateChange) {
      this.eventHandlers.onStateChange(this.state);
    }
  }

  private updateParticipantVideoStatus(participantId: string, videoReady: boolean): void {
    const next = this.state.participants.map((p: any) =>
      p.identity === participantId ? { ...p, videoReady } : p
    );
    const prev = this.state.participants;
    // 변경이 있을 때만 상태 업데이트 (불필요한 리렌더/루프 방지)
    const changed = next.some((p: any, idx: number) => p.videoReady !== prev[idx]?.videoReady);
    if (changed) {
      this.updateState({ participants: next });
    }
  }

  private setupDefaultEventHandlers(): void {
    this.eventHandlers = {
      onStateChange: null,
      onLog: null,
    };
  }

  setEventHandlers(handlers: any): void {
    this.eventHandlers = { ...this.eventHandlers, ...handlers };
  }

  getCurrentState(): any {
    return this.state;
  }
}
