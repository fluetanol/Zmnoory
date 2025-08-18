<!-- [FILEPATH] src/layouts/MainLayout.vue -->
<template>
  <div class="main-layout">
    <AppHeader />
    <main class="content">
      <slot/>
    </main>
    <SiteFooter />
  </div>
</template>

<script setup>
import AppHeader from '@/common/components/shared/AppHeader.vue'
import SiteFooter from '@/common/components/shared/SiteFooter.vue'
</script>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.content {
  flex: 1;
  width: 100%;
}
</style>
