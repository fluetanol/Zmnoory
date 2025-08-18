<!-- [FILEPATH] src/views/StoreView.vue -->
<template>
	<div class="page-wrapper">
		<div class="page-content">
			<div class="main-content-wrapper">
				<AppHeader />
			</div>
			<div class="store-page">
		<main class="container">
			<div v-if="user" class="store-header">
				<div class="store-header-title">
					<span class="nickname">{{ user.nickname }} 님</span>
					<span class="welcome">어서오세요   !</span>
				</div>
				<div class="store-header-point">
					<h1>{{ user.point }}</h1>
					<img style="width: 41px; height: 41px;" src="@/assets/currency_symbol.png" alt="money" />
				</div>
			</div>

			<hr v-if="user" style="border: none; border-top: 1px solid #a0a0a0; margin: 0;" />

			<div class="search-wrapper">
				<SearchView
					:filters="['카테고리', '가격']"
					:first_options="['음식', '음료', '문화', '디저트', '기타']"
					:second_options="['최소 가격', '최대 가격']"
					:is-range="true"
					@search="onSearch"
				></SearchView>
			</div>
			<div class="actions">
			<button v-if="isAdmin" class="product-create" @click="showModal = true">상품 추가하기</button>
			</div>

			<section class="grid">
				<ProductCard v-for="product in filteredProducts" :key="product.id" :product="product" />
			</section>

			<div v-if="showModal" class="modal-overlay">
				<ProductModal @close="showModal = false" @success="handleProductAdded" />
			</div>

			<!-- 상품 목록이 끝났을 때 표시할 섹션 -->
			<section class="coming-soon-section">
				<div class="coming-soon-content">
					<div class="coming-soon-image">
						<img src="@/assets/SendingHeart.png" alt="Coming Soon" />
					</div>
					<div class="coming-soon-text">
						<h3>상품을 계속해서 추가중입니다.</h3>
						<p>다음상품을 기대해주세요!</p>
					</div>
				</div>
			</section>
			</main>
			</div>
		</div>
		<SiteFooter></SiteFooter>
	</div>
</template>

<script setup lang="ts">
	import AppHeader from '@common/components/shared/AppHeader.vue'
	import ProductCard from '@/common/components/shared/ProductCard.vue'
	import { ref, onMounted, computed } from 'vue'
	import { type Product } from '@/services/info'
	import SearchView from '@/common/components/shared/SearchView.vue'
	import ProductModal from '@/common/modals/ProductModal.vue'
	import { useAccountStore } from '@/store/Accounts'
	import { useProductStore } from '@/store/Products'
	import SiteFooter from '@/common/components/shared/SiteFooter.vue'


	const showModal = ref<boolean>(false)
  const isAdmin = ref<boolean>(false)

	const accountStore = useAccountStore()
	const productStore = useProductStore()

	// 실시간 업데이트를 위해 computed 사용
	const user = computed(() => accountStore.member_me)
	const filteredProducts = ref<Product[] | null>(null)

	const reloadProduct = async () => {
		await productStore.getProductList()
		filteredProducts.value = productStore.product_list ?? []
	}

	onMounted(async() => {
		reloadProduct()

		await accountStore.getMyDetail()

		if (accountStore.member_me?.role === 'ADMIN') {
      isAdmin.value = true
    }
	})

	const handleProductAdded = async () => {
		await reloadProduct()
		showModal.value = false
	}

	type SearchParams = {
    keyword: string
    first: string
    second: string
    min: number
    max: number
  }

	const onSearch = (params: SearchParams) => {
    const { keyword, first, min, max } = params

    const base = productStore.product_list ?? []
    filteredProducts.value = base.filter(product => {
      const matchKeyword = keyword === '' || product.title.includes(keyword)
      const matchCategory = first === '' || product.category === first
      const matchPrice = product.price >= min && product.price <= max

      return matchKeyword && matchCategory && matchPrice
    })
  }
</script>

<style scoped>
.main-content-wrapper {
	max-width: 1440px;
	margin: 0 auto;
	width: 100%;
	padding: 0 20px;
	box-sizing: border-box;
}
	.store-page {
		width: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.container {
		width: 100%;
		max-width: 1200px;
		margin: 0 auto;
		padding: 30px 40px 80px;
		box-sizing: border-box;
	}

	.store-header {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 18px;
	}

	.store-header-title {
		display: flex;
		flex-direction: row;
		align-items: baseline;
	}

	.nickname {
		color: #2E2E2E;
		text-align: center;
		font-family: Inter;
		font-size: 40px;
		font-style: normal;
		font-weight: 700;
		line-height: normal;
	}

	.welcome {
		color: #2E2E2E;
		text-align: center;
		font-family: Inter;
		font-size: 20px;
		font-style: normal;
		font-weight: 600;
		line-height: normal;
		margin-left: 10px;
	}

	.store-header-point {
		display: flex;
		flex-direction: row;
		align-items: center;
		gap: 10px;
	}

	.grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 36px 32px;
		justify-items: center;
		margin-bottom: 60px;
	}

	/* 반응형 브레이크포인트 */
	@media (max-width: 1200px) {
		.grid {
			grid-template-columns: repeat(3, 1fr);
			gap: 24px 20px;
		}
	}

	@media (max-width: 900px) {
		.grid {
			grid-template-columns: repeat(2, 1fr);
			gap: 20px 16px;
		}

		.container {
			padding: 20px 20px 60px;
		}
	}

	@media (max-width: 600px) {
		.grid {
			grid-template-columns: 1fr;
			gap: 16px;
		}

		.container {
			padding: 16px 16px 40px;
		}

		.store-header {
			flex-direction: column;
			gap: 16px;
			align-items: flex-start;
		}

		.coming-soon-image img {
			width: 180px;
			height: 180px;
		}

		.coming-soon-text h3 {
			font-size: 16px;
		}

		.coming-soon-text p {
			font-size: 12px;
		}
	}

	/* Coming Soon 섹션 스타일 */
	.coming-soon-section {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 40px 0;
		opacity: 0.5;
		/* 50% 옅게 표시 */
	}

	.coming-soon-content {
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
		gap: 20px;
	}

	.coming-soon-image img {
		width: 240px;
		height: 240px;
		object-fit: contain;
	}

	.coming-soon-text h3 {
		font-size: 18px;
		font-weight: 600;
		color: #666;
		margin: 0;
	}

	.coming-soon-text p {
		font-size: 14px;
		color: #999;
		margin: 8px 0 0 0;
	}

	.search-wrapper {
		width: fit-content;
		margin: 60px auto;
	}


			/* 우측 정렬 컨테이너 */
		.actions {
		width: 100%;
		max-width: 1200px;    /* .container와 동일 기준 */
		display: flex;
		justify-content: flex-end;
		align-items: center;
		margin: 8px auto 24px; /* 살짝 위아래 간격 */
		padding: 0 0;          /* 필요 시 0~8px 정도 */
		}


		/* 버튼 자체 스타일: 고정 마진 제거 */
		.product-create {
		color: #2e2e2e;
		text-align: center;
		font-family: Inter, system-ui, -apple-system, Segoe UI, Roboto, 'Noto Sans KR', Arial, sans-serif;
		font-size: 15px;
		font-weight: 500;
		border: 1px solid #2e2e2e;
		background-color: #FBF5F0;
		width: 130px;
		height: 40px;
		border-radius: 20px;
		cursor: pointer;

		/* ❌ 기존 margin 제거 */
		margin: 0;
		}

/* 반응형에서도 우측 정렬 유지 (옵션) */
@media (max-width: 600px) {
  .actions {
    justify-content: flex-end; /* 필요시 center로 바꿔도 OK */
  }
}


  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.5); /* 어두운 배경 */
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000; /* 충분히 위에 표시 */
  }
</style>