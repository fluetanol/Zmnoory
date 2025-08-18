<template>
  <div class="background">
    <ToastContainer />
    <div v-if="stage === 'preGame'">
      <div class="center-card">
        <div class="card card-center-content pregame-card">
          <div class="card-header">
            <h2>방 참가</h2>
            <p class="subtitle">방 코드를 입력하고 닉네임을 정해 입장하세요</p>
          </div>
          <form @submit.prevent="handleSubmit" class="room-form">
            <div class="input-row">
              <label for="roomCode">방 코드</label>
              <div class="input-group">
                <span class="input-icon">#</span>
                <input
                  id="roomCode"
                  type="text"
                  v-model="roomCode"
                  maxlength="10"
                  placeholder="예) ABC123"
                  autocomplete="off"
                />
              </div>
            </div>
            <!-- 역할 선택 제거: 명시적 버튼 2개로 대체 -->
            <div class="input-row">
              <label for="nickname">닉네임</label>
              <div class="input-group">
                <input
                  id="nickname"
                  type="text"
                  v-model="nickname"
                  placeholder="표시할 이름"
                  autocomplete="off"
                />
                <button
                  type="button"
                  class="random-button"
                  @click="generateRandomNickname"
                >
                  랜덤
                </button>
              </div>
            </div>
            <div class="input-row" style="gap:12px; width:100%; justify-content:center;">
              <button type="button" class="enter-button" @click="handleJoinClick" :disabled="isConnecting || !roomCode || !nickname">
                {{ isCheckingRoom ? '확인 중...' : '방 입장' }}
              </button>
              <button type="button" class="enter-button" @click="handleCreateClick" :disabled="isConnecting || !roomCode || !nickname">
                {{ isCheckingRoom ? '확인 중...' : '방 생성' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <div v-if="stage === 'Game'" class="game-container">
      <div class="game-header">
        <div class="player-list-card">
          <h3 class="player-list-title">플레이어</h3>
          <div class="player-list">
            <div
              class="player-list-item"
              v-for="player in playerList"
              :key="player.id"
              :class="{ 'host-player': player.isHost, 'correct-player': (gameStage === 'final_step' && correctNameSet.has(player.name)) }"
            >
              <div class="player-info">
                <div class="player-avatar" :class="{ 'host-avatar': player.isHost }">
                  {{ player.name.charAt(0) }}
                </div>
                <div class="player-name">
                  {{ player.name }}
                  <span v-if="player.isHost" class="host-badge">👑</span>
                </div>
                <div class="player-score">{{ player.score }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="room-code-card">
          <h3 class="room-code-title">방 코드</h3>
          <div class="room-code-content">
            <div class="room-code-display">
              <span class="room-code-value" v-if="showRoomCode">{{
                currentRoomCode
              }}</span>
              <span class="room-code-hidden" v-else>••••••</span>
            </div>
            <button class="toggle-button" @click="toggleRoomCodeVisibility">
              <span v-if="showRoomCode">👁️</span>
              <span v-else>🙈</span>
            </button>
          </div>
        </div>
      </div>
      <div class="game-content">
        <!-- 메인 호스트 화면 -->
        <div class="main-screen-area">
          <div class="host-screen">
            <!-- 내가 호스트면 로컬 비디오 표시, 아니라면 호스트 원격 비디오 표시 -->
            <video
              v-if="role === 'host'"
              ref="localVideo"
              autoplay
              playsinline
              muted
              class="host-video"
            ></video>
            <video
              v-else
              ref="hostVideo"
              autoplay
              playsinline
              class="host-video"
            ></video>
            <div class="host-label">{{ hostLabel }}</div>
          </div>

          <!-- 이모지 미션 카드 (게임 시작 버튼 포함) -->
          <div class="emoji-mission-card" style="position: relative; z-index: 30; pointer-events: auto;">
            <div class="emoji-mission-content">
              <div class="mission-text">{{ stageText }}</div>
              <div v-if="role === 'host' && !hasGameStarted" style="display:flex; justify-content:center;">
                <button class="enter-button" style="width:auto; padding:8px 16px;" @click.stop.prevent="startGameByHost">
                  게임 시작
                </button>
              </div>
              <div class="mission-emoji" v-if="(role === 'host' && gameStage === 'first_step')">
                {{ currentEmoji }}
              </div>
              <div class="waiting-message" v-if="(role !== 'host' && gameStage === 'first_step')">
                문제 출제 중...
              </div>
              <div class="waiting-message" v-if="gameStage === 'check_answer'">
                집계중..
              </div>
              <div class="correct-answer" v-if="gameStage === 'final_step'">
                정답자: {{ finalCorrectNames.length ? finalCorrectNames.join(', ') : '없음' }}
              </div>
              <div class="timer-container">
                <div class="timer-display" :class="getTimerClass()">
                  {{ remainingTime }}초
                </div>
                <div class="timer-progress">
                  <div
                    class="timer-bar"
                    :style="{ width: (remainingTime / maxTime) * 100 + '%' }"
                    :class="getProgressBarClass()"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 참여자 화면 그리드 -->
        <div class="participants-area">
          <div class="participants-grid">
            <!-- 게스트 자신의 프리뷰 타일 -->
            <div
              v-if="role !== 'host'"
              class="participant-screen"
              :class="{ 'correct-participant': (gameStage === 'final_step' && correctNameSet.has(myNickname)) }"
            >
              <video
                ref="localVideo"
                autoplay
                playsinline
                muted
                class="participant-video"
              ></video>
              <div class="participant-label">{{ myNickname }} (나)</div>
            </div>
            <!-- 실제 참여자들 -->
            <div
              class="participant-screen"
              v-for="p in filteredParticipants"
              :key="p.identity"
              :class="{ 'correct-participant': (gameStage === 'final_step' && correctNameSet.has(p.identity)) }"
            >
              <video
                :ref="(el) => setRemoteVideoRef(p.identity, el as HTMLVideoElement | null)"
                autoplay
                playsinline
                class="participant-video"
              ></video>
              <div class="participant-label">{{ formatParticipantLabel(p.identity) }}</div>
            </div>
            <!-- 빈 슬롯들 (최대 6개까지) -->
            <div
              class="participant-screen empty-slot"
              v-for="i in Math.max(0, 6 - visibleGridCount)"
              :key="`empty-${i}`"
            >
              <div class="participant-video-placeholder">
                <div class="participant-label">대기 중...</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 정답 보기 카드 -->
        <div class="answer-choices-container">
          <div class="answer-choices-card">
            <div class="choices-grid">
              <button
                v-for="(choice, index) in answerChoices"
                :key="index"
                class="choice-button"
                :class="{ selected: selectedAnswer === choice.emotion }"
                @click="selectAnswer(choice.emotion)"
                :disabled="gameStage !== 'submit_answer' || role === 'host' || !!selectedAnswer"
              >
                <div class="choice-emoji">{{ choice.emoji }}</div>
              </button>
            </div>
          </div>
        </div>

        <!-- 플로팅 채팅 버튼 및 패널 (Game 상태에서만) -->
        <button
          v-if="stage === 'Game'"
          class="chat-fab"
          @click="toggleChat"
          title="채팅"
        >
          💬
        </button>
        <div v-if="stage === 'Game' && showChat" class="chat-panel">
          <div class="chat-header">채팅</div>
          <div ref="chatScroll" class="chat-messages">
            <div
              v-for="(c, idx) in chats"
              :key="idx"
              class="chat-row"
              :class="{ mine: c.nickname === myNickname }"
            >
              <div class="chat-bubble">
                <div class="chat-nick">{{ c.nickname }}</div>
                <div class="chat-text">{{ c.message }}</div>
              </div>
            </div>
          </div>
          <div class="chat-input">
            <input
              v-model="chatText"
              @keyup.enter="sendChatMessage"
              placeholder="메시지를 입력하세요"
            />
            <button @click="sendChatMessage">전송</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
// (no extra type-only imports needed)
import { useWebRTC } from "./modules/useWebRTC";
import { useGameServer } from "./modules/useGameServer";
import { GAME_SERVER_URL, LIVEKIT_URL } from '@/config/env';
import { ADJECTIVES, NOUNS } from "@/constants/nicknameData";
import { ToastContainer } from "./components";
import { useToast } from "./modules/useToast";
const { addToast } = useToast();

// preGame -> Game
// preGame: 게임 전. 아이디와 방 코드를 입력
// Game: 게임 중 화면.
const stage = ref("preGame");

// 방 코드 관련 상태
const currentRoomCode = ref("ABC123");
const showRoomCode = ref(false);
const roomCode = ref("");
const nickname = ref("");
const role = ref<'host' | 'guest'>('guest'); // 내부적으로만 사용
const localVideo = ref<HTMLVideoElement | null>(null);
const hostVideo = ref<HTMLVideoElement | null>(null);

// 방 상태 확인
const isCheckingRoom = ref(false);
const roomExists = ref<boolean | null>(null);
const roomStateOnServer = ref<string | null>(null);

// 내 닉네임(표시용) 유지
const myNickname = computed(() => nickname.value);

// 상태에 따른 호스트/게스트 라벨 관리
const hostLabel = computed(() => {
  const label = hostName.value || "호스트";
  // 내가 호스트라면 (나) 표시
  if (role.value === 'host' && myNickname.value && (label === myNickname.value)) {
    return `${label} (나)`;
  }
  return label;
});

// //필수 emit과 props들
// const emit_Result = defineEmits<{
//   (e:'emit_gameResult' , value : string) : void
//   (e:'emit_cropImages', value:ImagePayload[]):void
//   (e:'emit_video', value:string):void
//   (e: 'emit_videoBlob', value:Blob) : void
// }>()

// const props = defineProps<{
//   isTest : boolean
// }>()



function formatParticipantLabel(identity: string): string {
  // 게스트들의 라벨: identity 그대로, 내 닉네임이면 (나) 붙이기
  if (identity === myNickname.value) return `${identity} (나)`;
  return identity;
}

// WebRTC hooks
const {
  isConnected,
  isConnecting,
  participants,
  error,
  connect,
  disconnect,
  setVideoRef,
  setLocalVideoRef,
  sendAnswerSubmit,
} = useWebRTC();

const setRemoteVideoRef = (identity: string, el: HTMLVideoElement | null) => setVideoRef(identity, el);

// 게임서버 상태에서 호스트 닉네임을 가져와 메인 화면에 사용 (아래에서 currentRoom 정의 후 재계산)

const generateRandomNickname = () => {
  const adj = ADJECTIVES[Math.floor(Math.random() * ADJECTIVES.length)];
  const noun = NOUNS[Math.floor(Math.random() * NOUNS.length)];
  nickname.value = `${adj} ${noun}`;
};

const handleSubmit = () => {
  if (!roomCode.value || !nickname.value) return;
  currentRoomCode.value = roomCode.value;
  try {
    localStorage.setItem("ge.nickname", nickname.value);
  } catch (_) {}
  stage.value = "Game";
};

// 디자인 확인용: 즉시 게임 화면으로 전환하는 헬퍼
import { nextTick } from "vue";
const {
  connect: connectGameServer,
  createRoom: createGameRoom,
  joinRoom: joinGameRoom,
    startGame: startGameOnServer,
  submitAnswer: submitAnswerToServer,
  currentRoom,
  gameStage: serverStage,
    gameState: serverGameState,
    error: serverError,
  chats,
  sendChat,
} = useGameServer({
  serverUrl: GAME_SERVER_URL,
  livekitUrl: LIVEKIT_URL,
});

// final_step 정답자 표시용 리스트
const finalCorrectNames = ref<string[]>([]);
const correctNameSet = computed(() => new Set(finalCorrectNames.value));

// now currentRoom is available
// 방 정보의 호스트만 사용. 초기에 미정이면 빈 문자열을 두고 UI에서는 '호스트'로 대체 표기
const hostName = computed(() => currentRoom.value?.host || "");

// 문자열 정규화 유틸
function normalizeName(name: any): string {
  try { return (name ?? '').toString().trim(); } catch { return ''; }
}

// role 기반 실제 호스트 identity 산정 (참여자 그리드/메인 비디오에 공통 사용)
const hostIdentity = computed(() => {
  const room: any = (currentRoom as any).value;
  if (room && Array.isArray(room.players)) {
    const host = (room.players as any[]).find((p: any) => p.role === 'host');
    if (host?.nickname) return normalizeName(host.nickname);
  }
  return normalizeName(hostName.value);
});

// 채팅 UI 상태
const showChat = ref(false);
const chatText = ref("");
const chatScroll = ref<HTMLDivElement | null>(null);

const toggleChat = () => {
  showChat.value = !showChat.value;
  nextTick(() => {
    if (chatScroll.value) {
      chatScroll.value.scrollTop = chatScroll.value.scrollHeight;
    }
  });
};

const sendChatMessage = () => {
  const text = chatText.value.trim();
  if (!text) return;
  try { sendChat(text); } catch (_) {}
  chatText.value = "";
  nextTick(() => {
    if (chatScroll.value) {
      chatScroll.value.scrollTop = chatScroll.value.scrollHeight;
    }
  });
};

const enterRoom = async () => {
  if (!roomCode.value || !nickname.value) return;
  try {
    // 서버 방 상태 확인
    isCheckingRoom.value = true;
    const info = await fetchRoomInfo(roomCode.value);
    isCheckingRoom.value = false;
    // 입장 제약 조건 확인
    if (info.exists) {
      if (info.gameState !== 'waiting') {
        addToast('게임이 진행 중이거나 종료된 방에는 입장할 수 없습니다.', 'warning');
        return;
      }
    } else {
      // 방이 없는데 게스트로는 입장 불가
      if (role.value === 'guest') {
        addToast('존재하지 않는 방입니다. 방 코드를 확인하세요.', 'error');
        return;
      }
    }
    // 먼저 게임 화면을 렌더링하여 비디오 ref가 생성되도록 함
    stage.value = "Game";
    await nextTick();
    // 로컬 비디오 ref 연결 후 WebRTC 연결
    setLocalVideoRef(localVideo.value);
    // 1) 게임 서버 연결 및 방 생성/입장으로 LiveKit 토큰 준비
    await connectGameServer();
    if (role.value === 'host') {
      await createGameRoom(roomCode.value, nickname.value, 6);
    } else {
      await joinGameRoom(roomCode.value, nickname.value);
      // 게스트는 메인 화면에 호스트 스트림을 표시
      await nextTick();
      if (hostIdentity.value && hostVideo.value) {
        setRemoteVideoRef(hostIdentity.value, hostVideo.value);
      }
    }
    // 2) LiveKit 연결
    await connect(roomCode.value, nickname.value, role.value);
      // 연결 직후 로컬 트랙이 붙도록 약간의 대기(네고시에이션 안정화)
      await new Promise((r)=>setTimeout(r, 300));
      // 혹시 초기 attach가 누락되었을 때를 대비한 재시도
      if (localVideo.value && !localVideo.value.srcObject) {
        // 상태 싱크를 트리거하기 위해 토글-되돌리기
        await Promise.resolve();
      }
    currentRoomCode.value = roomCode.value;
    
    // 호스트라도 자동 시작하지 않음: 버튼으로만 시작
  } catch (e: any) {
    console.error(e);
    const msg = (e?.message || '').toString();
    if (role.value === 'host' && msg.includes('이미 존재')) {
      addToast('이미 존재하는 방입니다.', 'warning');
    } else if (msg) {
      addToast(msg, 'error');
    } else {
      addToast('연결 실패: 네트워크 상태를 확인하세요.', 'error');
    }
    // 연결 실패 시 다시 preGame으로 돌아가기
    stage.value = "preGame";
  }
};

// 버튼 핸들러: 방 입장(게스트)
const handleJoinClick = async () => {
  role.value = 'guest';
  // 사전 체크 및 에러 토스트 처리
  try {
    isCheckingRoom.value = true;
    const info = await fetchRoomInfo(roomCode.value);
    isCheckingRoom.value = false;
    if (!info.exists) {
      addToast('존재하지 않는 방입니다.', 'warning');
      return;
    }
    // 인원 수 체크(최대 7인)
    try {
      const res = await fetch(`${GAME_SERVER_URL}/rooms/${encodeURIComponent(roomCode.value)}`);
      if (res.ok) {
        const data = await res.json();
        const count = Array.isArray(data?.room?.players) ? data.room.players.length : 0;
        if (count >= 7) {
          addToast('방이 가득 찼습니다.', 'warning');
          return;
        }
      }
    } catch { /* pass */ }
    await enterRoom();
  } catch {
    addToast('서버와 통신할 수 없습니다.', 'error');
  }
};

// 버튼 핸들러: 방 생성(호스트)
const handleCreateClick = async () => {
  if (!roomCode.value || !nickname.value) return;
  try {
    isCheckingRoom.value = true;
    const info = await fetchRoomInfo(roomCode.value);
    isCheckingRoom.value = false;
    if (info.exists) {
      addToast('이미 존재하는 방입니다.', 'warning');
      return;
    }
    role.value = 'host';
    await enterRoom();
  } catch {
    addToast('예상치 못한 오류가 발생하였습니다.', 'error');
  }
};

// 방 코드 표시/숨기기 토글 함수
const toggleRoomCodeVisibility = () => {
  showRoomCode.value = !showRoomCode.value;
};

// 방 정보 조회(사전 체크)
async function fetchRoomInfo(code: string): Promise<{ exists: boolean; gameState: string | null; host: string | null; }>{
  try {
    const res = await fetch(`${GAME_SERVER_URL}/rooms/${encodeURIComponent(code)}`);
    if (!res.ok) {
      roomExists.value = false;
      roomStateOnServer.value = null;
      return { exists: false, gameState: null, host: null };
    }
    const data = await res.json();
    const room = data.room;
    roomExists.value = true;
    roomStateOnServer.value = room?.gameState || null;
    return { exists: true, gameState: room?.gameState || null, host: room?.host || null };
  } catch {
    // 네트워크 오류 시, 일단 존재 미확인으로 간주
    return { exists: false, gameState: null, host: null };
  }
}

// 입력 중 자동 방 상태 조회 제거: 버튼 클릭 시에만 서버 조회/생성 수행

// 게임 스테이지 관련 상태
type StageKey = 'first_step' | 'submit_answer' | 'check_answer' | 'final_step';
const gameStage = ref<StageKey>('first_step');
type Emoji = '😐' | '😢' | '😊' | '😡' | '🤢' | '😲' | '😨';
const currentEmoji = ref<Emoji>('😐');
const remainingTime = ref(10);
const maxTime = ref(10);
const stageText = ref("묘사할 이모지:");
const hasGameStarted = ref(false);

// 정답 보기 관련 상태
const selectedAnswer = ref<string | null>(null);
const answerChoices = ref<Array<{ emoji: Emoji; emotion: string; label: string }>>([]);

// 감정 매핑 객체
const emotionMap: Record<Emoji, { emotion: string; label: string }> = {
  "😐": { emotion: "neutral", label: "중립" },
  "😢": { emotion: "sad", label: "슬픔" },
  "😊": { emotion: "happy", label: "행복" },
  "😡": { emotion: "angry", label: "화남" },
  "🤢": { emotion: "disgusted", label: "혐오" },
  "😲": { emotion: "surprised", label: "놀람" },
  "😨": { emotion: "fearful", label: "두려움" },
};

// (클라이언트에서 보기 생성하지 않음: 서버가 submit_answer 진입 시 생성/배포)

// 서버가 제공하는 answerChoices를 주로 사용

// 정답 선택 함수 (정답 제출 단계에서만 동작)
const selectAnswer = async (emotion: string) => {
  if (gameStage.value !== "submit_answer") return;

  selectedAnswer.value = emotion;

  const correctEmotion = emotionMap[currentEmoji.value].emotion;
  const isCorrect = emotion === correctEmotion;

  console.log(`제출된 답: ${emotion}`);
  console.log(`정답: ${correctEmotion}`);
  console.log(`결과: ${isCorrect ? "정답" : "오답"}`);

  // 답변 제출 이벤트 전송
  try {
    await sendAnswerSubmit(emotion as string, correctEmotion);
    // 게임서버로도 제출 전송
    try { submitAnswerToServer(emotion as string); } catch (_) {}
  } catch (error) {
    console.error("답변 전송 실패:", error);
  }
};

// 타이머 색상 클래스 반환
const getTimerClass = () => {
  switch (gameStage.value) {
    case "first_step":
      return "timer-blue";
    case "submit_answer":
      return "timer-orange";
    case "check_answer":
      return "timer-green";
    case "final_step":
      return "timer-purple";
    default:
      return "";
  }
};

// 진행바 색상 클래스 반환
const getProgressBarClass = () => {
  switch (gameStage.value) {
    case "first_step":
      return "progress-blue";
    case "submit_answer":
      return "progress-orange";
    case "check_answer":
      return "progress-green";
    case "final_step":
      return "progress-purple";
    default:
      return "";
  }
};

// 컴포넌트 마운트 시 서버 주도형 상태를 사용 (로컬 사이클 비활성화)
import { onMounted, onUnmounted } from "vue";
// import type { ImagePayload } from "@/services/info";
onMounted(() => {
  // 서버에서 stage_changed 수신 시 아래 watch에서 동기화됨
});

// 컴포넌트 언마운트 시 타이머 정리
onUnmounted(() => {
  // 연결 해제
  if (isConnected.value) {
    disconnect();
  }
});

// 서버 스테이지 동기화: stage_changed/time_update를 반영
watch(serverStage, (s) => {
  if (!s) return;
  // 호스트: 첫 stage_changed 수신 시 시작 상태로 간주하고 동기화
  if (role.value === 'host' && !hasGameStarted.value) {
    hasGameStarted.value = true;
  }
  const stage = (s.stage as StageKey);
  gameStage.value = stage;
  stageText.value = s.stageText;
  if (typeof s.currentEmoji === 'string') {
    currentEmoji.value = s.currentEmoji as Emoji;
  }
  maxTime.value = s.maxTime;
  startLocalCountdown(s.remainingTime);
  // 새 라운드/제출 단계 진입 시 로컬 선택 초기화
  if (stage === 'submit_answer') {
    selectedAnswer.value = null;
  }
  // 게스트는 서버 스테이지 수신 시 시작 상태로 전환
  if (role.value !== 'host') hasGameStarted.value = true;
  if (Array.isArray((s as any).answerChoices)) {
    // answer_submit 단계에서만 보기 노출, answer_reveal에서도 정답 확인을 위해 유지 가능
    answerChoices.value = (s as any).answerChoices;
  } else {
    // 보기 페이로드가 누락되면 선택지 초기화 방지 (이전 라운드 유지되면 혼동되므로 비움)
    answerChoices.value = [] as any;
  }
  // 서버가 submit_answer 진입 시 보기를 생성/배포하므로, 클라이언트는 set_round 전송하지 않음

  // final_step: 서버가 전달한 correctPlayers를 이름 리스트로 변환
  if (stage === 'final_step') {
    const cps = (s as any).correctPlayers;
    if (Array.isArray(cps)) {
      finalCorrectNames.value = cps.map((p: any) => p.nickname);
    } else {
      finalCorrectNames.value = [];
    }
  } else {
    finalCorrectNames.value = [];
  }
});

// 호스트 전용: 시작 버튼 핸들러
const startGameByHost = async () => {
  if (role.value !== 'host' || hasGameStarted.value) return;
  try {
    const code = currentRoomCode.value || roomCode.value;
    if (!code) return;
    // 1) 서버에서 현재 방 인원 조회
    const res = await fetch(`${GAME_SERVER_URL}/rooms/${encodeURIComponent(code)}`);
    if (!res.ok) {
      addToast('서버와 통신할 수 없습니다.', 'error');
      return;
    }
    const data = await res.json();
    const count = Array.isArray(data?.room?.players) ? data.room.players.length : 0;
    // 2) 5인 이상일 때만 서버에 시작 요청
    if (count < 5) {
      addToast('최소 5명 참여 시에만 게임을 시작할 수 있습니다.', 'warning');
      return;
    }
    // 3) 서버에 시작 요청 (서버가 브로드캐스트로 진행 제어)
    try { startGameOnServer(); } catch (_) {}
  } catch (e) {
    console.error('게임 시작 실패:', e);
  }
};

// 서버에서 전달하는 isPlaying을 시작 여부 동기화로 사용
watch(() => (serverGameState as any)?.isPlaying, (v) => {
  if (typeof v === 'boolean') hasGameStarted.value = v;
});

// 게임서버 에러 토스트 처리(예: 최소 인원 미달, 중복 닉네임, 방 가득 참 등)
watch(serverError, (msg) => {
  if (!msg) return;
  const text = String(msg);
  if (text.includes('최소 5명')) {
    addToast('최소 5명 참여 시에만 게임을 시작할 수 있습니다.', 'warning');
    return;
  }
  if (text.includes('방이 가득 찼습니다')) {
    addToast('방이 가득 찼습니다.', 'warning');
    return;
  }
  if (text.includes('이미 사용 중인 닉네임')) {
    addToast('이미 사용 중인 닉네임입니다.', 'warning');
    return;
  }
  if (text.includes('존재하지 않는 방')) {
    addToast('존재하지 않는 방입니다.', 'warning');
    return;
  }
  // 기타 예외 처리
  addToast(text || '예상치 못한 오류가 발생하였습니다.', 'error');
});

// 스테이지가 바뀔 때마다 로컬 카운트다운 시작
let localTimer: ReturnType<typeof setInterval> | null = null;
function startLocalCountdown(seconds: number) {
  if (localTimer) clearInterval(localTimer);
  remainingTime.value = seconds;
  localTimer = setInterval(() => {
    remainingTime.value = Math.max(0, remainingTime.value - 1);
    if (remainingTime.value <= 0 && localTimer) {
      clearInterval(localTimer);
      localTimer = null;
    }
  }, 1000);
}

// 실제 플레이어 리스트 (호스트 + 게스트)
const playerList = computed(() => {
  // 게임서버에서 받은 방 정보가 있으면 이를 기준으로 정렬
  const room = (currentRoom as any).value as any;
  if (room && Array.isArray(room.players)) {
    const mapped = (room.players as any[]).map((p: any) => ({
      id: p.id as string,
      name: p.nickname as string,
      score: (p.score ?? 0) as number,
      isHost: (p.role === 'host') as boolean,
    }));
    // 호스트를 맨 위로, 나머지는 기존 순서 유지
    return mapped.slice().sort((a, b) => (a.isHost === b.isHost ? 0 : a.isHost ? -1 : 1));
  }

  // 초기 로딩 중에는 기존 표시 방식 사용 (호스트 + 현재 참가자 목록)
  const fallback: Array<{ id: string; name: string; score: number; isHost: boolean }> = [
    { id: 'host', name: (nickname as any).value || '호스트', score: 0, isHost: true },
  ];
  (participants as any).value.forEach((participant: any) => {
    fallback.push({ id: participant.identity as string, name: participant.identity as string, score: 0, isHost: false });
  });
  return fallback;
});

// 게스트일 때는 그리드에서 호스트 비디오를 중복 표시하지 않도록 제외
const filteredParticipants = computed(() => {
  if (role.value === 'host') return participants.value;
  const hid = hostIdentity.value;
  // 게스트의 경우 자신은 메인 타겟이 아니므로, 그리드에서는 호스트만 제외하고 나머지 원격 참가자를 표시
  return participants.value.filter(p => normalizeName(p.identity) !== hid && !p.isLocal);
});

// 호스트 변경/최초 설정 시, 게스트의 메인 비디오 바인딩을 갱신
watch(() => currentRoom.value?.host, (newHost) => {
  if (role.value !== 'host' && newHost && hostVideo.value) {
    setRemoteVideoRef(hostIdentity.value, hostVideo.value);
  }
});

// 호스트 비디오 자동 일시정지 로직 제거

// 연결 상태 모니터링
watch(isConnected, (connected) => {
  if (!connected && stage.value === 'Game') {
    console.log('연결이 끊어졌습니다.');
  }
});

// 에러 상태 모니터링
watch(error, (newError) => {
  if (newError) {
    console.error('WebRTC 에러:', newError);
  }
});

// 그리드 표시 개수(게스트는 본인 프리뷰 1 + 원격 참가자 수, 호스트는 원격 참가자 수)
const visibleGridCount = computed(() => {
  if (role.value === 'host') return filteredParticipants.value.length;
  return filteredParticipants.value.length + 1; // 본인 프리뷰 포함
});
</script>

<style scoped>
@import "./GuessExpressionView.css";
</style>
