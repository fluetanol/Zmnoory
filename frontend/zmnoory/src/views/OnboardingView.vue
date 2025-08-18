<!-- [FILEPATH] src/views/OnboardingView.vue -->
<template>
  <div class="onboarding-container" @wheel.prevent="handleWheel">
    <!-- Hero Section -->
    <section id="hero" class="hero-section" v-reveal>
      <!-- 상단 로고와 액션 버튼들 -->
      <div class="hero-top">
        <div class="hero-logo">
          <img src="@/assets/logo.png" alt="즈믄누리 로고" class="logo" />
        </div>
        <div v-if="!ref_isloggedIn">  
          <LoginHeader
            :style="{
              '--hover-bg':'#FCF8F4',
              '--hover-text' : '#2E2E2E'
            }"
          />
        </div>

        <div v-else>
          <LogoutHeader
              :style="{
              '--hover-bg':'#FCF8F4',
              '--hover-text' : '#2E2E2E'
            }"
          />
        </div>
      </div>
      
      <div class="hero-content">
        <h1 class="hero-title">
          동양인을 위한<br>
          <span class="highlight">AI 데이터 혁신</span>
        </h1>
        <p class="hero-subtitle">
          게임을 즐기며 AI 모델 개선에 기여하고,<br>
          고품질 얼굴 데이터셋으로 더 나은 AI의 미래를 함께 만들어요
        </p>
        <div class="hero-buttons">
          <BaseButton @click-event="scrollToSection('problem')" class="tertiary-button">
            서비스 소개
          </BaseButton>
          <BaseButton @click-event="scrollToSection('games')" class="secondary-button">
            회원가입하고 게임하기
          </BaseButton>
          <BaseButton @click-event="scrollToSection('b2b')" class="secondary-button">
            B2B 서비스 알아보기
          </BaseButton>
          <RouterLink to="/main">
            <BaseButton class="tertiary-button">
              홈페이지 바로가기
            </BaseButton>
          </RouterLink>
        </div>
      </div>
      <div class="hero-visual">
        <div class="floating-icons">
          <div class="icon-item" v-for="(icon, index) in floatingIcons" :key="index" 
               :style="{ animationDelay: index * 0.5 + 's' }">
            {{ icon }}
          </div>
        </div>
      </div>
    </section>

    <!-- Problem Section -->
    <section id="problem" class="problem-section" v-reveal>
      <div class="container">
        <h2 class="section-title">왜 필요할까요?</h2>
        <div class="problem-grid">
          <div class="problem-card" v-for="(problem, index) in problems" :key="index"
               v-reveal :style="{ transitionDelay: (index * 100) + 'ms' }">
            <div class="problem-icon">{{ problem.icon }}</div>
            <h3 class="problem-title">{{ problem.title }}</h3>
            <p class="problem-description">{{ problem.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Solution Section -->
    <section id="solution" class="solution-section" v-reveal>
      <div class="container">
        <h2 class="section-title">저희의 솔루션</h2>
        <div class="solution-content">
          <div class="solution-visual">
            <div class="process-flow">
              <div class="process-step" v-for="(step, index) in processSteps" :key="index"
                   v-reveal :style="{ transitionDelay: (index * 200) + 'ms' }">
                <div class="step-number">{{ index + 1 }}</div>
                <div class="step-icon">{{ step.icon }}</div>
                <h4 class="step-title">{{ step.title }}</h4>
                <p class="step-description">{{ step.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Games Section -->
    <section id="games" class="games-section" v-reveal>
      <div class="container">
        <h2 class="section-title">재미있는 게임으로 시작하세요</h2>
        <p class="section-subtitle">
          간단한 게임을 통해 얼굴 데이터를 제공하고, 리워드를 받아보세요!
        </p>
        <div class="features-grid">
          <div class="feature-card" v-for="(feature, index) in gameFeatures" :key="index"
               v-reveal :style="{ transitionDelay: (index * 150) + 'ms' }">
            <div class="feature-icon">{{ feature.icon }}</div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
        <div class="games-cta">
          <RouterLink to="/terms">
            <BaseButton class="cta-button">회원가입</BaseButton>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- B2B Section -->
    <section id="b2b" class="b2b-section" v-reveal>
      <div class="container">
        <div class="b2b-content">
          <div class="b2b-text">
            <h2 class="section-title">기업을 위한 고품질 데이터셋</h2>
            <p class="section-subtitle">
              동양인 특화 얼굴 데이터셋으로 AI 모델의 성능을 획기적으로 개선하세요
            </p>
            <ul class="b2b-benefits">
              <li v-for="(benefit, index) in b2bBenefits" :key="index" 
                  v-reveal :style="{ transitionDelay: (index * 100) + 'ms' }">
                <span class="benefit-icon">✓</span>
                {{ benefit }}
              </li>
            </ul>
            <RouterLink to="/data-request">
              <BaseButton class="b2b-button">데이터 요청하기</BaseButton>
            </RouterLink>
          </div>
          <div class="b2b-visual">
            <div class="data-visualization">
              <div class="data-point" v-for="n in 20" :key="n" 
                   :style="{ animationDelay: (n * 0.1) + 's' }"></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section id="cta" class="cta-section" v-reveal>
      <div class="container">
        <h2 class="cta-title">AI의 미래를 함께 만들어가요</h2>
        <p class="cta-subtitle">
          지금 시작하면 더 나은 AI 세상을 경험할 수 있습니다
        </p>
        <div class="cta-buttons">
          <RouterLink to="/signup">
            <BaseButton class="cta-primary">회원가입 하기</BaseButton>
          </RouterLink>
          
        </div>
      </div>
    </section>
  </div>
    <div class="section-navigation">
      <button @click="scrollUp" class="nav-button up-button">▲</button>
      <button @click="scrollDown" class="nav-button down-button">▼</button>
    </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import BaseButton from '@/common/components/shared/BaseButton.vue'
import { onMounted, ref } from 'vue'
import LoginHeader from '@/common/components/login/LoginHeader.vue'
import LogoutHeader from '@/common/components/login/LogoutHeader.vue'
import { useAccountStore } from '@/store/Accounts'



const sectionIds = ['hero', 'problem', 'games', 'b2b', 'cta']
let currentSectionIndex = 0

const updateCurrentSectionIndex = () => {
  const scrollPosition = window.scrollY + window.innerHeight / 2; // Mid-point of the viewport

  for (let i = sectionIds.length - 1; i >= 0; i--) {
    const section = document.getElementById(sectionIds[i]);
    if (section && section.offsetTop <= scrollPosition) {
      currentSectionIndex = i;
      break;
    }
  }
};

onMounted(() => {
  updateCurrentSectionIndex(); // Set initial index on mount
  window.addEventListener('scroll', updateCurrentSectionIndex); // Update on scroll
});

const scrollUp = () => {
  if (currentSectionIndex > 0) {
    currentSectionIndex--;
    scrollToSection(sectionIds[currentSectionIndex]);
  }
};

const scrollDown = () => {
  if (currentSectionIndex < sectionIds.length - 1) {
    currentSectionIndex++;
    scrollToSection(sectionIds[currentSectionIndex]);
  }
};

const scrollEnabled = ref(true); // New ref for scroll debouncing

const handleWheel = (event: WheelEvent) => {
  if (!scrollEnabled.value) return;

  scrollEnabled.value = false;
  setTimeout(() => {
    scrollEnabled.value = true;
  }, 800); // Debounce time in ms

  if (event.deltaY > 0) { // Scrolling down
    scrollDown();
  } else if (event.deltaY < 0) { // Scrolling up
    scrollUp();
  }
};


// Reveal directive for scroll animations
const observers = new WeakMap<Element, IntersectionObserver>()

const ref_isloggedIn = ref<boolean|null>(null)
const ref_userProfileImage = ref<string>('')

onMounted(()=>{
      
    const token = sessionStorage.getItem('accessToken');
    const userProfileImage = useAccountStore().userInfo?.profileImageUrl;

    if(token != null) ref_isloggedIn.value =true;
    else ref_isloggedIn.value = false;
    
    if(userProfileImage != null){
      ref_userProfileImage.value = userProfileImage;
    }
})

const vReveal = {
  mounted(
    el: HTMLElement,
    binding: { value?: { threshold?: number; rootMargin?: string; once?: boolean } }
  ) {
    
    const prefersReduced = window.matchMedia?.('(prefers-reduced-motion: reduce)')?.matches
    const opts = binding?.value || {}
    const threshold = opts.threshold ?? 0.15
    const rootMargin = opts.rootMargin ?? '0px 0px -10% 0px'
    const once = opts.once ?? true

    el.classList.add('reveal')

    if (prefersReduced) {
      el.classList.add('is-visible')
      return
    }

    const io = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          el.classList.add('is-visible')
          if (once) io.unobserve(el)
        } else if (!once) {
          el.classList.remove('is-visible')
        }
      })
    }, { threshold, rootMargin })

    io.observe(el)
    observers.set(el, io)
  },
  beforeUnmount(el: HTMLElement) {
    const io = observers.get(el)
    if (io) {
      io.unobserve(el)
      observers.delete(el)
    }
  }
}

// Scroll to section function
const scrollToSection = (sectionId: string) => {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

// Data for sections
const floatingIcons = ['🤖', '🎮', '📊', '💎', '🚀', '⭐']

const problems = [
  {
    icon: '😕', // Changed from '🚫' to '😕'
    title: 'AI 성능 한계',
    description: '기존 AI 모델은 동양인 얼굴 인식 정확도가 현저히 낮습니다'
  },
  {
    icon: '📉',
    title: '데이터 부족',
    description: '고품질 동양인 얼굴 데이터셋이 절대적으로 부족한 상황입니다'
  },
  {
    icon: '💸',
    title: '높은 비용',
    description: '양질의 데이터 수집에는 막대한 시간과 비용이 소요됩니다'
  }
]

const processSteps = [
  {
    icon: '🎮', // Changed from '▶' to '🎮'
    title: '게임 참여',
    description: '재미있는 표정 게임에 참여하여 자연스러운 데이터 생성'
  },
  {
    icon: '📸', // Changed from '📊' to '📸'
    title: '데이터 수집',
    description: '게임 과정에서 다양한 표정과 각도의 얼굴 데이터 수집'
  },
  {
    icon: '🏷️', // Changed from '🗂️' to '🏷️'
    title: '라벨링',
    description: '전문가가 수집된 데이터를 정확하게 라벨링 처리'
  },
  {
    icon: '🧠', // Changed from '▲' to '🧠'
    title: 'AI 학습',
    description: '고품질 데이터로 AI 모델의 동양인 인식 성능 향상'
  }
]

const gameFeatures = [
  {
    icon: '🏆', // Changed from '★' to '🏆'
    title: '리워드 획득',
    description: '게임 참여 시마다 포인트를 획득하고 다양한 혜택을 받아보세요'
  },
  {
    icon: '🌍', // Changed from '◫' to '🌍'
    title: 'AI 기여',
    description: '여러분의 참여가 더 나은 AI 세상을 만드는 데 직접 기여합니다'
  },
  {
    icon: '🖱️', // Changed from '✓' to '🖱️'
    title: '간편한 참여',
    description: '복잡한 절차 없이 웹캠만 있으면 언제든 쉽게 참여 가능합니다'
  },
  {
    icon: '🔒', // Changed from '⚿' to '🔒'
    title: '개인정보 보호',
    description: '모든 데이터는 익명화 처리되어 개인정보를 안전하게 보호합니다'
  }
]

const b2bBenefits = [
  '동양인 특화 고품질 얼굴 데이터셋',
  '다양한 표정, 각도, 조명 조건의 라벨링된 데이터',
  '실시간 AI 모델 성능 개선 확인 가능',
  '맞춤형 데이터 수집 및 가공 서비스',
  '지속적인 데이터 업데이트 및 확장'
]
</script>

<style scoped>
.section-navigation {
  position: fixed;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 1000;
}

.nav-button {
  background-color: #2E2E2E;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  font-size: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  transition: background-color 0.3s ease;
}

.nav-button:hover {
  background-color: #D4992B;
}

/* Reveal animation base styles */
.reveal {
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.6s ease-out;
}

.reveal.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.onboarding-container {
  width: 100%;
  min-height: 100vh;
}

/* Hero Section */
.hero-section {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #DEC9B6 0%, #fefdfb 100%);
  position: relative;
  overflow: hidden;
  padding: 40px 20px 0;
}

/* Hero Top - 로고와 액션 버튼들 */
.hero-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1220px;
  width: 100%;
  margin: 0 auto;
  z-index: 3;
}

.hero-logo .logo {
  height: 50px;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.login-link {
  font-family: Inter;
  font-size: 16px;
  font-weight: 500;
  color: #2E2E2E;
  text-decoration: none;
  transition: color 0.3s ease;
}

.login-link:hover {
  color: #D4992B;
}

.signup-button {
  font-size: 14px !important;
  padding: 8px 16px !important;
  width: auto !important;
  height: auto !important;
  border-radius: 6px;
}

/* Hero Content - 메인 콘텐츠 */
.hero-content {
  max-width: 600px;
  text-align: center;
  z-index: 2;
  margin: auto;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.hero-title {
  font-family: Inter;
  font-size: 48px;
  font-weight: 700;
  color: #2E2E2E;
  line-height: 1.2;
  margin-bottom: 24px;
}

.highlight {
  color: #D4992B;
}

.hero-subtitle {
  font-family: Inter;
  font-size: 18px;
  font-weight: 400;
  color: #666;
  line-height: 1.6;
  margin-bottom: 40px;
}

.hero-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.primary-button, .secondary-button, .tertiary-button {
  font-size: 18px !important;
  padding: 16px 32px !important;
  width: auto !important;
  height: auto !important;
  border-radius: 8px;
  transition: all 0.3s ease;
}

/* 
.primary-button :hover{
  background: #2E2E2E !important;
  color: white !important;
} */

.primary-button, .secondary-button {
  background: transparent !important;
  border: 2px solid #2E2E2E;
  color: #2E2E2E !important;
}

.primary-button:hover, .secondary-button:hover {
  background: #2E2E2E !important;
  color: white !important;
}

.tertiary-button {
  background: transparent !important;
  border: 2px solid #D4992B;
  color: #D4992B !important;
}

.tertiary-button:hover {
  background: #D4992B !important;
  color: white !important;
}

/* Floating Icons */
.hero-visual {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-icons {
  position: relative;
  width: 100%;
  height: 100%;
}

.icon-item {
  position: absolute;
  font-size: 40px;
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.icon-item:nth-child(1) { top: 20%; left: 10%; }
.icon-item:nth-child(2) { top: 30%; right: 15%; }
.icon-item:nth-child(3) { top: 60%; left: 20%; }
.icon-item:nth-child(4) { top: 70%; right: 25%; }
.icon-item:nth-child(5) { top: 15%; left: 60%; }
.icon-item:nth-child(6) { top: 80%; right: 10%; }

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

/* Common Section Styles */
.container {
  width: 1220px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  font-family: Inter;
  font-size: 40px;
  font-weight: 700;
  color: #2E2E2E;
  text-align: center;
  margin-bottom: 20px;
}

.section-subtitle {
  font-family: Inter;
  font-size: 18px;
  font-weight: 400;
  color: #666;
  text-align: center;
  margin-bottom: 60px;
  line-height: 1.6;
}

/* Problem Section */
.problem-section {
  padding: 100px 0;
  background: white;
}

.problem-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
  margin-top: 60px;
}

.problem-card {
  text-align: center;
  padding: 40px 30px;
  border-radius: 16px;
  background: #f8f9fa;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.problem-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.problem-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.problem-title {
  font-family: Inter;
  font-size: 24px;
  font-weight: 600;
  color: #2E2E2E;
  margin-bottom: 16px;
}

.problem-description {
  font-family: Inter;
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

/* Solution Section */
.solution-section {
  padding: 100px 0;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.process-flow {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-top: 60px;
}

.process-step {
  text-align: center;
  position: relative;
}

.step-number {
  width: 50px;
  height: 50px;
  background: transparent; /* Changed to transparent */
  border: 2px solid #2E2E2E; /* Added border */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: Inter;
  font-size: 24px;
  font-weight: 700;
  color: #2E2E2E; /* Kept the color for the number */
  margin: 0 auto 20px;
}

.step-icon {
  font-size: 50px;
  margin-bottom: 20px;
}

.step-title {
  font-family: Inter;
  font-size: 20px;
  font-weight: 600;
  color: #2E2E2E;
  margin-bottom: 12px;
}

.step-description {
  font-family: Inter;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* Games Section */
.games-section {
  padding: 100px 0;
  background: white;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.feature-card {
  padding: 40px 30px;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  text-align: center;
  transition: all 0.3s ease;
}

.feature-card:hover {
  border-color: #F5E7DA;
  box-shadow: 0 8px 25px rgba(245, 231, 218, 0.3);
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 50px;
  margin-bottom: 20px;
}

.feature-title {
  font-family: Inter;
  font-size: 20px;
  font-weight: 600;
  color: #2E2E2E;
  margin-bottom: 16px;
}

.feature-description {
  font-family: Inter;
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

.games-cta {
  text-align: center;
  margin-top: 60px;
}

.cta-button {
  font-size: 20px !important;
  padding: 20px 40px !important;
  width: auto !important;
  height: auto !important;
  border-radius: 8px;
}

/* B2B Section */
.b2b-section {
  padding: 100px 0;
  background: #2E2E2E;
  color: white;
}

.b2b-section .section-title,
.b2b-section .section-subtitle {
  color: white;
}

.b2b-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.b2b-text {
  text-align: left;
}

.b2b-text .section-title,
.b2b-text .section-subtitle {
  text-align: left;
  margin-bottom: 40px;
}

.b2b-benefits {
  list-style: none;
  padding: 0;
  margin: 0 0 40px 0;
}

.b2b-benefits li {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-family: Inter;
  font-size: 16px;
  line-height: 1.6;
}

.benefit-icon {
  width: 20px;
  height: 20px;
  background: #F5E7DA;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #2E2E2E;
  margin-right: 16px;
  flex-shrink: 0;
}

.b2b-button {
  background: #F5E7DA !important;
  color: #2E2E2E !important;
  font-size: 18px !important;
  padding: 16px 32px !important;
  width: auto !important;
  height: auto !important;
  border-radius: 8px;
}

.b2b-visual {
  display: flex;
  justify-content: center;
  align-items: center;
}

.data-visualization {
  position: relative;
  width: 300px;
  height: 300px;
  border: 2px solid #F5E7DA;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.data-point {
  position: absolute;
  width: 8px;
  height: 8px;
  background: #F5E7DA;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.data-point:nth-child(odd) { animation-direction: reverse; }

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

/* Position data points in circle */
.data-point:nth-child(1) { top: 10px; left: 50%; transform: translateX(-50%); }
.data-point:nth-child(2) { top: 25px; right: 25px; }
.data-point:nth-child(3) { top: 50%; right: 10px; transform: translateY(-50%); }
.data-point:nth-child(4) { bottom: 25px; right: 25px; }
.data-point:nth-child(5) { bottom: 10px; left: 50%; transform: translateX(-50%); }
.data-point:nth-child(6) { bottom: 25px; left: 25px; }
.data-point:nth-child(7) { top: 50%; left: 10px; transform: translateY(-50%); }
.data-point:nth-child(8) { top: 25px; left: 25px; }
.data-point:nth-child(9) { top: 35px; left: 40%; }
.data-point:nth-child(10) { top: 35px; right: 40%; }
.data-point:nth-child(11) { bottom: 35px; left: 40%; }
.data-point:nth-child(12) { bottom: 35px; right: 40%; }
.data-point:nth-child(13) { top: 60%; left: 30%; }
.data-point:nth-child(14) { top: 60%; right: 30%; }
.data-point:nth-child(15) { top: 40%; left: 30%; }
.data-point:nth-child(16) { top: 40%; right: 30%; }
.data-point:nth-child(17) { top: 50%; left: 40%; }
.data-point:nth-child(18) { top: 50%; right: 40%; }
.data-point:nth-child(19) { top: 45%; left: 50%; transform: translateX(-50%); }
.data-point:nth-child(20) { top: 55%; left: 50%; transform: translateX(-50%); }

/* CTA Section */
.cta-section {
  padding: 100px 0;
  background: linear-gradient(135deg, #F5E7DA 0%, #EED5C0 100%);
  text-align: center;
}

.cta-title {
  font-family: Inter;
  font-size: 36px;
  font-weight: 700;
  color: #2E2E2E;
  margin-bottom: 20px;
}

.cta-subtitle {
  font-family: Inter;
  font-size: 18px;
  color: #666;
  margin-bottom: 40px;
  line-height: 1.6;
}

.cta-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-primary, .cta-secondary {
  font-size: 18px !important;
  padding: 16px 32px !important;
  width: auto !important;
  height: auto !important;
  border-radius: 8px;
}

.cta-secondary {
  background: transparent !important;
  border: 2px solid #2E2E2E;
  color: #2E2E2E !important;
}

.cta-secondary:hover {
  background: #2E2E2E !important;
  color: white !important;
}

/* Responsive Design */
@media (max-width: 1280px) {
  .container {
    width: 95%;
    max-width: 1200px;
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 20px 15px 0;
  }
  
  .hero-top {
    margin-bottom: 20px;
  }
  
  .hero-logo .logo {
    height: 40px;
  }
  
  .hero-actions {
    gap: 15px;
  }
  
  .signup-button {
    font-size: 12px !important;
    padding: 6px 12px !important;
  }
  
  .hero-title {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 16px;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .section-title {
    font-size: 32px;
  }
  
  .b2b-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .b2b-text {
    text-align: center;
  }
  
  .b2b-text .section-title,
  .b2b-text .section-subtitle {
    text-align: center;
  }
  
  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }
}

@media (max-width: 480px) {
  .primary-button, .secondary-button, .tertiary-button,
  .cta-button, .b2b-button,
  .cta-primary, .cta-secondary {
    width: 300px !important;
  }
}
</style>