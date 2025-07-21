// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import TestView from '@/common/test/TestView.vue';

const routes = [
  {
    path: '/test',
    name: 'TestView',
    component: TestView,
  },
  // ...기존 라우트들
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
