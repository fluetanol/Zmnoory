<template>
  <div class="data-request-list">
    <div class="section-header">
      <h3 class="section-title">내 데이터 요청</h3>
      <button class="refresh-button" @click="refreshList" :disabled="isLoading">
        {{ isLoading ? '새로고침 중...' : '새로고침' }}
      </button>
    </div>
    
    <div v-if="isLoading && dataRequests.length === 0" class="loading-state">
      <p>데이터 요청 목록을 불러오고 있습니다...</p>
    </div>
    
    <div v-else-if="dataRequests.length === 0" class="empty-state">
      <p>제출한 데이터 요청이 없습니다.</p>
      <RouterLink to="/data-request">
        <button class="action-btn primary">데이터 요청하기</button>
      </RouterLink>
    </div>
    
    <div v-else class="request-grid">
      <div 
        v-for="request in dataRequests" 
        :key="request.id" 
        class="request-card"
      >
        <div class="request-header">
          <h4 class="company-name">{{ request.companyName }}</h4>
          <span 
            class="status-badge" 
            :class="dataRequestStore.getStatusColor(request.status)"
          >
            {{ dataRequestStore.getStatusLabel(request.status) }}
          </span>
        </div>
        
        <div class="request-info">
          <div class="info-row">
            <span class="label">연락처:</span>
            <span class="value">{{ request.contactInfo }}</span>
          </div>
          <div class="info-row">
            <span class="label">요청일:</span>
            <span class="value">{{ formatDate(request.requestDate) }}</span>
          </div>
          <div v-if="request.processedDate" class="info-row">
            <span class="label">처리일:</span>
            <span class="value">{{ formatDate(request.processedDate) }}</span>
          </div>
        </div>
        
        <div class="request-content">
          <p class="label">요청 내용:</p>
          <p class="content">{{ request.dataRequirements }}</p>
        </div>
        
        <div v-if="request.adminNotes" class="admin-notes">
          <p class="label">관리자 메모:</p>
          <p class="notes">{{ request.adminNotes }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useDataRequestStore } from '@/store/DataRequests'
import type { DataRequest } from '@/services/info'

const dataRequestStore = useDataRequestStore()

const dataRequests = ref<DataRequest[]>([])
const isLoading = ref(false)

// 날짜 포맷팅
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 목록 새로고침
const refreshList = async () => {
  isLoading.value = true
  try {
    dataRequests.value = await dataRequestStore.getMyDataRequests()
  } catch (error) {
    console.error('데이터 요청 목록 조회 실패:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  refreshList()
})
</script>

<style scoped>
.data-request-list {
  width: 100%;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #2E2E2E;
  margin: 0;
}

.refresh-button {
  padding: 8px 16px;
  border: 1px solid #A0A0A0;
  background: #fff;
  color: #2E2E2E;
  cursor: pointer;
  font-size: 14px;
  border-radius: 4px;
  transition: all 0.2s;
}

.refresh-button:hover:not(:disabled) {
  background: #F5F5F5;
}

.refresh-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.empty-state .action-btn {
  margin-top: 16px;
  padding: 12px 24px;
  border: 1px solid #F5E7DA;
  background: #F5E7DA;
  color: #2E2E2E;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: all 0.2s;
}

.empty-state .action-btn:hover {
  background: #E9DACB;
}

.request-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.request-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s;
}

.request-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.company-name {
  font-size: 18px;
  font-weight: 600;
  color: #2E2E2E;
  margin: 0;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-reviewing {
  background: #d1ecf1;
  color: #0c5460;
}

.status-approved {
  background: #d4edda;
  color: #155724;
}

.status-progress {
  background: #cce5ff;
  color: #004085;
}

.status-completed {
  background: #e8f5e8;
  color: #2e7d32;
}

.status-rejected {
  background: #ffebee;
  color: #d32f2f;
}

.request-info {
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
}

.label {
  font-weight: 600;
  color: #2E2E2E;
  min-width: 80px;
  margin-right: 12px;
}

.value {
  color: #666;
}

.request-content {
  margin-bottom: 16px;
}

.request-content .label {
  display: block;
  margin-bottom: 8px;
}

.content {
  color: #666;
  line-height: 1.5;
  margin: 0;
  background: #f8f9fa;
  padding: 12px;
  border-radius: 4px;
}

.admin-notes {
  border-top: 1px solid #e0e0e0;
  padding-top: 16px;
}

.admin-notes .label {
  display: block;
  margin-bottom: 8px;
  color: #2E2E2E;
}

.notes {
  color: #666;
  line-height: 1.5;
  margin: 0;
  background: #e8f4f8;
  padding: 12px;
  border-radius: 4px;
  border-left: 4px solid #17a2b8;
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .request-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .info-row {
    flex-direction: column;
    gap: 4px;
  }
  
  .label {
    min-width: auto;
    margin-right: 0;
  }
}
</style>