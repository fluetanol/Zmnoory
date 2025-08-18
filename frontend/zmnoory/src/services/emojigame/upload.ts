// [FILEPATH] src/services/emojigame/upload.ts
import axios from 'axios'
const testUploadUrl = 'https://zmnoory.free.beeceptor.com'

export const uploadImage = async (imageInfo: { image: string; imageName: string }) => {
  const blob = dataURLtoBlob(imageInfo.image)
  const formData = new FormData()
  formData.append('file', blob, `${imageInfo.imageName}.png`)

  return await axios.post(testUploadUrl, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadVideo = async (videoUrl: string) => {
  const response = await fetch(videoUrl)
  const blob = await response.blob()

  const formData = new FormData()
  formData.append('file', blob, `recorded_video.webm`)

  return await axios.post(testUploadUrl, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

function dataURLtoBlob(dataURL: string): Blob {
  const byteString = atob(dataURL.split(',')[1])
  const mimeString = dataURL.split(',')[0].split(':')[1].split(';')[0]
  const ab = new ArrayBuffer(byteString.length)
  const ia = new Uint8Array(ab)
  for (let i = 0; i < byteString.length; i++) {
    ia[i] = byteString.charCodeAt(i)
  }
  return new Blob([ab], { type: mimeString })
}
