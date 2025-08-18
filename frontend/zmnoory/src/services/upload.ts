import axios from '@/services/axios'
import axiosClean from 'axios' // Clean axios instance for S3 uploads

const LOCAL_URL = "http://localhost:8080"
const API_URL = window.location.hostname === "localhost"
  ? LOCAL_URL
  : "https://zmnoory.vercel.app/v1"

export interface ProfileImageUploadUrlResponse {
  participationId: null
  uploadUrl: string
  objectKey: string
  message: string
}

export interface ProfileImageUpdateResponse {
  data: string
  message: string
}

export interface ParticipationPresignedResponse {
  participationId: number
  videoUploadUrl: string
  videoObjectKey: string
  thumbnailUploadUrl: string
  thumbnailObjectKey: string
  message: string
}

export interface ParticipationCompletePayload {
  email: string
  gameTitle: string
  videoObjectKey: string
  thumbnailObjectKey: string
  title: string
  description: string
  isPublic: boolean
}

// MIME type normalization function
function normalizeContentType(contentType: string): string {
  const normalizedType = contentType.toLowerCase()
  
  // Normalize common image MIME types
  const mimeTypeMap: Record<string, string> = {
    'image/jpg': 'image/jpeg',
    'image/jpeg': 'image/jpeg',
    'image/png': 'image/png',
    'image/webp': 'image/webp',
    'image/gif': 'image/gif'
  }
  
  return mimeTypeMap[normalizedType] || normalizedType
}

export const profileImageService = {
  async getUploadUrl(fileName: string, contentType: string, isTest?: boolean): Promise<ProfileImageUploadUrlResponse> {
    const normalizedContentType = normalizeContentType(contentType)
    console.log('Original contentType:', contentType, '→ Normalized:', normalizedContentType)
    
    const connect_url = isTest ? LOCAL_URL : API_URL
    
    // Use URLSearchParams for application/x-www-form-urlencoded format
    const formData = new URLSearchParams()
    formData.append('fileName', fileName)
    formData.append('contentType', normalizedContentType)
    
    const response = await axios.post(
      `${connect_url}/api/members/profile-image/upload-url`,
      formData,
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    )
    console.log('Raw API response:', response.data)
    console.log('response.data.body?.data:', response.data.body?.data)
    console.log('Final returned value:', response.data.body?.data || response.data)
    return response.data.body?.data || response.data
  },

  async uploadToS3(uploadUrl: string, file: File): Promise<void> {
    const normalizedContentType = normalizeContentType(file.type)
    console.log('Upload contentType:', file.type, '→ Normalized:', normalizedContentType)
    
    // Use clean axios instance to avoid Authorization header conflict with presigned URL
    await axiosClean.put(uploadUrl, file, {
      headers: {
        'Content-Type': normalizedContentType
      }
    })
  },

  async updateProfile(objectKey: string, isTest?: boolean): Promise<ProfileImageUpdateResponse> {
    const connect_url = isTest ? LOCAL_URL : API_URL
    
    // Use URLSearchParams for application/x-www-form-urlencoded format
    const formData = new URLSearchParams()
    formData.append('key', objectKey)
    
    const response = await axios.patch(
      `${connect_url}/api/members/profile-image`,
      formData,
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    )
    console.log('updateProfile raw response:', response.data)
    
    // Backend returns URL string directly, wrap it in expected format
    const urlString = response.data.body?.data || response.data
    return {
      data: urlString,
      message: '프로필 이미지가 업데이트되었습니다.'
    }
  }
}

export const participationVideoService = {
  async getPresignedUrls(email: string, gameTitle: string, isTest?: boolean): Promise<ParticipationPresignedResponse> {
    const connect_url = isTest ? LOCAL_URL : API_URL
    const response = await axios.post(`${connect_url}/api/participations/public-presigned-url`, {
      email,
      gameTitle
    })
    return response.data.body?.data || response.data
  },

  async uploadToS3(uploadUrl: string, file: File | Blob, contentType: string): Promise<void> {
    // Use clean axios instance to avoid Authorization header conflict with presigned URL
    await axiosClean.put(uploadUrl, file, {
      headers: {
        'Content-Type': contentType
      }
    })
  },

  async completeUpload(payload: ParticipationCompletePayload, isTest?: boolean): Promise<void> {
    const connect_url = isTest ? LOCAL_URL : API_URL
    await axios.put(`${connect_url}/api/participations/complete`, payload)
  }
}

export function createThumbnailFromVideo(videoFile: File): Promise<File> {
  return new Promise((resolve, reject) => {
    const video = document.createElement('video')
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')

    if (!ctx) {
      reject(new Error('Canvas context not available'))
      return
    }

    video.addEventListener('loadedmetadata', () => {
      canvas.width = video.videoWidth
      canvas.height = video.videoHeight
      
      video.currentTime = Math.min(1, video.duration / 2)
    })

    video.addEventListener('seeked', () => {
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
      
      canvas.toBlob((blob) => {
        if (blob) {
          const thumbnailFile = new File([blob], `${videoFile.name}_thumbnail.jpg`, {
            type: 'image/jpeg'
          })
          resolve(thumbnailFile)
        } else {
          reject(new Error('Failed to create thumbnail blob'))
        }
      }, 'image/jpeg', 0.8)
    })

    video.addEventListener('error', () => {
      reject(new Error('Video loading failed'))
    })

    const url = URL.createObjectURL(videoFile)
    video.src = url
    video.load()
  })
}

export function validateFile(file: File, type: 'image' | 'video'): { valid: boolean; error?: string } {
  const maxSizes = {
    image: 10 * 1024 * 1024, // 10MB
    video: 100 * 1024 * 1024  // 100MB
  }

  const allowedTypes = {
    image: ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'],
    video: ['video/mp4', 'video/webm', 'video/avi', 'video/mov']
  }

  if (file.size > maxSizes[type]) {
    return {
      valid: false,
      error: `파일 크기가 너무 큽니다. 최대 ${type === 'image' ? '10MB' : '100MB'}까지 업로드 가능합니다.`
    }
  }

  if (!allowedTypes[type].includes(file.type)) {
    return {
      valid: false,
      error: `지원하지 않는 파일 형식입니다. ${type === 'image' ? 'JPG, PNG, WebP' : 'MP4, WebM, AVI, MOV'} 형식을 사용해주세요.`
    }
  }

  return { valid: true }
}