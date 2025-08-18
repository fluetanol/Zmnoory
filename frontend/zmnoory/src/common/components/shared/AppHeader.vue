  <!-- [FILEPATH] src/common/components/shared/AppHeader.vue -->
  <template>
    <div class="header-wrapper">
      <header class="app-header">
        <div class="header-left">
          <!-- 모바일 햄버거 메뉴 버튼 -->
          <button class="mobile-menu-button" type="button" aria-label="메뉴 열기" @click="showMobileMenu = !showMobileMenu">
            <span class="hamburger-line"></span>
            <span class="hamburger-line"></span>
            <span class="hamburger-line"></span>
          </button>

          <!-- 데스크톱 로고 -->
          <router-link to="/main" class="logo-link desktop-logo" aria-label="홈으로">
            <img src="@/assets/logo.png" alt="즈믄누리 로고" class="logo" />
          </router-link>

          <!-- 데스크톱 네비게이션 -->
          <nav class="navigation desktop-nav" aria-label="주요 메뉴">
            <router-link to="/games" class="nav-item">게임</router-link>
            <router-link to="/videos" class="nav-item">영상</router-link>
            <router-link to="/store" class="nav-item">상점</router-link>
            <router-link to="/data-request" class="nav-item">데이터 요청</router-link>
          </nav>
        </div>

        <!-- 모바일 로고 중앙 -->
        <div class="header-center">
          <router-link to="/" class="logo-link mobile-logo" aria-label="홈으로">
            <img src="@/assets/logo.png" alt="즈믄누리 로고" class="logo" />
          </router-link>
        </div>

        <div class="user-menu">
          <button class="search-button" type="button" aria-label="검색 열기" @click="showSearch = !showSearch">
            <img src="@/assets/search-icon.png" alt="" />
          </button>


          <LoginHeader v-if="!com_loggedIn" />
          <LogoutHeader v-else />
          <div v-else class="logged-in-menu">
            <router-link :to="{name: 'main'}" style="text-decoration: none;">
              <button class="login-button" type="button" @click="handleLogout">로그아웃</button>
            </router-link>
            <router-link to="/mypage" aria-label="마이페이지">
              <img :src="userProfileImage" alt="마이페이지" class="profile-picture" />
            </router-link>
          </div>

          <div v-if="showSearch" class="search-dropdown">
            <input
                v-model="searchQuery"
                type="text"
                placeholder="검색어 입력"
                @keyup.enter="onSearch"
            />
            <button type="button" aria-label="검색" @click="onSearch">
              <img src="@/assets/search-icon.png" alt="" />
            </button>
          </div>
        </div>
      </header>

      <!-- 모바일 메뉴 드롭다운 -->
      <div v-if="showMobileMenu" class="mobile-menu-dropdown">
        <nav class="mobile-navigation" aria-label="모바일 메뉴">
          <router-link to="/games" class="mobile-nav-item" @click="showMobileMenu = false">게임</router-link>
          <router-link to="/videos" class="mobile-nav-item" @click="showMobileMenu = false">영상</router-link>
          <router-link to="/store" class="mobile-nav-item" @click="showMobileMenu = false">상점</router-link>
          <router-link to="/data-request" class="mobile-nav-item" @click="showMobileMenu = false">데이터 요청</router-link>
        </nav>
      </div>
    </div>
  </template>

  <script setup>
  import { ref, computed } from 'vue'
  import { useRouter } from 'vue-router'
  import { useAccountStore } from '@/store/Accounts'
  import testProfileImage from '@/assets/test_profile.png'
  import LoginHeader from '../login/LoginHeader.vue'
  import LogoutHeader from '../login/LogoutHeader.vue'

  const showSearch = ref(false)
  const showMobileMenu = ref(false)
  const searchQuery = ref('')
  const router = useRouter()

  /* 로그인 상태 */
  const accountStore = useAccountStore()
  const com_loggedIn = computed(() => accountStore.isLoggedIn)
  const userProfileImage = computed(() => {
    return accountStore.userInfo?.profileImageUrl || testProfileImage
  })

  function handleLogout () {
    accountStore.logout()
  }

  function onSearch () {
    const q = searchQuery.value.trim()
    if (!q) return
    router.push({ path: '/games', query: { q } })
    showSearch.value = false
  }
  </script>

  <style scoped>
  .header-wrapper {
    position: relative;
  }

  .app-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 85px;
    padding: 0 8px;
    border-bottom: 1px solid #2E2E2E;
    box-sizing: border-box;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 35px;
  }

  .header-center {
    display: none;
  }

  .logo {
    width: 250px;
  }

  .navigation {
    display: flex;
    gap: 25px;
  }

  .nav-item {
    text-decoration: none;
    color: #2E2E2E;
    font-size: 24px;
    font-weight: 600;
    transition: color 0.2s ease;
  }

/* hover 시 글자 색상 살짝 변경 */
.nav-item:hover {
  color: #C4A484/* 강조 색상: 필요 시 브랜드 컬러로 변경 */
}

  .user-menu {
    position: relative;
    display: flex;
    align-items: center;
    gap: 20px;
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

.login-button,
.signup-button {
  transition: all 0.25s ease; /* hover 전환 부드럽게 */
}

/* 로그인 버튼 hover */
.login-button:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
}

/* 회원가입 버튼 hover */
.signup-button:hover {
  color: white;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 10px rgba(0,0,0,0.15);
}

  .search-button {
    border: none;
    background: none;
    cursor: pointer;
    height: 38px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0; /* 기본 여백 제거 */
  }

  .search-button img {
    width: 22px;
    height: 22px;
  }

  /* 모바일 햄버거 메뉴 버튼 */
  .mobile-menu-button {
    display: none;
    flex-direction: column;
    justify-content: space-between;
    width: 30px;
    height: 24px;
    background: none;
    border: none;
    cursor: pointer;
    padding: 0;
  }

  .hamburger-line {
    width: 100%;
    height: 3px;
    background-color: #2E2E2E;
    border-radius: 2px;
    transition: all 0.3s ease;
  }

  /* 모바일 메뉴 드롭다운 */
  .mobile-menu-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: #FFF;
    border-bottom: 1px solid #2E2E2E;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    z-index: 10;
  }

  .mobile-navigation {
    display: flex;
    flex-direction: column;
    padding: 20px;
  }

  .mobile-nav-item {
    text-decoration: none;
    color: #2E2E2E;
    font-size: 18px;
    font-weight: 600;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
  }

/* hover 효과 */
@media (hover: hover) and (pointer: fine) {
  .mobile-nav-item:hover {
    background-color: #FAF4EF; /* 살짝 밝은 베이지 */
    padding-left: 8px;         /* 왼쪽으로 약간 밀리는 느낌 */
  }
}

  .mobile-nav-item:last-child {
    border-bottom: none;
  }

  /* 검색 드롭다운 */
  .search-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    margin-top: 8px;
    display: flex;
    align-items: center;
    background: #FFF;
    border: 1px solid #CCC;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    border-radius: 4px;
    padding: 8px;
    gap: 8px;
    z-index: 10;
  }

  .search-dropdown input {
    border: 1px solid #CCC;
    height: 38px;
    padding: 0 8px;
  }

  .search-dropdown button {
    background: none;
    border: none;
    cursor: pointer;
  }

  .search-dropdown button img {
    width: 18px;
    height: 18px;
  }

  /* 공통: 로그인/회원가입 버튼 그룹 */
  .auth-buttons {
    display: flex;
    gap: 10px;
  }

  /* 기본 회원가입 버튼 스타일 (데스크톱에서 보임) */
  .signup-button {
    width: 93px;
    height: 38px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1.5px solid #2E2E2E;
    border-radius: 19px;
    background-color: #2E2E2E;
    font-size: 19px;
    font-weight: 600;
    color: #ffffff;
    text-decoration: none;
    cursor: pointer;
    text-align: center;
    font-family: Inter, sans-serif;
    line-height: normal;
  }

  .logged-in-menu {
    display: flex;
    align-items: center;
    gap: 18px;
  }

  .profile-picture {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
    display: block;
    cursor: pointer;
  }

  /* === 반응형: 마지막에 둬서 우선 적용되게 함 === */
  @media (max-width: 900px) {
    .app-header {
      padding: 0 20px;
      height: 70px;
    }

    .header-left {
      gap: 20px;
    }

    .desktop-logo { display: none; }

    .header-center {
      display: flex;
      align-items: center;
      justify-content: center;
      flex: 1;
    }

    .logo {
    width: 250px;
  }

    .desktop-nav { display: none; }

    .mobile-menu-button { display: flex; }

    .user-menu { gap: 15px; }

    .login-button {
      width: 80px;
      height: 32px;
      font-size: 14px;
    }

    .search-button img { width: 20px; height: 20px; }

    /* 모바일(≤900px)에서 회원가입 숨김 → 로그인만 보이게 */
    .auth-buttons .signup-button { display: none; }

    .auth-buttons { gap: 5px; }
  }

  @media (max-width: 480px) {
    .app-header {
      padding: 0; /* Removed horizontal padding */
      height: 60px;
    }

    .header-left { gap: 15px; }

    .logo { width: 150px; }

    .user-menu { gap: 12px; }

    .login-button {
      width: 70px;
      height: 28px;
      font-size: 14px;
    }

    .search-button img { width: 18px; height: 18px; }

    .mobile-menu-dropdown { padding: 0; }

    .mobile-navigation { padding: 16px; }

    .mobile-nav-item {
      font-size: 16px;
      padding: 10px 0;
    }
  }
  </style>
