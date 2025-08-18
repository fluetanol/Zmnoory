// [FILEPATH] src/store/DataRequests.ts
import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'
import type { DataRequest, DataRequestCreatePayload, DataRequestStatus } from '@/services/info'

export const useDataRequestStore = defineStore('dataRequest', () => {
  const LOCAL_URL = "http://localhost:8080"
  const API_URL = 
    window.location.hostname === "localhost"
      ? LOCAL_URL
      : "https://zmnoory.vercel.app/v1"

  // 내 데이터 요청 목록
  const myDataRequests = ref<DataRequest[]>([])
  
  // 전체 데이터 요청 목록 (관리자용)
  const allDataRequests = ref<DataRequest[]>([])

  // 사용자용: 데이터 요청 제출
  const createDataRequest = async (payload: DataRequestCreatePayload, isTest?: boolean): Promise<{ success: boolean; message: string }> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      await axios.post(`${connect_url}/api/data-requests`, payload)
      return {
        success: true,
        message: '데이터 요청이 성공적으로 제출되었습니다.'
      }
    } catch (err: any) {
      console.error('데이터 요청 제출 실패:', err)
      return {
        success: false,
        message: err.response?.data?.message || '데이터 요청 제출에 실패했습니다.'
      }
    }
  }

  // 사용자용: 내 요청 목록 조회
  const getMyDataRequests = async (isTest?: boolean): Promise<DataRequest[]> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const url = `${connect_url}/api/members/me/data-requests`
      
      const res = await axios.get(url, {
        headers: { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' }
      })
      
      const list = res.data.body?.data ?? res.data.data ?? []
      
      myDataRequests.value = list.slice()
      return myDataRequests.value
    } catch (err) {
      console.error('내 데이터 요청 목록 조회 실패:', err)
      myDataRequests.value = []
      return []
    }
  }

  // 관리자용: 전체 요청 목록 조회
  const getAllDataRequests = async (status?: DataRequestStatus, isTest?: boolean): Promise<DataRequest[]> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const params = status ? { status } : {}
      const res = await axios.get(`${connect_url}/api/admin/data-requests`, {
        params,
        headers: { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' }
      })
      const list = res.data.body?.data ?? res.data.data ?? []
      allDataRequests.value = list.slice()
      return allDataRequests.value
    } catch (err) {
      console.error('전체 데이터 요청 목록 조회 실패:', err)
      allDataRequests.value = []
      return []
    }
  }

  // 관리자용: 요청 상세 조회
  const getDataRequestById = async (id: number, isTest?: boolean): Promise<DataRequest | null> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/admin/data-requests/${id}`)
      return res.data.body?.data ?? res.data.data ?? null
    } catch (err) {
      console.error('데이터 요청 상세 조회 실패:', err)
      return null
    }
  }

  // 관리자용: 상태 변경
  const updateDataRequestStatus = async (id: number, status: DataRequestStatus, isTest?: boolean): Promise<{ success: boolean; message: string }> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      await axios.patch(`${connect_url}/api/admin/data-requests/${id}/status`, { status })
      // 목록 재조회
      await getAllDataRequests(undefined, isTest)
      return {
        success: true,
        message: '상태가 성공적으로 변경되었습니다.'
      }
    } catch (err: any) {
      console.error('데이터 요청 상태 변경 실패:', err)
      return {
        success: false,
        message: err.response?.data?.message || '상태 변경에 실패했습니다.'
      }
    }
  }

  // 관리자용: 관리자 메모 추가/수정
  const updateDataRequestNotes = async (id: number, adminNotes: string, isTest?: boolean): Promise<{ success: boolean; message: string }> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      await axios.patch(`${connect_url}/api/admin/data-requests/${id}/notes`, { adminNotes })
      // 목록 재조회
      await getAllDataRequests(undefined, isTest)
      return {
        success: true,
        message: '메모가 성공적으로 저장되었습니다.'
      }
    } catch (err: any) {
      console.error('데이터 요청 메모 저장 실패:', err)
      return {
        success: false,
        message: err.response?.data?.message || '메모 저장에 실패했습니다.'
      }
    }
  }

  // 상태별 한글 라벨
  const getStatusLabel = (status: DataRequestStatus): string => {
    const statusLabels: Record<DataRequestStatus, string> = {
      'PENDING': '대기중',
      'REVIEWING': '검토중', 
      'APPROVED': '승인됨',
      'IN_PROGRESS': '진행중',
      'COMPLETED': '완료',
      'REJECTED': '거절됨'
    }
    return statusLabels[status] || status
  }

  // 상태별 색상 클래스
  const getStatusColor = (status: DataRequestStatus): string => {
    const statusColors: Record<DataRequestStatus, string> = {
      'PENDING': 'status-pending',
      'REVIEWING': 'status-reviewing',
      'APPROVED': 'status-approved', 
      'IN_PROGRESS': 'status-progress',
      'COMPLETED': 'status-completed',
      'REJECTED': 'status-rejected'
    }
    return statusColors[status] || 'status-pending'
  }

  return {
    myDataRequests,
    allDataRequests,
    createDataRequest,
    getMyDataRequests,
    getAllDataRequests,
    getDataRequestById,
    updateDataRequestStatus,
    updateDataRequestNotes,
    getStatusLabel,
    getStatusColor
  }
})