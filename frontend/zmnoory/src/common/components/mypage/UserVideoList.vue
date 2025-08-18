<template>
  <div class="video-list-container">
    <div v-if="videos && videos.length > 0" class="video-grid">
      <VideoCard 
        v-for="video in videos" 
        :key="video.id" 
        :video="video" 
      />
    </div>
    <div v-else class="empty-message">
      <p>업로드한 영상이 없습니다.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue';
import type { Video } from '@/services/info';
import VideoCard from '@/common/components/shared/VideoCard.vue';

defineProps({
  videos: {
    type: Array as PropType<Video[] | null>,
    required: true,
  }
});
</script>

<style scoped>
.video-grid {
  display: grid;
  /* 화면 크기에 따라 2~4개의 컬럼으로 자동 조절됩니다. */
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}
.empty-message {
  padding: 40px;
  text-align: center;
  color: #888;
  background-color: #f9f9f9;
  border-radius: 8px;
}
</style>