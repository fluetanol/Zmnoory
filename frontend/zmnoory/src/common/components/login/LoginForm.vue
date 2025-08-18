<!-- [FILEPATH] src/common/components/login/LoginForm.vue -->
<template>
  <form class="login-form" @submit.prevent="handleLogin">
    <input v-model="email" type="text" class="form-input" placeholder="아이디(이메일)" />
    <div class="password-wrapper">
      <input v-model="password" :type="isPasswordVisible ? 'text' : 'password'" class="form-input password-input" placeholder="비밀번호" />
      <img
        :src="isPasswordVisible ? visibleIcon : invisibleIcon"
        alt="비밀번호 토글"
        class="password-toggle"
        @click="togglePasswordVisibility">
    </div>
    <span class="password-info">영문, 숫자, 특수문자를 포함한 8자 이상입니다</span>
    <button type="submit" class="login-button">로그인</button>
    <button type="button" @click="goToSignup" class="signup-link">회원가입</button>
  </form>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// 1. Pinia 스토어 가져오기
import { useAccountStore } from '@/store/Accounts';

import visibleIcon from '@/assets/password_visible.png'
import invisibleIcon from '@/assets/password_invisible.png'

const router = useRouter();
const accountStore = useAccountStore(); // 스토어 인스턴스 생성

// 2. 아이디, 비밀번호를 위한 ref 생성
const email = ref('');
const password = ref('');
const isPasswordVisible = ref(false);

// 비밀번호 보기 함수
function togglePasswordVisibility() {
  isPasswordVisible.value = !isPasswordVisible.value;
}

// 3. 로그인 처리 함수
async function handleLogin() {
  if (!email.value || !password.value) {
    alert('아이디와 비밀번호를 모두 입력해주세요.');
    return;
  }
  // 스토어의 login 액션 호출
  await accountStore.login({ email: email.value, password: password.value });
  // 로그인 성공 후 페이지 이동은 스토어의 login 함수 내부에서 처리됩니다.
}

function goToSignup() {
  router.push('/terms');
}
</script>


<style scoped>
.login-form {
  width: 441px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-input {
  height: 65px;
  border: 1px solid #A0A0A0;
  padding: 0 20px;
  font-size: 16px;
  border-radius: 4px;
}

.form-input::placeholder {
  color: #A0A0A0;
}

.password-wrapper {
  position: relative;
}

.password-input {
  width: 100%;
  padding-right: 50px; /* 아이콘 공간 확보 */
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

.login-button {
  height: 73px;
  background-color: #F5E7DA;
  color: #2E2E2E;
  font-size: 20px;
  font-weight: 700;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 40px;
      transition: all 0.2s ease; /* 부드러운 변화 */
  
}

.login-button:hover {
  background-color: #E9DACB; /* 살짝 진하게 */
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
}


/* 4. button을 a 태그처럼 보이도록 스타일을 추가/수정합니다 */
.signup-link {
  color: #2E2E2E;
  font-size: 15px;
  align-self: flex-start;
  margin-top: 15px;
  
  /* button 스타일 초기화 */
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  text-decoration: underline; /* 링크처럼 보이게 */
}

.password-info {
  margin: -15px 0 0 5px;
  color: #A0A0A0;
  font-family: Inter;
  font-size: 12.5px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
</style>