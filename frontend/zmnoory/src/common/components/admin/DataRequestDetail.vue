<template>
  <div class="management-page">
    <div class="page-header">
      <div class="header-left">
        <button class="back-btn" @click="goBack">← 뒤로가기</button>
        <h2 class="page-title">데이터 요청 상세</h2>
      </div>
    </div>

    <div class="content-area" v-if="dataRequest">
      <div class="detail-card">
        <div class="detail-section">
          <h3>기본 정보</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <label>회사명</label>
              <span>{{ dataRequest.companyName }}</span>
            </div>
            <div class="detail-item">
              <label>연락처</label>
              <span>{{ dataRequest.contactInfo }}</span>
            </div>
            <div class="detail-item">
              <label>상태</label>
              <select v-model="dataRequest.status" @change="updateStatus" class="status-select">
                <option value="PENDING">대기중</option>
                <option value="REVIEWING">검토중</option>
                <option value="APPROVED">승인됨</option>
                <option value="IN_PROGRESS">진행중</option>
                <option value="COMPLETED">완료</option>
                <option value="REJECTED">거절됨</option>
              </select>
            </div>
            <div class="detail-item">
              <label>요청일</label>
              <span>{{ formatDate(dataRequest.requestDate) }}</span>
            </div>
            <div class="detail-item" v-if="dataRequest.processedDate">
              <label>처리일</label>
              <span>{{ formatDate(dataRequest.processedDate) }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h3>요청 내용</h3>
          <div class="content-box">
            {{ dataRequest.dataRequirements }}
          </div>
        </div>

        <div class="detail-section">
          <h3>관리자 메모</h3>
          <div class="memo-section">
            <textarea 
              v-model="adminNotes" 
              class="memo-textarea" 
              placeholder="관리자 메모를 입력하세요..."
              rows="4"
            ></textarea>
            <button class="action-btn primary" @click="saveNotes">메모 저장</button>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="loading" class="loading">
      데이터를 불러오는 중...
    </div>

    <div v-else class="error">
      데이터를 불러올 수 없습니다.
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { DataRequest } from '@/services/info'
import { useDataRequestStore } from '@/store/DataRequests'

const props = defineProps<{
  requestId: number
}>()

const emit = defineEmits<{
  goBack: []
}>()

const dataRequestStore = useDataRequestStore()

const dataRequest = ref<DataRequest | null>(null)
const adminNotes = ref('')
const loading = ref(true)

onMounted(() => {
  loadDataRequestDetail()
})

const loadDataRequestDetail = async () => {
  try {
    loading.value = true
    const isLocalDev = window.location.hostname === 'localhost'
    dataRequest.value = await dataRequestStore.getDataRequestById(props.requestId, isLocalDev)
    adminNotes.value = dataRequest.value?.adminNotes || ''
  } catch (error) {
    console.error('데이터 요청 상세 조회 실패:', error)
    alert('데이터 요청 상세 정보를 불러오는데 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const updateStatus = async () => {
  if (!dataRequest.value) return
  
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    const result = await dataRequestStore.updateDataRequestStatus(
      dataRequest.value.id, 
      dataRequest.value.status, 
      isLocalDev
    )
    if (result.success) {
      alert(result.message)
      loadDataRequestDetail()
    } else {
      alert(result.message)
    }
  } catch (error) {
    console.error('상태 변경 실패:', error)
    alert('상태 변경에 실패했습니다.')
  }
}

const saveNotes = async () => {
  if (!dataRequest.value) return
  
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    const result = await dataRequestStore.updateDataRequestNotes(
      dataRequest.value.id, 
      adminNotes.value, 
      isLocalDev
    )
    if (result.success) {
      alert(result.message)
      loadDataRequestDetail()
    } else {
      alert(result.message)
    }
  } catch (error) {
    console.error('메모 저장 실패:', error)
    alert('메모 저장에 실패했습니다.')
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('ko-KR')
}

const goBack = () => {
  emit('goBack')
}
</script>

<style scoped>
.management-page {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #E0E0E0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.back-btn {
  padding: 8px 16px;
  border: 1px solid #A0A0A0;
  background: #fff;
  color: #2E2E2E;
  cursor: pointer;
  font-size: 14px;
  border-radius: 4px;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #F5F5F5;
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

.detail-card {
  background: white;
  border: 1px solid #E0E0E0;
  border-radius: 8px;
  overflow: hidden;
}

.detail-section {
  padding: 25px;
  border-bottom: 1px solid #E0E0E0;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #2E2E2E;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item label {
  font-size: 14px;
  font-weight: 600;
  color: #666;
}

.detail-item span {
  font-size: 16px;
  color: #2E2E2E;
}

.status-select {
  padding: 8px 12px;
  border: 1px solid #A0A0A0;
  font-size: 16px;
  border-radius: 4px;
  color: #2E2E2E;
}

.content-box {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 4px;
  border-left: 3px solid #17a2b8;
  font-size: 16px;
  line-height: 1.6;
  white-space: pre-wrap;
  min-height: 100px;
}

.memo-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.memo-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #A0A0A0;
  border-radius: 4px;
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
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
  align-self: flex-start;
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

.loading, .error {
  text-align: center;
  padding: 50px;
  font-size: 16px;
  color: #666;
}
</style>