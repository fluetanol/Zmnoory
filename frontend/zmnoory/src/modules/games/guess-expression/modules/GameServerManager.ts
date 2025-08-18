
export interface GameServerConfig {
  serverUrl: string;
  livekitUrl: string;
}

export interface GamePlayer {
  id: string;
  nickname: string;
  role: 'host' | 'guest';
  score: number;
  isConnected: boolean;
}

export interface GameRoom {
  roomCode: string;
  host: string;
  players: GamePlayer[];
  gameState: 'waiting' | 'playing' | 'finished';
  maxPlayers: number;
  createdAt: string;
}

export interface GameStage {
  stage: 'first_step' | 'submit_answer' | 'check_answer' | 'final_step';
  currentEmoji: string;
  remainingTime: number;
  maxTime: number;
  stageText: string;
}

export interface AnswerChoice {
  emoji: string;
  emotion: string;
  label: string;
}

export interface GameMessage {
  type: string;
  data: any;
}

export class GameServerManager {
  private ws: WebSocket | null = null;
  private config: GameServerConfig;
  private currentRoom: GameRoom | null = null;
  private currentPlayer: GamePlayer | null = null;
  private eventHandlers: Map<string, Function[]> = new Map();
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 1000;

  constructor(config: GameServerConfig) {
    this.config = config;
  }

  // 이벤트 리스너 등록
  on(event: string, handler: Function) {
    if (!this.eventHandlers.has(event)) {
      this.eventHandlers.set(event, []);
    }
    this.eventHandlers.get(event)!.push(handler);
  }

  // 이벤트 리스너 제거
  off(event: string, handler: Function) {
    const handlers = this.eventHandlers.get(event);
    if (handlers) {
      const index = handlers.indexOf(handler);
      if (index > -1) {
        handlers.splice(index, 1);
      }
    }
  }

  // 이벤트 발생
  private emit(event: string, data: any) {
    const handlers = this.eventHandlers.get(event);
    if (handlers) {
      handlers.forEach(handler => {
        try {
          handler(data);
        } catch (error) {
          console.error(`이벤트 핸들러 오류 (${event}):`, error);
        }
      });
    }
  }

  // WebSocket 연결
  async connectWebSocket(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.ws = new WebSocket(this.config.serverUrl.replace('http', 'ws'));

        this.ws.onopen = () => {
          console.log('게임 서버 WebSocket 연결됨');
          this.reconnectAttempts = 0;
          this.emit('connected', {});
          resolve();
        };

        this.ws.onmessage = (event) => {
          try {
            const message: GameMessage = JSON.parse(event.data);
            this.handleGameMessage(message);
          } catch (error) {
            console.error('메시지 파싱 오류:', error);
          }
        };

        this.ws.onclose = (event) => {
          console.log('게임 서버 WebSocket 연결 종료:', event.code, event.reason);
          this.emit('disconnected', { code: event.code, reason: event.reason });
          
          // 자동 재연결 시도
          if (this.reconnectAttempts < this.maxReconnectAttempts) {
            setTimeout(() => {
              this.reconnectAttempts++;
              console.log(`재연결 시도 ${this.reconnectAttempts}/${this.maxReconnectAttempts}`);
              this.connectWebSocket();
            }, this.reconnectDelay * this.reconnectAttempts);
          }
        };

        this.ws.onerror = (error) => {
          console.error('게임 서버 WebSocket 오류:', error);
          reject(error);
        };

      } catch (error) {
        reject(error);
      }
    });
  }

  // WebSocket 연결 해제
  disconnectWebSocket() {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }

  // 메시지 전송
  private sendMessage(type: string, data: any) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const message: GameMessage = { type, data };
      this.ws.send(JSON.stringify(message));
    } else {
      console.error('WebSocket이 연결되지 않았습니다.');
    }
  }

  // 채팅 전송 (공개 메서드)
  public sendChat(roomCode: string, text: string) {
    if (!roomCode || !text || !text.trim()) return;
    this.sendMessage('chat', { roomCode, text: text.trim(), timestamp: Date.now() });
  }

  // 방 생성
  async createRoom(roomCode: string, hostNickname: string, maxPlayers: number = 6): Promise<GameRoom> {
    try {
      const response = await fetch(`${this.config.serverUrl}/rooms/create`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          roomCode,
          hostNickname,
          maxPlayers
        })
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || '방 생성에 실패했습니다.');
      }

      const result = await response.json();
      return result.room;
    } catch (error) {
      console.error('방 생성 오류:', error);
      throw error;
    }
  }

  // 방 입장
  async joinRoom(roomCode: string, nickname: string): Promise<GameRoom> {
    try {
      const response = await fetch(`${this.config.serverUrl}/rooms/${roomCode}/join`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ nickname })
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || '방 입장에 실패했습니다.');
      }

      const result = await response.json();
      return result.room;
    } catch (error) {
      console.error('방 입장 오류:', error);
      throw error;
    }
  }

  // WebSocket을 통한 방 입장
  joinRoomWebSocket(roomCode: string, nickname: string, role: 'host' | 'guest') {
    this.sendMessage('join_room', {
      roomCode,
      nickname,
      role
    });
    // 현재 플레이어 컨텍스트 보존 (서버 에코 메시지 매칭용)
    this.currentPlayer = {
      id: '',
      nickname,
      role,
      score: 0,
      isConnected: true,
    } as GamePlayer;
  }

  // 방 퇴장
  leaveRoom() {
    if (this.currentRoom && this.currentPlayer) {
      this.sendMessage('leave_room', {
        roomCode: this.currentRoom.roomCode,
        playerId: this.currentPlayer.id
      });
    }
  }

  // 게임 시작
  startGame() {
    if (this.currentRoom && this.currentPlayer) {
      this.sendMessage('start_game', {
        roomCode: this.currentRoom.roomCode,
        playerId: this.currentPlayer.id
      });
    }
  }

  // 정답 제출
  submitAnswer(emotion: string) {
    if (this.currentRoom && this.currentPlayer) {
      this.sendMessage('submit_answer', {
        roomCode: this.currentRoom.roomCode,
        playerId: this.currentPlayer.id,
        emotion,
        timestamp: Date.now()
      });
    }
  }

  // 호스트: 라운드 데이터 설정(정답 이모지 + 4개 보기)
  setRound(currentEmoji: string, answerChoices: Array<{ emoji: string; emotion: string; label: string }>) {
    if (this.currentRoom && this.currentPlayer && this.currentPlayer.role === 'host') {
      this.sendMessage('set_round', {
        roomCode: this.currentRoom.roomCode,
        currentEmoji,
        answerChoices
      });
    }
  }

  // 게임 종료
  endGame() {
    if (this.currentRoom && this.currentPlayer) {
      this.sendMessage('end_game', {
        roomCode: this.currentRoom.roomCode,
        playerId: this.currentPlayer.id
      });
    }
  }

  // 미디어 상태 변경
  mediaStatusChanged(isConnected: boolean) {
    if (this.currentRoom && this.currentPlayer) {
      this.sendMessage('media_status_changed', {
        roomCode: this.currentRoom.roomCode,
        playerId: this.currentPlayer.id,
        isConnected
      });
    }
  }

  // LiveKit 토큰 요청
  requestLiveKitToken(playerName: string) {
    if (this.currentRoom) {
      this.sendMessage('get_livekit_token', {
        roomCode: this.currentRoom.roomCode,
        playerName
      });
    }
  }

  // 게임 메시지 처리
  private handleGameMessage(message: GameMessage) {
    console.log('게임 메시지 수신:', message.type, message.data);

    switch (message.type) {
      case 'room_joined':
        this.currentRoom = message.data;
        // 서버에서 내려준 실제 player id 매핑
        if (this.currentPlayer) {
          const matched = message.data.players.find((p: GamePlayer) => p.nickname === this.currentPlayer!.nickname);
          if (matched) this.currentPlayer = matched;
        }
        this.emit('room_joined', message.data);
        break;

      case 'player_joined':
        this.emit('player_joined', message.data);
        break;

      case 'player_left':
        this.emit('player_left', message.data);
        break;

      case 'game_started':
        this.emit('game_started', message.data);
        break;

      case 'stage_changed':
        this.emit('stage_changed', message.data);
        break;
      
      // 서버가 set_round를 처리하면 stage_changed가 재브로드캐스트되어 이 이벤트로 수신됨

      case 'time_update':
        this.emit('time_update', message.data);
        break;

      case 'answer_result':
        this.emit('answer_result', message.data);
        break;

      case 'game_ended':
        this.emit('game_ended', message.data);
        break;

      case 'room_delete':
        this.emit('room_delete', message.data);
        break;

      case 'chat':
        this.emit('chat', message.data);
        break;

      case 'livekit_token':
        this.emit('livekit_token', message.data);
        break;

      case 'error':
        this.emit('error', message.data);
        break;

      default:
        console.warn('알 수 없는 메시지 타입:', message.type);
    }
  }

  // 현재 방 정보 조회
  getCurrentRoom(): GameRoom | null {
    return this.currentRoom;
  }

  // 현재 플레이어 정보 조회
  getCurrentPlayer(): GamePlayer | null {
    return this.currentPlayer;
  }

  // 연결 상태 확인
  isConnected(): boolean {
    return this.ws?.readyState === WebSocket.OPEN;
  }

  // 서버 상태 확인
  async checkServerHealth(): Promise<boolean> {
    try {
      const response = await fetch(`${this.config.serverUrl}/health`);
      return response.ok;
    } catch (error) {
      console.error('서버 상태 확인 실패:', error);
      return false;
    }
  }

  // LiveKit 연결 상태 확인
  async checkLiveKitHealth(): Promise<boolean> {
    try {
      const response = await fetch(`${this.config.serverUrl}/livekit/health`);
      if (response.ok) {
        const data = await response.json();
        return data.connected;
      }
      return false;
    } catch (error) {
      console.error('LiveKit 상태 확인 실패:', error);
      return false;
    }
  }

  // 서버 통계 조회
  async getServerStats(): Promise<any> {
    try {
      const response = await fetch(`${this.config.serverUrl}/stats`);
      if (response.ok) {
        return await response.json();
      }
      throw new Error('통계 조회 실패');
    } catch (error) {
      console.error('서버 통계 조회 실패:', error);
      throw error;
    }
  }
}
