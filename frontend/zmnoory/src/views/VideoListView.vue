<!-- [FILEPATH] src/views/VideoListView.vue -->
<template>
  <div class="page-wrapper">
    <div class="page-content">
      <div class="main-content-wrapper">
        <AppHeader />
      </div>

      <div class="videos-page">
        <main class="container">
          <SimpleSearch style="margin-bottom: 58px;" @search="onSearch" />

          <!-- 스켈레톤: 데이터 로딩 중 -->
          <section v-if="isLoading" class="grid">
            <div v-for="n in 6" :key="n" class="card-skeleton" aria-hidden="true" />
          </section>

          <!-- 비디오 목록 -->
          <section v-else class="grid">
            <RouterLink
              v-for="video in filteredVideos"
              :key="video.id"
              :to="{ name: 'VideoDetail', params: { id: video.id } }"
              class="grid-item-link"
            >
              <VideoCard :video="video" />
            </RouterLink>
          </section>

          <!-- 비디오 목록이 끝났을 때 표시할 섹션 -->
          <section v-if="!isLoading && filteredVideos.length > 0" class="coming-soon-section">
            <div class="coming-soon-content">
              <div class="coming-soon-image">
                <img src="@/assets/SendingHeart.png" alt="Coming Soon" />
              </div>
              <div class="coming-soon-text">
                <h3>영상을 계속해서 추가중입니다.</h3>
                <p>다음영상을 기대해주세요!</p>
              </div>
            </div>
          </section>
        </main>
      </div>
    </div>
    <SiteFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@common/components/shared/AppHeader.vue'
import SiteFooter from '@/common/components/shared/SiteFooter.vue'
import VideoCard from '@/common/components/shared/VideoCard.vue'
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useVideoStore } from '@/store/Videos'
import type { Video } from '@/services/info'
import SimpleSearch from '@/common/components/shared/SimpleSearch.vue'

const videoStore = useVideoStore()
const videos = ref<Video[]>([])
const keyword = ref('')
const isLoading = ref(true)

onMounted(async () => {
  try {
    await videoStore.getVideoList()
    videos.value = videoStore.video_list ?? []
  } finally {
    isLoading.value = false
  }
})

function onSearch(payload: { keyword: string }) {
  keyword.value = payload.keyword ?? ''
}

const filteredVideos = computed<Video[]>(() => {
  const q = keyword.value.trim().toLowerCase()
  if (!q) return videos.value
  return videos.value.filter(v => {
    const title = (v.title ?? '').toLowerCase()
    const desc = (v.description ?? '').toLowerCase()
    const nick = (v.memberNickname ?? '').toLowerCase()
    return title.includes(q) || desc.includes(q) || nick.includes(q)
  })
})
</script>

<style scoped>
.main-content-wrapper {
  max-width: 1440px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

.videos-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.container {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 30px 20px 80px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ====== GRID: 기본 3열 / 태블릿 2열 / 모바일 1열 ====== */
.grid {
  display: grid;
  /* 기본(데스크탑): 3열 고정 */
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 36px 32px;
  align-self: stretch;
}

/* 링크가 카드 전체를 차지하도록 */
.grid-item-link {
  display: block;
  width: 100%;
  text-decoration: none;
}

/* === 반응형 (max-width 기준이 아니라 min-width 계층을 명확히) === */
/* 모바일 ~600px: 1열 */
@media (max-width: 600px) {
  .grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .container {
    padding: 16px 16px 40px;
  }
  .coming-soon-image img {
    width: 180px;
    height: 180px;
  }
  .coming-soon-text h3 { font-size: 16px; }
  .coming-soon-text p  { font-size: 12px; }
}

/* 태블릿 601~1024px: 2열 */
@media (min-width: 601px) and (max-width: 1024px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 20px 16px;
  }
  .container {
    padding: 20px 20px 60px;
  }
}

/* 대형 화면 1025px~ : 3열 유지 (기본값이 3열이라 생략 가능) */

/* ====== 스켈레톤 카드 ====== */
.card-skeleton {
  width: 100%;
  height: 260px;
  border-radius: 12px;
  background: linear-gradient(90deg, #f2f2f2 25%, #eaeaea 37%, #f2f2f2 63%);
  background-size: 400% 100%;
  animation: shimmer 1.4s ease-in-out infinite;
}
@keyframes shimmer {
  0%   { background-position: 100% 0; }
  100% { background-position: 0 0; }
}

/* ====== Coming Soon 섹션 ====== */
.coming-soon-section {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
  opacity: 0.5;
}

.coming-soon-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 20px;
}

.coming-soon-image img {
  width: 240px;
  height: 240px;
  object-fit: contain;
}

.coming-soon-text h3 {
  font-size: 18px;
  font-weight: 600;
  color: #666;
  margin: 0;
}
.coming-soon-text p {
  font-size: 14px;
  color: #999;
  margin: 8px 0 0 0;
}
</style>
