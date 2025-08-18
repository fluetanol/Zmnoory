<!-- [FILEPATH] src/views/LoginView.vue -->
<template>
  <div class="login-container">
    <img src="@/assets/LoginBackGround.png" alt="배경" class="bg-image-element" />

    <div class="login-card reveal-login">
      <RouterLink :to="{name: 'main'}">
        <img src="@/assets/home_button.png" alt="홈으로" class="to-home">
      </RouterLink>
      <Logo />
      <LoginForm />
      <Stamp />
      <LoginFooter />
    </div>
  </div>
</template>

<script setup lang="ts">
  // 아토믹 디자인으로 분리된 컴포넌트들을 가져옵니다.
  import Logo from '@common/components/login/Logo.vue';
  import LoginForm from '@common/components/login/LoginForm.vue';
  import Stamp from '@common/components/login/Stamp.vue';
  import LoginFooter from '@/common/components/login/LoginFooter.vue';
  import { RouterLink } from 'vue-router';
</script>

<style scoped>
  /* 로그인 페이지 전체를 감싸는 컨테이너 */
  .login-container {
    width: 100vw;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    overflow: hidden;
    
    /* 1. 남는 여백을 검은색으로 채웁니다. */
    background-color: black;
  }

  /* src/views/LoginView.vue의 <style scoped> 안 */

  .bg-image-element {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    z-index: 0; 
  }

  /* 로그인 카드 스타일 */
  .login-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 537px;
    background: white;
    padding: 40px;
    box-shadow: 0 10px 50px rgba(0, 0, 0, 0.25);
    box-sizing: border-box;
    
    /* 3. 배경 이미지 위에 확실하게 올라오도록 설정합니다. */
    position: relative; 
    z-index: 1;
  }

  .to-home {
    position: absolute;
    top: 15px;
    left: 15px;
    cursor: pointer;
    z-index: 2;
    width: 20px;
  }


  /* ✨ 로그인 카드 진입 애니메이션 */
@keyframes fadeUpLogin {
  from {
    opacity: 0;
    transform: translateY(8px) scale(0.995);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.reveal-login {
  /* 처음 진입할 때 한 번만 자연스럽게 등장 */
  animation: fadeUpLogin 460ms cubic-bezier(0.22, 1, 0.36, 1) both;
  will-change: opacity, transform;
}

/* 카드 내부 요소도 아주 미세하게 순차 등장 (감초 느낌) */
.reveal-login .login-form > * {
  opacity: 0;
  animation: fadeUpLogin 420ms ease both;
}

/* 순서에 따라 40ms 정도만 지연 */
.reveal-login .login-form > *:nth-child(1) { animation-delay: 80ms; }
.reveal-login .login-form > *:nth-child(2) { animation-delay: 120ms; }
.reveal-login .login-form > *:nth-child(3) { animation-delay: 160ms; }
.reveal-login .login-form > *:nth-child(4) { animation-delay: 200ms; }
.reveal-login .login-form > *:nth-child(5) { animation-delay: 240ms; }

/* 접근성: 모션 최소화 환경에서는 즉시 표시 */
@media (prefers-reduced-motion: reduce) {
  .reveal-login,
  .reveal-login .login-form > * {
    animation: none !important;
    opacity: 1 !important;
    transform: none !important;
  }
}
</style>