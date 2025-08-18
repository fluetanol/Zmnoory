<template>
  <div class="modal">
    <button class="close-btn" @click="emit('close')" aria-label="닫기">×</button>

    <h3 class="modal-title">기프티콘 등록</h3>

    <!-- 상품 선택 -->
    <select class="modal-select" v-model="selectedProductId">
      <option value="">상품을 선택하세요</option>
      <option v-for="product in products" :key="product.id" :value="product.id">
        {{ product.title }} ({{ product.category }})
      </option>
    </select>

    <!-- 기프티콘 이미지 업로드 -->
    <input type="file" @change="handleImageUpload" accept="image/*" />

    <div v-if="giftCardImage">
      <p>미리보기:</p>
      <img :src="giftCardImage" alt="기프티콘 이미지 미리보기" style="max-width: 100%; height: auto;" />
    </div>

    <BaseButton class="base-button" @click="submitGiftCard">기프티콘 등록</BaseButton>
  </div>
</template>

<script setup lang="ts">
import BaseButton from '../components/shared/BaseButton.vue'
import { ref, onMounted } from 'vue'
import type { Product } from '@/services/info'
import { useGiftStore } from '@/store/gift'
import { useProductStore } from '@/store/Products'
import type { GiftCardCreateRequest } from '@/services/gift'

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'success'): void
}>()

const giftStore = useGiftStore()
const productStore = useProductStore()

const products = ref<Product[]>([])
const selectedProductId = ref('')
const giftCardImage = ref<string | null>(null)

onMounted(async () => {
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    products.value = await productStore.getProductList(isLocalDev)
  } catch (error) {
    console.error('상품 목록 로드 실패:', error)
  }
})

const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    const reader = new FileReader();
    reader.onload = (e) => {
      giftCardImage.value = e.target?.result as string;
    };
    reader.readAsDataURL(target.files[0]);
  }
};

const submitGiftCard = async () => {
  if (!selectedProductId.value) {
    alert('상품을 선택해주세요')
    return
  }
  if (!giftCardImage.value) {
    alert('기프티콘 이미지를 업로드해주세요')
    return
  }

  try {
    const payload: GiftCardCreateRequest = {
      productId: Number(selectedProductId.value),
      giftCardImage: giftCardImage.value,
    };

    const isLocalDev = window.location.hostname === 'localhost'
    await giftStore.register(payload, isLocalDev);

    alert('기프티콘이 성공적으로 등록되었습니다!')
    emit('success')
    emit('close')
  } catch (err: any) {
    console.error('기프티콘 등록 에러:', err)
    const errorMessage = err?.response?.data?.message || err?.message || '등록 중 오류가 발생했습니다.'
    alert(`기프티콘 등록 실패: ${errorMessage}`)
  }
}
</script>

<style scoped>
.modal {
  position: relative;
  flex-shrink: 0;
  background: #FFF;
  border: 1px solid black;
  padding: 40px;
  max-width: 500px;
  width: 100%;
}

.modal-title {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  color: #2E2E2E;
  margin-bottom: 30px;
}

.modal-select {
  width: 441px;
  height: 65px;
  border: 1px solid #A0A0A0;
  color: #2E2E2E;
  font-family: Inter;
  font-size: 20px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  padding-left: 18px;
  margin-bottom: 20px;
  box-sizing: border-box;
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
  margin-top: 20px;
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
