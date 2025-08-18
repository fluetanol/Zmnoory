<template>
  <div class="dashboard-container">
    <!-- 좌측 사이드바 -->
    <aside class="sidebar">
      <!-- 로고 영역 -->
      <div class="logo-section">
        <div class="logo-circle">
          <img
            src="@/assets/logo_small.png"
            alt="즈믄누리"
            class="logo-image"
          />
        </div>
        <h2 class="logo-text">즈믄누리</h2>
      </div>

      <!-- 메뉴 항목들 -->
      <nav class="sidebar-nav">
        <ul class="nav-list">
          <li class="nav-item" :class="{ active: activeMenu === 'analytics' }">
            <button @click="setActiveMenu('analytics')" class="nav-button">
              <span class="nav-icon">📈</span>
              <span class="nav-text">데이터 현황</span>
            </button>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- 메인 콘텐츠 영역 -->
    <main class="main-content">
      <!-- 상단 헤더 -->
      <header class="content-header">
        <h1 class="page-title">{{ getPageTitle() }}</h1>
      </header>

      <!-- 콘텐츠 영역 -->
      <div class="content-area">
        <!-- 대시보드 (데이터 현황으로 리다이렉트) -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard-content">
          <div class="redirect-notice">
            <div class="notice-icon">📊</div>
            <h3>데이터 현황으로 이동 중...</h3>
            <p>자동으로 데이터 현황 페이지로 이동합니다.</p>
          </div>
        </div>

        <!-- 데이터 현황 -->
        <div v-if="activeMenu === 'analytics'" class="analytics-content">
          <div class="analytics-header">
            <h3>S3 데이터 분석</h3>
            <button
              @click="loadS3Analysis"
              class="refresh-btn"
              :disabled="s3Loading"
            >
              <span class="refresh-icon">{{ s3Loading ? "⏳" : "🔄" }}</span>
              {{ s3Loading ? "분석 중..." : "데이터 새로고침" }}
            </button>
          </div>

          <!-- 로딩 상태 -->
          <div v-if="s3Loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>S3 데이터를 분석하고 있습니다...</p>
          </div>

          <!-- 에러 상태 -->
          <div v-else-if="s3Error" class="error-state">
            <div class="error-icon">❌</div>
            <p>{{ s3Error }}</p>
            <button @click="loadS3Analysis" class="retry-btn">다시 시도</button>
          </div>

          <!-- 분석 결과 -->
          <div v-else-if="s3Analysis" class="analysis-results">
            <!-- 전체 통계 -->
            <div class="stats-overview">
              <h4>전체 통계</h4>
              <div class="stats-grid">
                <div class="stat-card">
                  <div class="stat-icon">📁</div>
                  <div class="stat-content">
                    <h3 class="stat-number">{{ s3Analysis.totalFiles }}</h3>
                    <p class="stat-label">총 파일 수</p>
                  </div>
                </div>

                <div class="stat-card">
                  <div class="stat-icon">💾</div>
                  <div class="stat-content">
                    <h3 class="stat-number">
                      {{
                        s3AnalyticsService.formatFileSize(s3Analysis.totalSize)
                      }}
                    </h3>
                    <p class="stat-label">총 용량</p>
                  </div>
                </div>

                <div class="stat-card">
                  <div class="stat-icon">👥</div>
                  <div class="stat-content">
                    <h3 class="stat-number">{{ s3Analysis.memberCount }}</h3>
                    <p class="stat-label">참여 멤버 수</p>
                  </div>
                </div>

                <div class="stat-card">
                  <div class="stat-icon">🎮</div>
                  <div class="stat-content">
                    <h3 class="stat-number">{{ s3Analysis.gameCount }}</h3>
                    <p class="stat-label">게임 종류</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 파일 타입별 통계 -->
            <div class="file-type-stats">
              <h4>파일 타입별 통계</h4>
              <div class="type-stats-grid">
                <div class="type-stat-card video">
                  <div class="type-icon">🎬</div>
                  <div class="type-content">
                    <h3 class="type-number">{{ s3Analysis.videoCount }}</h3>
                    <p class="type-label">영상 파일</p>
                  </div>
                </div>

                <div class="type-stat-card image">
                  <div class="type-icon">🖼️</div>
                  <div class="type-content">
                    <h3 class="type-number">{{ s3Analysis.imageCount }}</h3>
                    <p class="type-label">이미지 파일</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 최근 파일 목록 -->
            <div class="recent-files">
              <h4>최근 업로드된 파일 (최대 10개)</h4>
              <div class="files-list">
                <div
                  v-for="file in s3Analysis.recentFiles"
                  :key="file.key"
                  class="file-item"
                >
                  <div class="file-icon">
                    {{
                      file.type === "video"
                        ? "🎬"
                        : file.type === "image"
                          ? "🖼️"
                          : "📄"
                    }}
                  </div>
                  <div class="file-info">
                    <div class="file-name">{{ file.key.split("/").pop() }}</div>
                    <div class="file-details">
                      <span class="file-path">{{ file.key }}</span>
                      <span class="file-size">{{
                        s3AnalyticsService.formatFileSize(file.size)
                      }}</span>
                      <span class="file-date">{{
                        new Date(file.lastModified).toLocaleDateString("ko-KR")
                      }}</span>
                    </div>
                  </div>
                  <div class="file-actions">
                    <button
                      v-if="file.type === 'image'"
                      @click="openImagePreview(file)"
                      class="preview-btn"
                      title="이미지 미리보기"
                    >
                      👁️
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 분석 차트 -->
            <div v-if="dateAnalysis && gameAnalysis" class="analytics-charts">
              <h4>데이터 분석 차트</h4>

              <!-- 날짜별 차트 -->
              <DateChart
                :labels="dateAnalysis.labels"
                :video-data="dateAnalysis.videoData"
                :image-data="dateAnalysis.imageData"
                :total-data="dateAnalysis.totalData"
                @period-change="handleDatePeriodChange"
              />

              <!-- 게임별 차트 -->
              <GameChart
                :labels="gameAnalysis.labels"
                :video-data="gameAnalysis.videoData"
                :image-data="gameAnalysis.imageData"
                :total-data="gameAnalysis.totalData"
                :game-stats="gameAnalysis.gameStats"
              />
            </div>

            <!-- 파일 탐색기 -->
            <div v-if="fileTypeStats" class="file-explorer">
              <div class="explorer-header">
                <h4>파일 탐색기</h4>
                <div class="filter-controls">
                  <!-- 파일 타입 탭 -->
                  <div class="file-type-tabs">
                    <button
                      @click="switchFileType('videos')"
                      :class="{ active: currentFileType === 'videos' }"
                      class="tab-btn"
                    >
                      🎬 영상 파일 ({{ fileTypeStats.videoCount }}개)
                    </button>
                    <button
                      @click="switchFileType('images')"
                      :class="{ active: currentFileType === 'images' }"
                      class="tab-btn"
                    >
                      🖼️ 이미지 파일 ({{ fileTypeStats.imageCount }}개)
                    </button>
                  </div>

                  <!-- 감정별 필터 -->
                  <div class="emotion-filters" v-if="emotionList.length > 0">
                    <label class="filter-label">감정별 필터:</label>
                    <div class="emotion-tabs">
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
                        :class="{
                          active: selectedEmotion === emotion,
                          empty:
                            !emotionStats[emotion] ||
                            emotionStats[emotion].count === 0,
                        }"
                        class="emotion-btn"
                      >
                        {{ getEmotionLabel(emotion) }}
                        <span class="emotion-count">
                          ({{ emotionStats[emotion]?.count || 0 }})
                        </span>
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <div class="explorer-content">
                <!-- 왼쪽: 파일 그리드 -->
                <div class="grid-section">
                  <FileGrid
                    :files="filteredFiles"
                    :title="getFilteredTitle()"
                    :show-emotion-filter="false"
                    @file-select="handleFileSelect"
                  />
                </div>

                <!-- 오른쪽: 파일 목록 -->
                <div class="list-section">
                  <FileList
                    :files="filteredFiles"
                    @file-select="handleFileSelect"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 초기 상태 -->
          <div v-else class="initial-state">
            <div class="initial-icon">📊</div>
            <h4>S3 데이터 분석</h4>
            <p>버튼을 클릭하여 S3 데이터를 분석하세요.</p>
            <button @click="loadS3Analysis" class="analyze-btn">
              <span class="btn-icon">🔍</span>
              데이터 분석 시작
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 이미지 미리보기 모달 -->
    <ImagePreview
      :is-visible="imagePreviewVisible"
      :image-info="selectedImageInfo"
      @close="closeImagePreview"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import {
  s3AnalyticsService,
  type DataAnalysis,
  type S3FileInfo,
} from "@/services/s3Analytics";
import DateChart from "@/common/components/analytics/DateChart.vue";
import GameChart from "@/common/components/analytics/GameChart.vue";
import ImagePreview from "@/common/components/analytics/ImagePreview.vue";
import FileGrid from "@/common/components/analytics/FileGrid.vue";
import FileList from "@/common/components/analytics/FileList.vue";

const activeMenu = ref("analytics");

// S3 데이터 분석 상태
const s3Analysis = ref<DataAnalysis | null>(null);
const s3Loading = ref(false);
const s3Error = ref<string | null>(null);

// 파일 타입별 데이터 상태
const fileTypeStats = ref<{
  videos: S3FileInfo[];
  images: S3FileInfo[];
  videoCount: number;
  imageCount: number;
  videoTotalSize: number;
  imageTotalSize: number;
} | null>(null);

// 감정별 데이터 상태 (모든 감정을 항상 표시)
const emotionList = ref<string[]>([
  "angry",
  "sad",
  "happy",
  "neutral",
  "fearful",
  "surprised",
  "disgusted",
]);
const emotionStats = ref<
  Record<string, { count: number; imageCount: number; videoCount: number }>
>({});
const selectedEmotion = ref<string>("all");

// 현재 보여줄 파일 타입
const currentFileType = ref<"videos" | "images">("videos");

// 차트 데이터 상태
const dateAnalysis = ref<{
  labels: string[];
  videoData: number[];
  imageData: number[];
  totalData: number[];
} | null>(null);

// 선택된 기간
const selectedDatePeriod = ref(30);

const gameAnalysis = ref<{
  labels: string[];
  videoData: number[];
  imageData: number[];
  totalData: number[];
  gameStats: Array<{
    gameId: string;
    videoCount: number;
    imageCount: number;
    totalSize: number;
  }>;
} | null>(null);

// 이미지 미리보기 상태
const imagePreviewVisible = ref(false);
const selectedImageInfo = ref<{
  fileName: string;
  path: string;
  size: string;
  uploadDate: string;
  s3Key: string;
} | null>(null);

// 파일 탐색기 상태
const selectedFileForDetail = ref<S3FileInfo | null>(null);

const setActiveMenu = (menu: string) => {
  activeMenu.value = menu;
};

const getPageTitle = () => {
  return "데이터 현황";
};

// 감정 통계 초기화
const initializeEmotionStats = () => {
  const initialStats: Record<
    string,
    { count: number; imageCount: number; videoCount: number }
  > = {};

  // 모든 7개 감정을 0으로 초기화
  emotionList.value.forEach((emotion) => {
    initialStats[emotion] = { count: 0, imageCount: 0, videoCount: 0 };
  });

  emotionStats.value = initialStats;
};

// S3 데이터 분석 로드
const loadS3Analysis = async () => {
  s3Loading.value = true;
  s3Error.value = null;

  try {
    s3Analysis.value = await s3AnalyticsService.analyzeData();
    fileTypeStats.value = await s3AnalyticsService.getFileTypeStats();

    // 감정별 데이터 로드
    initializeEmotionStats(); // 먼저 0으로 초기화
    const actualEmotionStats = await s3AnalyticsService.getEmotionStats();

    // 실제 데이터로 업데이트 (0이었던 값들은 그대로 유지)
    Object.assign(emotionStats.value, actualEmotionStats);

    dateAnalysis.value = await s3AnalyticsService.getDateAnalysis(
      selectedDatePeriod.value
    );
    gameAnalysis.value = await s3AnalyticsService.getGameAnalysis();

    console.log("감정별 통계:", emotionStats.value);
  } catch (error) {
    console.error("S3 데이터 분석 실패:", error);
    s3Error.value = "S3 데이터 분석에 실패했습니다.";
  } finally {
    s3Loading.value = false;
  }
};

// 파일 타입 변경
const switchFileType = (type: "videos" | "images") => {
  currentFileType.value = type;
};

// 감정별 필터링
const switchEmotion = (emotion: string) => {
  selectedEmotion.value = emotion;
};

// 감정 라벨을 한국어로 변환
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

// 현재 필터링된 파일 목록 (computed)
const filteredFiles = computed(() => {
  if (!fileTypeStats.value) return [];

  let files =
    currentFileType.value === "videos"
      ? fileTypeStats.value.videos
      : fileTypeStats.value.images;

  // 감정별 필터링
  if (selectedEmotion.value !== "all") {
    files = files.filter((file) => file.emotion === selectedEmotion.value);
  }

  return files;
});

// 필터링된 제목 생성
const getFilteredTitle = (): string => {
  const fileType = currentFileType.value === "videos" ? "영상" : "이미지";
  const emotion =
    selectedEmotion.value === "all"
      ? "전체"
      : getEmotionLabel(selectedEmotion.value);
  const count = filteredFiles.value.length;

  return `${emotion} ${fileType} 파일 (${count}개)`;
};

// 날짜 기간 변경
const handleDatePeriodChange = async (days: number) => {
  selectedDatePeriod.value = days;
  try {
    dateAnalysis.value = await s3AnalyticsService.getDateAnalysis(days);
  } catch (error) {
    console.error("날짜별 분석 데이터 업데이트 실패:", error);
  }
};

// 이미지 미리보기 열기
const openImagePreview = (file: S3FileInfo) => {
  selectedImageInfo.value = {
    fileName: file.key.split("/").pop() || "",
    path: file.key,
    size: s3AnalyticsService.formatFileSize(file.size),
    uploadDate: new Date(file.lastModified).toLocaleDateString("ko-KR"),
    s3Key: file.key,
  };
  imagePreviewVisible.value = true;
};

// 이미지 미리보기 닫기
const closeImagePreview = () => {
  imagePreviewVisible.value = false;
  selectedImageInfo.value = null;
};

// 파일 선택 처리
const handleFileSelect = (file: S3FileInfo) => {
  selectedFileForDetail.value = file;
};

onMounted(async () => {
  // 기본 메뉴를 analytics로 설정
  activeMenu.value = "analytics";

  // 데이터 현황 분석 자동 시작
  await loadS3Analysis();
});
</script>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 사이드바 스타일 */
.sidebar {
  width: 280px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 0;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  position: fixed;
  height: 100vh;
  overflow-y: auto;
}

.logo-section {
  text-align: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  margin-bottom: 30px;
}

.logo-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.logo-image {
  width: 50px;
  height: 50px;
  object-fit: contain;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.sidebar-nav {
  padding: 0 20px;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin-bottom: 8px;
}

.nav-button {
  width: 100%;
  padding: 15px 20px;
  background: transparent;
  border: none;
  color: white;
  display: flex;
  align-items: center;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s ease;
  text-align: left;
}

.nav-button:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

.nav-item.active .nav-button {
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.nav-icon {
  font-size: 1.2rem;
  margin-right: 15px;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-size: 1rem;
  font-weight: 500;
}

/* 메인 콘텐츠 영역 */
.main-content {
  flex: 1;
  margin-left: 280px;
  padding: 20px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.refresh-btn {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  transition: background 0.3s ease;
}

.refresh-btn:hover {
  background: #5a6fd8;
}

.refresh-icon {
  font-size: 1rem;
}

/* 콘텐츠 영역 */
.content-area {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  min-height: calc(100vh - 200px);
}

/* 대시보드 스타일 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 15px;
  padding: 25px;
  display: flex;
  align-items: center;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 2.5rem;
  margin-right: 20px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  margin: 0 0 5px 0;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
  margin: 0;
  font-weight: 500;
}

/* 최근 활동 */
.recent-activity {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 25px;
}

.recent-activity h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.activity-icon {
  font-size: 1.2rem;
  margin-right: 15px;
}

.activity-text {
  flex: 1;
  color: #2c3e50;
  font-weight: 500;
}

.activity-time {
  color: #7f8c8d;
  font-size: 0.9rem;
}

/* AWS 설정 섹션 */
.aws-config-section {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 25px;
  margin-bottom: 30px;
}

.aws-config-section h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.config-item {
  background: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.config-item label {
  display: block;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.config-item code {
  display: block;
  background: #e9ecef;
  padding: 8px 12px;
  border-radius: 6px;
  font-family: "Courier New", monospace;
  font-size: 0.85rem;
  color: #495057;
  word-break: break-all;
}

.aws-actions {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 25px;
}

.aws-actions h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.action-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.action-btn {
  padding: 12px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  transition: background 0.3s ease;
}

.action-btn:hover {
  background: #5a6fd8;
}

.btn-icon {
  font-size: 1rem;
}

/* S3 분석 스타일 */
.analytics-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.loading-state,
.error-state,
.initial-state {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error-icon,
.initial-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.retry-btn,
.analyze-btn {
  padding: 12px 24px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  margin-top: 20px;
  transition: background 0.3s ease;
}

.retry-btn:hover,
.analyze-btn:hover {
  background: #5a6fd8;
}

.stats-overview,
.file-type-stats,
.recent-files {
  margin-bottom: 40px;
}

.stats-overview h4,
.file-type-stats h4,
.recent-files h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.type-stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.type-stat-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border-left: 5px solid;
}

.type-stat-card.video {
  border-left-color: #e74c3c;
}

.type-stat-card.image {
  border-left-color: #3498db;
}

.type-icon {
  font-size: 2rem;
  margin-right: 20px;
}

.type-content {
  flex: 1;
}

.type-number {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 5px 0;
}

.type-label {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin: 0;
  font-weight: 500;
}

.files-list {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 10px;
  margin-bottom: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.file-item:last-child {
  margin-bottom: 0;
}

.file-icon {
  font-size: 1.5rem;
  margin-right: 15px;
  width: 30px;
  text-align: center;
}

.file-info {
  flex: 1;
}

.file-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.file-details {
  display: flex;
  gap: 15px;
  font-size: 0.85rem;
  color: #7f8c8d;
  flex-wrap: wrap;
}

.file-path {
  flex: 1;
  min-width: 200px;
}

.file-size,
.file-date {
  white-space: nowrap;
}

.file-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-btn {
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 6px 10px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-btn:hover {
  background: #5a6fd8;
}

/* 분석 차트 스타일 */
.analytics-charts {
  margin-top: 40px;
  margin-bottom: 40px;
}

.analytics-charts h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

/* 파일 탐색기 스타일 */
.file-explorer {
  margin-top: 40px;
}

.explorer-header {
  margin-bottom: 20px;
}

.explorer-header h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.explorer-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  min-height: 600px;
}

.grid-section {
  min-height: 600px;
}

.list-section {
  min-height: 600px;
}

/* 파일 타입별 상세 목록 스타일 */
.file-type-details {
  margin-top: 40px;
}

.file-type-header {
  margin-bottom: 20px;
}

.file-type-header h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.3rem;
}

.filter-controls {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.file-type-tabs {
  display: flex;
  gap: 10px;
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 10px;
}

.tab-btn {
  padding: 10px 20px;
  background: #f8f9fa;
  border: none;
  border-radius: 8px 8px 0 0;
  cursor: pointer;
  font-weight: 500;
  color: #6c757d;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  background: #e9ecef;
  color: #495057;
}

.tab-btn.active {
  background: #667eea;
  color: white;
}

.file-list-section {
  margin-top: 20px;
}

.section-header {
  margin-bottom: 15px;
}

.section-header h5 {
  margin: 0;
  color: #495057;
  font-size: 1.1rem;
  font-weight: 600;
}

.file-item.video-file {
  border-left: 4px solid #e74c3c;
}

.file-item.image-file {
  border-left: 4px solid #3498db;
}

.file-member,
.file-game {
  background: #e9ecef;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  color: #495057;
  white-space: nowrap;
}

.file-member {
  background: #d4edda;
  color: #155724;
}

.file-game {
  background: #d1ecf1;
  color: #0c5460;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .explorer-content {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .grid-section,
  .list-section {
    min-height: 400px;
  }

  .sidebar {
    width: 250px;
  }

  .main-content {
    margin-left: 250px;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: auto;
    position: relative;
  }

  .main-content {
    margin-left: 0;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .content-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .page-title {
    font-size: 1.5rem;
  }
}

@media (max-width: 480px) {
  .main-content {
    padding: 15px;
  }

  .content-area {
    padding: 20px;
  }

  .stat-card {
    padding: 20px;
  }

  .stat-icon {
    font-size: 2rem;
    width: 50px;
    height: 50px;
    margin-right: 15px;
  }

  .stat-number {
    font-size: 1.5rem;
  }
}

/* 리다이렉트 알림 스타일 */
.redirect-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  text-align: center;
  background: white;
  border-radius: 15px;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.notice-icon {
  font-size: 4rem;
  margin-bottom: 20px;
  animation: pulse 2s infinite;
}

.redirect-notice h3 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 1.5rem;
}

.redirect-notice p {
  color: #6c757d;
  font-size: 1.1rem;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

/* 감정별 필터 스타일 */
.emotion-filters {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.filter-label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 0.9rem;
}

.emotion-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.emotion-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 20px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
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

.emotion-btn.empty {
  opacity: 0.5;
  background: #f8f9fa;
  color: #6c757d;
  border-color: #dee2e6;
}

.emotion-btn.empty:hover {
  background: #e9ecef;
  opacity: 0.7;
}

.emotion-count {
  font-size: 0.75rem;
  opacity: 0.8;
}
</style>
