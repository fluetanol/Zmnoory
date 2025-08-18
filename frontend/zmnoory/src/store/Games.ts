import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'
import type { Game, GameRegisterPayload } from '@/services/info'

export const useGameStore = defineStore('game', () => {
  const LOCAL_URL = "http://localhost:8080"
    const API_URL   =
    window.location.hostname === "localhost"
      ? LOCAL_URL                // 로컬 프론트 → 로컬 백엔드
      : "https://zmnoory.vercel.app/v1"
  
  // 게임 삭제
  const deleteGame = async (id: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.delete(`${connect_url}/api/games/${id}`)
      console.log('게임 삭제 delete', res)
    } catch (err) {
      console.log(err)
    }
  }

  // 게임 단건 조회
  const game_detail = ref<Game | null>(null)
  const getGameDetail = async (id: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/games/${id}`)
      console.log('게임 디테일 get',res)
      game_detail.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 전체 게임 목록 조회
  const game_list = ref<Game[] | null>(null)
  const getGameList = async (isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/games`)
      console.log('전체 게임 목록 get')
      game_list.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  } 

  // 게임 난이도 검색
  type Difficulty = 'EASY' | 'MEDIUM' | 'HARD' | 'VERY_HARD'
  const game_difficulty = ref<Game[] | null>(null)
  const getGameDifficulty = async (difficulty: Difficulty, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/games/search`, {
        params: {difficulty}
      })
      console.log('게임 난이도 검색 get')
      game_difficulty.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 게임 등록
  const gameRegister = async (payload: GameRegisterPayload, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/games`, payload)
      console.log('게임 등록 post', res)
    } catch (err) {
      console.log(err)
    }
  }

  // 게임 수정
  const gameUpdate = async (id: number, payload: GameRegisterPayload, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.put(`${connect_url}/api/games/${id}`, payload)
      console.log('게임 수정 put', res)
    } catch (err) {
      console.log(err)
    }
  }

  // 관리자용: 전체 게임 목록 조회 (데이터 반환)
  const getAllGames = async (isTest?: boolean): Promise<Game[]> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/games`, {
        params: { _ts: Date.now() },
        headers: { 'Cache-Control': 'no-cache', 'Pragma': 'no-cache' },
      })
      return res.data.body?.data ?? []
    } catch (err) {
      console.error('게임 목록 조회 실패:', err)
      throw err
    }
  }

  return {
    deleteGame,
    game_detail,
    getGameDetail,
    game_list,
    getGameList,
    game_difficulty,
    getGameDifficulty,
    gameRegister,
    gameUpdate,
    getAllGames
  }
})