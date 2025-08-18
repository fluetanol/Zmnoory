// [FILEPATH] src/services/auth.ts
import axios from 'axios'
// 공통 인스턴스 쓰고 싶으면 http.ts 만들어서 import 해도 됨.
const http = axios.create({
  baseURL: '/api',  // 백엔드에 맞게 수정
  timeout: 8000
})

export async function checkEmailAPI(email: string): Promise<boolean> {
  const { data } = await http.get('/users/check-email', { params: { email } })
  return data.available
}

export async function checkNicknameAPI(nickname: string): Promise<boolean> {
  const { data } = await http.get('/users/check-nickname', { params: { nickname } })
  return data.available
}

export async function signupAPI(payload: {
  nickname: string
  birthdate: string
  gender: string
  email: string
  password: string
}) {
  const { data } = await http.post('/users', payload)
  return data
}

export async function loginAPI(payload: { email: string; password: string }) {
  const { data } = await http.post('/auth/login', payload)
  return data // 토큰 등
}
