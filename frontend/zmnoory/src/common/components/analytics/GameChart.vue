<template>
  <div class="chart-container">
    <h4>게임별 업로드 현황</h4>
    <div class="chart-wrapper">
      <Bar :data="chartData" :options="chartOptions" class="chart" />
    </div>

    <!-- 게임별 상세 통계 -->
    <div class="game-stats">
      <h5>게임별 상세 통계</h5>
      <div class="stats-grid">
        <div
          v-for="stat in gameStats"
          :key="stat.gameId"
          class="game-stat-card"
        >
          <div class="game-id">{{ stat.gameId }}</div>
          <div class="stat-details">
            <div class="stat-item">
              <span class="stat-label">영상:</span>
              <span class="stat-value">{{ stat.videoCount }}개</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">이미지:</span>
              <span class="stat-value">{{ stat.imageCount }}개</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">총 용량:</span>
              <span class="stat-value">{{
                formatFileSize(stat.totalSize)
              }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { Bar } from "vue-chartjs";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

interface GameStat {
  gameId: string;
  videoCount: number;
  imageCount: number;
  totalSize: number;
}

interface Props {
  labels: string[];
  videoData: number[];
  imageData: number[];
  totalData: number[];
  gameStats: GameStat[];
}

const props = defineProps<Props>();

const chartData = computed(() => ({
  labels: props.labels,
  datasets: [
    {
      label: "영상 파일",
      data: props.videoData,
      backgroundColor: "#e74c3c",
      borderColor: "#c0392b",
      borderWidth: 1,
    },
    {
      label: "이미지 파일",
      data: props.imageData,
      backgroundColor: "#3498db",
      borderColor: "#2980b9",
      borderWidth: 1,
    },
  ],
}));

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: "top" as const,
    },
    title: {
      display: false,
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        stepSize: 1,
      },
    },
  },
};

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return "0 Bytes";

  const k = 1024;
  const sizes = ["Bytes", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};
</script>

<style scoped>
.chart-container {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.chart-container h4 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.chart-wrapper {
  height: 300px;
  position: relative;
  margin-bottom: 30px;
}

.chart {
  width: 100% !important;
  height: 100% !important;
}

.game-stats h5 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.game-stat-card {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 15px;
  border-left: 4px solid #667eea;
}

.game-id {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 1rem;
}

.stat-details {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
}

.stat-value {
  font-weight: 500;
  color: #495057;
  font-size: 0.9rem;
}
</style>
