import { ref } from 'vue';

export type ToastType = 'info' | 'success' | 'warning' | 'error';

export interface ToastItem {
  id: number;
  message: string;
  type: ToastType;
  duration: number; // ms
}

const toasts = ref<ToastItem[]>([]);

export function useToast() {
  function removeToast(id: number) {
    toasts.value = toasts.value.filter(t => t.id !== id);
  }

  function addToast(message: string, type: ToastType = 'info', duration = 2500) {
    const id = Date.now() + Math.floor(Math.random() * 1000);
    const item: ToastItem = { id, message, type, duration };
    toasts.value.push(item);
    setTimeout(() => removeToast(id), duration);
  }

  return { toasts, addToast, removeToast };
}


