// [FILEPATH] src/store/Likes.ts
import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'

export const useLikeStore = defineStore('like', () => {
  const LOCAL_URL = "http://localhost:8080"
    const API_URL   =
    window.location.hostname === "localhost"
      ? LOCAL_URL                // 로컬 프론트 → 로컬 백엔드
      : "https://zmnoory.vercel.app/v1"

  // 좋아요 상태 조회
  const like_status = ref<boolean | null>(null)
  const isLike = async(videoId: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/likes/videos/${videoId}`)
      console.log('좋아요 상태 get')
      like_status.value = res.data.body.data.liked
    } catch (err) {
      console.log(err)
    }
  }

  // 비디오 좋아요 수 조회
  const like_count = ref<number | null>(null)
  const countLike = async(videoId: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/likes/videos/${videoId}/count`)
      console.log('좋아요 개수 get')
      like_count.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 좋아요 토글
  const toggleLike = async(videoId: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/likes/videos/${videoId}`)
      console.log('좋아요 토글 post', res)
    } catch (err) {
      console.log(err)
    }
  }

  return {
    like_status,
    isLike,
    like_count,
    countLike,
    toggleLike,
  }
})