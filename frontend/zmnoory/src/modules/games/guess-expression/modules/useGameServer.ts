import { ref, reactive, onMounted, onUnmounted } from 'vue';
import { GameServerManager } from './GameServerManager';
import { GAME_SERVER_URL, LIVEKIT_URL } from '@/config/env';
import type { GameServerConfig, GameRoom, GamePlayer, GameStage, AnswerChoice } from './GameServerManager';

export interface UseGameServerOptions {
  serverUrl?: string;
  livekitUrl?: string;
}

export function useGameServer(options: UseGameServerOptions = {}) {
  const config: GameServerConfig = {
    serverUrl: options.serverUrl || GAME_SERVER_URL,
    livekitUrl: options.livekitUrl || LIVEKIT_URL,
  };

  const gameServer = new GameServerManager(config);

  // 반응형 상태
  const isConnected = ref(false);
  const isConnecting = ref(false);
  const currentRoom = ref<GameRoom | null>(null);
  const currentPlayer = ref<GamePlayer | null>(null);
  const gameStage = ref<GameStage | null>(null);
  const players = ref<GamePlayer[]>([]);
  const error = ref<string | null>(null);
  const logs = ref<string[]>([]);
  const chats = ref<Array<{ playerId: string; nickname: string; message: string; timestamp: number }>>([]);

  // 게임 상태
  const gameState = reactive({
    isPlaying: false,
    currentEmoji: '',
    remainingTime: 0,
    maxTime: 0,
    stageText: '',
    answerChoices: [] as AnswerChoice[],
    selectedAnswer: null as string | null,
    showResult: false,
    isCorrect: false
  });

  // 이벤트 핸들러 설정
  const setupEventHandlers = () => {
    gameServer.on('connected', () => {
      isConnected.value = true;
      isConnecting.value = false;
      addLog('게임 서버에 연결되었습니다.', 'success');
      try {
        chats.value.push({ playerId: 'system', nickname: '시스템', message: '게임 서버에 연결되었습니다.', timestamp: Date.now() });
      } catch (_) {}
    });

    gameServer.on('disconnected', (data: { code?: number; reason?: string }) => {
      isConnected.value = false;
      addLog(`게임 서버 연결이 끊어졌습니다: ${data.reason || '알 수 없는 이유'}`, 'error');
    });

    gameServer.on('room_joined', (room: GameRoom) => {
      currentRoom.value = room;
      currentPlayer.value = room.players.find(p => p.nickname === currentPlayer.value?.nickname) || null;
      players.value = room.players;
      addLog(`방에 입장했습니다: ${room.roomCode}`, 'info');
    });

    gameServer.on('player_joined', (data: { player: GamePlayer }) => {
      if (currentRoom.value) {
        const newPlayer = data.player;
        const existingIndex = currentRoom.value.players.findIndex(p => p.id === newPlayer.id);
        if (existingIndex === -1) {
          currentRoom.value.players.push(newPlayer);
          players.value = currentRoom.value.players;
        }
        addLog(`${newPlayer.nickname}님이 입장했습니다.`, 'info');
      }
    });

    gameServer.on('player_left', (data: { playerId: string }) => {
      if (currentRoom.value) {
        currentRoom.value.players = currentRoom.value.players.filter(p => p.id !== data.playerId);
        players.value = currentRoom.value.players;
        addLog('플레이어가 퇴장했습니다.', 'info');
      }
    });

    gameServer.on('game_started', (_data: unknown) => {
      gameState.isPlaying = true;
      addLog('게임이 시작되었습니다.', 'success');
    });

    gameServer.on('stage_changed', (stage: GameStage) => {
      gameStage.value = stage;
      gameState.currentEmoji = stage.currentEmoji;
      gameState.remainingTime = stage.remainingTime;
      gameState.maxTime = stage.maxTime;
      gameState.stageText = stage.stageText;
      // 서버가 내려주는 선택지 동기화
      // @ts-ignore - 런타임에서 유효한 필드
      if ((stage as any).answerChoices) {
        // 타입 가드 없이 서버 페이로드를 그대로 반영
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        gameState.answerChoices = (stage as any).answerChoices as AnswerChoice[];
      }
      gameState.selectedAnswer = null;
      gameState.showResult = false;
      
      addLog(`게임 스테이지 변경: ${stage.stage}`, 'info');

    // final_step: 정답자/스코어 반영
    // 서버가 players, correctPlayers를 함께 보내는 경우 UI에도 즉시 반영
    // @ts-ignore
    if (stage.stage === 'final_step') {
      const st: any = stage as any;
      if (Array.isArray(st.players)) {
        if (currentRoom.value) {
          // players: [{id, nickname, score, isHost}]
          currentRoom.value.players = st.players.map((p: any) => ({
            id: p.id,
            nickname: p.nickname,
            role: p.isHost ? 'host' : 'guest',
            score: p.score,
            isConnected: true,
          }));
          players.value = currentRoom.value.players;
        }
      }
      if (Array.isArray(st.correctPlayers)) {
        // correctPlayers: [{id, nickname}]
        const names = st.correctPlayers.map((p: any) => p.nickname).join(', ');
        addLog(`정답자: ${names || '없음'}`, 'info');
      }
    }
    });

    gameServer.on('time_update', (data: { remainingTime: number }) => {
      gameState.remainingTime = data.remainingTime;
    });

    // 서버 포맷에 맞게 필드명 반영: { isCorrect, submittedEmotion, correctEmotion, scoreChange }
    gameServer.on('answer_result', (data: { isCorrect: boolean; submittedEmotion: string; correctEmotion: string; scoreChange: number }) => {
      gameState.showResult = true;
      gameState.isCorrect = data.isCorrect;
      gameState.selectedAnswer = data.submittedEmotion;

      const resultText = data.isCorrect ? '정답입니다!' : '틀렸습니다.';
      addLog(`${resultText} (정답: ${data.correctEmotion})`, data.isCorrect ? 'success' : 'error');
    });

    gameServer.on('game_ended', (_data: unknown) => {
      gameState.isPlaying = false;
      addLog('게임이 종료되었습니다.', 'info');
    });

    // 방 삭제 시 처리: 클라이언트 새로고침
    gameServer.on('room_delete', (_data: { roomCode: string; reason?: string }) => {
      addLog('방이 삭제되었습니다. 페이지를 새로고침합니다.', 'warning');
      try {
        window.location.reload();
      } catch (_) {}
    });

    gameServer.on('livekit_token', (_data: { roomCode: string; playerName: string; token: string; livekitUrl: string }) => {
      addLog('LiveKit 토큰을 받았습니다.', 'info');
      // LiveKit 토큰을 useWebRTC 훅에 전달
      // 이 부분은 useWebRTC 훅과 연동하여 구현
    });

    gameServer.on('error', (data: { message: string }) => {
      error.value = data.message;
      addLog(`오류: ${data.message}`, 'error');
    });

    // 채팅 수신
    gameServer.on('chat', (data: { roomCode: string; playerId: string; nickname: string; message: string; timestamp: number }) => {
      try {
        chats.value.push({ playerId: data.playerId, nickname: data.nickname, message: data.message, timestamp: data.timestamp });
      } catch (e) {
        // ignore
      }
    });
  };

  // 로그 추가
  const addLog = (message: string, type: 'info' | 'success' | 'error' | 'warning' = 'info') => {
    const timestamp = new Date().toLocaleTimeString();
    const logEntry = `[${timestamp}] [${type}] ${message}`;
    logs.value.push(logEntry);
    
    // 로그가 너무 많아지면 오래된 것부터 제거
    if (logs.value.length > 100) {
      logs.value = logs.value.slice(-50);
    }
    
    console.log(`[GameServer] ${logEntry}`);
  };

  // 로그 정리
  const clearLogs = () => {
    logs.value = [];
  };

  // WebSocket 연결
  const connect = async () => {
    try {
      isConnecting.value = true;
      error.value = null;
      
      await gameServer.connectWebSocket();
      addLog('WebSocket 연결을 시도합니다...', 'info');
    } catch (err) {
      error.value = err instanceof Error ? err.message : '연결 실패';
      isConnecting.value = false;
      addLog(`연결 실패: ${error.value}`, 'error');
      throw err;
    }
  };

  // 연결 해제
  const disconnect = () => {
    gameServer.disconnectWebSocket();
    isConnected.value = false;
    isConnecting.value = false;
    addLog('연결을 해제했습니다.', 'info');
  };

  // 방 생성
  const createRoom = async (roomCode: string, hostNickname: string, maxPlayers: number = 6) => {
    try {
      const room = await gameServer.createRoom(roomCode, hostNickname, maxPlayers);
      currentRoom.value = room;
      currentPlayer.value = room.players.find(p => p.nickname === hostNickname) || null;
      players.value = room.players;
      
      // WebSocket을 통한 방 입장
      gameServer.joinRoomWebSocket(roomCode, hostNickname, 'host');
      
      addLog(`방을 생성했습니다: ${roomCode}`, 'success');
      return room;
    } catch (err) {
      error.value = err instanceof Error ? err.message : '방 생성 실패';
      addLog(`방 생성 실패: ${error.value}`, 'error');
      throw err;
    }
  };

  // 방 입장
  const joinRoom = async (roomCode: string, nickname: string) => {
    try {
      const room = await gameServer.joinRoom(roomCode, nickname);
      currentRoom.value = room;
      currentPlayer.value = room.players.find(p => p.nickname === nickname) || null;
      players.value = room.players;
      
      // WebSocket을 통한 방 입장
      gameServer.joinRoomWebSocket(roomCode, nickname, 'guest');
      
      addLog(`방에 입장했습니다: ${roomCode}`, 'success');
      return room;
    } catch (err) {
      error.value = err instanceof Error ? err.message : '방 입장 실패';
      addLog(`방 입장 실패: ${error.value}`, 'error');
      throw err;
    }
  };

  // 방 퇴장
  const leaveRoom = () => {
    gameServer.leaveRoom();
    currentRoom.value = null;
    currentPlayer.value = null;
    players.value = [];
    gameState.isPlaying = false;
    addLog('방을 퇴장했습니다.', 'info');
  };

  // 게임 시작
  const startGame = () => {
    if (currentPlayer.value?.role !== 'host') {
      error.value = '호스트만 게임을 시작할 수 있습니다.';
      addLog(error.value, 'error');
      return;
    }
    
    gameServer.startGame();
    addLog('게임 시작을 요청했습니다.', 'info');
  };

  // 정답 제출
  const submitAnswer = (emotion: string) => {
    if (!gameState.isPlaying) {
      addLog('게임이 진행 중이 아닙니다.', 'warning');
      return;
    }
    
    gameServer.submitAnswer(emotion);
    gameState.selectedAnswer = emotion;
    addLog(`정답을 제출했습니다: ${emotion}`, 'info');
  };

  // 게임 종료
  const endGame = () => {
    if (currentPlayer.value?.role !== 'host') {
      error.value = '호스트만 게임을 종료할 수 있습니다.';
      addLog(error.value, 'error');
      return;
    }
    
    gameServer.endGame();
    addLog('게임 종료를 요청했습니다.', 'info');
  };

  // LiveKit 토큰 요청
  const requestLiveKitToken = (playerName: string) => {
    gameServer.requestLiveKitToken(playerName);
  };

  // 미디어 상태 변경
  const mediaStatusChanged = (isConnected: boolean) => {
    gameServer.mediaStatusChanged(isConnected);
  };

  // 서버 상태 확인
  const checkServerHealth = async () => {
    try {
      const isHealthy = await gameServer.checkServerHealth();
      addLog(`서버 상태: ${isHealthy ? '정상' : '오류'}`, isHealthy ? 'success' : 'error');
      return isHealthy;
    } catch (err) {
      addLog('서버 상태 확인 실패', 'error');
      return false;
    }
  };

  // LiveKit 상태 확인
  const checkLiveKitHealth = async () => {
    try {
      const isHealthy = await gameServer.checkLiveKitHealth();
      addLog(`LiveKit 상태: ${isHealthy ? '정상' : '오류'}`, isHealthy ? 'success' : 'error');
      return isHealthy;
    } catch (err) {
      addLog('LiveKit 상태 확인 실패', 'error');
      return false;
    }
  };

  // 컴포넌트 마운트 시 이벤트 핸들러 설정
  onMounted(() => {
    setupEventHandlers();
  });

  // 컴포넌트 언마운트 시 연결 해제
  onUnmounted(() => {
    disconnect();
  });

  return {
    // 상태
    isConnected,
    isConnecting,
    currentRoom,
    currentPlayer,
    gameStage,
    players,
    error,
    logs,
    gameState,

    // 메서드
    connect,
    disconnect,
    createRoom,
    joinRoom,
    leaveRoom,
    startGame,
    submitAnswer,
    setRound: (currentEmoji: string, answerChoices: Array<{ emoji: string; emotion: string; label: string }>) => gameServer.setRound(currentEmoji, answerChoices),
    endGame,
    requestLiveKitToken,
    mediaStatusChanged,
    checkServerHealth,
    checkLiveKitHealth,
    addLog,
    clearLogs,
    chats,
    // 채팅 전송
    sendChat: (text: string) => gameServer.sendChat(currentRoom.value?.roomCode || '', text),

    // 게임 서버 인스턴스 (고급 사용자용)
    gameServer
  };
}
