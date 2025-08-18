<template>

  <div class="logged-in-menu">
    <router-link :to="{name: 'main'}" style="text-decoration: none;">
      <button class="login-button" type="button" @click="handleLogout">로그아웃</button>
    </router-link>
    <router-link to="/mypage" aria-label="마이페이지">
      <img :src="com_userProfileImage" alt="마이페이지" class="profile-picture" />
    </router-link>
  </div>

</template>


<script setup lang="ts">
import { useAccountStore } from '@/store/Accounts';
import { computed } from 'vue';
import testProfileImage from '@/assets/test_profile.png'


const accountStore = useAccountStore();

const com_userProfileImage = computed(() => {
  return accountStore.userInfo?.profileImageUrl || testProfileImage
})

function handleLogout () {
  useAccountStore().logout()
}

</script>


<style scoped>
.logged-in-menu {
  display: flex;
  align-items: center;
  gap: 12px;

  --hover-bg: #ffffff;
  --hover-text : #2e2e2e;
}

  .profile-picture {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
    display: block;
    cursor: pointer;
  }


  .login-button {
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

  .login-button {
  transition: all 0.25s ease; /* hover 전환 부드럽게 */
}

  /* 로그인 버튼 hover */
.login-button:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
  background-color: var(--hover-bg);
  color: var(--hover-text);
}


</style>