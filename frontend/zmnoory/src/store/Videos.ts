import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'
import type { ImageUploadRequest, Video, VideoUpdatePayload } from '@/services/info'

export const useVideoStore = defineStore('video', () => {
  const LOCAL_URL = "http://localhost:8080"
  const API_URL   =
    window.location.hostname === "localhost"
      ? LOCAL_URL                // 로컬 프론트 → 로컬 백엔드
      : "https://zmnoory.vercel.app/v1"

  // 비디오 상세 조회
  const video_detail = ref<Video | null>(null)
  const getVideoDetail = async (videoId: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/videos/${videoId}`)
      console.log('비디오 상세 조회 get')
      video_detail.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 전체 영상 리스트 조회
  const video_list = ref<Video[] | null>(null)
  const getVideoList = async (isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      // public API이므로 완전히 새로운 axios 인스턴스 사용 (토큰 없이)
      const publicAxios = axios.create({
        baseURL: '',
      })
      
      const res = await publicAxios.get(`${connect_url}/api/videos/public`)
      console.log('전체 영상 목록 get', res.data.body.data)
      video_list.value = res.data.body.data
    } catch (err: any) {
      console.log('영상 목록 조회 실패:', err)
      video_list.value = [] // 실패 시 빈 배열로 설정
    }
  }

  // 사용자별 업로드 영상 목록 조회
  const member_videos = ref<Video[] | null>(null)
  const getMemberVideos = async (memberId: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/videos/member/${memberId}`)
      console.log('사용자 업로드 영상 목록 get')
      member_videos.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 비디오 정보 업데이트
  const updateVideo = async (videoId: number, payload: VideoUpdatePayload, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/videos/${videoId}`, payload)
      console.log('비디오 정보 업데이트 post', res)
    } catch (err) {
      console.log(err)
    }
  }


  const uploadImages = async (payload: ImageUploadRequest, isTest?:boolean) : Promise<void> =>{
      const connect_url = isTest ? LOCAL_URL : API_URL
      try {
        const response = await axios.post(`${connect_url}/api/videos/images`, payload);
        if (response.status === 200) {
          console.log('이미지 업로드 성공:', response.data);
        } else {
          console.warn('예상치 못한 응답 코드:', response.status);
        }
      } catch (error) {
        console.error('이미지 업로드 실패:', error);
        throw error;
      }
    }

  // 내 영상 목록을 저장할 상태
  const myVideos = ref<Video[] | null>(null)

  // '내 영상 목록'을 불러오는 액션
  const getMyVideos = async (isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/videos/my`)
      myVideos.value = res.data.body.data
    } catch (err) {
      console.log('내 영상 목록 조회 실패:', err)
    }
  }

  return {
    video_detail,
    getVideoDetail,
    video_list,
    getVideoList,
    member_videos,
    getMemberVideos,
    updateVideo,
    uploadImages,
    myVideos,
    getMyVideos
  }
})