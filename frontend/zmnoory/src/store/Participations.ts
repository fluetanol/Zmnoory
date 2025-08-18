// [FILEPATH] src/store/Participations.ts
import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'
import { type ParticipatedPayload, type StartPayload, type ParticipateReward, 
  type UrlPayload, type UrlResponse, type RewardResponase, type PublicUrlPayload, type PublicUrlResponse } from '@/services/info' 

export const useParticipationStore = defineStore('participation', () => {
  const LOCAL_URL = "http://localhost:8080"
    const API_URL   =
    window.location.hostname === "localhost"
      ? LOCAL_URL                // 로컬 프론트 → 로컬 백엔드
      : "https://zmnoory.vercel.app/v1"

  // 로그인한 유저 게임 참여내역 조회
  const participated_game = ref<ParticipatedPayload[] | null>(null)
  const getParticipatedGame = async (isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/participations/member/me`)
      console.log('참여 내역 조희 get', res)
      participated_game.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 게임 참여 시작
  const gameStart = async (payload: StartPayload, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/participations/start`, payload)
      console.log('게임 시작 post', res)
    } catch (err) {
      console.log(err)
    }
  }

  // 영상 업로드용 Presigned URL 생성
  const presigned_url = ref<UrlResponse | null>(null)
  const createUrl = async (payload: UrlPayload, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/participations/presigned-url`, payload)
      console.log('URL 생성 post')
      presigned_url.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  const public_presigned_url = ref<PublicUrlResponse| null>(null)
  const createPublicUrl = async (payload: PublicUrlPayload, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/participations/public-presigned-url`, payload)
      console.log('Public URL 생성 post')
      public_presigned_url.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 참여 완료 및 리워드 지급
  const rewardResponse = ref<RewardResponase | null>(null);
  const gameEnd = async (payload: ParticipateReward, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.put(`${connect_url}/api/participations/complete`, payload)
      console.log('리워드 지급 put', res)
      rewardResponse.value = res.data.body.data;
      
    } catch (err) {
      console.log(err)
    }
  }

  const gameDeleteAll = async (isTest? :boolean) :Promise<void> =>{
    const connect_url = isTest ? LOCAL_URL : API_URL
    try{
      const res = await axios.delete(`${connect_url}/api/participations/me`)
      console.log("delete success", res)
    }catch(err){
      console.error(err);
    }
  }




  return {
    participated_game,
    getParticipatedGame,
    gameStart,
    presigned_url,
    createUrl,
    gameEnd,
    gameDeleteAll,
    rewardResponse,
    public_presigned_url,
    createPublicUrl
  }
})