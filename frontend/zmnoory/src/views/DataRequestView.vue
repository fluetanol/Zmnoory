<!-- [FILEPATH] src/views/DataRequestView.vue -->
<template>
  <div class="login-container">
    <img src="@/assets/data-request-bg.png" alt="배경" class="bg-image-element" />

    <!-- 로그인 카드와 동일한 크기/모션 -->
    <div class="login-card reveal-login">
      <RouterLink :to="{name: 'main'}">
        <img src="@/assets/home_button.png" alt="홈으로" class="to-home" />
      </RouterLink>

      <!-- 상단 로고 (로그인 페이지와 동일 컴포지션) -->
      <img src="@/assets/logo_big.png" alt="데이터 요청" style="max-width: 100%; height: auto; margin-bottom: 10px;" />

      <!-- 폼: 로그인 폼과 동일 폭(441px)로 정렬 -->
      <form class="request-form" @submit.prevent="handleSubmit">
        <div class="form-field">
          <input 
            type="text" 
            class="form-input" 
            :class="{ 'error': errors.companyName }"
            placeholder="기업명" 
            v-model="formData.companyName"
            :disabled="isSubmitting"
          />
          <span v-if="errors.companyName" class="error-message">{{ errors.companyName }}</span>
        </div>

        <div class="form-field">
          <input 
            type="email" 
            class="form-input" 
            :class="{ 'error': errors.contactInfo }"
            inputmode="email" 
            placeholder="연락처 (이메일)" 
            v-model="formData.contactInfo"
            :disabled="isSubmitting"
            readonly
          />
          <span v-if="errors.contactInfo" class="error-message">{{ errors.contactInfo }}</span>
        </div>

        <div class="form-field">
          <textarea 
            class="form-textarea" 
            :class="{ 'error': errors.dataRequirements }"
            rows="6" 
            placeholder="필요한 데이터 정보를 상세히 입력해주세요 (최소 10자)"
            v-model="formData.dataRequirements"
            :disabled="isSubmitting"
          ></textarea>
          <span v-if="errors.dataRequirements" class="error-message">{{ errors.dataRequirements }}</span>
        </div>

        <button 
          type="submit" 
          class="login-button"
          :disabled="isSubmitting"
          :class="{ 'submitting': isSubmitting }"
        >
          {{ isSubmitting ? '제출 중...' : '신청하기' }}
        </button>

        <div class="stamp-footer">
          <img src="@/assets/stamp.png" alt="stamp" class="stamp-image" />
          <LoginFooter />
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LoginFooter from '@/common/components/login/LoginFooter.vue'
import { RouterLink } from 'vue-router'
import { useDataRequestStore } from '@/store/DataRequests'
import { useAccountStore } from '@/store/Accounts'

const router = useRouter()
const dataRequestStore = useDataRequestStore()
const accountStore = useAccountStore()

// 폼 데이터
const formData = ref({
  companyName: '',
  contactInfo: '',
  dataRequirements: ''
})

// 폼 상태
const isSubmitting = ref(false)
const errors = ref<Record<string, string>>({})

// 이메일 validation
const isValidEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 폼 validation
const validateForm = (): boolean => {
  errors.value = {}
  
  if (!formData.value.companyName.trim()) {
    errors.value.companyName = '기업명을 입력해주세요.'
  }
  
  if (!formData.value.contactInfo.trim()) {
    errors.value.contactInfo = '연락처를 입력해주세요.'
  } else if (!isValidEmail(formData.value.contactInfo)) {
    errors.value.contactInfo = '올바른 이메일 형식을 입력해주세요.'
  }
  
  if (!formData.value.dataRequirements.trim()) {
    errors.value.dataRequirements = '필요한 데이터 정보를 입력해주세요.'
  } else if (formData.value.dataRequirements.length < 10) {
    errors.value.dataRequirements = '데이터 요구사항을 10자 이상 입력해주세요.'
  }
  
  return Object.keys(errors.value).length === 0
}

// 폼 제출
const handleSubmit = async () => {
  if (!validateForm()) return
  
  isSubmitting.value = true
  
  try {
    const result = await dataRequestStore.createDataRequest({
      companyName: formData.value.companyName.trim(),
      contactInfo: formData.value.contactInfo.trim(),
      dataRequirements: formData.value.dataRequirements.trim()
    })
    
    if (result.success) {
      alert('데이터 요청이 성공적으로 제출되었습니다!\n담당자가 검토 후 연락드리겠습니다.')
      
      // 데이터 요청 목록 새로고침
      await dataRequestStore.getMyDataRequests()
      
      // 폼 초기화
      formData.value = {
        companyName: '',
        contactInfo: accountStore.userInfo?.email || '',
        dataRequirements: ''
      }
      
      // 메인 페이지로 이동
      router.push({ name: 'main' })
    } else {
      alert(result.message)
    }
  } catch (error) {
    console.error('데이터 요청 제출 중 오류:', error)
    alert('요청 제출 중 오류가 발생했습니다. 다시 시도해주세요.')
  } finally {
    isSubmitting.value = false
  }
}

// 페이지 로드 시 로그인한 사용자 이메일 설정
onMounted(async () => {
  await accountStore.checkAuthStatus()
  if (accountStore.userInfo?.email) {
    formData.value.contactInfo = accountStore.userInfo.email
  }
})
</script>

<style scoped>
/* ===== 로그인 페이지와 동일 레이아웃 ===== */
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  background-color: black; /* 남는 여백 채움 */
}

.bg-image-element {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  object-fit: cover;
  z-index: 0;
}

.login-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 530px;                /* 로그인과 동일 */
  background: white;
  padding: 34px;
  box-shadow: 0 10px 50px rgba(0,0,0,0.25);
  box-sizing: border-box;
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

/* ===== 폼(로그인 폼과 동일 폭/톤) ===== */
.request-form {
  width: 420px;               /* 로그인폼과 동일 너비 */
  max-width: 100%;
  display: flex;
  flex-direction: column;
  gap: 17px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-input,
.form-textarea {
  width: 100%;
  border: 1px solid #A0A0A0;
  border-radius: 4px;
  font-size: 16px;
  font-family: inherit;
  padding: 0 20px;
  color: #2E2E2E;
  background: #fff;
}

.form-input {
  height: 55px;               /* 로그인 input과 동일 높이 */
}

.form-textarea {
  padding-top: 12px;
  padding-bottom: 12px;
  resize: none;
  line-height: 1.4;
  min-height: 130px;
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: #A0A0A0;
}

/* 포커스 피드백(로그인 페이지 톤에 맞춤) */
.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #8B5E3C;
  box-shadow: 0 0 0 3px rgba(139, 94, 60, 0.12);
}

/* 에러 상태 */
.form-input.error,
.form-textarea.error {
  border-color: #dc3545;
  box-shadow: 0 0 0 3px rgba(220, 53, 69, 0.12);
}

.error-message {
  font-size: 14px;
  color: #dc3545;
  margin-top: 4px;
}

/* 비활성화 상태 */
.form-input:disabled,
.form-textarea:disabled {
  background-color: #f8f9fa;
  cursor: not-allowed;
  opacity: 0.7;
}

/* 읽기 전용 상태 */
.form-input:read-only {
  background-color: #f8f9fa;
  cursor: default;
  color: #6c757d;
}

/* 버튼도 로그인 페이지 버튼과 동일 규격 */
.login-button {
  height: 65px;
  background-color: #F5E7DA;
  color: #2E2E2E;
  font-size: 20px;
  font-weight: 700;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
    transition: all 0.2s ease; /* 부드러운 변화 */
}


.login-button:hover:not(:disabled) {
  background-color: #E9DACB; /* 살짝 진하게 */
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
}

.login-button:disabled {
  background-color: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.login-button.submitting {
  position: relative;
}

/* 하단 도장 + 푸터 */
.stamp-footer {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.stamp-image {
  width: 60px;
  height: 60px;
}

/* ===== 로그인 페이지와 같은 진입 모션 ===== */
@keyframes fadeUpLogin {
  from { opacity: 0; transform: translateY(8px) scale(0.995); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}
.reveal-login {
  animation: fadeUpLogin 460ms cubic-bezier(0.22, 1, 0.36, 1) both;
  will-change: opacity, transform;
}

/* 소형 화면 대응(로그인 페이지에 없는 보강만 최소화) */
@media (max-width: 560px) {
  .login-card {
    width: calc(100vw - 32px);
    padding: 24px;
  }
  .request-form { width: 100%; }
}

/* 접근성 */
@media (prefers-reduced-motion: reduce) {
  .reveal-login { animation: none !important; }
}
</style>
