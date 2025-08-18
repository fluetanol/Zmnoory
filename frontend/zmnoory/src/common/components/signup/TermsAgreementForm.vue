<!-- [FILEPATH] src/common/components/signup/TermsAgreementForm.vue -->
<template>
  <div class="form-container">
    <h2 class="title">다음 내용에 동의해주세요</h2>
    <div class="title-separator"></div>


    <!-- 모두 동의 -->
    <div class="all-agree-box">
      <input type="checkbox" class="checkbox-item" v-model="allChecked" />
      <div class="agree-text-group">
        <p class="agree-main-text">모두 동의</p>
        <p class="agree-sub-text">필수 및 선택 항목 동의 포함</p>
      </div>
    </div>

    <!-- 개별 약관 -->
    <div class="terms-list">
      <!-- 필수 항목 -->
      <div class="term-item required">
        <input type="checkbox" class="checkbox-item" v-model="terms.service" />
        <p class="term-text">서비스 이용 약관 동의 <span class="required-badge">(필수)</span></p>
        <button type="button" class="view-btn" @click="openTerms('service')">보기</button>
      </div>
      <div class="separator"></div>

      <div class="term-item required">
        <input type="checkbox" class="checkbox-item" v-model="terms.privacy" />
        <p class="term-text">개인정보 수집 및 이용 동의 <span class="required-badge">(필수)</span></p>
        <button type="button" class="view-btn" @click="openTerms('privacy')">보기</button>
      </div>
      <div class="separator"></div>

      <div class="term-item required">
        <input type="checkbox" class="checkbox-item" v-model="terms.faceData" />
        <p class="term-text">얼굴 데이터 수집 및 이용 동의 <span class="required-badge">(필수)</span></p>
        <button type="button" class="view-btn" @click="openTerms('faceData')">보기</button>
      </div>
      <div class="separator"></div>

      <!-- 선택 항목 -->
      <div class="term-item optional">
        <input type="checkbox" class="checkbox-item" v-model="terms.thirdParty" />
        <p class="term-text">제3자 판매 및 AI 모델 학습 동의 <span class="optional-badge">(선택)</span></p>
        <button type="button" class="view-btn" @click="openTerms('thirdParty')">보기</button>
      </div>
      <div class="separator"></div>

    </div>

    <button
      class="submit-button"
      @click="goToNextStep"
      :disabled="!isAllRequiredAgreed"
    >
      다음
    </button>

    <!-- 모달 -->
    <TermsModal
      v-if="openedKey"
      :termType="openedKey"
      @close="closeTerms"
    />
  </div>
</template>

<script setup>
import { reactive, computed, ref } from 'vue'
import TermsModal from './TermsModal.vue'

const emit = defineEmits(['next-step'])

/** 개별 항목 상태 */
const terms = reactive({
  service: false,
  privacy: false,
  faceData: false,
  thirdParty: false,
})

/** 모두 동의 (파생 상태) */
const allChecked = computed({
  get: () => Object.values(terms).every(Boolean),
  set: (val) => {
    Object.keys(terms).forEach(k => { terms[k] = val })
  }
})

const requiredTerms = ['service', 'privacy', 'faceData']
const optionalTerms = ['thirdParty']

/** 필수 항목 모두 동의 여부 */
const isAllRequiredAgreed = computed(() => {
  return requiredTerms.every(key => terms[key])
})

function goToNextStep() {
  if (isAllRequiredAgreed.value) {
    emit('next-step', { optionalConsent: terms.thirdParty })
  }
}

/* --------- 약관 보기 모달 --------- */
const openedKey = ref(null)

function openTerms(key) {
  openedKey.value = key
}
function closeTerms() {
  openedKey.value = null
}
</script>

<style scoped>
.form-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-family: 'Inter', sans-serif;
}
.title {
  font-weight: 700;
  font-size: 40px;
  color: #2E2E2E;
  margin-bottom: 20px; /* ↓ 원래 50px → 20px로 줄임 */
}

.title-separator {
  width: 700px;
  height: 0.5px;
  background-color: #2E2E2E;
  margin-bottom: 30px; /* ↓ 구분선 아래 여백 보충 */
}

.all-agree-box {
  box-sizing: border-box;
  width: 100%;
  height: 73px;
  background: #F5E7DA;
  border: 1px solid #A0A0A0;
  display: flex;
  align-items: center;
  padding: 0 25px;
  margin-bottom: 40px;
}
.checkbox-item {
  width: 23px;
  height: 23px;
  border: 1px solid #A0A0A0;
  margin-right: 22px;
  cursor: pointer;
}
.agree-text-group {
  display: flex;
  align-items: baseline;
}
.agree-main-text {
  font-weight: 600;
  font-size: 20px;
  color: #2E2E2E;
  margin-right: 12px;
}
.agree-sub-text {
  font-weight: 400;
  font-size: 15px;
  color: #2E2E2E;
}
.terms-list {
  width: 100%;
  padding: 0 25px;
  box-sizing: border-box;
}
.term-item {
  display: flex;
  align-items: center;
  height: 78px;
  gap: 12px;
}
.term-text {
  flex: 1;
  font-weight: 400;
  font-size: 20px;
  color: #2E2E2E;
}
.view-btn {
  border: 1px solid #A0A0A0;
  background: #FFFFFF;
  padding: 4px 12px;
  font-size: 14px;
  cursor: pointer;
  border-radius: 4px;
  flex-shrink: 0;
}
.separator {
  width: 100%;
  height: 1px;
  background-color: #A0A0A0;
  border: none;
}
.submit-button {
  width: 441px;
  height: 73px;
  background: #F5E7DA;
  border: 1px solid #A0A0A0;
  font-weight: 700;
  font-size: 20px;
  color: #2E2E2E;
  cursor: pointer;
  margin-top: 40px;
}
.submit-button:disabled {
  background-color: #E0E0E0;
  cursor: not-allowed;
  color: #A0A0A0;
}

.required-badge {
  color: #757575;
  font-weight: 500;
}

.optional-badge {
  color: #757575;
  font-weight: 500;
}

.term-item.required {
  padding-left: 22px;
}

.term-item.optional {
  padding-left: 22px;
}

</style>
