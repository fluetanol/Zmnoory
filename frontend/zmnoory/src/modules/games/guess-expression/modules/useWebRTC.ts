import { ref, onMounted, onUnmounted } from "vue";
import { WebRTCManager } from "./WebRTCManager";
import type { WebRTCParticipant, WebRTCLog } from "./types";

export function useWebRTC() {
  // 반응형 상태
  const isConnected = ref(false);
  const isConnecting = ref(false);
  const participants = ref<WebRTCParticipant[]>([]);
  const logs = ref<WebRTCLog[]>([]);
  const error = ref<string | null>(null);

  // WebRTC 매니저 인스턴스
  const webrtcManager = new WebRTCManager();

  // 상태 동기화 함수
  const syncState = (state: any) => {
    isConnected.value = state.isConnected;
    isConnecting.value = state.isConnecting;
    participants.value = state.participants;
    logs.value = state.logs;
    error.value = state.error;
  };

  // 로그 추가 함수
  const addLog = (log: WebRTCLog) => {
    logs.value.push(log);
  };

  // 이벤트 핸들러 설정
  webrtcManager.setEventHandlers({
    onStateChange: syncState,
    onLog: addLog,
  });

  // 연결 함수
  const connect = async (
    roomCode: string,
    participantName: string,
    _role: "host" | "guest" = "guest"
  ): Promise<void> => {
    try {
      await webrtcManager.connect(roomCode, participantName);
    } catch (err) {
      error.value = err instanceof Error ? err.message : "연결 실패";
      throw err;
    }
  };

  // 연결 해제 함수
  const disconnect = async (): Promise<void> => {
    try {
      await webrtcManager.disconnect();
    } catch (err) {
      error.value = err instanceof Error ? err.message : "연결 해제 실패";
    }
  };

  // 오디오 토글 함수
  const toggleAudio = async (): Promise<void> => {
    try {
      await webrtcManager.toggleAudio();
    } catch (err) {
      error.value = err instanceof Error ? err.message : "오디오 토글 실패";
    }
  };

  // 비디오 토글 함수
  const toggleVideo = async (): Promise<void> => {
    try {
      await webrtcManager.toggleVideo();
    } catch (err) {
      error.value = err instanceof Error ? err.message : "비디오 토글 실패";
    }
  };

  // 데이터 패킷 전송 함수
  const sendDataPacket = async (data: any): Promise<void> => {
    try {
      await webrtcManager.sendDataPacket(data);
    } catch (err) {
      error.value = err instanceof Error ? err.message : "데이터 전송 실패";
    }
  };

  // 게임 이벤트 전송 함수들
  const sendGameStart = async (): Promise<void> => {
    await sendDataPacket({ type: 'game_start', timestamp: Date.now() });
  };

  const sendAnswerSubmit = async (answer: string, emotion: string): Promise<void> => {
    await sendDataPacket({ 
      type: 'answer_submit', 
      answer, 
      emotion, 
      timestamp: Date.now() 
    });
  };

  const sendGameEnd = async (results: any): Promise<void> => {
    await sendDataPacket({ 
      type: 'game_end', 
      results, 
      timestamp: Date.now() 
    });
  };

  const sendEmojiChange = async (emoji: string): Promise<void> => {
    await sendDataPacket({ 
      type: 'emoji_change', 
      emoji, 
      timestamp: Date.now() 
    });
  };

  // 호스트: 라운드 정보 세트(정답 이모지와 4개 보기)
  // NOTE: set_round 데이터채널 이벤트는 현재 외부로 노출하지 않음 (서버 웹소켓으로 직접 전송 사용)

  // 비디오 참조 설정 함수
  const setVideoRef = (
    participantId: string,
    videoElement: HTMLVideoElement | null
  ): void => {
    webrtcManager.setVideoRef(participantId, videoElement);
  };

  const setLocalVideoRef = (videoElement: HTMLVideoElement | null): void => {
    webrtcManager.setLocalVideoRef(videoElement);
  };

  // 방 코드 설정 함수
  const setRoomCode = (_code: string): void => {
    // WebRTC 매니저에 방 코드 설정 로직이 있다면 여기서 호출
  };

  // 참가자 이름 설정 함수
  const setParticipantName = (_name: string): void => {
    // WebRTC 매니저에 참가자 이름 설정 로직이 있다면 여기서 호출
  };

  // 로그 정리 함수
  const clearLogs = (): void => {
    webrtcManager.clearLogs();
  };

  // 현재 상태 가져오기 함수
  const getCurrentState = (): any => {
    return webrtcManager.getCurrentState();
  };

  // 컴포넌트 마운트 시 초기 상태 동기화
  onMounted(() => {
    const initialState = webrtcManager.getCurrentState();
    syncState(initialState);
  });

  // 컴포넌트 언마운트 시 연결 해제
  onUnmounted(() => {
    if (isConnected.value) {
      disconnect();
    }
  });

  return {
    // 상태
    isConnected,
    isConnecting,
    participants,
    logs,
    error,

    // 메서드
    connect,
    disconnect,
    toggleAudio,
    toggleVideo,
    setVideoRef,
    setLocalVideoRef,
    setRoomCode,
    setParticipantName,
    clearLogs,
    getCurrentState,

    // 데이터 패킷 관련
    sendDataPacket,
    sendGameStart,
    sendAnswerSubmit,
    sendGameEnd,
    sendEmojiChange,
    // 고급: 필요 시 외부에서 사용
    // sendSetRound,
  };
}
