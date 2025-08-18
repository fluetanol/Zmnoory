<!-- [FILEPATH] src/views/TermsAgreementView.vue -->
<template>
  <MainLayout>
    <div class="page-center">
      <TermsAgreementForm @next-step="handleNextStep" />
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import MainLayout from '@/layouts/MainLayout.vue'
import TermsAgreementForm from '@/common/components/signup/TermsAgreementForm.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const handleNextStep = (termsData: { optionalConsent: boolean }) => {
  router.push({
    path: '/signup',
    query: { optionalConsent: termsData.optionalConsent.toString() }
  })
}
</script>

<style scoped>
.page-center {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  padding: 80px 24px 80px;
  box-sizing: border-box;
}

:deep(.title) {
  margin-bottom: 40px;
}

:deep(.title-separator) {
  margin-bottom: 72px;
}
</style>
