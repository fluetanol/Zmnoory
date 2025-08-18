<!-- [FILEPATH] src/common/components/shared/SiteFooter.vue -->
<template>
  <footer class="site-footer">
    <!-- 1) 왼쪽에 카피라이트 -->
    <p class="footer-text">© 2025 ZMNNOORY. All rights reserved.</p>
    <!-- 2) 오른쪽에 Stamp -->
    <Stamp class="footer-stamp" aria-label="ZMNNOORY Stamp" />
  </footer>
</template>

<script setup>
import Stamp from '@/common/components/login/Stamp.vue'
</script>

<style scoped>
.site-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 0.75rem 1rem;
  box-sizing: border-box;
}

.footer-text {
  display: flex;
  align-items: center;
  margin: 0;
  font-size: 0.875rem;
  height: 2.5rem;      /* 도장과 동일한 높이 */
  color: #2e2e2e;
}

.footer-stamp {
  display: block;
  margin: 0 !important;   /* Stamp 컴포넌트 기본 여백 제거 */
  flex-shrink: 0;
  width: 2.5rem;
  height: 2.5rem;         /* 텍스트 박스와 같은 높이 */
  align-self: center;
}

/* 태블릿 이하 */
@media (max-width: 768px) {
  .site-footer {
    flex-direction: column;
    gap: 0.5rem;
    padding: 1rem;
    text-align: center;
  }
  .footer-text {
    font-size: 0.85rem;
    height: 2.5rem;
  }
  .footer-stamp {
    width: 2.5rem;
    height: 2.5rem;
    margin: 0 !important;
  }
}

/* 모바일 이하 */
@media (max-width: 480px) {
  .site-footer {
    padding: 0.5rem;
  }
  .footer-text {
    font-size: 0.75rem;
    height: 2.5rem;
  }
  .footer-stamp {
    width: 2.5rem;
    height: 2.5rem;
    margin: 0 !important;
  }
}
</style>
