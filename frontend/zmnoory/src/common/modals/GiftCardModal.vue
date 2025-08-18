<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>기프티콘 상세 정보</h2>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>

      <div class="modal-body">
        <div class="giftcard-image">
          <img :src="giftcard.giftCardImage" :alt="giftcard.productTitle" />
        </div>
      </div>

      <div class="modal-footer">
        <button class="cancel-btn" @click="$emit('close')">닫기</button>
      </div>
      </div>
  </div>
</template>

<script setup lang="ts">
import type { GiftCard } from '@/services/info'

defineProps<{
  giftcard: GiftCard
}>()

defineEmits<{
  close: []
}>()
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2e2e2e;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #2e2e2e;
}

.modal-body {
  padding: 24px;
}

.giftcard-image {
  width: 100%;
  height: auto;
  min-height: 300px;
  max-height: 500px;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.giftcard-image img {
  max-width: 100%;
  max-height: 500px;
  width: auto;
  height: auto;
  object-fit: contain;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e0e0e0;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #e0e0e0;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;
}

.cancel-btn:hover {
  background: #eeeeee;
  border-color: #d0d0d0;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 10px;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .copy-field {
    flex-direction: column;
    align-items: stretch;
  }
  
  .qr-code img {
    width: 150px;
    height: 150px;
  }
}
</style>