<template>
  <div class="profile-image-upload">
    <div class="upload-container">
      <div class="image-preview" @click="triggerFileInput">
        <img 
          v-if="previewUrl || currentImageUrl" 
          :src="previewUrl || currentImageUrl" 
          alt="Profile preview"
          class="preview-image"
        />
        <div v-else class="placeholder">
          <span class="upload-icon">📷</span>
          <span class="upload-text">프로필 사진 업로드</span>
        </div>
        <div v-if="isUploading" class="upload-overlay">
          <div class="progress-container">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${uploadProgress}%` }"></div>
            </div>
            <span class="progress-text">{{ uploadProgress }}%</span>
          </div>
        </div>
      </div>
      
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        @change="handleFileSelect"
        class="file-input"
      />
      
      <div class="upload-actions">
        <button 
          v-if="selectedFile && !isUploading" 
          @click="uploadImage"
          class="upload-btn primary"
          :disabled="isUploading"
        >
          업로드
        </button>
        <button 
          v-if="selectedFile" 
          @click="cancelUpload"
          class="cancel-btn"
          :disabled="isUploading"
        >
          취소
        </button>
      </div>
    </div>
    
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { profileImageService, validateFile } from '@/services/upload'

interface Props {
  currentImageUrl?: string
  isTest?: boolean
}

interface Emits {
  (e: 'uploaded', imageUrl: string): void
  (e: 'error', error: string): void
}

const props = withDefaults(defineProps<Props>(), {
  currentImageUrl: '',
  isTest: false
})

const emit = defineEmits<Emits>()

const fileInput = ref<HTMLInputElement>()
const selectedFile = ref<File | null>(null)
const previewUrl = ref<string>('')
const isUploading = ref(false)
const uploadProgress = ref(0)
const errorMessage = ref('')
const successMessage = ref('')

const triggerFileInput = () => {
  if (!isUploading.value) {
    fileInput.value?.click()
  }
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) return
  
  clearMessages()
  
  const validation = validateFile(file, 'image')
  if (!validation.valid) {
    errorMessage.value = validation.error || '파일 검증에 실패했습니다.'
    return
  }
  
  selectedFile.value = file
  previewUrl.value = URL.createObjectURL(file)
}

const uploadImage = async () => {
  if (!selectedFile.value) return
  
  isUploading.value = true
  uploadProgress.value = 0
  clearMessages()
  
  try {
    // Step 1: Get presigned URL
    uploadProgress.value = 20
    const uploadUrlResponse = await profileImageService.getUploadUrl(
      selectedFile.value.name,
      selectedFile.value.type,
      props.isTest
    )
    console.log('Step 1 - Presigned URL response:', uploadUrlResponse)
    
    // Step 2: Upload to S3
    uploadProgress.value = 40
    await profileImageService.uploadToS3(uploadUrlResponse.uploadUrl, selectedFile.value)
    console.log('Step 2 - S3 upload completed')
    
    // Step 3: Update profile
    uploadProgress.value = 80
    console.log('Step 3 - Calling updateProfile with objectKey:', uploadUrlResponse.objectKey)
    const updateResponse = await profileImageService.updateProfile(uploadUrlResponse.objectKey, props.isTest)
    console.log('Step 3 - Profile update response:', updateResponse)
    
    uploadProgress.value = 100
    successMessage.value = updateResponse.message || '프로필 사진이 업데이트되었습니다.'
    emit('uploaded', updateResponse.data)
    
    // Clean up
    selectedFile.value = null
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
      previewUrl.value = ''
    }
    
  } catch (error: any) {
    console.error('Profile image upload failed:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '업로드에 실패했습니다.'
    errorMessage.value = errorMsg
    emit('error', errorMsg)
  } finally {
    isUploading.value = false
    uploadProgress.value = 0
  }
}

const cancelUpload = () => {
  selectedFile.value = null
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = ''
  }
  clearMessages()
}

const clearMessages = () => {
  errorMessage.value = ''
  successMessage.value = ''
}

// Clean up URLs when component is unmounted
watch(() => previewUrl.value, (newUrl, oldUrl) => {
  if (oldUrl && oldUrl !== newUrl) {
    URL.revokeObjectURL(oldUrl)
  }
})
</script>

<style scoped>
.profile-image-upload {
  max-width: 300px;
}

.upload-container {
  position: relative;
  border: 2px dashed #e1e5e9;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: border-color 0.3s ease;
}

.upload-container:hover {
  border-color: #007bff;
}

.image-preview {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto 16px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #e1e5e9;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background-color: #f8f9fa;
  color: #6c757d;
}

.upload-icon {
  font-size: 2rem;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 0.9rem;
  font-weight: 500;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.progress-container {
  text-align: center;
  color: white;
}

.progress-bar {
  width: 120px;
  height: 6px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  margin-bottom: 8px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #007bff;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.8rem;
  font-weight: 600;
}

.file-input {
  display: none;
}

.upload-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.upload-btn, .cancel-btn {
  padding: 8px 16px;
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

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover:not(:disabled) {
  background: #545b62;
}

.upload-btn:disabled, .cancel-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  margin-top: 12px;
  padding: 8px 12px;
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 6px;
  font-size: 0.9rem;
}

.success-message {
  margin-top: 12px;
  padding: 8px 12px;
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
  border-radius: 6px;
  font-size: 0.9rem;
}
</style>