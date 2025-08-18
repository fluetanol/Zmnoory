<!-- [FILEPATH] src/views/GameListView.vue -->
<template>
  <div class="page-wrapper">
    <div class="page-content">
      <div class="main-content-wrapper">
        <AppHeader />
      </div>

      <div class="main-container">
        <SearchView
          :filters="['난이도', '보상']"
          :first_options="['쉬움', '중간', '어려움', '매우 어려움']"
          :second_options="['최소 보상', '최대 보상']"
          :is-range="true"
          @search="onSearch"
        />

        <!-- 상단 우측: 게임 추가 버튼 -->
        <div class="actions">
          <button v-if="isAdmin" class="game-create" @click="showModal = true">게임 추가하기</button>
        </div>

        <!-- 카드 그리드 -->
        <div class="card-container">
          <!-- 데모용 임시 카드 (필요 없으면 지우세요) -->
          <RouterLink
            v-for="game in filterGames"
            :key="game.id"
            :to="{ name: 'GameDetail', params: { id: game.id } }"
            class="card-link"
          >
            <GameCard
              :game="game"
              :participationStatus="getParticipationStatus(game.title)"
            />
          </RouterLink>
        </div>

        <div v-if="showModal" class="modal-overlay">
          <GameModal @close="showModal = false" @success="handleGameCreated" />
        </div>

        <!-- Coming Soon -->
        <section class="coming-soon-section">
          <div class="coming-soon-content">
            <div class="coming-soon-image">
              <img src="@/assets/SendingHeart.png" alt="Coming Soon" />
            </div>
            <div class="coming-soon-text">
              <h3>게임을 계속해서 추가중입니다.</h3>
              <p>다음게임을 기대해주세요!</p>
            </div>
          </div>
        </section>
      </div>
    </div>
    <SiteFooter />
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/common/components/shared/AppHeader.vue'
import SiteFooter from '@/common/components/shared/SiteFooter.vue'
import SearchView from '@/common/components/shared/SearchView.vue'
import GameCard from '@/common/components/shared/GameCard.vue'
import GameModal from '@/common/modals/GameModal.vue'
import { useGameStore } from '@/store/Games'
import { useAccountStore } from '@/store/Accounts'
import { useParticipationStore } from '@/store/Participations'
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import type { Game } from '@/services/info'

const showModal = ref(false)
const isAdmin = ref(false)

const gamestore = useGameStore()
const accountstore = useAccountStore()
const participationstore = useParticipationStore()
const filterGames = ref<Game[]>([])

const getParticipationStatus = (title: string): 'COMPLETED' | 'NOT_PARTICIPATED' => {
  const found = (participationstore.participated_game ?? []).find(p => p.gameTitle === title)
  return found ? (found.status as 'COMPLETED' | 'NOT_PARTICIPATED') : 'NOT_PARTICIPATED'
}

const loadList = async () => {
  await gamestore.getGameList()
  filterGames.value = gamestore.game_list ?? []
}

onMounted(async () => {
  await loadList()
  await accountstore.getMyDetail()
  if (accountstore.member_me?.role === 'ADMIN') isAdmin.value = true
})

const handleGameCreated = async () => {
  await loadList()
  showModal.value = false
}

type SearchParams = {
  keyword: string
  first: string
  second: string
  min: number
  max: number
}

const onSearch = (params: SearchParams) => {
  const { keyword, first, min, max } = params
  const base = gamestore.game_list ?? []
  filterGames.value = base.filter(game => {
    const matchKeyword = keyword === '' || game.title.includes(keyword) || game.description.includes(keyword)
    const matchDifficulty = first === '' || game.difficulty === first
    const matchPoint = game.point >= min && game.point <= max
    return matchKeyword && matchDifficulty && matchPoint
  })
}
</script>

<style scoped>
.main-content-wrapper {
  max-width: 1440px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
  box-sizing: border-box;
}

.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 1440px;
  margin: 0 auto;
  padding: 30px 20px 80px;
  box-sizing: border-box;
}

/* 우측 정렬 액션 영역 */
.actions {
  width: 100%;
  max-width: 1440px;
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

/* 반응형 그리드: 기본 4열 → 3열 → 2열 → 1열 */
.card-container {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr)); /* 기본 3열 */
  gap: 32px 24px;
  width: 100%;
  margin-top: 32px;

  /* 서로 다른 카드 높이에도 겹치지 않게 */
  align-items: start;
}

/* 1280px 이하: 3열 */
@media (max-width: 1280px) {
  .card-container {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 28px 20px;
  }
}

/* 900px 이하: 2열 */
@media (max-width: 900px) {
  .card-container {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 24px 18px;
  }
}

/* 600px 이하: 1열 */
@media (max-width: 600px) {
  .card-container {
    grid-template-columns: 1fr;
    gap: 18px;
  }
}

/* 링크가 그리드 셀 전체를 차지하도록 */
.card-link {
  display: block;
  width: 100%;
  text-decoration: none;
}

/* Coming Soon */
.coming-soon-section {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
  opacity: 0.5;
}

.coming-soon-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 20px;
}

.coming-soon-image img {
  width: 240px;
  height: 240px;
  object-fit: contain;
}

.coming-soon-text h3 {
  font-size: 18px;
  font-weight: 600;
  color: #666;
  margin: 0;
}

.coming-soon-text p {
  font-size: 14px;
  color: #999;
  margin: 8px 0 0 0;
}

/* 게임 추가 버튼: 고정 마진 제거, 반응형 정렬 */
.game-create {
  color: #2e2e2e;
  font-family: Inter, system-ui, -apple-system, Segoe UI, Roboto, 'Noto Sans KR', Arial, sans-serif;
  font-size: 15px;
  font-weight: 500;
  border: 1px solid #2e2e2e;
  background-color: #FBF5F0;
  width: 130px;
  height: 40px;
  border-radius: 20px;
  cursor: pointer;
}

/* 모달 오버레이 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, .5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
</style>
