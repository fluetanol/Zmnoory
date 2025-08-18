<template>
  <div class="file-list-container">
    <div class="list-header">
      <h4>파일 목록</h4>
      <div class="list-controls">
        <input
          v-model="searchTerm"
          type="text"
          placeholder="파일명 검색..."
          class="search-input"
        />
        <select v-model="sortBy" class="sort-select">
          <option value="name">이름순</option>
          <option value="date">날짜순</option>
          <option value="size">크기순</option>
        </select>
      </div>
    </div>

    <div class="file-list">
      <div
        v-for="file in filteredAndSortedFiles"
        :key="file.key"
        class="file-item"
        @click="selectFile(file)"
        :class="{ selected: selectedFile?.key === file.key }"
      >
        <div class="file-icon">
          {{ file.type === "video" ? "🎬" : "🖼️" }}
        </div>
        <div class="file-details">
          <div class="file-name">{{ getFileName(file.key) }}</div>
          <div class="file-path">{{ file.key }}</div>
          <div class="file-meta">
            <span class="file-size">{{ formatFileSize(file.size) }}</span>
            <span class="file-date">{{ formatDate(file.lastModified) }}</span>
            <span v-if="file.memberId" class="file-member"
              >멤버: {{ file.memberId }}</span
            >
            <span v-if="file.gameId" class="file-game"
              >게임: {{ file.gameId }}</span
            >
          </div>
        </div>
      </div>
    </div>

    <!-- 선택된 파일 상세 정보 -->
    <div v-if="selectedFile" class="selected-file-info">
      <h5>선택된 파일 정보</h5>
      <div class="info-grid">
        <div class="info-item">
          <span class="label">파일명:</span>
          <span class="value">{{ getFileName(selectedFile.key) }}</span>
        </div>
        <div class="info-item">
          <span class="label">전체 경로:</span>
          <span class="value">{{ selectedFile.key }}</span>
        </div>
        <div class="info-item">
          <span class="label">파일 크기:</span>
          <span class="value">{{ formatFileSize(selectedFile.size) }}</span>
        </div>
        <div class="info-item">
          <span class="label">업로드 날짜:</span>
          <span class="value">{{ formatDate(selectedFile.lastModified) }}</span>
        </div>
        <div class="info-item">
          <span class="label">파일 타입:</span>
          <span class="value">{{
            selectedFile.type === "video" ? "영상" : "이미지"
          }}</span>
        </div>
        <div v-if="selectedFile.memberId" class="info-item">
          <span class="label">멤버 ID:</span>
          <span class="value">{{ selectedFile.memberId }}</span>
        </div>
        <div v-if="selectedFile.gameId" class="info-item">
          <span class="label">게임 ID:</span>
          <span class="value">{{ selectedFile.gameId }}</span>
        </div>
        <div v-if="selectedFile.uuid" class="info-item">
          <span class="label">UUID:</span>
          <span class="value">{{ selectedFile.uuid }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import type { S3FileInfo } from "@/services/s3Analytics";

interface Props {
  files: S3FileInfo[];
}

const props = defineProps<Props>();

const emit = defineEmits<{
  fileSelect: [file: S3FileInfo];
}>();

const searchTerm = ref("");
const sortBy = ref<"name" | "date" | "size">("date");
const selectedFile = ref<S3FileInfo | null>(null);

// 필터링 및 정렬된 파일 목록
const filteredAndSortedFiles = computed(() => {
  let filtered = props.files;

  // 검색 필터링
  if (searchTerm.value) {
    const term = searchTerm.value.toLowerCase();
    filtered = filtered.filter(
      (file) =>
        file.key.toLowerCase().includes(term) ||
        (file.memberId && file.memberId.toLowerCase().includes(term)) ||
        (file.gameId && file.gameId.toLowerCase().includes(term))
    );
  }

  // 정렬
  return filtered.sort((a, b) => {
    switch (sortBy.value) {
      case "name":
        return getFileName(a.key).localeCompare(getFileName(b.key));
      case "date":
        return b.lastModified.getTime() - a.lastModified.getTime();
      case "size":
        return b.size - a.size;
      default:
        return 0;
    }
  });
});

// 파일 선택
const selectFile = (file: S3FileInfo) => {
  selectedFile.value = file;
  emit("fileSelect", file);
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
</script>

<style scoped>
.file-list-container {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.list-header {
  margin-bottom: 20px;
}

.list-header h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.list-controls {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 0.9rem;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.sort-select {
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 0.9rem;
  background: white;
  cursor: pointer;
}

.sort-select:focus {
  outline: none;
  border-color: #667eea;
}

.file-list {
  flex: 1;
  overflow-y: auto;
  max-height: 400px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  margin-bottom: 8px;
}

.file-item:hover {
  background: #f8f9fa;
  border-color: #dee2e6;
}

.file-item.selected {
  background: #e3f2fd;
  border-color: #667eea;
}

.file-icon {
  font-size: 1.5rem;
  margin-right: 12px;
  width: 30px;
  text-align: center;
}

.file-details {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
  font-size: 0.9rem;
  word-break: break-all;
}

.file-path {
  font-size: 0.8rem;
  color: #6c757d;
  margin-bottom: 4px;
  word-break: break-all;
}

.file-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 0.75rem;
}

.file-size,
.file-date {
  color: #495057;
}

.file-member,
.file-game {
  background: #e9ecef;
  color: #495057;
  padding: 2px 6px;
  border-radius: 10px;
}

.selected-file-info {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 10px;
  border-top: 2px solid #667eea;
}

.selected-file-info h5 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.info-grid {
  display: grid;
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: flex-start;
}

.label {
  font-weight: 600;
  color: #495057;
  min-width: 100px;
  margin-right: 10px;
  font-size: 0.9rem;
}

.value {
  color: #6c757d;
  word-break: break-all;
  font-size: 0.9rem;
  flex: 1;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .list-controls {
    flex-direction: column;
  }

  .search-input {
    min-width: auto;
  }

  .file-meta {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
