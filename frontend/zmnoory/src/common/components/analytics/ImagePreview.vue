<template>
  <div v-if="isVisible" class="image-preview-overlay" @click="closePreview">
    <div class="image-preview-modal" @click.stop>
      <div class="modal-header">
        <h3>이미지 미리보기</h3>
        <button @click="closePreview" class="close-btn">×</button>
      </div>
      <div class="modal-content">
        <div class="image-info">
          <div class="info-item">
            <span class="label">파일명:</span>
            <span class="value">{{ imageInfo?.fileName }}</span>
          </div>
          <div class="info-item">
            <span class="label">경로:</span>
            <span class="value">{{ imageInfo?.path }}</span>
          </div>
          <div class="info-item">
            <span class="label">크기:</span>
            <span class="value">{{ imageInfo?.size }}</span>
          </div>
          <div class="info-item">
            <span class="label">업로드:</span>
            <span class="value">{{ imageInfo?.uploadDate }}</span>
          </div>
        </div>
        <div class="image-container">
          <img
            :src="imageUrl"
            :alt="imageInfo?.fileName"
            @load="imageLoaded = true"
            @error="imageError = true"
            class="preview-image"
          />
          <div v-if="!imageLoaded && !imageError" class="loading">
            <div class="loading-spinner"></div>
            <p>이미지 로딩 중...</p>
          </div>
          <div v-if="imageError" class="error">
            <p>이미지를 불러올 수 없습니다.</p>
            <button @click="retryLoad" class="retry-btn">다시 시도</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";

interface ImageInfo {
  fileName: string;
  path: string;
  size: string;
  uploadDate: string;
  s3Key: string;
}

interface Props {
  isVisible: boolean;
  imageInfo: ImageInfo | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  close: [];
}>();

const imageLoaded = ref(false);
const imageError = ref(false);

const imageUrl = computed(() => {
  if (!props.imageInfo?.s3Key) return "";

  // S3 직접 URL 사용
  return `https://zmnnoory-media.s3.ap-northeast-2.amazonaws.com/${props.imageInfo.s3Key}`;
});

const closePreview = () => {
  imageLoaded.value = false;
  imageError.value = false;
  emit("close");
};

const retryLoad = () => {
  imageLoaded.value = false;
  imageError.value = false;
  // 이미지 URL을 강제로 다시 로드하기 위해 key를 변경
  const img = document.querySelector(".preview-image") as HTMLImageElement;
  if (img) {
    img.src = imageUrl.value + "?t=" + Date.now();
  }
};
</script>

<style scoped>
.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.image-preview-modal {
  background: white;
  border-radius: 15px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  color: #6c757d;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.3s ease;
}

.close-btn:hover {
  background: #f8f9fa;
  color: #495057;
}

.modal-content {
  padding: 20px;
  max-height: calc(90vh - 80px);
  overflow-y: auto;
}

.image-info {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 10px;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  font-weight: 600;
  color: #495057;
  min-width: 80px;
  margin-right: 10px;
}

.value {
  color: #6c757d;
  word-break: break-all;
}

.image-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  background: #f8f9fa;
  border-radius: 10px;
  overflow: hidden;
}

.preview-image {
  max-width: 100%;
  max-height: 60vh;
  object-fit: contain;
  border-radius: 8px;
}

.loading,
.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error p {
  color: #dc3545;
  margin-bottom: 15px;
}

.retry-btn {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.3s ease;
}

.retry-btn:hover {
  background: #5a6fd8;
}
</style>
