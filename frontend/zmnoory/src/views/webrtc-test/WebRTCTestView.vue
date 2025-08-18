<template>
  <div class="webrtc-test-container">
    <h1 class="title">🎥 WebRTC 테스트 환경 (webrtc-test 시스템)</h1>

    <!-- 연결 상태 -->
    <div class="status-section">
      <div class="status-card" :class="statusClass">
        <h3>🔗 연결 상태</h3>
        <p class="status-text">{{ connectionStatus }}</p>
        <div class="participants-count" v-if="participants.length > 0">
          👥 참가자: {{ participants.length }}명
        </div>
      </div>
    </div>

    <!-- 연결 제어 -->
    <div class="controls-section">
      <div class="controls-card">
        <h3>⚙️ 제어판</h3>
        <div class="control-group">
          <input
            v-model="roomName"
            placeholder="방 이름을 입력하세요"
            class="room-input"
            :disabled="isConnected"
          />
          <button
            @click="isConnected ? disconnectRoom() : connectRoom()"
            :class="['connect-btn', isConnected ? 'disconnect' : 'connect']"
            :disabled="isConnecting"
          >
            {{
              isConnecting
                ? "연결 중..."
                : isConnected
                  ? "🔌 연결 해제"
                  : "🚀 연결하기"
            }}
          </button>
        </div>

        <div class="media-controls" v-if="isConnected">
          <button
            @click="toggleAudio"
            :class="['media-btn', audioEnabled ? 'enabled' : 'disabled']"
          >
            {{ audioEnabled ? "🎤" : "🔇" }} 마이크
          </button>
          <button
            @click="toggleVideo"
            :class="['media-btn', videoEnabled ? 'enabled' : 'disabled']"
          >
            {{ videoEnabled ? "📹" : "📷" }} 카메라
          </button>
        </div>
      </div>
    </div>

    <!-- 비디오 영역 -->
    <div class="video-section" v-if="isConnected">
      <div class="video-grid">
        <!-- 로컬 비디오 -->
        <div class="video-container local">
          <div class="video-header">
            <span class="participant-name">🏠 나 (로컬)</span>
          </div>
          <video
            ref="localVideo"
            autoplay
            muted
            playsinline
            class="video-element"
          ></video>
        </div>

        <!-- 원격 비디오들 -->
        <div
          v-for="participant in participants"
          :key="participant.identity"
          class="video-container remote"
        >
          <div class="video-header">
            <span class="participant-name">👤 {{ participant.identity }}</span>
          </div>
          <video
            :ref="(el) => setRemoteVideoRef(participant.identity, el)"
            autoplay
            playsinline
            class="video-element"
          ></video>
        </div>
      </div>
    </div>

    <!-- 로그 영역 -->
    <div class="logs-section">
      <div class="logs-card">
        <h3>📋 연결 로그</h3>
        <div class="logs-container">
          <div
            v-for="(log, index) in logs"
            :key="index"
            :class="['log-entry', log.type]"
          >
            <span class="log-time">{{ log.time }}</span>
            <span class="log-message">{{ log.message }}</span>
          </div>
        </div>
        <button @click="clearLogs" class="clear-logs-btn">
          🗑️ 로그 지우기
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { Room, RoomEvent, Track } from "livekit-client";
import { SignJWT } from "jose";
import { markRaw } from "vue";

export default {
  name: "WebRTCTestView",
  data() {
    return {
      room: null,
      isConnected: false,
      isConnecting: false,
      connectionStatus: "⚪ 연결 대기 중",
      roomName: "test-room",
      participants: [],
      audioEnabled: true,
      videoEnabled: true,
      logs: [],

      // LiveKit 설정 (배포된 webrtc-test 시스템 연결)
      wsUrl: "wss://zmnnoory.r-e.kr/",
      // 백업 연결 (필요시)
      directWsUrl: "wss://zmnnoory.r-e.kr/",
      liveKitApiKey: "devkey",
      liveKitApiSecret: "webrtc-dev-secret",

      // 배포된 시스템 정보
      deployedSystemName: "webrtc-test",
      namespace: "webrtc-system",
      livekitPort: 7880,
      remoteVideoEls: {},
    };
  },
  computed: {
    statusClass() {
      if (this.isConnecting) return "connecting";
      if (this.isConnected) return "connected";
      return "disconnected";
    },
  },
  mounted() {
    this.addLog("info", "WebRTC 테스트 환경이 초기화되었습니다.");
  },
  beforeUnmount() {
    this.disconnectRoom();
  },
  methods: {
    async connectRoom() {
      if (this.isConnecting || this.isConnected) return;

      this.isConnecting = true;
      this.connectionStatus = "🔄 연결 중...";
      this.addLog("info", `방 "${this.roomName}"에 연결을 시도합니다...`);
      this.addLog(
        "info",
        `배포된 시스템: ${this.deployedSystemName} (네임스페이스: ${this.namespace})`
      );
      this.addLog("info", `LiveKit 포트: ${this.livekitPort}`);

      try {
        // JWT 토큰 생성
        const token = await this.generateJWTToken();
        this.addLog("info", "JWT 토큰이 생성되었습니다.");
        this.addLog("info", `연결 URL: ${this.wsUrl}`);
        this.addLog("info", `사용된 API 키: ${this.liveKitApiKey}`);
        console.log("Generated JWT Token:", token);

        // 임시: 간단한 토큰으로 테스트
        // const simpleToken = await this.generateSimpleToken()
        // console.log('Simple JWT Token:', simpleToken)

        // LiveKit Room 생성 (배포된 시스템에 맞는 설정)
        this.room = markRaw(
          new Room({
            adaptiveStream: true,
            dynacast: true,
            publishDefaults: {
              simulcast: true,
            },
          })
        );
        this.setupRoomEvents();

        // 연결 시도 (TLS 인증서가 적용된 직접 연결)
        this.addLog("info", `WebSocket 연결 시도: ${this.wsUrl}`);
        this.addLog("info", "TLS 인증서가 적용된 LiveKit 서버에 연결 중...");

        await this.room.connect(this.wsUrl, token);

        this.isConnected = true;
        this.connectionStatus = "🟢 연결됨";
        this.addLog("success", "방에 성공적으로 연결되었습니다!");
        this.addLog(
          "info",
          `연결된 서버: ${this.room.engine.connectedServerAddr || "Unknown"}`
        );

        // 이미 방에 있던 참가자들의 트랙 초기화
        const participantsMap = this.room?.participants;
        if (participantsMap) {
          const list = participantsMap.values
            ? Array.from(participantsMap.values())
            : Object.values(participantsMap);
          list.forEach((p) => {
            this.ensureParticipantEntry(p);
            this.setupParticipantEvents(p);
            this.attachExistingVideo(p);
          });
        }

        // 로컬 미디어 스트림 시작
        await this.startLocalMedia();
      } catch (error) {
        this.addLog("error", `연결 실패: ${error.message}`);
        this.addLog(
          "error",
          `배포된 시스템 (${this.deployedSystemName})에 연결할 수 없습니다.`
        );
        console.error("연결 오류 상세:", error);

        if (error.code) {
          this.addLog("error", `오류 코드: ${error.code}`);
        }

        // 일반적인 오류 원인 안내
        if (error.message.includes("WebSocket")) {
          this.addLog(
            "warning",
            "웹소켓 연결 실패: Ingress WebSocket 설정을 확인하세요."
          );
        }
        if (error.message.includes("token") || error.message.includes("auth")) {
          this.addLog(
            "warning",
            "JWT 토큰 인증 실패: API 키/시크릿을 확인하세요."
          );
        }
        if (error.message.includes("timeout")) {
          this.addLog(
            "warning",
            "연결 시간 초과: LiveKit 서버 상태를 확인하세요."
          );
        }

        this.connectionStatus = "🔴 연결 실패";
      } finally {
        this.isConnecting = false;
      }
    },

    async disconnectRoom() {
      if (this.room) {
        this.room.disconnect();
        this.room = null;
      }

      this.isConnected = false;
      this.connectionStatus = "⚪ 연결 대기 중";
      this.participants = [];
      this.addLog("info", "연결이 해제되었습니다.");
    },

    setupRoomEvents() {
      this.room.on(RoomEvent.Connected, () => {
        this.addLog("success", "방에 연결되었습니다.");
      });

      this.room.on(RoomEvent.ParticipantConnected, (participant) => {
        this.participants.push({ identity: participant.identity });
        this.addLog("info", `${participant.identity}님이 입장했습니다.`);
        this.setupParticipantEvents(participant);
      });

      this.room.on(RoomEvent.ParticipantDisconnected, (participant) => {
        this.participants = this.participants.filter(
          (p) => p.identity !== participant.identity
        );
        this.addLog("info", `${participant.identity}님이 퇴장했습니다.`);
        this.disconnectParticipantVideo(participant.identity);
      });

      this.room.on(RoomEvent.Disconnected, () => {
        this.addLog("warning", "방 연결이 끊어졌습니다.");
        this.disconnectRoom();
      });

      this.room.on(RoomEvent.Error, (error) => {
        this.addLog("error", `방 오류: ${error.message}`);
        console.error("방 오류:", error);
      });

      // 룸 레벨 트랙 이벤트(후입장 이벤트 누락 방지)
      this.room.on(RoomEvent.TrackPublished, async (pub, participant) => {
        try {
          if (
            pub?.isSubscribed === false &&
            typeof pub.setSubscribed === "function"
          ) {
            await pub.setSubscribed(true).catch(() => {});
            this.addLog(
              "info",
              `${participant.identity}의 ${pub.kind} 트랙 구독 요청`
            );
          }
        } catch (_) {}
      });

      this.room.on(
        RoomEvent.TrackSubscribed,
        async (track, publication, participant) => {
          const identity = participant?.identity;
          this.addLog("info", `TrackSubscribed: ${identity} / ${track.kind}`);
          if (track.kind !== "video" || !identity) return;
          this.ensureParticipantEntry(participant);
          await this.$nextTick();
          this.attachVideoElement(identity, track);
        }
      );

      this.room.on(
        RoomEvent.TrackUnsubscribed,
        (track, publication, participant) => {
          const identity = participant?.identity;
          if (track.kind !== "video" || !identity) return;
          this.addLog("info", `TrackUnsubscribed: ${identity}`);
          this.detachVideoElement(identity, track);
        }
      );
    },

    setupParticipantEvents(participant) {
      participant.on("trackSubscribed", async (track, publication) => {
        const identity = participant.identity;
        this.addLog("info", `${identity}의 ${track.kind} 트랙을 구독했습니다.`);
        if (track.kind !== "video") return;
        await this.$nextTick();
        this.attachVideoElement(identity, track);
      });

      // 원격 트랙이 이미 publish 되었지만 아직 구독/트랙이 없는 경우 대비
      participant.on("trackPublished", (pub) => {
        try {
          // 구독이 비활성화되어 있으면 강제 구독
          if (
            pub?.isSubscribed === false &&
            typeof pub.setSubscribed === "function"
          ) {
            pub.setSubscribed(true).catch(() => {});
          }
        } catch (_) {}
      });

      participant.on("trackUnsubscribed", (track) => {
        if (track.kind !== "video") return;
        const identity = participant.identity;
        this.detachVideoElement(identity, track);
      });
    },

    ensureParticipantEntry(participant) {
      if (!this.participants.find((p) => p.identity === participant.identity)) {
        this.participants.push({ identity: participant.identity });
      }
    },

    async attachExistingVideo(participant) {
      // 이미 publish된 비디오 트랙이 있으면 즉시 붙인다
      let pubs = [];
      if (participant?.videoTracks?.values) {
        pubs = pubs.concat(Array.from(participant.videoTracks.values()));
      }
      if (!pubs.length && participant?.tracks?.values) {
        pubs = pubs.concat(Array.from(participant.tracks.values()));
      }

      // 카메라 소스 우선 조회
      const camPub = participant?.getTrackPublication?.(Track.Source.Camera);
      if (camPub) pubs = [camPub, ...pubs];

      for (const pub of pubs) {
        const track = pub?.track;
        // 구독 보장
        if (!pub?.isSubscribed && typeof pub?.setSubscribed === "function") {
          try {
            await pub.setSubscribed(true);
          } catch (_) {}
        }
        if (!track || track.kind !== "video") continue;
        await this.$nextTick();
        this.attachVideoElement(participant.identity, track);
      }
    },

    // 안정적인 비디오 attach/detach 헬퍼 (ref 지연에 대비해 짧은 재시도 포함)
    attachVideoElement(identity, track, attempt = 0) {
      const el = this.remoteVideoEls[identity];
      if (!el) {
        if (attempt < 3) {
          setTimeout(
            () => this.attachVideoElement(identity, track, attempt + 1),
            150
          );
        } else {
          this.addLog("warning", `attach 지연: ${identity} 엘리먼트 없음`);
        }
        return;
      }
      try {
        el.playsInline = true;
        el.autoplay = true;
        el.muted = true;
        track.attach(el);
        if (typeof el.play === "function") el.play().catch(() => {});
      } catch (e) {
        console.warn("attachVideoElement error", e);
      }
    },

    detachVideoElement(identity, track) {
      const el = this.remoteVideoEls[identity];
      if (!el) return;
      try {
        track.detach(el);
      } catch (_) {}
    },

    disconnectParticipantVideo(identity) {
      const el = this.remoteVideoEls[identity];
      if (el) {
        try {
          el.srcObject = null;
        } catch (_) {}
        delete this.remoteVideoEls[identity];
      }
    },

    async startLocalMedia() {
      try {
        await this.room.localParticipant.setCameraEnabled(this.videoEnabled);
        await this.room.localParticipant.setMicrophoneEnabled(
          this.audioEnabled
        );

        const attachLocalVideo = () => {
          const pub = this.room?.localParticipant?.getTrackPublication?.(
            Track.Source.Camera
          );
          const videoEl = this.$refs?.localVideo;
          if (pub?.track && videoEl) {
            pub.track.attach(videoEl);
            this.addLog("success", "로컬 미디어 스트림이 시작되었습니다.");
            return true;
          }
          return false;
        };

        if (!attachLocalVideo()) {
          this.room.on(RoomEvent.LocalTrackPublished, (_pub, participant) => {
            if (participant?.isLocal) {
              attachLocalVideo();
            }
          });
          // 혹시 이벤트가 안 오는 경우를 대비한 짧은 재시도
          setTimeout(() => attachLocalVideo(), 500);
        }
      } catch (error) {
        this.addLog("error", `미디어 시작 실패: ${error.message}`);
      }
    },

    async toggleAudio() {
      try {
        this.audioEnabled = !this.audioEnabled;
        await this.room.localParticipant.setMicrophoneEnabled(
          this.audioEnabled
        );
        this.addLog(
          "info",
          `마이크가 ${this.audioEnabled ? "켜졌" : "꺼졌"}습니다.`
        );
      } catch (error) {
        this.addLog("error", `마이크 토글 실패: ${error.message}`);
      }
    },

    async toggleVideo() {
      try {
        this.videoEnabled = !this.videoEnabled;
        await this.room.localParticipant.setCameraEnabled(this.videoEnabled);
        this.addLog(
          "info",
          `카메라가 ${this.videoEnabled ? "켜졌" : "꺼졌"}습니다.`
        );
      } catch (error) {
        this.addLog("error", `카메라 토글 실패: ${error.message}`);
      }
    },

    async generateJWTToken() {
      const key = new TextEncoder().encode(this.liveKitApiSecret);
      const now = Math.floor(Date.now() / 1000);
      const payload = {
        room: this.roomName,
        video: {
          room: this.roomName,
          roomJoin: true,
          canPublish: true,
          canSubscribe: true,
          canUpdateOwnMetadata: true,
        },
      };
      const jwt = await new SignJWT(payload)
        .setProtectedHeader({ alg: "HS256", typ: "JWT" })
        .setIssuer(this.liveKitApiKey)
        .setSubject(`user-${Date.now()}`)
        .setIssuedAt(now)
        .setExpirationTime(now + 3600)
        .sign(key);
      return jwt;
    },

    addLog(type, message) {
      const now = new Date();
      const time = now.toLocaleTimeString();
      this.logs.unshift({ type, message, time });

      // 최대 50개 로그만 유지
      if (this.logs.length > 50) {
        this.logs = this.logs.slice(0, 50);
      }
    },

    clearLogs() {
      this.logs = [];
      this.addLog("info", "로그가 지워졌습니다.");
    },

    setRemoteVideoRef(identity, el) {
      if (el) {
        this.remoteVideoEls[identity] = el;
      } else {
        delete this.remoteVideoEls[identity];
      }
    },
  },
};
</script>

<style scoped>
.webrtc-test-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

.title {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 2.5rem;
}

/* 상태 섹션 */
.status-section {
  margin-bottom: 30px;
}

.status-card {
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
}

.status-card.disconnected {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border: 2px solid #dee2e6;
}

.status-card.connecting {
  background: linear-gradient(135deg, #fff3cd, #ffeaa7);
  border: 2px solid #ffc107;
}

.status-card.connected {
  background: linear-gradient(135deg, #d4edda, #c3e6cb);
  border: 2px solid #28a745;
}

.status-text {
  font-size: 1.2rem;
  font-weight: bold;
  margin: 10px 0;
}

.participants-count {
  font-size: 1rem;
  color: #6c757d;
}

/* 제어 섹션 */
.controls-section {
  margin-bottom: 30px;
}

.controls-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.control-group {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.room-input {
  flex: 1;
  min-width: 200px;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
}

.room-input:focus {
  outline: none;
  border-color: #007bff;
}

.connect-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.connect-btn.connect {
  background: linear-gradient(135deg, #28a745, #20c997);
  color: white;
}

.connect-btn.disconnect {
  background: linear-gradient(135deg, #dc3545, #c82333);
  color: white;
}

.connect-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.connect-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.media-controls {
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
}

.media-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.media-btn.enabled {
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
}

.media-btn.disabled {
  background: linear-gradient(135deg, #6c757d, #5a6268);
  color: white;
}

/* 비디오 섹션 */
.video-section {
  margin-bottom: 30px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.video-container {
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.video-header {
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 10px 15px;
  text-align: center;
}

.video-element {
  width: 100%;
  height: 240px;
  object-fit: cover;
  display: block;
}

.video-container.local .video-header {
  background: linear-gradient(135deg, #007bff, #0056b3);
}

/* 로그 섹션 */
.logs-section {
  margin-bottom: 20px;
}

.logs-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.logs-container {
  max-height: 300px;
  overflow-y: auto;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.log-entry {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 4px;
  font-family: "Courier New", monospace;
  font-size: 0.9rem;
}

.log-entry.info {
  background: rgba(23, 162, 184, 0.1);
  color: #0c5460;
}

.log-entry.success {
  background: rgba(40, 167, 69, 0.1);
  color: #155724;
}

.log-entry.warning {
  background: rgba(255, 193, 7, 0.1);
  color: #856404;
}

.log-entry.error {
  background: rgba(220, 53, 69, 0.1);
  color: #721c24;
}

.log-time {
  font-weight: bold;
  min-width: 80px;
}

.clear-logs-btn {
  padding: 8px 16px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
}

.clear-logs-btn:hover {
  background: #5a6268;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .control-group {
    flex-direction: column;
  }

  .room-input {
    min-width: auto;
  }

  .video-grid {
    grid-template-columns: 1fr;
  }

  .media-controls {
    justify-content: stretch;
  }

  .media-btn {
    flex: 1;
  }
}
</style>
