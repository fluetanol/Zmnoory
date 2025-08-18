<template>
  <div class="chart-container">
    <div class="chart-header">
      <h4>날짜별 업로드 현황</h4>
      <div class="period-selector">
        <button
          v-for="period in periods"
          :key="period.days"
          @click="selectPeriod(period.days)"
          :class="{ active: selectedPeriod === period.days }"
          class="period-btn"
        >
          {{ period.label }}
        </button>
      </div>
    </div>
    <div class="chart-wrapper">
      <Line :data="chartData" :options="chartOptions" class="chart" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { Line } from "vue-chartjs";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

interface Props {
  labels: string[];
  videoData: number[];
  imageData: number[];
  totalData: number[];
}

const props = defineProps<Props>();

// 기간 선택 관련
const selectedPeriod = ref(30);

const periods = [
  { days: 1, label: "1일" },
  { days: 7, label: "7일" },
  { days: 15, label: "15일" },
  { days: 30, label: "30일" },
];

const emit = defineEmits<{
  periodChange: [days: number];
}>();

const selectPeriod = (days: number) => {
  selectedPeriod.value = days;
  emit("periodChange", days);
};

const chartData = computed(() => ({
  labels: props.labels,
  datasets: [
    {
      label: "영상 파일",
      data: props.videoData,
      borderColor: "#e74c3c",
      backgroundColor: "rgba(231, 76, 60, 0.1)",
      tension: 0.4,
      fill: false,
    },
    {
      label: "이미지 파일",
      data: props.imageData,
      borderColor: "#3498db",
      backgroundColor: "rgba(52, 152, 219, 0.1)",
      tension: 0.4,
      fill: false,
    },
    {
      label: "전체",
      data: props.totalData,
      borderColor: "#2ecc71",
      backgroundColor: "rgba(46, 204, 113, 0.1)",
      tension: 0.4,
      fill: false,
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
</script>

<style scoped>
.chart-container {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.period-selector {
  display: flex;
  gap: 8px;
}

.period-btn {
  padding: 6px 12px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  color: #6c757d;
  transition: all 0.3s ease;
}

.period-btn:hover {
  background: #e9ecef;
  color: #495057;
}

.period-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.chart-wrapper {
  height: 300px;
  position: relative;
}

.chart {
  width: 100% !important;
  height: 100% !important;
}
</style>
