<template>
  <div class="management-page">
    <div class="page-header">
      <h2 class="page-title">게임 관리</h2>
      <button class="action-btn primary" @click="openGameModal">게임 등록</button>
    </div>

    <div class="content-area">
      <div class="games-list">
        <div v-for="game in games" :key="game.id" class="item-card">
          <div class="item-info">
            <img :src="game.thumbnail" :alt="game.title" class="item-thumbnail" />
            <div>
              <h4>{{ game.title }}</h4>
              <p>{{ game.description }}</p>
              <span class="item-meta">포인트: {{ game.point }} | 난이도: {{ game.difficulty }}</span>
            </div>
          </div>
          <div class="item-actions">
            <button class="action-btn small" @click="editGame(game)">수정</button>
            <button class="action-btn small danger" @click="deleteGame(game.id)">삭제</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 게임 등록/수정 모달 -->
    <div v-if="showGameModal" class="modal-overlay" @click="closeGameModal">
      <div @click.stop>
        <GameModal 
          :game="selectedGame"
          @close="closeGameModal" 
          @success="handleGameSuccess" 
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Game } from '@/services/info'
import { useGameStore } from '@/store/Games'
import GameModal from '@/common/modals/GameModal.vue'

const gameStore = useGameStore()

// 모달 상태
const showGameModal = ref(false)
const selectedGame = ref<Game | null>(null)

// 데이터
const games = ref<Game[]>([])

onMounted(() => {
  loadGames()
})

// 게임 관련 함수
const loadGames = async () => {
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    games.value = await gameStore.getAllGames(isLocalDev)
  } catch (error) {
    console.error('게임 목록 로드 실패:', error)
    alert('게임 목록을 불러오는데 실패했습니다.')
  }
}

const openGameModal = () => {
  selectedGame.value = null
  showGameModal.value = true
}

const editGame = (game: Game) => {
  selectedGame.value = game
  showGameModal.value = true
}

const deleteGame = async (gameId: number) => {
  if (!confirm('정말로 이 게임을 삭제하시겠습니까?\n\n⚠️ 주의: 해당 게임에 참여한 사용자 데이터가 있는 경우 삭제가 불가능할 수 있습니다.')) return
  
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    await gameStore.deleteGame(gameId, isLocalDev)
    alert('게임이 삭제되었습니다.')
    loadGames()
  } catch (error: any) {
    console.error('게임 삭제 실패:', error)
    
    if (error?.response?.data?.body?.message?.includes('foreign key constraint')) {
      alert('게임 삭제 실패: 해당 게임에 참여한 사용자 데이터가 존재합니다.\n참여 데이터를 먼저 처리한 후 다시 시도해주세요.')
    } else {
      alert('게임 삭제에 실패했습니다.')
    }
  }
}

const closeGameModal = () => {
  showGameModal.value = false
  selectedGame.value = null
}

const handleGameSuccess = () => {
  closeGameModal()
  loadGames()
}
</script>

<style scoped>
.management-page {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #E0E0E0;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #2E2E2E;
  margin: 0;
}

.content-area {
  flex: 1;
}

.games-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.item-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border: 1px solid #E0E0E0;
  background: #FAFAFA;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.item-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.item-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
}

.item-info h4 {
  margin: 0 0 5px 0;
  color: #2E2E2E;
  font-size: 18px;
  font-weight: 600;
}

.item-info p {
  margin: 0 0 5px 0;
  color: #666;
  font-size: 14px;
}

.item-meta {
  font-size: 12px;
  color: #888;
}

.item-thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}

.item-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #A0A0A0;
  background: #fff;
  color: #2E2E2E;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  border-radius: 4px;
}

.action-btn:hover {
  background: #F5F5F5;
}

.action-btn.primary {
  background: #F5E7DA;
  border-color: #D4B896;
  font-weight: 600;
  padding: 12px 24px;
  font-size: 16px;
}

.action-btn.small {
  padding: 6px 12px;
  font-size: 12px;
}

.action-btn.danger {
  color: #dc3545;
  border-color: #dc3545;
}

.action-btn.danger:hover {
  background: #dc3545;
  color: white;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
</style>