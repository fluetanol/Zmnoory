<!-- [FILEPATH] src/views/MyPageView.vue -->
<template>
  <div class="page-wrapper">
    <div class="page-content">
      <div class="main-content-wrapper">
        <AppHeader></AppHeader>
      </div>
      <div class="mypage-container" :class="{ 'admin-layout': userInfo?.role === 'ADMIN' }">
      <aside v-if="userInfo?.role !== 'ADMIN'" class="decorative-sidebar">
        
        <div class="profile-image-wrapper">
          <img :src="previewUrl || userInfo?.profileImageUrl || testProfileImage" alt="프로필 이미지" class="profile-picture" />

          <!-- 숨김 파일 입력 -->
          <input
            ref="photoInput"
            type="file"
            accept="image/*"
            class="photo-input"
            @change="onFileSelected"
          />

          <!-- 프로필 사진 변경 pill 버튼 -->
          <button
            v-if="isEditing"
            type="button"
            class="change-photo-btn"
            @click="photoInput?.click()"
            aria-label="프로필 사진 변경"
          >
            <svg class="icon-pencil" viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1.003 1.003 0 0 0 0-1.42l-2.34-2.34a1.003 1.003 0 0 0-1.42 0l-1.83 1.83 3.75 3.75 1.84-1.82z"
                fill="currentColor"
              />
            </svg>
            <span>프로필 사진 변경</span>
          </button>

        </div>
        <img :src="profileBackgroundImage" alt="장식 이미지" class="sidebar-image" />
      </aside>

      <main class="main-content">
        <!-- 관리자인 경우 관리자 패널만 표시 -->
        <AdminPanel v-if="userInfo?.role === 'ADMIN'" />
        
        <!-- 일반 사용자인 경우 기본 프로필과 기능들 표시 -->
        <template v-else>
          <UserProfile 
            :user="userInfo" 
            :video-count="videoCount" 
            :selected-photo="selectedPhoto"
            @update:isEditing="isEditing = $event" 
            @photo-uploaded="clearPhotoSelection"
            @photo-cancelled="clearPhotoSelection"
          />

          <template v-if="!isEditing">
            <section class="accordion-section">
              <div class="accordion-header" @click="isVideosExpanded = !isVideosExpanded">
                <span class="arrow" :class="{ 'expanded': isVideosExpanded }">▶</span>
                <h3 class="accordion-title">영상</h3>
              </div>
              <div class="divider"></div>
              <div v-if="isVideosExpanded" class="accordion-content">
                <UserVideoList :videos="myVideos" />
              </div>
            </section>

            <section class="accordion-section">
              <div class="accordion-header" @click="isGiftCardsExpanded = !isGiftCardsExpanded">
                <span class="arrow" :class="{ 'expanded': isGiftCardsExpanded }">▶</span>
                <h3 class="accordion-title">내 기프티콘</h3>
              </div>
              <div class="divider"></div>
              <div v-if="isGiftCardsExpanded" class="accordion-content">
                <UserGiftCardList 
                  :gift-cards="myGiftCards" 
                  @open-detail="openGiftCardDetail"
                />
              </div>
            </section>

            <!-- 데이터 요청이 있는 경우에만 표시 -->
            <section v-if="hasDataRequests" class="accordion-section">
              <div class="accordion-header" @click="isDataRequestsExpanded = !isDataRequestsExpanded">
                <span class="arrow" :class="{ 'expanded': isDataRequestsExpanded }">▶</span>
                <h3 class="accordion-title">데이터 요청</h3>
              </div>
              <div class="divider"></div>
              <div v-if="isDataRequestsExpanded" class="accordion-content">
                <UserDataRequestList />
              </div>
            </section>
          </template>
        </template>
      </main>

      </div>
    </div>
    
    <!-- 기프티콘 상세 모달 -->
    <GiftCardModal 
      v-if="selectedGiftCard" 
      :giftcard="selectedGiftCard" 
      @close="selectedGiftCard = null" 
      @updated="handleGiftCardUpdated"
    />
    
    <SiteFooter></SiteFooter>
  </div>
</template>

<script setup lang="ts">
// ref를 다시 import 목록에 추가합니다.
import { ref, computed, onMounted, watch } from 'vue';
import AppHeader from '@/common/components/shared/AppHeader.vue';
import UserProfile from '@/common/components/mypage/UserProfile.vue';
import UserVideoList from '@/common/components/mypage/UserVideoList.vue';
import UserGiftCardList from '@/common/components/mypage/UserGiftCardList.vue';
import UserDataRequestList from '@/common/components/mypage/UserDataRequestList.vue';
import AdminPanel from '@/common/components/mypage/AdminPanel.vue';
import GiftCardModal from '@/common/modals/GiftCardModal.vue';
import SiteFooter from '@/common/components/shared/SiteFooter.vue';
import { useAccountStore } from '@/store/Accounts';
import { useVideoStore } from '@/store/Videos';
import { useGiftCardStore } from '@/store/GiftCards';
import { useDataRequestStore } from '@/store/DataRequests';
import type { GiftCard } from '@/services/info';
import testProfileImage from '@/assets/test_profile.png';
import profileBackgroundImage from '@/assets/profile_background.jpg';

const accountStore = useAccountStore();
const videoStore = useVideoStore();
const giftCardStore = useGiftCardStore();
const dataRequestStore = useDataRequestStore();

// ✨ [다시 추가] 아코디언 UI를 위한 ref 변수들
const isVideosExpanded = ref(false);
const isGiftCardsExpanded = ref(false);
const isDataRequestsExpanded = ref(false);
const photoInput = ref<HTMLInputElement | null>(null);
const isEditing = ref(false);
const selectedPhoto = ref<File | null>(null);
const previewUrl = ref<string | null>(null);
const selectedGiftCard = ref<GiftCard | null>(null);




// 페이지가 로드될 때 데이터 요청
onMounted(async () => {
  await accountStore.checkAuthStatus(undefined, false);
  videoStore.getMyVideos();
  giftCardStore.getMyGiftCards();
  
  // 데이터 요청 목록 가져오기 (로그인된 사용자 기준)
  dataRequestStore.getMyDataRequests();
});

// 스토어 데이터를 computed로 안전하게 가져오기
const userInfo = computed(() => accountStore.userInfo);
const videoCount = computed(() => videoStore.myVideos?.length ?? 0);
const myVideos = computed(() => videoStore.myVideos);
const myGiftCards = computed(() => giftCardStore.myGiftCards);
const myDataRequests = computed(() => dataRequestStore.myDataRequests);

// 데이터 요청이 있는지 확인
const hasDataRequests = computed(() => {
  const hasRequests = myDataRequests.value && myDataRequests.value.length > 0;
  return hasRequests;
});


watch([userInfo, videoCount], () => {
  // 데이터 변경 감지
}, { immediate: true });

// 로그인 상태가 변경될 때 데이터 요청 목록 다시 가져오기
watch(() => accountStore.isLoggedIn, (isLoggedIn) => {
  if (isLoggedIn) {
    dataRequestStore.getMyDataRequests();
  }
}, { immediate: true });

const onFileSelected = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  
  if (file) {
    if (file.size > 5 * 1024 * 1024) {
      alert('파일 크기는 5MB 이하여야 합니다.');
      return;
    }
    
    if (!file.type.startsWith('image/')) {
      alert('이미지 파일만 업로드 가능합니다.');
      return;
    }
    
    selectedPhoto.value = file;
    
    const reader = new FileReader();
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const clearPhotoSelection = () => {
  selectedPhoto.value = null;
  previewUrl.value = null;
  if (photoInput.value) {
    photoInput.value.value = '';
  }
};

const openGiftCardDetail = (giftcard: GiftCard) => {
  selectedGiftCard.value = giftcard;
};

const handleGiftCardUpdated = async () => {
  await giftCardStore.getMyGiftCards();
};

</script>

<style scoped>
.main-content-wrapper {
  max-width: 1440px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}
.page-wrapper {
  background-color: #FFFFFF;
}
.mypage-container {
  display: flex;
  max-width: 1200px; /* 전체 컨테이너 최대 너비 */
  margin: 0 auto; /* 중앙 정렬 */
  padding: 30px 40px 80px; /* 좌우 패딩 증가 */
  box-sizing: border-box;
  gap: 70px; /* 사이드바와 콘텐츠 사이 간격 */
}
.admin-layout {
  justify-content: center !important;
}

.admin-layout .main-content {
  max-width: 1200px;
  width: 100%;
}

.decorative-sidebar {
  position: relative;
  width: 203px;
  min-width: 203px;
  height: 600px;
  min-height: 600px;
  flex-shrink: 0;
}
.sidebar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.profile-image-wrapper {
  /* 버튼/아바타용 변수는 여기서 정의(스코프 문제 방지) */
  --avatar-size: 158px;
  --pill-w: 141px;
  --pill-h: 29px;
  --pill-gap: 10px;

  position: absolute;
  top: 23px;
  left: 50%;
  transform: translateX(-50%);

  width: var(--avatar-size);
  height: var(--avatar-size);      /* 버튼 위치 계산 기준 */
  min-width: var(--avatar-size);
  min-height: var(--avatar-size);
  display: grid;
  justify-items: center;
  align-items: center;
  z-index: 1;                      /* 배경 이미지 위 */
  flex-shrink: 0;
}

.profile-picture {
  width: var(--avatar-size);
  height: var(--avatar-size);
  border-radius: 50%;
  object-fit: cover;
  display: block;
  border: 2px solid #E8E8E8;
  box-sizing: border-box;
  flex-shrink: 0;
  min-width: var(--avatar-size);
  min-height: var(--avatar-size);
}

/* 파일 입력 숨김 */
.photo-input { display: none; }

/* 피그마 141x29 pill 버튼: 아바타 바로 아래 정렬 */
.change-photo-btn {
  position: absolute;
  left: 50%;
  top: calc(100% + var(--pill-gap));   /* 아바타 높이 + 간격 */
  transform: translateX(-50%);

  width: var(--pill-w);
  height: var(--pill-h);

  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8px;

  background: #F5F5F5;
  color: #5A5A5A;
  border: 1px solid #E8E8E8;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;

  font-family: 'Inter', sans-serif;
  font-weight: 400;
  font-size: 15px;
  line-height: 18px;

  /* 세로로 뒤집히는 현상 방지 */
  white-space: nowrap;
  writing-mode: horizontal-tb;

  z-index: 2;
}
.change-photo-btn:hover {
  background: #EBEBEB;
  border-color: #D0D0D0;
}
.change-photo-btn span { writing-mode: horizontal-tb; }
.change-photo-btn .icon-pencil {
  width: 16px;
  height: 16px;
  transform: rotate(-45deg);
  color: #5A5A5A;
  flex: 0 0 auto;
}


.main-content {
  flex-grow: 1;
}
.accordion-section {
  margin-top: 40px;
}
.accordion-header {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
}
.arrow {
  font-size: 16px;
  color: #5A5A5A;
  transition: transform 0.2s;
}
.arrow.expanded {
  transform: rotate(90deg);
}
.accordion-title {
  font-weight: 700;
  font-size: 20px;
  color: #2E2E2E;
}
.divider {
  border-top: 1px solid #A0A0A0;
  margin-top: 15px;
}
</style>