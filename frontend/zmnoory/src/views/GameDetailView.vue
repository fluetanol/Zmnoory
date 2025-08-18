<!-- [FILEPATH] src/views/GameDetailView.vue -->
<template>
  <div class="main-content-wrapper">
    <AppHeader />
  </div>

  <div class="game-detail" v-if="game">
    <!-- 좌측 썸네일 -->
    <div class="thumbnail-wrapper">
      <img :src="game.thumbnail" alt="게임 썸네일" />
    </div>

    <!-- 우측 정보 카드 -->
    <div class="info-card">
      <h1 class="game-title">{{ game.title }}</h1>
      <p class="game-desc">{{ game.description }}</p>
      <p class="data-info">
        플레이어님의 <strong>{{ game.requireDataType }}</strong> 데이터를 수집합니다
      </p>

      <hr />

      <div class="section">
        <h2>게임 방법</h2>
        <p>{{ game.explanation }}</p>
      </div>

      <div class="section row">
        <span class="label">난이도</span>
        <span class="value">{{ game.difficulty }}</span>
      </div>

      <div class="section row">
        <span class="label">리워드</span>
        <div class="value reward">
          {{ game.point }}
          <img src="@/assets/currency_symbol.png" alt="포인트" />
        </div>
      </div>

      <!-- 버튼 영역: 기존 상태 로직 그대로 -->
      <div class="button-group">
        <button
          v-if="gameResult === null"
          class="cta-btn"
          @click="gameStart"
        >
          게임 시작
        </button>

        <div v-else-if="gameResult === 'none'" class="status-btn">게임 중</div>
        <div v-else-if="gameResult === 'update'" class="status-btn">저장 중</div>

        <button
          v-else-if="gameResult === 'success'"
          class="cta-btn"
          @click="upload"
        >
          업로드
        </button>
      </div>
    </div>
  </div>

  <!-- ====== 기존 오버레이/모달 유지 ====== -->
  <!-- 업로드 진행 오버레이 -->
  <div class="overlay" v-if="isUpload">
    <div class="overlay-content">
      <video :src="ref_videoURL!" controls autoplay class="preview-video"></video>
        <VideoModal
          v-if="accountStore.member_me?.email && videoS3Url && videoObjectKey"
          :email="accountStore.member_me?.email"
          :gameTitle="game?.title || ''"
          :videoUrl="videoS3Url!"
          :thumbnail-url="thumbnailS3Url!"
          :video-object-key="videoObjectKey!"
          :thumbnail-object-key="thumbnailObjectKey!"
          :image-crops="imageCrops!"
          :video-blob="ref_videoBlob!"
          @emit_uploadfinish="handleUploadFinish"
        />

    </div>
  </div>

  <!-- 게임 플레이 오버레이 -->
  <div v-if="showGame" class="overlay">
    <BaseButton @click-event="onGameExit" class="exit-btn">
      <svg viewBox="0 0 24 24" width="25" height="25" aria-hidden="true">
        <path d="M6 6l12 12M18 6L6 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </BaseButton>
    <GameComponent
      :is-test="ref_isTest"
      @emit_game-result="handleGameResult"
      @emit_crop-images="handleCropImage"
      @emit_video="handleVideoURL"
      @emit_videoBlob="handleVideoBlob"
      v-if="showGame"
    />
  </div>

  <!-- 결과 선택 모달 -->
  <div v-if="ref_showResultModal" class="overlay">
    <div class="result-modal">
      <video :src="ref_videoURL!" autoplay loop></video>
      <h1>게임 플레이 영상을 커뮤니티에 업로드 할까요?</h1>
      <div class="button-group row-3">
        <BaseButton class="upload-btn" @click-event="upload">업로드 하기</BaseButton>
        <BaseButton class="download-btn" @click-event="onDownloadVideo">다운로드 하기</BaseButton>
        <BaseButton class="cancel-btn" @click-event="onResultModalExit">안하기</BaseButton>
      </div>
    </div>
  </div>

  <!-- 업로드 완료 안내 모달 -->
  <div v-if="showUploadCompleteModal" class="overlay">
    <div class="result-modal">
      <h1>커뮤니티에 영상이 업로드 되었습니다!</h1>
      <p>확인하시겠어요?</p>
      <div class="button-group row-2">
        <BaseButton class="confirm-btn" @click-event="goToVideoDetail">확인</BaseButton>
        <BaseButton class="cancel-btn" @click-event="goToGameList">취소</BaseButton>
      </div>
    </div>
  </div>

  <SiteFooter />
</template>

<script setup lang="ts">
import AppHeader from '@/common/components/shared/AppHeader.vue'
import SiteFooter from '@/common/components/shared/SiteFooter.vue'
import BaseButton from '@/common/components/shared/BaseButton.vue'
import VideoModal from '@/common/modals/VideoModal.vue'

import { ref, computed, onMounted, defineAsyncComponent } from 'vue'
import { useRouter } from 'vue-router'
import { useGameStore } from '@/store/Games'
import { useAccountStore } from '@/store/Accounts'
import { useParticipationStore } from '@/store/Participations'
import { loadGameById } from '@/modules/games/registry'
import type { ImagePayload, MyMember } from '@/services/info'

const router = useRouter()
const gamestore = useGameStore()
const accountStore = useAccountStore()
const participationStore = useParticipationStore()

const props = defineProps<{ id: number }>()
const gameId = computed(() => Number(props.id))

const GameComponent = computed(() => {
  const loader = loadGameById(gameId.value)
  console.log("로드 ",loader)
  return loader ? defineAsyncComponent({ loader }) : null
})

const game = computed(() => gamestore.game_detail)

const showGame = ref<boolean>(false)
const videoS3Url = ref<string | null>(null)
const thumbnailS3Url = ref<string | null>(null)
const videoObjectKey = ref<string | null>(null)
const thumbnailObjectKey = ref<string | null>(null)
const imageCrops = ref<ImagePayload[] | null>(null)

const ref_videoURL = ref<string>('')
const ref_showResultModal = ref<boolean>(false)
const ref_videoBlob = ref<Blob>()
const ref_isTest = ref<boolean>(true)
const showUploadCompleteModal = ref<boolean>(false)
const uploadedVideoId = ref<number | null>(null)

let member: MyMember | null = null

onMounted(async () => {
  await gamestore.getGameDetail(gameId.value)
  const loader = loadGameById(gameId.value)
  console.log("loader ", loader);
  if (accountStore.member_me == null && accountStore.isLoggedIn) {
    member = await accountStore.getMyDetail()
  } else {
    member = accountStore.member_me
  }
  console.log("찍히고 있다 찍히고 있어 !! ", member?.optionalConsent)
  ref_isTest.value = !member?.optionalConsent!
})

const gameResult = ref<string | null>(null)
function handleGameResult(result: string) {
  gameResult.value = result
  if (result === 'fail') {
    showGame.value = false
  } else if (result === 'success') {
    showGame.value = false
    ref_showResultModal.value = true
  }
}

function handleCropImage(result: ImagePayload[]) {
  imageCrops.value = result
}
function handleVideoURL(result: string) {
  ref_videoURL.value = result
}
function handleVideoBlob(result: Blob) {
  ref_videoBlob.value = result
}

const isUpload = ref<boolean | null>(false)

function gameStart() {
  showGame.value = true
}

async function upload() {
  isUpload.value = true
  ref_showResultModal.value = false

  await participationStore.createPublicUrl({
    email: member?.email!,
    gameTitle: game.value?.title!
  })

  const publicurl = participationStore.public_presigned_url
  videoS3Url.value = publicurl?.videoUploadUrl!
  thumbnailS3Url.value = publicurl?.thumbnailUploadUrl!
  videoObjectKey.value = publicurl?.videoObjectKey!
  thumbnailObjectKey.value = publicurl?.thumbnailObjectKey!
}

function handleUploadFinish(videoId: number) {
  isUpload.value = false
  uploadedVideoId.value = videoId
  showUploadCompleteModal.value = true
}

function onResultModalExit() {
  gameResult.value = 'fail'
  ref_showResultModal.value = false
}

function onDownloadVideo() {
  const a = document.createElement('a')
  a.href = ref_videoURL.value
  a.download = 'gameplay.webm'
  document.body.appendChild(a)
  a.click()
  a.remove()
}

function onGameExit() {
  showGame.value = false
  gameResult.value = null
}

function goToVideoDetail() {
  if (uploadedVideoId.value) {
    router.push({ name: 'VideoDetail', params: { id: uploadedVideoId.value } })
  }
}

function goToGameList() {
  router.push({ name: 'games' })
}
</script>

<style scoped>
/* ===== 헤더 wrapper ===== */
.main-content-wrapper {
  max-width: 1440px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

/* ===== 메인 레이아웃(개선) ===== */
.game-detail {
  display: flex;
  gap: 50px;
  padding: 60px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.thumbnail-wrapper img {
  width: 760px;
  height: 560px;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.info-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 18px;
  align-self: center;
}

.game-title {
  font-size: 26px;
  font-weight: 700;
  color: #2e2e2e;
  text-align: left;
}

.game-desc {
  font-size: 16px;
  color: #444;
  line-height: 1.5;
}

.data-info {
  font-size: 14px;
  color: #888;
}

hr {
  border: none;
  border-top: 1px solid #e0e0e0;
  margin: 8px 0 0;
}

.section {
  margin-top: 10px;
}

.section h2 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
}

.section.row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  font-weight: 600;
  color: #333;
}

.value {
  font-size: 15px;
  color: #555;
}

.reward img {
  width: 15px;
  height: 15px;
  margin-left: 4px;
}

.button-group {
  margin-top: 14px;
  display: flex;
  gap: 12px;
}

/* CTA 버튼(로그인 버튼 톤과 통일) */
.cta-btn {
  flex: 1;
  height: 60px;
  border-radius: 10px;
  background: #f5e7da;
  color: #2e2e2e;
  font-size: 18px;
  font-weight: 700;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}
.cta-btn:hover {
  background-color: #dec9b6;
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(0,0,0,0.12);
}
.cta-btn:active {
  transform: translateY(0);
  box-shadow: none;
}

/* 상태 버튼(비활성 느낌) */
.status-btn {
  flex: 1;
  height: 60px;
  border-radius: 10px;
  background: #e5e5e5;
  color: #2e2e2e;
  font-size: 16px;
  font-weight: 600;
  border: none;
  display: grid;
  place-items: center;
}

/* 반응형 */
@media (max-width: 1024px) {
  .thumbnail-wrapper img {
    width: 60vw;
    height: auto;
  }
}
@media (max-width: 900px) {
  .game-detail {
    flex-direction: column;
    align-items: center;
    gap: 24px;
    padding-top: 40px;
  }
  .thumbnail-wrapper img {
    width: 100%;
    height: auto;
  }
  .info-card {
    width: 100%;
  }
}

/* ====== 기존 오버레이/모달 스타일 유지 + 약간 정리 ====== */
.overlay {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 동그란 종료 버튼 */
:deep(.exit-btn) {
  position: absolute;
  top: 12px;
  right: 25px;
  width: 44px;
  height: 44px;
  border-radius: 9999px;
  background: radial-gradient(120% 120% at 30% 30%, rgba(255,255,255,0.98), rgba(240,240,240,0.9));
  color: #222;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 10px 25px rgba(0,0,0,0.25),
    inset 0 0 0 1px rgba(255,255,255,0.6);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  cursor: pointer;
  transition: transform 140ms ease, box-shadow 140ms ease, background-color 140ms ease, opacity 140ms ease;
  z-index : 100;
}
:deep(.exit-btn:hover) {
  transform: translateY(-1px) scale(1.05);
  box-shadow:
    0 12px 30px rgba(0,0,0,0.28),
    0 0 0 6px rgba(255,64,129,0.16),
    inset 0 0 0 1px rgba(255,255,255,0.7);
}
:deep(.exit-btn:active) {
  transform: translateY(0) scale(0.96);
  background: radial-gradient(120% 120% at 30% 30%, rgba(245,245,245,0.95), rgba(230,230,230,0.9));
  box-shadow:
    0 8px 18px rgba(0,0,0,0.22),
    0 0 0 2px rgba(255,64,129,0.28),
    inset 0 0 0 1px rgba(255,255,255,0.6);
}
:deep(.exit-btn:focus-visible) {
  outline: none;
  box-shadow:
    0 10px 25px rgba(0,0,0,0.25),
    0 0 0 3px rgba(255,255,255,0.9),
    0 0 0 6px rgba(255,64,129,0.5);
}
:deep(.exit-btn)::after {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: 9999px;
  box-shadow: 0 0 0 0 rgba(255,64,129,0.0);
  pointer-events: none;
  transition: box-shadow 280ms ease;
}
:deep(.exit-btn:active)::after {
  box-shadow: 0 0 0 10px rgba(255,64,129,0.22);
}
@media (prefers-reduced-motion: reduce) {
  :deep(.exit-btn),
  :deep(.exit-btn:hover),
  :deep(.exit-btn:active),
  :deep(.exit-btn::after) {
    transition: none;
  }
}

/* 결과 모달 */
.result-modal {
  background: white;
  border-radius: 12px;
  padding: 20px;
  width: 650px;
  max-width: 90%;
  box-shadow: 0 8px 20px rgba(0,0,0,0.25);
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.result-modal video {
  width: 100%;
  border-radius: 8px;
}
.result-modal h1 {
  font-size: 1.2rem;
  margin: 0;
}

.button-group.row-3 {
  display: flex;
  gap: 25px;
  justify-content: center;
}
.button-group.row-2 {
  display: flex;
  gap: 25px;
  justify-content: center;
}
.upload-btn,
.download-btn,
.cancel-btn,
.confirm-btn {
  flex: 1;
}

/* 업로드 오버레이 내부 레이아웃 */
.overlay-content {
  width: min(1100px, 96vw);
  max-height: 88vh;
  display: flex;
  gap: 5%;
}
.preview-video {
  width: 45%;
  aspect-ratio: 5 / 4;
  background: #000;
  border-radius: 12px;
  box-shadow: 0 10px 24px rgba(0,0,0,0.25);
}
.form-pane {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 10px 24px rgba(0,0,0,0.12);
  max-height: 88vh;
}
.video-modal:deep(.modal-input) {
  height: 46px;
  font-size: 15px;
}

/* 작은 화면에서 한열로 */
@media (max-width: 900px) {
  .overlay-content {
    flex-direction: column;
    gap: 16px;
    max-height: 92vh;
  }
  .form-pane { max-height: none; }
}
</style>
