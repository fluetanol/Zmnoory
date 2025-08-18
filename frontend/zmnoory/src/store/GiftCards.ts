// [FILEPATH] src/store/GiftCards.ts
import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'
import type { GiftCard } from '@/services/info'

export const useGiftCardStore = defineStore('giftcard', () => {
  const LOCAL_URL = "http://localhost:8080"
  const API_URL = 
    window.location.hostname === "localhost"
      ? LOCAL_URL                // 로컬 프론트 → 로컬 백엔드
      : "https://zmnoory.vercel.app/v1"

  // 내 기프티콘 목록
  const myGiftCards = ref<GiftCard[]>([])

  // 내 기프티콘 목록 조회
  const getMyGiftCards = async (isTest?: boolean): Promise<GiftCard[]> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/giftcards/my`, {
        params: { _ts: Date.now() },
        headers: { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' },
      })
      const list = res.data.body?.data ?? res.data.data ?? []
      myGiftCards.value = list.slice()
      return myGiftCards.value
    } catch (err) {
      console.error('기프티콘 목록 조회 실패:', err)
      myGiftCards.value = []
      return []
    }
  }

  // 기프티콘 사용 완료 처리
  const useGiftCard = async (id: number, isTest?: boolean): Promise<{ success: boolean; message: string }> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      await axios.patch(`${connect_url}/api/giftcards/${id}/use`)
      // 목록 재조회
      await getMyGiftCards(isTest)
      return {
        success: true,
        message: '기프티콘이 사용 완료 처리되었습니다.'
      }
    } catch (err: any) {
      console.error('기프티콘 사용 처리 실패:', err)
      return {
        success: false,
        message: '사용 처리에 실패했습니다. 다시 시도해주세요.'
      }
    }
  }

  // 관리자용: 전체 기프티콘 조회
  const getAllGiftCards = async (isTest?: boolean): Promise<GiftCard[]> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/admin/giftcards`)
      return res.data.body?.data ?? res.data.data ?? []
    } catch (err) {
      console.error('전체 기프티콘 목록 조회 실패:', err)
      throw err
    }
  }

  // 기프티콘 직접 등록
  const registerGiftCard = async (giftCardData: any, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      await axios.post(`${connect_url}/api/admin/giftcards`, giftCardData)
    } catch (err) {
      console.error('기프티콘 등록 실패:', err)
      throw err
    }
  }

  // 관리자용: 상품별 기프티콘 생성
  const createGiftCards = async (productId: number, count: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      await axios.post(`${connect_url}/api/admin/giftcards`, {
        productId,
        count
      })
    } catch (err) {
      console.error('기프티콘 생성 실패:', err)
      throw err
    }
  }

  // 관리자용: 기프티콘 상태 변경
  const updateGiftCardStatus = async (id: number, status: 'ASSIGNED' | 'USED' | 'EXPIRED', isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      await axios.patch(`${connect_url}/api/admin/giftcards/${id}/status`, {
        status
      })
    } catch (err) {
      console.error('기프티콘 상태 변경 실패:', err)
      throw err
    }
  }

  return {
    myGiftCards,
    getMyGiftCards,
    useGiftCard,
    getAllGiftCards,
    registerGiftCard,
    createGiftCards,
    updateGiftCardStatus
  }
})