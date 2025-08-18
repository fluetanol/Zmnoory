// [FILEPATH] src/store/Products.ts
import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'
import type { Product, CreateProductPayload } from '@/services/info'

export const useProductStore = defineStore('product', () => {
  const LOCAL_URL = "http://localhost:8080"
    const API_URL   =
    window.location.hostname === "localhost"
      ? LOCAL_URL                // 로컬 프론트 → 로컬 백엔드
      : "https://zmnoory.vercel.app/v1"
  
  // 상품 삭제
  const deleteProduct = async (id: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.delete(`${connect_url}/api/products/${id}`)
      console.log('상품 삭제 delete', res)
    } catch (err) {
      console.error('상품 삭제 실패:', err)
      throw err
    }
  }

  // 상품 단건 조회
  const product_detail = ref<Product | null>(null)
  const getProductDetail = async (id: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/products/${id}`)
      console.log('상품 단건 조회 get')
      product_detail.value = res.data.body.data
    } catch (err) {
      console.error('상품 조회 실패:', err)
      throw err
    }
  }

  // 상품 전체 목록 조회
  const product_list = ref<Product[]>([]) // null 대신 빈 배열로 시작 권장
  const getProductList = async (isTest?: boolean): Promise<Product[]> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/products`, {
        params: { _ts: Date.now() },                 // ← 캐시 버스터
        headers: { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' }, // ← 프록시/브라우저 캐시 회피
      })
      const list = res.data.body?.data ?? []
      product_list.value = list.slice()              // ← 새 배열로 교체(렌더 트리거 확실)
      return product_list.value
    } catch (err) {
      console.error('상품 목록 조회 실패:', err)
      product_list.value = []
      return []
    }
  }

  // 상품 등록
  const createProduct = async(payload: CreateProductPayload, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/products`, payload)
      console.log('상품 등록 post', res)
    } catch (err) {
      console.error('상품 등록 실패:', err)
      throw err  // 에러를 다시 던져서 호출하는 곳에서 처리할 수 있도록 함
    }
  }

  // 상품 수정
  const updateProduct = async(id: number, payload: CreateProductPayload, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.put(`${connect_url}/api/products/${id}`, payload)
      console.log('상품 수정 put', res)
    } catch (err) {
      console.error('상품 수정 실패:', err)
      throw err
    }
  }

  // 상품 구매
  const orderProduct = async(productTitle: string, isTest?: boolean): Promise<{ success: boolean; message: string; data?: any }> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/orders`, {
        productTitle
      })
      console.log('상품 구매 post', res)
      return {
        success: true,
        message: '기프티콘이 발급되었습니다! 마이페이지에서 확인하세요.',
        data: res.data.data
      }
    } catch(err: any) {
      console.log(err)
      let message = '구매에 실패했습니다. 다시 시도해주세요.'
      
      // 서버에서 온 구체적인 에러 메시지 표시
      if (err.response?.data?.body?.message) {
        message = err.response.data.body.message
      } else if (err.response?.data?.message) {
        message = err.response.data.message
      }
      
      if (err.response?.status === 400) {
        const errorMessage = err.response.data?.message || ''
        if (errorMessage.includes('포인트')) {
          message = '포인트가 부족합니다.'
        } else if (errorMessage.includes('기프티콘')) {
          message = '해당 상품의 기프티콘이 품절되었습니다.'
        }
      }
      
      return {
        success: false,
        message
      }
    }
  }

  return {
    deleteProduct,
    product_detail,
    getProductDetail,
    product_list,
    getProductList,
    createProduct,
    updateProduct,
    orderProduct,
  }
})