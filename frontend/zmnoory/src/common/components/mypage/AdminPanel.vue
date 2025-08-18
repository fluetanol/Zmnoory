<template>
  <div class="admin-panel">
    <!-- 왼쪽 네비게이션 사이드바 -->
    <nav class="admin-sidebar">
      <div class="sidebar-header">
        <h2 class="admin-title">관리자 대시보드</h2>
      </div>

      <ul class="nav-menu">
        <li
          class="nav-item"
          :class="{ active: currentPage === 'overview' }"
          @click="setCurrentPage('overview')"
        >
          <span class="nav-text">대시보드 개요</span>
        </li>
        <li
          class="nav-item"
          :class="{ active: currentPage === 'games' }"
          @click="setCurrentPage('games')"
        >
          <span class="nav-text">게임 관리</span>
        </li>
        <li
          class="nav-item"
          :class="{ active: currentPage === 'products' }"
          @click="setCurrentPage('products')"
        >
          <span class="nav-text">상품 관리</span>
        </li>
        <li
          class="nav-item"
          :class="{ active: currentPage === 'giftcards' }"
          @click="setCurrentPage('giftcards')"
        >
          <span class="nav-text">기프티콘 관리</span>
        </li>
        <li
          class="nav-item"
          :class="{ active: currentPage === 'data-requests' }"
          @click="setCurrentPage('data-requests')"
        >
          <span class="nav-text">데이터 요청 관리</span>
        </li>
        <li class="nav-item control-panel-item" @click="goToControlPanel">
          <span class="nav-text">Control Panel</span>
        </li>
      </ul>
    </nav>

    <!-- 오른쪽 메인 콘텐츠 영역 -->
    <main class="admin-content">
      <!-- 대시보드 개요 -->
      <div v-if="currentPage === 'overview'" class="overview-page">
        <h2 class="page-title">대시보드 개요</h2>
        <div class="overview-grid">
          <div class="overview-card" @click="setCurrentPage('games')">
            <div class="card-info">
              <h3>게임 관리</h3>
              <p>{{ games.length }}개의 게임</p>
            </div>
          </div>
          <div class="overview-card" @click="setCurrentPage('products')">
            <div class="card-info">
              <h3>상품 관리</h3>
              <p>{{ products.length }}개의 상품</p>
            </div>
          </div>
          <div class="overview-card" @click="setCurrentPage('giftcards')">
            <div class="card-info">
              <h3>기프티콘 관리</h3>
              <p>{{ allGiftCards.length }}개의 기프티콘</p>
            </div>
          </div>
          <div class="overview-card" @click="setCurrentPage('data-requests')">
            <div class="card-info">
              <h3>데이터 요청 관리</h3>
              <p>{{ allDataRequests.length }}개의 요청</p>
            </div>
          </div>
          <div class="overview-card" @click="goToControlPanel">
            <div class="card-info">
              <h3>Control Panel</h3>
              <p>시스템 제어 패널</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 각 관리 페이지 -->
      <GameManagement v-if="currentPage === 'games'" />
      <ProductManagement v-if="currentPage === 'products'" />
      <GiftCardManagement v-if="currentPage === 'giftcards'" />
      <DataRequestManagement v-if="currentPage === 'data-requests'" />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import type { Game, Product, GiftCard, DataRequest } from "@/services/info";
import { useGameStore } from "@/store/Games";
import { useProductStore } from "@/store/Products";
import { useGiftCardStore } from "@/store/GiftCards";
import { useDataRequestStore } from "@/store/DataRequests";
import GameManagement from "@/common/components/admin/GameManagement.vue";
import ProductManagement from "@/common/components/admin/ProductManagement.vue";
import GiftCardManagement from "@/common/components/admin/GiftCardManagement.vue";
import DataRequestManagement from "@/common/components/admin/DataRequestManagement.vue";

const router = useRouter();
const gameStore = useGameStore();
const productStore = useProductStore();
const giftCardStore = useGiftCardStore();
const dataRequestStore = useDataRequestStore();

// Control Panel 이동 함수
const goToControlPanel = () => {
  router.push("/control-panel");
};

// 페이지 상태
const currentPage = ref("overview");

// 개요 페이지용 데이터
const games = ref<Game[]>([]);
const products = ref<Product[]>([]);
const allGiftCards = ref<GiftCard[]>([]);
const allDataRequests = ref<DataRequest[]>([]);

onMounted(() => {
  loadOverviewData();
});

// 페이지 전환
const setCurrentPage = (page: string) => {
  currentPage.value = page;
};

// 개요 데이터 로드
const loadOverviewData = async () => {
  try {
    const isLocalDev = window.location.hostname === "localhost";

    // 병렬로 모든 데이터 로드
    const [gamesData, productsData, giftCardsData, dataRequestsData] =
      await Promise.all([
        gameStore.getAllGames(isLocalDev),
        productStore.getProductList(isLocalDev),
        giftCardStore.getAllGiftCards(isLocalDev),
        dataRequestStore.getAllDataRequests(undefined, isLocalDev),
      ]);

    games.value = gamesData;
    products.value = productsData;
    allGiftCards.value = giftCardsData;
    allDataRequests.value = dataRequestsData;
  } catch (error) {
    console.error("개요 데이터 로드 실패:", error);
  }
};
</script>

<style scoped>
.admin-panel {
  display: flex;
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
  gap: 20px;
}

/* 왼쪽 사이드바 */
.admin-sidebar {
  width: 280px;
  background: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 500px;
  z-index: 100;
  border-radius: 8px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #34495e;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 0 20px;
}

.admin-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  text-align: center;
}

.nav-menu {
  list-style: none;
  padding: 0;
  margin: 20px 0 0 0;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.nav-item:hover {
  background: #34495e;
  border-left-color: #3498db;
}

.nav-item.active {
  background: #34495e;
  border-left-color: #e74c3c;
}

.nav-icon {
  font-size: 18px;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-size: 14px;
  font-weight: 500;
}

.control-panel-item {
  margin-top: 20px;
  border-top: 1px solid #34495e;
  padding-top: 20px;
}

.control-panel-item:hover {
  background: #34495e;
  border-left-color: #667eea;
}

/* 오른쪽 메인 콘텐츠 */
.admin-content {
  flex: 1;
  padding: 0;
  overflow-y: auto;
  background: white;
  border-radius: 8px;
  min-height: 500px;
}

/* 개요 페이지 */
.overview-page {
  padding: 30px 40px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 30px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
}

.overview-card {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  padding: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.overview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border-color: #3498db;
}

.card-icon {
  font-size: 48px;
  margin-bottom: 15px;
  text-align: center;
}

.card-info h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
  text-align: center;
}

.card-info p {
  font-size: 14px;
  color: #6c757d;
  margin: 0;
  text-align: center;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .admin-sidebar {
    width: 240px;
  }

  .overview-page {
    padding: 20px 25px;
  }

  .overview-grid {
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 200px;
  }

  .nav-text {
    font-size: 12px;
  }

  .admin-title {
    font-size: 16px;
  }

  .overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
