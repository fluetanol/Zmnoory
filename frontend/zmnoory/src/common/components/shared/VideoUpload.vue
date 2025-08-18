<template>
  <div class="video-upload">
    <div class="upload-container">
      <!-- File Selection -->
      <div v-if="!selectedVideo" class="file-selector">
        <div class="drop-zone" @click="triggerFileInput" @dragover.prevent @drop.prevent="handleDrop">
          <span class="upload-icon">🎥</span>
          <span class="upload-text">비디오 파일을 선택하거나 드래그해주세요</span>
          <span class="file-info">최대 100MB, MP4/WebM/AVI/MOV 지원</span>
        </div>
        <input
          ref="fileInput"
          type="file"
          accept="video/*"
          @change="handleFileSelect"
          class="file-input"
        />
      </div>

      <!-- Video Preview -->
      <div v-else-if="!isUploading" class="video-preview">
        <video 
          ref="videoPreview"
          :src="videoPreviewUrl" 
          controls 
          class="preview-video"
          @loadedmetadata="onVideoLoaded"
        ></video>
        
        <!-- Thumbnail Preview -->
        <div v-if="thumbnailUrl" class="thumbnail-preview">
          <img :src="thumbnailUrl" alt="Video thumbnail" class="thumbnail-image" />
          <span class="thumbnail-label">생성된 썸네일</span>
        </div>

        <!-- Upload Form -->
        <div class="upload-form">
          <div class="form-group">
            <label>제목 *</label>
            <input 
              v-model="videoTitle" 
              type="text" 
              placeholder="비디오 제목을 입력해주세요"
              class="form-input"
              required
            />
          </div>
          
          <div class="form-group">
            <label>설명</label>
            <textarea 
              v-model="videoDescription" 
              placeholder="비디오 설명을 입력해주세요"
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-group checkbox-group">
            <label class="checkbox-label">
              <input v-model="isPublic" type="checkbox" />
              <span class="checkmark"></span>
              공개 비디오로 설정
            </label>
          </div>

          <div class="form-actions">
            <button @click="uploadVideo" class="upload-btn primary" :disabled="!canUpload">
              업로드 시작
            </button>
            <button @click="cancelUpload" class="cancel-btn">
              취소
            </button>
          </div>
        </div>
      </div>

      <!-- Upload Progress -->
      <div v-else class="upload-progress">
        <div class="progress-header">
          <h3>업로드 중...</h3>
          <span class="progress-percentage">{{ Math.round(totalProgress) }}%</span>
        </div>
        
        <div class="progress-details">
          <div class="progress-item">
            <span class="progress-label">썸네일 생성:</span>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${thumbnailProgress}%` }"></div>
            </div>
            <span class="progress-status">{{ getProgressStatus('thumbnail') }}</span>
          </div>
          
          <div class="progress-item">
            <span class="progress-label">비디오 업로드:</span>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${videoUploadProgress}%` }"></div>
            </div>
            <span class="progress-status">{{ getProgressStatus('video') }}</span>
          </div>
          
          <div class="progress-item">
            <span class="progress-label">썸네일 업로드:</span>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${thumbnailUploadProgress}%` }"></div>
            </div>
            <span class="progress-status">{{ getProgressStatus('thumbnailUpload') }}</span>
          </div>
          
          <div class="progress-item">
            <span class="progress-label">완료 처리:</span>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${completeProgress}%` }"></div>
            </div>
            <span class="progress-status">{{ getProgressStatus('complete') }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Messages -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue'
import { participationVideoService, createThumbnailFromVideo, validateFile } from '@/services/upload'

interface Props {
  email: string
  gameTitle: string
  isTest?: boolean
}

interface Emits {
  (e: 'uploaded', data: { videoUrl: string; thumbnailUrl: string; title: string }): void
  (e: 'error', error: string): void
}

const props = withDefaults(defineProps<Props>(), {
  isTest: false
})

const emit = defineEmits<Emits>()

// File handling
const fileInput = ref<HTMLInputElement>()
const videoPreview = ref<HTMLVideoElement>()
const selectedVideo = ref<File | null>(null)
const videoPreviewUrl = ref('')
const thumbnailFile = ref<File | null>(null)
const thumbnailUrl = ref('')

// Form data
const videoTitle = ref('')
const videoDescription = ref('')
const isPublic = ref(true)

// Upload state
const isUploading = ref(false)
const thumbnailProgress = ref(0)
const videoUploadProgress = ref(0)
const thumbnailUploadProgress = ref(0)
const completeProgress = ref(0)

// Messages
const errorMessage = ref('')
const successMessage = ref('')

// Upload progress tracking
const currentStep = ref<'thumbnail' | 'video' | 'thumbnailUpload' | 'complete' | 'done'>('thumbnail')

const totalProgress = computed(() => {
  return (thumbnailProgress.value + videoUploadProgress.value + thumbnailUploadProgress.value + completeProgress.value) / 4
})

const canUpload = computed(() => {
  return selectedVideo.value && videoTitle.value.trim() && thumbnailFile.value && !isUploading.value
})

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  handleVideoFile(file)
}

const handleDrop = (event: DragEvent) => {
  const file = event.dataTransfer?.files[0]
  handleVideoFile(file)
}

const handleVideoFile = async (file: File | undefined) => {
  if (!file) return

  clearMessages()
  
  const validation = validateFile(file, 'video')
  if (!validation.valid) {
    errorMessage.value = validation.error || '파일 검증에 실패했습니다.'
    return
  }

  selectedVideo.value = file
  videoPreviewUrl.value = URL.createObjectURL(file)
  
  // Auto-generate title from filename
  videoTitle.value = file.name.replace(/\.[^/.]+$/, '')
  
  // Generate thumbnail
  try {
    thumbnailProgress.value = 50
    const thumbnail = await createThumbnailFromVideo(file)
    thumbnailFile.value = thumbnail
    thumbnailUrl.value = URL.createObjectURL(thumbnail)
    thumbnailProgress.value = 100
  } catch (error) {
    console.error('Thumbnail generation failed:', error)
    errorMessage.value = '썸네일 생성에 실패했습니다.'
  }
}

const onVideoLoaded = () => {
  // Video loaded, ready for upload
}

const uploadVideo = async () => {
  if (!selectedVideo.value || !thumbnailFile.value || !props.email || !props.gameTitle) {
    errorMessage.value = '필수 정보가 누락되었습니다.'
    return
  }

  isUploading.value = true
  clearMessages()
  resetProgress()

  try {
    // Step 1: Get presigned URLs
    currentStep.value = 'video'
    const presignedResponse = await participationVideoService.getPresignedUrls(
      props.email,
      props.gameTitle,
      props.isTest
    )
    
    // Step 2: Upload video to S3
    videoUploadProgress.value = 50
    await participationVideoService.uploadToS3(
      presignedResponse.videoUploadUrl,
      selectedVideo.value,
      selectedVideo.value.type
    )
    videoUploadProgress.value = 100

    // Step 3: Upload thumbnail to S3
    currentStep.value = 'thumbnailUpload'
    thumbnailUploadProgress.value = 50
    await participationVideoService.uploadToS3(
      presignedResponse.thumbnailUploadUrl,
      thumbnailFile.value,
      'image/jpeg'
    )
    thumbnailUploadProgress.value = 100

    // Step 4: Complete the upload (send only objectKeys)
    currentStep.value = 'complete'
    completeProgress.value = 50
    
    await participationVideoService.completeUpload({
      email: props.email,
      gameTitle: props.gameTitle,
      videoObjectKey: presignedResponse.videoObjectKey,
      thumbnailObjectKey: presignedResponse.thumbnailObjectKey,
      title: videoTitle.value,
      description: videoDescription.value,
      isPublic: isPublic.value
    }, props.isTest)
    
    completeProgress.value = 100
    currentStep.value = 'done'
    
    successMessage.value = '비디오가 성공적으로 업로드되었습니다!'
    emit('uploaded', {
      videoUrl: presignedResponse.videoObjectKey,
      thumbnailUrl: presignedResponse.thumbnailObjectKey,
      title: videoTitle.value
    })
    
    // Reset form
    resetForm()
    
  } catch (error: any) {
    console.error('Video upload failed:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '업로드에 실패했습니다.'
    errorMessage.value = errorMsg
    emit('error', errorMsg)
  } finally {
    isUploading.value = false
  }
}

const cancelUpload = () => {
  resetForm()
  clearMessages()
}

const resetForm = () => {
  selectedVideo.value = null
  thumbnailFile.value = null
  videoTitle.value = ''
  videoDescription.value = ''
  isPublic.value = true
  
  if (videoPreviewUrl.value) {
    URL.revokeObjectURL(videoPreviewUrl.value)
    videoPreviewUrl.value = ''
  }
  
  if (thumbnailUrl.value) {
    URL.revokeObjectURL(thumbnailUrl.value)
    thumbnailUrl.value = ''
  }
  
  resetProgress()
}

const resetProgress = () => {
  thumbnailProgress.value = 0
  videoUploadProgress.value = 0
  thumbnailUploadProgress.value = 0
  completeProgress.value = 0
  currentStep.value = 'thumbnail'
}

const clearMessages = () => {
  errorMessage.value = ''
  successMessage.value = ''
}

const getProgressStatus = (step: string) => {
  const stepMap = {
    'thumbnail': { step: 'thumbnail', progress: thumbnailProgress.value },
    'video': { step: 'video', progress: videoUploadProgress.value },
    'thumbnailUpload': { step: 'thumbnailUpload', progress: thumbnailUploadProgress.value },
    'complete': { step: 'complete', progress: completeProgress.value }
  }
  
  const current = stepMap[step as keyof typeof stepMap]
  if (!current) return '대기중'
  
  if (current.progress === 0) return '대기중'
  if (current.progress === 100) return '완료'
  if (currentStep.value === current.step) return '진행중'
  return current.progress > 0 ? '완료' : '대기중'
}

// Cleanup on unmount
onUnmounted(() => {
  if (videoPreviewUrl.value) {
    URL.revokeObjectURL(videoPreviewUrl.value)
  }
  if (thumbnailUrl.value) {
    URL.revokeObjectURL(thumbnailUrl.value)
  }
})
</script>

<style scoped>
.video-upload {
  max-width: 600px;
  margin: 0 auto;
}

.upload-container {
  border: 2px dashed #e1e5e9;
  border-radius: 12px;
  padding: 24px;
  transition: border-color 0.3s ease;
}

.upload-container:hover {
  border-color: #007bff;
}

.file-selector .drop-zone {
  text-align: center;
  padding: 40px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.file-selector .drop-zone:hover {
  background-color: #f8f9fa;
}

.upload-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 16px;
}

.upload-text {
  display: block;
  font-size: 1.1rem;
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
}

.file-info {
  font-size: 0.9rem;
  color: #6c757d;
}

.file-input {
  display: none;
}

.video-preview {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.preview-video {
  width: 100%;
  max-height: 300px;
  border-radius: 8px;
}

.thumbnail-preview {
  text-align: center;
}

.thumbnail-image {
  max-width: 200px;
  max-height: 150px;
  border-radius: 8px;
  border: 2px solid #e1e5e9;
}

.thumbnail-label {
  display: block;
  margin-top: 8px;
  font-size: 0.9rem;
  color: #6c757d;
}

.upload-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #495057;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 1rem;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.checkbox-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: normal;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.upload-btn, .cancel-btn {
  padding: 12px 24px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.upload-btn.primary {
  background: #007bff;
  color: white;
}

.upload-btn.primary:hover:not(:disabled) {
  background: #0056b3;
}

.upload-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #545b62;
}

.upload-progress {
  text-align: center;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.progress-header h3 {
  margin: 0;
  color: #495057;
}

.progress-percentage {
  font-size: 1.5rem;
  font-weight: 600;
  color: #007bff;
}

.progress-details {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 12px;
  text-align: left;
}

.progress-label {
  min-width: 120px;
  font-size: 0.9rem;
  color: #495057;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #007bff;
  transition: width 0.3s ease;
}

.progress-status {
  min-width: 60px;
  font-size: 0.8rem;
  color: #6c757d;
  text-align: right;
}

.error-message {
  margin-top: 16px;
  padding: 12px 16px;
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 6px;
  font-size: 0.9rem;
}

.success-message {
  margin-top: 16px;
  padding: 12px 16px;
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  border-radius: 6px;
  font-size: 0.9rem;
}
</style>