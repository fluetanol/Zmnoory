<template>
  <div class="file-grid-container">
    <div class="grid-header">
      <div class="header-content">
        <h4>{{ title }} ({{ filteredFiles.length }}개 파일)</h4>

        <!-- 감정 필터 -->
        <div
          v-if="showEmotionFilter && emotionList.length > 0"
          class="emotion-filter"
        >
          <label class="filter-label">감정별 필터:</label>
          <div class="emotion-buttons">
            <button
              @click="switchEmotion('all')"
              :class="{ active: selectedEmotion === 'all' }"
              class="emotion-btn"
            >
              전체
            </button>
            <button
              v-for="emotion in emotionList"
              :key="emotion"
              @click="switchEmotion(emotion)"
              :class="{ active: selectedEmotion === emotion }"
              class="emotion-btn"
            >
              {{ getEmotionLabel(emotion) }}
            </button>
          </div>
        </div>
      </div>

      <div class="grid-controls">
        <button
          @click="previousPage"
          :disabled="currentPage === 1"
          class="nav-btn"
        >
          ←
        </button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        <button
          @click="nextPage"
          :disabled="currentPage === totalPages"
          class="nav-btn"
        >
          →
        </button>
      </div>
    </div>

    <div class="file-grid">
      <div
        v-for="file in paginatedFiles"
        :key="file.key"
        class="file-card"
        @click="selectFile(file)"
        :class="{ selected: selectedFile?.key === file.key }"
      >
        <!-- 이미지 미리보기 -->
        <div v-if="file.type === 'image'" class="image-preview">
          <img
            v-if="getImageUrlSync(file.key)"
            :src="getImageUrlSync(file.key)"
            :alt="getFileName(file.key)"
            @load="onImageLoad(file.key)"
            @error="onImageError(file.key)"
            class="preview-image"
            :class="{ 'image-loaded': imageLoaded[file.key] }"
          />
          <div
            v-if="!imageLoaded[file.key] && !imageErrors[file.key]"
            class="loading-overlay"
          >
            <div class="loading-spinner"></div>
            <p>이미지 로딩 중...</p>
          </div>
          <div v-if="imageErrors[file.key]" class="error-overlay">
            <span>❌</span>
            <p>이미지를 불러올 수 없습니다</p>
          </div>
        </div>

        <!-- 영상 썸네일 -->
        <div v-else class="video-thumbnail">
          <div class="video-icon">🎬</div>
        </div>

        <!-- 파일 정보 -->
        <div class="file-info">
          <div class="file-name">{{ getFileName(file.key) }}</div>
          <div class="file-size">{{ formatFileSize(file.size) }}</div>
          <div class="file-date">{{ formatDate(file.lastModified) }}</div>
          <div v-if="file.memberId" class="file-member">
            멤버: {{ file.memberId }}
          </div>
          <div v-if="file.gameId" class="file-game">
            게임: {{ file.gameId }}
          </div>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination">
      <button
        @click="goToPage(1)"
        :disabled="currentPage === 1"
        class="page-btn"
      >
        처음
      </button>
      <button
        @click="previousPage"
        :disabled="currentPage === 1"
        class="page-btn"
      >
        이전
      </button>
      <span class="page-numbers">
        <button
          v-for="page in visiblePages"
          :key="page"
          @click="goToPage(page)"
          :class="{ active: currentPage === page }"
          class="page-number"
        >
          {{ page }}
        </button>
      </span>
      <button
        @click="nextPage"
        :disabled="currentPage === totalPages"
        class="page-btn"
      >
        다음
      </button>
      <button
        @click="goToPage(totalPages)"
        :disabled="currentPage === totalPages"
        class="page-btn"
      >
        마지막
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import type { S3FileInfo } from "@/services/s3Analytics";
import { s3AnalyticsService } from "@/services/s3Analytics";

interface Props {
  files: S3FileInfo[];
  title: string;
  itemsPerPage?: number;
  showEmotionFilter?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  itemsPerPage: 20,
  showEmotionFilter: false,
});

const emit = defineEmits<{
  fileSelect: [file: S3FileInfo];
}>();

const currentPage = ref(1);
const selectedFile = ref<S3FileInfo | null>(null);
const imageLoaded = ref<Record<string, boolean>>({});
const imageErrors = ref<Record<string, boolean>>({});
const imageUrls = ref<Record<string, string>>({});

// 감정 필터 상태
const selectedEmotion = ref<string>("all");
const emotionList = ref<string[]>([]);

// 이미지 로딩 상태 초기화
const initializeImageStates = () => {
  imageLoaded.value = {};
  imageErrors.value = {};
  imageUrls.value = {};
};

// 감정별 필터링된 파일 목록
const filteredFiles = computed(() => {
  if (!props.showEmotionFilter || selectedEmotion.value === "all") {
    return props.files;
  }
  return props.files.filter((file) => file.emotion === selectedEmotion.value);
});

// 페이지네이션 계산 (3줄 = 15개 아이템으로 설정)
const itemsPerPage = computed(() => {
  // 그리드는 5열이므로 3줄이면 15개
  return 15;
});

const totalPages = computed(() =>
  Math.ceil(filteredFiles.value.length / itemsPerPage.value)
);

const paginatedFiles = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredFiles.value.slice(start, end);
});

// 보여줄 페이지 번호들
const visiblePages = computed(() => {
  const pages = [];
  const maxVisible = 5;
  let start = Math.max(1, currentPage.value - Math.floor(maxVisible / 2));
  let end = Math.min(totalPages.value, start + maxVisible - 1);

  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1);
  }

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});

// 파일 선택
const selectFile = (file: S3FileInfo) => {
  selectedFile.value = file;
  emit("fileSelect", file);
};

// 페이지 네비게이션
const goToPage = (page: number) => {
  currentPage.value = page;
};

const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

// 이미지 URL 생성 (S3에서 직접 가져오기)
const getImageUrl = async (key: string) => {
  if (imageUrls.value[key]) {
    return imageUrls.value[key];
  }

  try {
    console.log(`이미지 데이터 요청: ${key}`);
    const dataUrl = await s3AnalyticsService.getImageData(key);
    imageUrls.value[key] = dataUrl;
    return dataUrl;
  } catch (error) {
    console.error(`이미지 로드 실패: ${key}`, error);
    imageErrors.value[key] = true;
    // 폴백 URL 반환
    const fallbackUrl = `https://zmnnoory-media.s3.ap-northeast-2.amazonaws.com/${key}`;
    imageUrls.value[key] = fallbackUrl;
    return fallbackUrl;
  }
};

// 이미지 URL을 동기적으로 가져오기 위한 컴퓨티드
const getImageUrlSync = (key: string) => {
  // 이미지가 캐시되어 있으면 바로 반환
  if (imageUrls.value[key]) {
    return imageUrls.value[key];
  }

  // 캐시되어 있지 않으면 로딩 시작
  getImageUrl(key).then(() => {
    // 이미지 로드 완료 후 반응성 업데이트를 위해 트리거
  });

  // 임시로 빈 이미지 반환
  return "";
};

// 파일명 추출
const getFileName = (key: string) => {
  return key.split("/").pop() || key;
};

// 파일 크기 포맷팅
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return "0 Bytes";

  const k = 1024;
  const sizes = ["Bytes", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

// 날짜 포맷팅
const formatDate = (date: Date) => {
  return new Date(date).toLocaleDateString("ko-KR");
};

// 감정 관련 함수들
const getEmotionLabel = (emotion: string): string => {
  const emotionLabels: Record<string, string> = {
    angry: "화남 😠",
    sad: "슬픔 😢",
    happy: "기쁨 😊",
    neutral: "무표정 😐",
    fearful: "두려움 😨",
    surprised: "놀람 😲",
    disgusted: "혐오 🤢",
  };

  return emotionLabels[emotion] || emotion;
};

const switchEmotion = (emotion: string) => {
  selectedEmotion.value = emotion;
  currentPage.value = 1; // 필터 변경 시 첫 페이지로 이동
};

// 감정 목록 추출
const extractEmotions = () => {
  const emotions = new Set<string>();
  props.files.forEach((file) => {
    if (file.emotion) {
      emotions.add(file.emotion);
    }
  });
  emotionList.value = Array.from(emotions).sort();
};

// 이미지 로드 이벤트
const onImageLoad = (key: string) => {
  console.log(`이미지 로드 성공: ${key}`);
  imageLoaded.value[key] = true;
  imageErrors.value[key] = false;
};

const onImageError = (key: string) => {
  console.error(`이미지 로드 실패: ${key}`);
  imageErrors.value[key] = true;
  imageLoaded.value[key] = false;
};

// 페이지가 변경되면 이미지 상태 초기화 및 이미지 미리 로드
watch(
  () => currentPage.value,
  () => {
    initializeImageStates();
    preloadImages();
  }
);

// 현재 페이지의 이미지들을 미리 로드
const preloadImages = async () => {
  const imageFiles = paginatedFiles.value.filter(
    (file) => file.type === "image"
  );

  for (const file of imageFiles) {
    try {
      await getImageUrl(file.key);
    } catch (error) {
      console.error(`이미지 미리로드 실패: ${file.key}`, error);
    }
  }
};

// 컴포넌트 마운트 시 첫 페이지 이미지 미리 로드
watch(
  () => props.files,
  () => {
    currentPage.value = 1;
    selectedFile.value = null;
    selectedEmotion.value = "all";
    initializeImageStates();
    extractEmotions(); // 감정 목록 추출
    // 약간의 지연 후 이미지 미리 로드 (DOM 업데이트 대기)
    setTimeout(() => {
      preloadImages();
    }, 100);
  },
  { immediate: true }
);
</script>

<style scoped>
.file-grid-container {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.grid-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 20px;
}

.header-content {
  flex: 1;
}

.grid-header h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.emotion-filter {
  margin-top: 10px;
}

.filter-label {
  display: block;
  font-weight: 600;
  color: #495057;
  font-size: 0.9rem;
  margin-bottom: 8px;
}

.emotion-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.emotion-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 15px;
  padding: 6px 12px;
  cursor: pointer;
  font-size: 0.8rem;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.emotion-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.emotion-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.grid-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-btn {
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s ease;
}

.nav-btn:hover:not(:disabled) {
  background: #5a6fd8;
}

.nav-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.page-info {
  font-weight: 500;
  color: #495057;
  min-width: 60px;
  text-align: center;
}

.file-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.file-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.file-card:hover {
  background: #e9ecef;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.file-card.selected {
  border-color: #667eea;
  background: #e3f2fd;
}

.image-preview,
.video-thumbnail {
  position: relative;
  width: 100%;
  height: 120px;
  background: #fff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.preview-image.image-loaded {
  opacity: 1;
}

.video-icon {
  font-size: 3rem;
  color: #6c757d;
}

.loading-overlay,
.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
}

.loading-overlay p,
.error-overlay p {
  margin: 8px 0 0 0;
  font-size: 0.8rem;
  color: #6c757d;
  text-align: center;
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error-overlay span {
  font-size: 2rem;
  color: #dc3545;
}

.file-info {
  text-align: center;
}

.file-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
  font-size: 0.9rem;
  word-break: break-all;
  line-height: 1.2;
}

.file-size,
.file-date {
  font-size: 0.8rem;
  color: #6c757d;
  margin-bottom: 3px;
}

.file-member,
.file-game {
  font-size: 0.75rem;
  color: #495057;
  background: #e9ecef;
  padding: 2px 6px;
  border-radius: 10px;
  margin: 2px 0;
  display: inline-block;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.page-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #e9ecef;
  border-color: #adb5bd;
}

.page-btn:disabled {
  background: #f8f9fa;
  color: #adb5bd;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  min-width: 40px;
}

.page-number:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.page-number.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .file-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
  }

  .grid-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .pagination {
    flex-wrap: wrap;
    gap: 5px;
  }
}
</style>
