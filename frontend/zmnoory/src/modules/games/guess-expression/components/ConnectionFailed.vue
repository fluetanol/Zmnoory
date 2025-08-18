<template>
  <div class="connection-failed-container">
    <div class="center-card">
      <div class="card card-center-content failed-card">
        <div class="failed-content">
          <div class="failed-icon">❌</div>
          <h2>연결 실패</h2>
          <p class="error-message">{{ errorMessage }}</p>
          <div class="retry-options">
            <button type="button" class="retry-button" @click="handleRetry">
              다시 시도
            </button>
            <button type="button" class="back-button" @click="handleBack">
              돌아가기
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Props
const props = defineProps({
  errorMessage: {
    type: String,
    default: "연결에 실패했습니다.",
  },
});

// Emits
const emit = defineEmits(["retry", "go-back"]);

// 이벤트 핸들러
const handleRetry = () => {
  emit("retry");
};

const handleBack = () => {
  emit("go-back");
};
</script>

<style scoped>
@import "../styles/ConnectionFailed.css";
</style>
