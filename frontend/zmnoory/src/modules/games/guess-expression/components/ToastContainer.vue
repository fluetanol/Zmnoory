<template>
  <div class="toast-container">
    <div
      v-for="t in toasts"
      :key="t.id"
      class="toast"
      :class="t.type"
    >
      <span class="toast-message">{{ t.message }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useToast } from "../modules/useToast";
const { toasts } = useToast();
</script>

<style scoped>
.toast-container {
  position: fixed;
  right: 16px;
  bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 9999;
}
.toast {
  min-width: 200px;
  max-width: 360px;
  padding: 10px 12px;
  border-radius: 8px;
  color: #fff;
  font-size: 0.9rem;
  box-shadow: 0 6px 20px rgba(0,0,0,0.25);
  opacity: 0.95;
}
.toast.info { background: #3b82f6; }
.toast.success { background: #10b981; }
.toast.warning { background: #f59e0b; }
.toast.error { background: #ef4444; }
.toast-message { line-height: 1.3; }
</style>


