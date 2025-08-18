<!-- [FILEPATH] src/common/modals/GameModal.vue -->
<template>
  <div class="modal">
    <button class="close-btn" @click="emit('close')" aria-label="닫기">×</button>

    <BaseInput class="modal-input" v-model="title" :type="'text'" :placeholder="'상품명'"></BaseInput>
    <VerticalPicker
      class="modal-input"
      v-model="category"
      :options="['CAFE', 'FOOD', 'GIFT']"
    />
    <BaseInput class="modal-input" v-model="price" :type="'text'" :placeholder="'가격'"></BaseInput>
    <div class="file-input-wrapper">
      <span class="file-name">{{ fileName || '썸네일 이미지' }}</span>
      <label class="upload-button">
        업로드
        <input type="file" accept=".png, .jpg" class="file-input-hidden" @change="handleFileChange" />
      </label>
    </div>

    <BaseButton class="base-button" @click="submitProduct">{{ props.product ? '상품 수정' : '상품 추가' }}</BaseButton>
  </div>
</template>

<script setup lang="ts">
  import BaseInput from '../components/shared/BaseInput.vue'
  import { ref, watch } from 'vue'
  import type { CreateProductPayload, Product } from '@/services/info'
  import { useProductStore } from '@/store/Products'
  import VerticalPicker from '../components/shared/VerticalPicker.vue';
import BaseButton from '../components/shared/BaseButton.vue';

  const props = defineProps<{
    product?: Product | null
  }>()

  const emit = defineEmits<{
    (e: 'close'): void
    (e: 'success'): void
  }>()

  const productStore = useProductStore()

  const fileName = ref<string>('')

  const title = ref('')
  const category = ref('CAFE')  // 기본값을 첫 번째 옵션으로 설정
  const price = ref('')
  const thumbnail = ref<File | null>(null)

  // 한글 카테고리를 영어로 변환하는 함수
  const convertCategoryToEnglish = (koreanCategory: string): string => {
    const categoryMap: Record<string, string> = {
      '카페': 'CAFE',
      '음식': 'FOOD',
      '선물': 'GIFT',
      // 이미 영어인 경우 그대로 반환
      'CAFE': 'CAFE',
      'FOOD': 'FOOD',
      'GIFT': 'GIFT'
    }
    return categoryMap[koreanCategory] || 'CAFE'
  }

  // 폼 데이터 초기화 함수
  const initializeFormData = () => {
    if (props.product) {
      title.value = props.product.title
      // 카테고리는 영어로 변환해서 저장
      category.value = convertCategoryToEnglish(props.product.category)
      price.value = props.product.price.toString()
      fileName.value = '기존 썸네일'
    } else {
      // 등록 모드일 때는 모든 필드 초기화
      title.value = ''
      category.value = 'CAFE'
      price.value = ''
      fileName.value = ''
      thumbnail.value = null
    }
  }

  // props.product가 변경될 때마다 폼 데이터 업데이트 (즉시 실행 포함)
  watch(() => props.product, () => {
    initializeFormData()
  }, { immediate: true })

  const handleFileChange = (e: Event) => {
    const target = e.target as HTMLInputElement
    const file = target.files?.[0]
    if (file) {
      fileName.value = file.name
      thumbnail.value = file
    }
  }

  const submitProduct = async () => {
    // 입력 유효성 검사
    if (!title.value.trim()) {
      alert('상품명을 입력해주세요')
      return
    }
    if (!category.value) {
      alert('카테고리를 선택해주세요')
      return
    }
    if (!price.value || Number(price.value) <= 0) {
      alert('올바른 가격을 입력해주세요')
      return
    }
    // 수정 모드일 때는 새 썸네일 없어도 됨
    if (!props.product && !thumbnail.value) {
      alert('썸네일 이미지를 업로드해주세요')
      return
    }

    const processProductData = async (base64Thumbnail?: string) => {
      let payload: CreateProductPayload

      if (props.product && !base64Thumbnail) {
        // 수정 모드이고 새 썸네일이 없는 경우: 썸네일 제외하고 전송
        payload = {
          title: title.value,
          category: category.value,
          price: Number(price.value) ?? 0,
          thumbnail: props.product.thumbnail // 기존 썸네일 유지
        }
      } else {
        // 등록 모드이거나 새 썸네일이 있는 경우
        payload = {
          title: title.value,
          category: category.value,
          price: Number(price.value) ?? 0,
          thumbnail: base64Thumbnail || '',
        }
      }

      console.log('전송할 payload:', payload)

      try {
        if (props.product) {
          // 수정 모드
          await productStore.updateProduct(props.product.id, payload)
          alert('상품이 성공적으로 수정되었습니다!')
        } else {
          // 등록 모드
          await productStore.createProduct(payload)
          alert('상품이 성공적으로 등록되었습니다!')
        }
        emit('success')
        emit('close')
      } catch (err: any) {
        console.error('상품 처리 에러:', err)
        const errorMessage = err?.response?.data?.body?.data?.thumbnail || 
                           err?.response?.data?.message || 
                           err?.message || 
                           '처리 중 오류가 발생했습니다.'
        alert(`상품 ${props.product ? '수정' : '등록'} 실패: ${errorMessage}`)
      }
    }

    if (thumbnail.value) {
      const reader = new FileReader()
      reader.onload = async () => {
        const base64Thumbnail = reader.result as string
        await processProductData(base64Thumbnail)
      }
      reader.readAsDataURL(thumbnail.value)
    } else {
      await processProductData()
    }
  }
</script>

<style scoped>
  .modal {
    position: relative;
    flex-shrink: 0;
    background: #FFF;
    border: 1px solid black;
    padding: 40px
  }

  .modal-input {
    margin-bottom: 20px;
  }

  .drop-input {
    width: 215px;
    height: 65px;
    border: 1px solid #A0A0A0;
    color: #A0A0A0;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    padding-left: 18px;
    margin-bottom: 20px;
  }

  .drop-input-box {
    display: flex;
    gap: 11px;
  }

  .file-input {
    width: 441px;
    height: 65px;
    border: 1px solid #A0A0A0;
    color: #A0A0A0;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    padding-left: 18px;
    margin-bottom: 20px;
  }

  .base-button {
    width: 441px;
    height: 73px;
    background: #F5E7DA;
    color: #2E2E2E;
    border: 1px solid #A0A0A0;
    text-align: center;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 700;
    line-height: normal;
    cursor: pointer;
  }

  .file-input-wrapper {
    width: 441px;
    height: 65px;
    border: 1px solid #A0A0A0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 18px;
    font-family: Inter;
    font-size: 20px;
    color: #A0A0A0;
    margin-bottom: 20px;
    box-sizing: border-box;
  }

  .file-name {
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    color: #A0A0A0;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
  }

  .upload-button {
    background: #F5E7DA;
    border: none;
    padding: 8px 12px;
    cursor: pointer;
    font-weight: bold;
    font-size: 16px;
    color: #333;
    white-space: nowrap;
    border: 1px solid #a0a0a0;
  }

  .file-input-hidden {
    display: none;
  }

  .close-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    border: none;
    background: none;
    font-size: 20px;
    font-weight: bold;
    cursor: pointer;
    color: #2E2E2E;
  }
</style>