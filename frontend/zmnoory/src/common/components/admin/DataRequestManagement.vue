<template>
  <div class="management-page">
    <!-- 목록 페이지 -->
    <div v-if="!showDetailPage">
      <div class="page-header">
        <h2 class="page-title">데이터 요청 관리</h2>
        <select v-model="selectedStatus" @change="filterByStatus" class="filter-select">
          <option value="">전체 상태</option>
          <option value="PENDING">대기중</option>
          <option value="REVIEWING">검토중</option>
          <option value="APPROVED">승인됨</option>
          <option value="IN_PROGRESS">진행중</option>
          <option value="COMPLETED">완료</option>
          <option value="REJECTED">거절됨</option>
        </select>
      </div>

      <div class="content-area">
        <div class="datarequests-list">
          <div v-for="request in allDataRequests" :key="request.id" class="item-card data-request-card">
            <div class="item-info">
              <h4>{{ request.companyName }}</h4>
              <p>연락처: {{ request.contactInfo }}</p>
              <span class="item-meta">
                요청일: {{ formatDate(request.requestDate) }}
                <span v-if="request.processedDate"> | 처리일: {{ formatDate(request.processedDate) }}</span>
              </span>
            </div>
            <div class="item-actions">
              <select v-model="request.status" @change="updateDataRequestStatus(request)" class="status-select">
                <option value="PENDING">대기중</option>
                <option value="REVIEWING">검토중</option>
                <option value="APPROVED">승인됨</option>
                <option value="IN_PROGRESS">진행중</option>
                <option value="COMPLETED">완료</option>
                <option value="REJECTED">거절됨</option>
              </select>
              <button class="action-btn small" @click="viewDetail(request.id)">상세보기</button>
            </div>
            <div v-if="request.adminNotes" class="admin-notes">
              <p><strong>관리자 메모:</strong> {{ request.adminNotes }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 상세 페이지 -->
    <DataRequestDetail 
      v-if="showDetailPage" 
      :requestId="selectedRequestId" 
      @goBack="goBackToList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { DataRequest, DataRequestStatus } from '@/services/info'
import { useDataRequestStore } from '@/store/DataRequests'
import DataRequestDetail from './DataRequestDetail.vue'

const dataRequestStore = useDataRequestStore()

// 데이터
const allDataRequests = ref<DataRequest[]>([])
const selectedStatus = ref<DataRequestStatus | ''>('')

// 페이지 상태
const showDetailPage = ref(false)
const selectedRequestId = ref<number>(0)

onMounted(() => {
  loadAllDataRequests()
})

// 데이터 요청 관련 함수
const loadAllDataRequests = async () => {
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    allDataRequests.value = await dataRequestStore.getAllDataRequests(undefined, isLocalDev)
  } catch (error) {
    console.error('데이터 요청 목록 로드 실패:', error)
    alert('데이터 요청 목록을 불러오는데 실패했습니다.')
  }
}

const filterByStatus = async () => {
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    const status = selectedStatus.value || undefined
    allDataRequests.value = await dataRequestStore.getAllDataRequests(status as DataRequestStatus, isLocalDev)
  } catch (error) {
    console.error('데이터 요청 필터링 실패:', error)
    alert('데이터 요청 필터링에 실패했습니다.')
  }
}

const updateDataRequestStatus = async (request: DataRequest) => {
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    const result = await dataRequestStore.updateDataRequestStatus(request.id, request.status, isLocalDev)
    if (result.success) {
      alert(result.message)
      // 현재 필터 상태를 유지하면서 목록 재조회
      filterByStatus()
    } else {
      alert(result.message)
    }
  } catch (error) {
    console.error('데이터 요청 상태 변경 실패:', error)
    alert('상태 변경에 실패했습니다.')
  }
}

const viewDetail = (requestId: number) => {
  selectedRequestId.value = requestId
  showDetailPage.value = true
}

const goBackToList = () => {
  showDetailPage.value = false
  selectedRequestId.value = 0
  // 목록을 다시 로드해서 변경사항 반영
  filterByStatus()
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
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

.filter-select {
  padding: 8px 16px;
  border: 1px solid #A0A0A0;
  background: #fff;
  color: #2E2E2E;
  font-size: 14px;
  cursor: pointer;
  border-radius: 4px;
}

.content-area {
  flex: 1;
}

.datarequests-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.data-request-card {
  flex-direction: column;
  align-items: stretch;
  padding: 20px;
  border: 1px solid #E0E0E0;
  background: #FAFAFA;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.data-request-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.data-request-card .item-info {
  flex-direction: column;
  align-items: stretch;
  gap: 8px;
  margin-bottom: 12px;
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

.request-content {
  background: #f8f9fa;
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 3px solid #17a2b8;
  font-size: 13px;
  line-height: 1.4;
  max-height: 80px;
  overflow-y: auto;
}

.item-meta {
  font-size: 12px;
  color: #888;
}

.item-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.status-select {
  padding: 4px 8px;
  border: 1px solid #A0A0A0;
  font-size: 12px;
  border-radius: 4px;
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

.action-btn.small {
  padding: 6px 12px;
  font-size: 12px;
}

.admin-notes {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e0e0e0;
}

.admin-notes p {
  margin: 0;
  font-size: 13px;
  color: #666;
  background: #e8f4f8;
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 3px solid #28a745;
}
</style>