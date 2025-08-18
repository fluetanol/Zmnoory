<!-- [FILEPATH] src/common/components/shared/ProductCard.vue -->
<template>
  <div class="product-card">
    <div class="img-wrapper">
      <img :src="product.thumbnail" alt="chicken"></img>
    </div>
    <span class="category">{{ product.title }}</span>
    <span class="title">{{ product.category }}</span>
    <div class="point">
      <span>{{ product.price }}</span>
      <img src="@/assets/currency_symbol.png" alt="point symbol" />
    </div>
    <button class="buy" @click="buyRequest" :disabled="isLoading">
      {{ isLoading ? '처리중...' : 'BUY' }}
    </button>
  </div>
</template>

<script setup lang="ts">
  import type { Product } from '@/services/info'
  import { useProductStore } from '@/store/Products'
  import { ref } from 'vue'
  import { useAccountStore } from '@/store/Accounts'

  const props = defineProps<{
    product: Product
  }>()

  const productStore = useProductStore()
  const accountStore = useAccountStore()
  const isLoading = ref(false)

  const buyRequest = async() => {
    if (isLoading.value) return
    
    // 구매 확인 창
    const confirmPurchase = confirm(`${props.product.title}을(를) ${props.product.price} 포인트로 구매하시겠습니까?`)
    if (!confirmPurchase) return
    
    isLoading.value = true
    try {
      const result = await productStore.orderProduct(props.product.title)
      
      if (result.success) {
        alert(result.message)
        // 포인트 정보 업데이트
        await accountStore.getMyDetail()
      } else {
        alert(result.message)
      }
    } finally {
      isLoading.value = false
    }
  }
</script>

<style scoped>
  .product-card {
    width: 275px;
    height: 370px;
    display: flex;
    flex-direction: column;
    position: relative;
  }

  .img-wrapper {
    width: 100%;
  }

  .img-wrapper img {
    width: 100%;
    height: 275px;
    display: block;
  }

  .category {
    width: 100%;
    height: 24px;
    color: #2E2E2E;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
    margin: 15px 0 0 9px;
  }

  .title {
    width: 100%;
    height: 18px;
    color: #2E2E2E;
    font-family: Inter;
    font-size: 15px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    margin: 7px 0 0 9px;
  }

  .point {
    height: 24px;
    display: flex;
    align-items: center;
    color: #2E2E2E;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
    margin: 7px 0 0 9px;
  }

  .point img {
    width: 18px;
    height: 18px;
    margin: 4px 0 0 3px;
  }

  .buy {
    position: absolute;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    flex-direction: column;
    width: 55px;
    height: 22px;
    flex-shrink: 0;
    border-radius: 8px;
    background: #00D722;
    color: #FFF;
    font-family: Inter;
    font-size: 15px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    border: none;
    cursor: pointer;  
  }

  .buy:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
</style>