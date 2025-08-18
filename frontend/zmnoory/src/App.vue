<!-- [FILEPATH] src/App.vue -->
<template>
  <div class="app-layout">
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router';
import { onMounted, watch } from 'vue';
import { useAccountStore } from '@/store/Accounts';

const accountStore = useAccountStore();

    const {token} = accountStore.useAuthToken()
    const router = useRouter();
    watch(token, (v) => {
      const needsAuth = router.currentRoute.value.matched.some(r => r.meta?.requiresAuth)
      if (!v && needsAuth) {
        router.replace({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
      }
    }, { immediate: true })

onMounted(() => {
  const token = sessionStorage.getItem("accessToken");
  accountStore.isLoggedIn = !!token; // '!!'는 값을 boolean으로 바꿔줌

  // 백그라운드에서 서버를 통해 실제 유효성을 검증하고 유저 정보를 로드
  if (token) {
    accountStore.checkAuthStatus(); 
  }
});


</script>
<style>
/* 전역 스타일 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scrollbar-gutter: stable;
}

html, body {
  height: 100%;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 페이지 컨텐츠 wrapper */
.page-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.page-content {
  flex: 1;
}

/* SiteFooter는 항상 하단에 위치 */
.site-footer {
  margin-top: auto;
}
</style>