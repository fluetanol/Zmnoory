<template>
  <div class="game-header">
    <div class="player-list-card">
      <h3 class="player-list-title">플레이어</h3>
      <div class="player-list">
        <div
          class="player-list-item"
          v-for="player in players"
          :key="player.id"
          :class="{ 'local-player': player.isLocal }"
        >
          <div class="player-info">
            <div class="player-avatar">
              {{ player.name.charAt(0) }}
            </div>
            <div class="player-name">{{ player.name }}</div>
            <div class="player-score">{{ player.score }}</div>
            <div class="player-status" v-if="showVideoStatus">
              <span v-if="player.videoReady" class="video-ready">📹</span>
              <span v-else class="video-loading">⏳</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="room-code-card">
      <h3 class="room-code-title">방 코드</h3>
      <div class="room-code-content">
        <div class="room-code-display">
          <span class="room-code-value" v-if="showRoomCode">{{
            roomCode
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
</template>

<script setup>
import { ref } from "vue";

// Props
const props = defineProps({
  players: {
    type: Array,
    default: () => [],
  },
  roomCode: {
    type: String,
    default: "",
  },
  showVideoStatus: {
    type: Boolean,
    default: false,
  },
});

// Emits
const emit = defineEmits(["toggle-room-code"]);

// 반응형 데이터
const showRoomCode = ref(false);

// 방 코드 표시/숨기기 토글
const toggleRoomCodeVisibility = () => {
  showRoomCode.value = !showRoomCode.value;
  emit("toggle-room-code", showRoomCode.value);
};
</script>

<style scoped>
@import "../styles/GameHeader.css";
</style>
