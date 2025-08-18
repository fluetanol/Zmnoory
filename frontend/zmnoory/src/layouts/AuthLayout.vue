<!-- [FILEPATH] src/layouts/AuthLayout.vue -->
<template>
  <div class="auth-layout-container">
    <img src="@/assets/logo.png" alt="즈믄누리 로고" class="logo" />

    <!-- 배경이미지 제거 -->
    <!-- <div class="background-image"></div> -->

    <div class="auth-card">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.logo {
  position: absolute;
  top: 34px;
  left: 36px;
  width: 303px;
  z-index: 2;
}

/* 중앙 정렬 단일 컬럼 레이아웃 */
.auth-layout-container {
  position: relative;
  display: flex;
  flex-direction: column;          /* 수직 흐름 */
  align-items: center;             /* 수평 중앙 */
  justify-content: center;         /* 수직 중앙 */
  width: 100vw;
  height: 100vh;
  background: #F3F3F3;             /* 와이어프레임 배경 톤 */
  overflow: hidden;
  padding: 0;                      /* 우측 패딩 제거 */
  box-sizing: border-box;
}

/* 필요 없으므로 주석 처리하거나 삭제해도 됨 */
.background-image {
  display: none;
}

.auth-card {
  position: relative;
  z-index: 1;
  width: 658px;
  max-width: calc(100% - 64px);    /* 작은 화면 대응 */
  background: #FFFFFF;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
  padding: 40px;
  box-sizing: border-box;
  border: 1px solid #E0E0E0;
  border-radius: 6px;
  /* 높이는 내용에 맞게 자동 */
}</style>
