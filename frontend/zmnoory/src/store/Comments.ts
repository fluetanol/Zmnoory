import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from '@/services/axios'
import type { Comment } from '@/services/info'

export const useCommentStore = defineStore('comment', () => {
  const LOCAL_URL = "http://localhost:8080"
    const API_URL   =
    window.location.hostname === "localhost"
      ? LOCAL_URL                // 로컬 프론트 → 로컬 백엔드
      : "https://zmnoory.vercel.app/v1"
  
  // 댓글 삭제
  const deleteComment = async(commentId: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      await axios.delete(`${connect_url}/api/comments/${commentId}`)
    } catch (err) {
      console.log(err)
    }
  }

  // 비디오 댓글 목록 조회
  const comment_list = ref<Comment[] | null>(null)
  const getCommentList = async(videoId: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/comments/videos/${videoId}`)
      comment_list.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 비디오 댓글 수 조회
  const comment_amount = ref<number | null>(null)
  const getCommentAmount = async(videoId: number, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.get(`${connect_url}/api/comments/videos/${videoId}/count`)
      console.log('댓글 수 get')
      comment_amount.value = res.data.body.data
    } catch (err) {
      console.log(err)
    }
  }

  // 댓글 작성
  const createComment = async(videoId: number, content: string, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.post(`${connect_url}/api/comments/videos/${videoId}`, {content})
      console.log('댓글 작성 post', res)
      await getCommentList(videoId, isTest)
    } catch (err) {
      console.log(err)
    }
  }

  // 댓글 수정
  const updateComment = async(commentId: number, content: string, isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL
    try {
      const res = await axios.put(`${connect_url}/api/comments/${commentId}`, content)
      console.log('댓글 수정 put', res)
    } catch (err) {
      console.log(err)
    }
  }

  return {
    deleteComment,
    comment_list,
    getCommentList,
    comment_amount,
    getCommentAmount,
    createComment,
    updateComment,
  }
})