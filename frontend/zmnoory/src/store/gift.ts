import { defineStore } from 'pinia'
import { registerGift, type GiftCardCreateRequest } from '@/services/gift'

export const useGiftStore = defineStore('gift', () => {
  const register = async (payload: GiftCardCreateRequest, isTest?: boolean) => {
    try {
      await registerGift(payload, isTest)
    } catch (err) {
      console.error('기프티콘 등록 실패:', err)
      throw err
    }
  }

  return { register }
})
