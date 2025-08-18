<!-- [FILEPATH] src/common/components/signup/SignupForm.vue -->
<template>
  <div class="signup-form-container">
    <h2 class="title">기본 정보 입력</h2>
    <div class="title-separator"></div>

    <form @submit.prevent="submit" class="form">
      <!-- 닉네임 -->
      <div class="form-group">
        <div class="input-with-button">
          <input
            type="text"
            id="nickname"
            placeholder="닉네임"
            v-model.trim="form.nickname"
            :class="{ 'input-invalid': errors.nickname }"
            @input="onNicknameTyping()"
            @blur="touched.nickname = true; validateNickname()"
          />
          <button
            type="button"
            class="check-button"
            @click="checkNickname"
            :disabled="!isNicknameFormatValid || nicknameStatus === STATUS.CHECKING"
          >
            {{ nicknameStatus === STATUS.CHECKING ? '확인중...' : '중복확인' }}
          </button>
        </div>

        <template v-if="errors.nickname">
          <p class="error-text">{{ errors.nickname }}</p>
        </template>
        <template v-else-if="nicknameStatus === STATUS.TAKEN">
          <p class="error-text">이미 사용 중인 닉네임입니다.</p>
        </template>
        <template v-else-if="nicknameStatus === STATUS.AVAILABLE">
          <p class="success-text">사용 가능한 닉네임입니다.</p>
        </template>
        <template v-else-if="touched.nickname && isNicknameFormatValid">
          <p class="success-text">형식이 올바릅니다. 중복확인을 진행해주세요.</p>
        </template>
        <template v-else>
          <p class="helper-text">{{ HELPERS.nickname }}</p>
        </template>
      </div>

      <!-- 생년월일/성별 -->
      <div class="form-group">
        <label class="form-label">생년월일 / 성별</label>

        <div class="input-with-button">
          <div class="birthdate-group">
            <!-- 연 -->
            <BirthField
              v-model="form.birthYear"
              :options="years"
              placeholder="YYYY"
              :width="120"
              :invalid="!!errors.birthYear"
              :max-length="4"
              :min="1900"
              :max="currentYear"
              numeric-only
              @touched="touched.birthYear = true; validateBirthdate()"
            />
            <!-- 월 -->
            <BirthField
              v-model="form.birthMonth"
              :options="months"
              placeholder="MM"
              :width="93"
              :invalid="!!errors.birthMonth"
              :max-length="2"
              :min="1"
              :max="12"
              numeric-only
              @touched="touched.birthMonth = true; validateBirthdate()"
            />
            <!-- 일 -->
            <BirthField
              v-model="form.birthDay"
              :options="days"
              placeholder="DD"
              :width="93"
              :invalid="!!errors.birthDay"
              :max-length="2"
              :min="1"
              :max="days.length"
              numeric-only
              @touched="touched.birthDay = true; validateBirthdate()"
            />
          </div>



          <button type="button" class="gender-button" :class="{ active: form.gender === '남' }" @click="form.gender = '남'">남</button>
          <button type="button" class="gender-button" :class="{ active: form.gender === '여' }" @click="form.gender = '여'">여</button>
        </div>

        <p v-if="errors.birthYear || errors.birthMonth || errors.birthDay" class="error-text">
          {{ errors.birthYear || errors.birthMonth || errors.birthDay }}
        </p>
      </div>

      <!-- 이메일 -->
      <div class="form-group">
        <div class="input-with-button">
          <input
            type="email"
            id="email"
            placeholder="아이디 (이메일)"
            v-model.trim="form.email"
            :class="{ 'input-invalid': errors.email }"
            @input="onEmailTyping()"
            @blur="touched.email = true; validateEmail()"
          />
          <button
            type="button"
            class="check-button"
            @click="checkEmail"
            :disabled="!isEmailFormatValid || emailStatus === STATUS.CHECKING"
          >
            {{ emailStatus === STATUS.CHECKING ? '확인중...' : '중복확인' }}
          </button>
        </div>
        <template v-if="errors.email">
          <p class="error-text">{{ errors.email }}</p>
        </template>
        <template v-else-if="emailStatus === STATUS.TAKEN">
          <p class="error-text">이미 사용 중인 이메일입니다.</p>
        </template>
        <template v-else-if="emailStatus === STATUS.AVAILABLE">
          <p class="success-text">사용 가능한 이메일입니다.</p>
        </template>
        <template v-else-if="touched.email && isEmailFormatValid">
          <p class="success-text">형식이 올바릅니다. 중복확인을 진행해주세요.</p>
        </template>
        <template v-else>
          <p class="helper-text">{{ HELPERS.email }}</p>
        </template>
      </div>

      <!-- 비밀번호 -->
      <div class="form-group">
        <div class="input-with-button">
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password"
            placeholder="비밀번호"
            v-model="form.password"
            :class="{ 'input-invalid': errors.password }"
            @input="validatePassword()"
            @blur="touched.password = true; validatePassword()"
          />
          <img
            :src="showPassword ? visibleIcon : invisibleIcon"
            alt="비밀번호 토글"
            class="password-toggle"
            @click="showPassword = !showPassword">
        </div>
        <template v-if="errors.password">
          <p class="error-text">{{ errors.password }}</p>
        </template>
        <template v-else-if="touched.password && form.password && !errors.password">
          <p class="success-text">조건을 모두 만족합니다.</p>
        </template>
        <template v-else>
          <p class="helper-text">{{ HELPERS.password }}</p>
        </template>
      </div>

      <!-- 비밀번호 확인 -->
      <div class="form-group">
        <div class="input-with-button">
          <input
            :type="showPasswordConfirm ? 'text' : 'password'"
            id="passwordConfirm"
            placeholder="비밀번호 확인"
            v-model="form.passwordConfirm"
            :class="{ 'input-invalid': errors.passwordConfirm }"
            @input="validatePasswordConfirm()"
            @blur="touched.passwordConfirm = true; validatePasswordConfirm()"
          />
          <img
            :src="showPasswordConfirm ? visibleIcon : invisibleIcon"
            alt="비밀번호 토글"
            class="password-toggle"
            @click="showPasswordConfirm = !showPasswordConfirm">
        </div>
        <template v-if="errors.passwordConfirm">
          <p class="error-text">{{ errors.passwordConfirm }}</p>
        </template>
        <template v-else-if="touched.passwordConfirm && form.passwordConfirm && !errors.passwordConfirm">
          <p class="success-text">비밀번호가 일치합니다.</p>
        </template>
        <template v-else>
          <p class="helper-text">{{ HELPERS.passwordConfirm }}</p>
        </template>
      </div>

      <!-- 추천인 -->
      <div class="form-group">
        <input
          type="text"
          id="recommenderNickname"
          placeholder="추천인 (선택)"
          v-model.trim="form.recommenderNickname"
        />
      </div>

      <button
        type="submit"
        class="submit-button"
        :disabled="!canSubmit || isSubmitting"
      >{{ isSubmitting ? '처리중...' : '회원가입' }}</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { REGEX, HELPERS, STATUS, type Status } from '@/constants/form';
import { useAccountStore } from '@/store/Accounts';
import { ADJECTIVES, NOUNS } from '@/constants/nicknameData';
import { signupSchema } from '@/schemas/authSchema';
import type { ZodIssue } from 'zod';
import BirthField from './BirthField.vue';

import visibleIcon from '@/assets/password_visible.png'
import invisibleIcon from '@/assets/password_invisible.png'

const accountStore = useAccountStore();
const router = useRouter();
const route = useRoute();

// 약관 페이지에서 전달받은 선택 약관 동의 여부
const optionalConsent = ref(route.query.optionalConsent === 'true');

// 디버깅용 로그
console.log('[DEBUG] 쿼리 파라미터 optionalConsent:', route.query.optionalConsent);
console.log('[DEBUG] 선택 약관 동의 여부:', optionalConsent.value);

/* ---------- 날짜 옵션 ---------- */
const currentYear = new Date().getFullYear();
const years = ref(Array.from({ length: 100 }, (_, i) => String(currentYear - i)));
const months = ref(Array.from({ length: 12 }, (_, i) => String(i + 1)));
const days = ref(Array.from({ length: 31 }, (_, i) => String(i + 1)));
function daysInMonth(y: number, m: number) { return new Date(y, m, 0).getDate(); }

/* ---------- 상태 ---------- */
const form = reactive({
  nickname: '',
  email: '',
  password: '',
  passwordConfirm: '',
  gender: '남' as '남' | '여',
  birthYear: '',
  birthMonth: '',
  birthDay: '',
  recommenderNickname: '',
});

const errors = reactive({
  nickname: '',
  birthYear: '',
  birthMonth: '',
  birthDay: '',
  email: '',
  password: '',
  passwordConfirm: '',
});

const touched = reactive({
  nickname: false,
  email: false,
  password: false,
  passwordConfirm: false,
  birthYear: false,
  birthMonth: false,
  birthDay: false,
});

const showPassword = ref(false);
const showPasswordConfirm = ref(false);
const emailStatus = ref<Status>(STATUS.IDLE);
const nicknameStatus = ref<Status>(STATUS.IDLE);
const isSubmitting = ref(false);

/* ---------- 랜덤 닉네임 자동 생성 ---------- */
const generateAndSetUniqueNickname = async () => {
  let isAvailable = false;
  let newNickname = '';
  let attempts = 0;
  const maxAttempts = 10;

  nicknameStatus.value = STATUS.CHECKING;

  while (!isAvailable && attempts < maxAttempts) {
    attempts++;
    const adj = ADJECTIVES[Math.floor(Math.random() * ADJECTIVES.length)];
    const noun = NOUNS[Math.floor(Math.random() * NOUNS.length)];
    newNickname = `${adj}${noun}${Math.floor(Math.random() * 100)}`;
    if (!REGEX.nickname.test(newNickname)) continue;

    try {
      isAvailable = await accountStore.checkNickname(newNickname);
    } catch (error) {
      console.error("닉네임 중복 확인 API 오류:", error);
      break;
    }
  }

  if (isAvailable) {
    form.nickname = newNickname;
    nicknameStatus.value = STATUS.AVAILABLE;
  } else {
    errors.nickname = '사용 가능한 닉네임을 찾지 못했습니다. 직접 입력해주세요.';
    nicknameStatus.value = STATUS.IDLE;
  }
};
onMounted(() => { generateAndSetUniqueNickname(); });

/* ---------- 검증 ---------- */
function validateNickname() {
  if (!form.nickname) errors.nickname = '닉네임을 입력해야 합니다.';
  else if (!REGEX.nickname.test(form.nickname)) errors.nickname = HELPERS.nickname;
  else errors.nickname = '';
}

function validateEmail() {
  if (!form.email) errors.email = '이메일을 입력해야 합니다.';
  else if (!REGEX.email.test(form.email)) errors.email = '이메일 형식이 올바르지 않습니다.';
  else errors.email = '';
}

function validatePassword() {
  if (!form.password) errors.password = '비밀번호를 입력해야 합니다.';
  else if (!REGEX.password.test(form.password)) errors.password = '영문, 숫자, 특수문자를 포함해 8자 이상이어야 합니다.';
  else errors.password = '';
  validatePasswordConfirm();
}

function validatePasswordConfirm() {
  if (!form.passwordConfirm) errors.passwordConfirm = '비밀번호 확인을 입력해야 합니다.';
  else if (form.password !== form.passwordConfirm) errors.passwordConfirm = '비밀번호가 일치하지 않습니다.';
  else errors.passwordConfirm = '';
}

function validateBirthdate() {
  // reset
  errors.birthYear = ''; errors.birthMonth = ''; errors.birthDay = '';

  const y = parseInt(form.birthYear, 10);
  const m = parseInt(form.birthMonth, 10);
  const d = parseInt(form.birthDay, 10);

  if (!form.birthYear || isNaN(y) || form.birthYear.length !== 4 || y < 1900 || y > currentYear) {
    errors.birthYear = '정확한 연도를 입력해주세요 (YYYY)';
  }
  if (!form.birthMonth || isNaN(m) || m < 1 || m > 12) {
    errors.birthMonth = '월을 선택해주세요.';
  }
  if (!form.birthDay || isNaN(d)) {
    errors.birthDay = '일을 입력해주세요.';
  } else if (!errors.birthYear && !errors.birthMonth) {
    const dim = daysInMonth(y, m);
    if (d < 1 || d > dim) errors.birthDay = `${m}월은 ${dim}일까지입니다.`;
  }
}

/* 입력 정규화 & 말일 반영 */
watch([() => form.birthYear, () => form.birthMonth], ([yy, mm]) => {
  const y = parseInt(yy, 10);
  const m = parseInt(mm, 10);
  const dim = (!isNaN(y) && !isNaN(m)) ? daysInMonth(y, m) : 31;
  days.value = Array.from({ length: dim }, (_, i) => String(i + 1));
  const d = parseInt(form.birthDay, 10);
  if (!isNaN(d) && d > dim) form.birthDay = String(dim);
  validateBirthdate();
});

/* ---------- 중복확인 ---------- */
const isEmailFormatValid = computed(() => REGEX.email.test(form.email));
const isNicknameFormatValid = computed(() => REGEX.nickname.test(form.nickname));

function onEmailTyping() { emailStatus.value = STATUS.IDLE; validateEmail(); }
function onNicknameTyping() { nicknameStatus.value = STATUS.IDLE; validateNickname(); }

async function checkEmail() {
  validateEmail();
  if (errors.email) return;
  emailStatus.value = STATUS.CHECKING;
  try {
    const ok = await accountStore.checkEmail(form.email);
    emailStatus.value = ok ? STATUS.AVAILABLE : STATUS.TAKEN;
  } catch {
    emailStatus.value = STATUS.IDLE;
    errors.email = '서버 오류입니다. 잠시 후 다시 시도해주세요.';
  }
}

async function checkNickname() {
  validateNickname();
  if (errors.nickname) return;
  nicknameStatus.value = STATUS.CHECKING;
  try {
    const ok = await accountStore.checkNickname(form.nickname);
    nicknameStatus.value = ok ? STATUS.AVAILABLE : STATUS.TAKEN;
  } catch {
    nicknameStatus.value = STATUS.IDLE;
    errors.nickname = '서버 오류입니다. 잠시 후 다시 시도해주세요.';
  }
}

/* ---------- 제출 가능 여부 ---------- */
const canSubmit = computed(() => {
  return (
    !errors.nickname &&
    !errors.birthYear &&
    !errors.birthMonth &&
    !errors.birthDay &&
    !errors.email &&
    !errors.password &&
    !errors.passwordConfirm &&
    form.nickname &&
    form.birthYear && form.birthMonth && form.birthDay &&
    form.email &&
    form.password &&
    form.passwordConfirm &&
    emailStatus.value === STATUS.AVAILABLE &&
    nicknameStatus.value === STATUS.AVAILABLE
  );
});

/* ---------- 제출 ---------- */
const submit = async () => {
  isSubmitting.value = true;

  // 로컬 검증
  validateNickname();
  validateEmail();
  validatePassword();
  validateBirthdate();
  if (!canSubmit.value) { isSubmitting.value = false; return; }

  // Zod 검증
  Object.keys(errors).forEach(k => (errors as any)[k] = '');
  const result = signupSchema.safeParse(form);
  if (!result.success) {
    result.error.issues.forEach((issue: ZodIssue) => {
      const key = issue.path[0] as keyof typeof errors;
      if (errors[key] !== undefined) errors[key] = issue.message;
    });
    isSubmitting.value = false;
    return;
  }

  const { birthYear, birthMonth, birthDay, passwordConfirm, ...payload } = result.data as any;
  const birthday = `${birthYear}-${String(birthMonth).padStart(2, '0')}-${String(birthDay).padStart(2, '0')}`;

  // ✅ 서버 enum으로 매핑
  const genderMapped = form.gender === '남' ? 'MALE' : 'FEMALE';

  // 서버로 보낼 최종 페이로드 (optionalConsent 포함)
  const requestPayload = { ...payload, birthday, gender: genderMapped, optionalConsent: optionalConsent.value };

  console.log('[SIGNUP][REQUEST] ->', JSON.stringify(requestPayload));

  try {
    await accountStore.signUp(requestPayload);
    console.log('[SIGNUP][SUCCESS]');
    alert('가입이 완료되었습니다!');
    router.push('/login');
  } catch (error: any) {
    console.error('[SIGNUP][UI CATCH]', {
      status: error?.response?.status,
      data: error?.response?.data,
      sent: requestPayload,
    });
    alert('회원가입 중 오류가 발생했습니다. (콘솔 확인)');
  } finally {
    isSubmitting.value = false;
  }
};



/* ---------- 입력 변경 시 상태 초기화 ---------- */
watch(() => form.email, () => { emailStatus.value = STATUS.IDLE; });
watch(() => form.nickname, () => {
  if (nicknameStatus.value === STATUS.AVAILABLE) nicknameStatus.value = STATUS.IDLE;
});

watch(() => form.birthYear,  () => { touched.birthYear  = true; validateBirthdate(); });
watch(() => form.birthMonth, () => { touched.birthMonth = true; validateBirthdate(); });
watch(() => form.birthDay,   () => { touched.birthDay   = true; validateBirthdate(); });
</script>

<style scoped>
.signup-form-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-family: 'Inter', sans-serif;
  color: #2E2E2E;
  width: 100%;
}
.title { font-size: 40px; font-weight: 700; margin-bottom: 25px; }
.title-separator { width: 600px; height: 0.5px; background-color: #2E2E2E; margin-bottom: 44px; }

.form { width: 100%; max-width: 441px; display: flex; flex-direction: column; gap: 20px; }
.form-group { display: flex; flex-direction: column; gap: 6px; }

.form-group input {
  height: 60px; padding: 0 16px; border: 1px solid #A0A0A0; border-radius: 0;
  font-size: 18px; line-height: 1.2;
}
.form-group input::placeholder { color: #A0A0A0; font-weight: 400; }

.helper-text, .error-text, .success-text { font-size: 12.5px; line-height: 1.35; margin-top: 0; padding-left: 4px; }
.error-text { color: #E53935; }
.success-text { color: #2E7D32; }

.input-with-button { display: flex; gap: 8px; position: relative;}
.input-with-button > input { flex-grow: 1; width: 100%; }

.check-button {
  flex-shrink: 0; width: 110px; height: 60px; background: #F5E7DA; border: 1px solid #A0A0A0;
  border-radius: 0; font-weight: 600; font-size: 18px; color: #2E2E2E; cursor: pointer;
}
.check-button.small-btn { width: 90px; }
.check-button:disabled { opacity: 0.5; cursor: not-allowed; }

.gender-button {
  flex-shrink: 0; width: 51px; height: 60px; border: 1px solid #A0A0A0; border-radius: 0; background: #FFFFFF;
  font-weight: 600; font-size: 18px; color: #2E2E2E; cursor: pointer; line-height: 1;
}
.gender-button.active { background: #F5E7DA; }

.submit-button {
  width: 100%; height: 68px; background: #F5E7DA; border: 1px solid #A0A0A0; border-radius: 0;
  font-weight: 700; font-size: 19px; color: #2E2E2E; cursor: pointer; margin-top: 6px;
}
.submit-button:disabled { opacity: 0.5; cursor: not-allowed; }

.input-invalid { border-color: #E53935; }

.form-label { font-weight: 600; font-size: 14px; margin-bottom: 4px; padding-left: 4px; text-align: left; }


.birthdate-group {

  display: flex;

  gap: 8px;
}

.password-toggle {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  width: 24px;
  height: 24px;
}
</style>
