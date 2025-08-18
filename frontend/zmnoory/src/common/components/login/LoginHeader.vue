<template>
       <div class="auth-buttons">
            <router-link to="/login" class="login-button">로그인</router-link>
            <router-link to="/terms" class="signup-button">회원가입</router-link>
        </div>
</template>


<style scoped>
  /* 공통: 로그인/회원가입 버튼 그룹 */
  .auth-buttons {
    display: flex;
    gap: 10px;
  }

.login-button,
.signup-button {
    width: 93px;
    height: 38px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1.5px solid #2E2E2E;
    border-radius: 19px;
    background-color: transparent;
    font-size: 19px;
    font-weight: 600;
    color: #2E2E2E;
    text-decoration: none;
    cursor: pointer;
    text-align: center;
    font-family: Inter, sans-serif;
    line-height: normal;
  }

.login-button,
.signup-button {
  transition: all 0.25s ease; /* hover 전환 부드럽게 */
}

.auth-buttons {
  /* 기본값(부모가 안 내려주면 이 값 사용) */
  --hover-bg: #ffffff;
  --hover-text : #2e2e2e;
}


/* 로그인 버튼 hover */
.login-button:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
  background : var(--hover-bg);
  color : var(--hover-text);
}

/* 회원가입 버튼 hover */
.signup-button:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
  background: var(--hover-bg);
  color : var(--hover-text);
}


</style>