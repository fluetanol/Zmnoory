<!-- [FILEPATH] src/modules/main/Request.vue -->
<template>
  <div class="request-container">
    <div class="text-area">
      <h1 class="main-text">“ 천 개의 얼굴, 천 개의 세계”</h1>
      <p class="sub-text">
        한 사람의 얼굴은 그 사람의 세계를 나타낸다. <br />
        즈믄누리와 함께 그 세계를 만들어나가세요.
      </p>
      <button class="request-button" @click="handleRequest">요청하기</button>
    </div>
    <div class="image-area">
      <img src="@/assets/request_image.png" alt="요청 이미지" />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router';

  const router = useRouter();

  const handleRequest = () => {
    router.push('/data-request');
  };
</script>
<style scoped>
  .request-container {
    display: flex;
    width: 100%;
    height: 500px;
    background-color: #F5F5F5;
  }

  .text-area {
    flex: 0 0 30%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-end;
    text-align: center;
  }

  .main-text {
    font-size: 32px;
    font-weight: 700;
    color: #2e2e2e;
    margin-bottom: 16px;
  }

  .sub-text {
    font-size: 16px;
    color: #4f4f4f;
    margin-bottom: 28px;
    line-height: 1.6;
    margin-right: 47.5px;
  }

  .request-button {
    background-color: #fff1e5;
    border: none;
    border-radius: 8px;
    padding: 12px 24px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    margin-right: 139px;
  }

  .image-area {
    flex: 0 0 70%;
    overflow: hidden;
    position: relative;
    clip-path: polygon(20% 0%, 100% 0%, 100% 100%, 0% 100%);
  }

  .image-area img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  /* 반응형 스타일 */
  @media (max-width: 768px) {
    .request-container {
      height: 300px;
      flex-direction: column;
    }
    
    .text-area {
      flex: 1;
      align-items: center;
      justify-content: center;
      padding: 0 20px;
    }
    
    .main-text {
      font-size: 24px;
      margin-bottom: 12px;
    }
    
    .sub-text {
      font-size: 14px;
      margin-bottom: 20px;
      margin-right: 0;
      text-align: center;
    }
    
    .request-button {
      margin-right: 0;
      padding: 10px 20px;
      font-size: 14px;
    }
    
    .image-area {
      display: none;
    }
  }

  @media (max-width: 480px) {
    .request-container {
      height: 250px;
    }
    
    .text-area {
      padding: 0 16px;
    }
    
    .main-text {
      font-size: 20px;
      margin-bottom: 10px;
    }
    
    .sub-text {
      font-size: 13px;
      margin-bottom: 16px;
    }
    
    .request-button {
      padding: 8px 16px;
      font-size: 13px;
    }
  }
</style>