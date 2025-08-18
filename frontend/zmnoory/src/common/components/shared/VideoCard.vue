<!-- [FILEPATH] src/common/components/shared/VideoCard.vue -->
<template>
  <div class="video-card-wrapper">
    <div class="video-card">
      <img :src="video.thumbnailUrl" alt="video thumbnail">
    </div>
    <div class="video-info">
      <img class="profile" :src="video.memberProfileImageUrl || fallbackProfileImage" alt="profile">
      <div class="text-info">
        <span class="title">{{ video.title }}</span>
        <span class="nickname">{{ video.memberNickname }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import fallbackProfileImage from '@/assets/test_profile.png'

  defineProps<{
    video: {
      title: string
      memberNickname: string
      thumbnailUrl: string
      memberProfileImageUrl?: string
    }
  }>()
</script>

<style scoped>
  .video-card-wrapper {
    width:100%;
    height: 340px;
    display: flex;
    flex-direction: column;
    /* 부드러운 애니메이션 */
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }

  @media (hover: hover) and (pointer: fine) {
    .video-card-wrapper:hover {
      transform: translateY(-4px) scale(1.03);
    }

    .video-card-wrapper:hover .video-card {
      box-shadow: 0 8px 15px rgba(0, 0, 0, 0.25);
    }
  }

  .video-card {
    width : 100%;
    height: 260px;
    flex-shrink: 0;
    border-radius: 15px;
    background: #F5F5F5;
    display: flex;
    justify-content: center;
    align-items: center;
    filter: drop-shadow(0 5px 5px rgba(0, 0, 0, 0.25));
    overflow: hidden;
  }

  .video-card img {
    height: 100%;
    width: auto;
    object-fit: cover;
    display: block;
  }

  .profile {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    margin: 14px 16px 0 0;
  }

  .video-info {
    display: flex;
    align-items: flex-start;
  }

  .text-info {
    display: flex;
    flex-direction: column;
  }

  .title {
    display: flex;
    width: 402px;
    height: auto;
    flex-direction: column;
    justify-content: flex-start;
    flex-shrink: 0;
    color: #2E2E2E;
    font-family: Inter;
    font-size: 15px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
    margin-top: 14px;
  }

  .nickname {
    color: #A0A0A0;
    font-family: Inter;
    font-size: 12px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    margin-top: 5px;
  }
</style>