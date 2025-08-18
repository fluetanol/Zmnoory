<template>
  <div class="giftcard-list">
    <div v-if="giftCards.length === 0" class="empty-state">
      <p>구매한 기프티콘이 없습니다.</p>
    </div>
    <div v-else class="giftcard-grid">
      <div 
        v-for="giftcard in giftCards" 
        :key="giftcard.id" 
        class="giftcard-item"
        @click="openGiftCardDetail(giftcard)"
      >
        <div class="giftcard-thumbnail">
          <img :src="giftcard.productThumbnail" :alt="giftcard.productTitle" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { GiftCard } from '@/services/info'

defineProps<{
  giftCards: GiftCard[]
}>()

const emit = defineEmits<{
  openDetail: [giftcard: GiftCard]
}>()

const openGiftCardDetail = (giftcard: GiftCard) => {
  emit('openDetail', giftcard)
}


</script>

<style scoped>
.giftcard-list {
  width: 100%;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #666;
  font-size: 16px;
}

.giftcard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.giftcard-item {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.giftcard-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.giftcard-thumbnail {
  width: 100%;
  height: 250px;
  overflow: hidden;
}

.giftcard-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.giftcard-info {
  padding: 16px;
  position: relative;
}

.product-title {
  font-size: 18px;
  font-weight: 600;
  color: #2e2e2e;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.giftcard-dates {
  margin-bottom: 12px;
}

.purchase-date,
.expiry-date {
  font-size: 14px;
  color: #666;
  margin: 4px 0;
}

.expiry-date.expiring-soon {
  color: #d32f2f;
  font-weight: 600;
}

.warning {
  color: #d32f2f;
  font-weight: 700;
}

.status-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
}

.status-available {
  background: #e8f5e8;
  color: #2e7d32;
}

.status-used {
  background: #f5f5f5;
  color: #666;
}

.status-expired {
  background: #ffebee;
  color: #d32f2f;
}

@media (max-width: 768px) {
  .giftcard-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .giftcard-item {
    margin: 0 10px;
  }
}
</style>