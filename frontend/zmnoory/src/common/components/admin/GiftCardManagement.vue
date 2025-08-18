<template>
  <div class="management-page">
    <div class="page-header">
      <h2 class="page-title">기프티콘 관리</h2>
      <button class="action-btn primary" @click="showGiftCardRegisterModal = true">기프티콘 등록</button>
    </div>

    <div class="content-area">
      <div class="giftcards-list">
        <div v-for="giftCard in allGiftCards" :key="giftCard.id" class="item-card">
          <div class="item-info">
            <img :src="giftCard.productThumbnail" :alt="giftCard.productTitle" class="item-thumbnail" />
            <div>
              <h4>{{ giftCard.productTitle }}</h4>
              <span class="item-meta">
                <span :class="{ 'purchased': giftCard.isPurchased, 'available': !giftCard.isPurchased }">
                  {{ giftCard.isPurchased ? '구매됨' : '미구매' }}
                </span>
              </span>
              <p v-if="giftCard.isPurchased && giftCard.purchasedAt" class="purchase-date">
                구매일: {{ formatDate(giftCard.purchasedAt) }}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 기프티콘 등록 모달 -->
    <div v-if="showGiftCardRegisterModal" class="modal-overlay" @click="closeGiftCardRegisterModal">
      <div @click.stop>
        <GiftCardRegisterModal 
          @close="closeGiftCardRegisterModal" 
          @success="handleGiftCardRegisterSuccess" 
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { GiftCard } from '@/services/info'
import { useGiftCardStore } from '@/store/GiftCards'
import GiftCardRegisterModal from '@/common/modals/GiftCardRegisterModal.vue'

const giftCardStore = useGiftCardStore()

// 모달 상태
const showGiftCardRegisterModal = ref(false)

// 데이터
const allGiftCards = ref<GiftCard[]>([])

onMounted(() => {
  loadAllGiftCards()
})

// 기프티콘 관련 함수
const loadAllGiftCards = async () => {
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    allGiftCards.value = await giftCardStore.getAllGiftCards(isLocalDev)
  } catch (error) {
    console.error('기프티콘 목록 로드 실패:', error)
    alert('기프티콘 목록을 불러오는데 실패했습니다.')
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const closeGiftCardRegisterModal = () => {
  showGiftCardRegisterModal.value = false
}

const handleGiftCardRegisterSuccess = () => {
  closeGiftCardRegisterModal()
  loadAllGiftCards()
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

.giftcards-list {
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

.item-meta {
  font-size: 14px;
  color: #888;
}

.purchased {
  color: #28a745;
  font-weight: 600;
}

.available {
  color: #007bff;
  font-weight: 600;
}

.purchase-date {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #666;
}

.item-thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
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