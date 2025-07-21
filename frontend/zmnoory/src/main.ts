import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router/test/TestRouter'; // 👈 여기 중요

//createApp(App).mount('#app')
createApp(App).use(router).mount('#app');