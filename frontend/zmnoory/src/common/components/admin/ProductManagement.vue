<template>
  <div class="management-page">
    <div class="page-header">
      <h2 class="page-title">상품 관리</h2>
      <button class="action-btn primary" @click="openProductModal">상품 등록</button>
    </div>

    <div class="content-area">
      <div class="products-list">
        <div v-for="product in products" :key="product.id" class="item-card">
          <div class="item-info">
            <img :src="product.thumbnail" :alt="product.title" class="item-thumbnail" />
            <div>
              <h4>{{ product.title }}</h4>
              <p>카테고리: {{ product.category }}</p>
              <span class="item-meta">가격: {{ product.price }}원</span>
            </div>
          </div>
          <div class="item-actions">
            <button class="action-btn small" @click="editProduct(product)">수정</button>
            <button class="action-btn small danger" @click="deleteProduct(product.id)">삭제</button>
            <button class="action-btn small success" @click="createGiftCards()">기프티콘 등록</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 상품 등록/수정 모달 -->
    <div v-if="showProductModal" class="modal-overlay" @click="closeProductModal">
      <div @click.stop>
        <ProductModal 
          :product="selectedProduct"
          @close="closeProductModal" 
          @success="handleProductSuccess" 
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Product } from '@/services/info'
import { useProductStore } from '@/store/Products'
import ProductModal from '@/common/modals/ProductModal.vue'

const productStore = useProductStore()

// 모달 상태
const showProductModal = ref(false)
const selectedProduct = ref<Product | null>(null)

// 데이터
const products = ref<Product[]>([])

onMounted(() => {
  loadProducts()
})

// 상품 관련 함수
const loadProducts = async () => {
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    products.value = await productStore.getProductList(isLocalDev)
  } catch (error) {
    console.error('상품 목록 로드 실패:', error)
    alert('상품 목록을 불러오는데 실패했습니다.')
  }
}

const openProductModal = () => {
  selectedProduct.value = null
  showProductModal.value = true
}

const editProduct = (product: Product) => {
  selectedProduct.value = product
  showProductModal.value = true
}

const deleteProduct = async (productId: number) => {
  if (!confirm('정말로 이 상품을 삭제하시겠습니까?')) return
  
  try {
    const isLocalDev = window.location.hostname === 'localhost'
    await productStore.deleteProduct(productId, isLocalDev)
    alert('상품이 삭제되었습니다.')
    loadProducts()
  } catch (error) {
    console.error('상품 삭제 실패:', error)
    alert('상품 삭제에 실패했습니다.')
  }
}

const createGiftCards = () => {
  // 기프티콘 등록 로직 - 추후 구현
  alert('기프티콘 등록 기능은 기프티콘 관리 페이지에서 사용해주세요.')
}

const closeProductModal = () => {
  showProductModal.value = false
  selectedProduct.value = null
}

const handleProductSuccess = () => {
  closeProductModal()
  loadProducts()
}
</script>

<style scoped>
.management-page {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #E0E0E0;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #2E2E2E;
  margin: 0;
}

.content-area {
  flex: 1;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.item-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border: 1px solid #E0E0E0;
  background: #FAFAFA;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.item-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.item-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
}

.item-info h4 {
  margin: 0 0 5px 0;
  color: #2E2E2E;
  font-size: 18px;
  font-weight: 600;
}

.item-info p {
  margin: 0 0 5px 0;
  color: #666;
  font-size: 14px;
}

.item-meta {
  font-size: 12px;
  color: #888;
}

.item-thumbnail {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}

.item-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #A0A0A0;
  background: #fff;
  color: #2E2E2E;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  border-radius: 4px;
}

.action-btn:hover {
  background: #F5F5F5;
}

.action-btn.primary {
  background: #F5E7DA;
  border-color: #D4B896;
  font-weight: 600;
  padding: 12px 24px;
  font-size: 16px;
}

.action-btn.small {
  padding: 6px 12px;
  font-size: 12px;
}

.action-btn.danger {
  color: #dc3545;
  border-color: #dc3545;
}

.action-btn.danger:hover {
  background: #dc3545;
  color: white;
}

.action-btn.success {
  color: #28a745;
  border-color: #28a745;
}

.action-btn.success:hover {
  background: #28a745;
  color: white;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
</style>