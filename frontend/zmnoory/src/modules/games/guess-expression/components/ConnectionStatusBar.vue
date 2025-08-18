<template>
  <div class="connection-status-bar" :class="statusClass">
    <div class="status-indicator">
      <span class="status-dot"></span>
      <span class="status-text">{{ connectionStatus }}</span>
    </div>
    <div class="participant-count">👥 {{ participantCount }}명 참가</div>
  </div>
</template>

<script setup>
import { computed } from "vue";

// Props
const props = defineProps({
  connectionStatus: {
    type: String,
    default: "연결 대기 중",
  },
  participantCount: {
    type: Number,
    default: 0,
  },
  connectionType: {
    type: String,
    default: "disconnected", // 'connecting', 'connected', 'disconnected'
  },
});

// 연결 상태 클래스 계산
const statusClass = computed(() => {
  return props.connectionType;
});
</script>

<style scoped>
@import "../styles/ConnectionStatusBar.css";
</style>
