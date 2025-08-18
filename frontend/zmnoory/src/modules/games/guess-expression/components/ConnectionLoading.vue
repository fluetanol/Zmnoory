<template>
  <div class="connecting-container">
    <div class="center-card">
      <div class="card card-center-content connecting-card">
        <div class="connecting-content">
          <div class="connecting-spinner">
            <div class="spinner"></div>
          </div>
          <h2>연결 중...</h2>
          <p class="connecting-status">{{ connectionStatus }}</p>
          <div class="connecting-progress">
            <div class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: connectionProgress + '%' }"
              ></div>
            </div>
            <span class="progress-text">{{ connectionProgress }}%</span>
          </div>
          <button
            type="button"
            class="cancel-button"
            @click="handleCancel"
            :disabled="connectionProgress > 80"
          >
            연결 취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Props
const props = defineProps({
  connectionStatus: {
    type: String,
    default: "연결 준비 중...",
  },
  connectionProgress: {
    type: Number,
    default: 0,
  },
});

// Emits
const emit = defineEmits(["cancel-connection"]);
</script>

<script>
// 이벤트 핸들러
const handleCancel = () => {
  emit("cancel-connection");
};
</script>

<style scoped>
@import "../styles/ConnectionLoading.css";
</style>
