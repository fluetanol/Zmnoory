<!-- [FILEPATH] src/views/MainView.vue -->
<template>
  <div class="page-wrapper">
    <div class="page-content">
      <div class="main-content-wrapper">
        <AppHeader></AppHeader>
        <!-- 메인 배너 이미지 -->
        <img class="main-image" src="@/assets/main_image.png" alt="main" v-reveal>
      </div>
      <div class="main-container">

    <!-- 게임 목록 -->
    <div class="container" v-reveal>
      <div>
        <span class="title">게임 목록</span>
        <RouterLink :to="{ name: 'games' }" class="wrapper">
          <span class="link">전체 게임 보러가기</span>
          <div class="triangle-right"></div>
        </RouterLink>
      </div>
      <span class="tag"># MZ가 많이 플레이하는 게임</span>
      <div class="game-list">
        <RouterLink
          v-for="(game, i) in randomGames"
          :key="game.id ?? i"
          :to="game.id === 0 ? '#' : { name: 'GameDetail', params: { id: game.id } }"
          style="text-decoration: none;"
          :class="{ 'blank-game': game.id === 0 }"
          @click="game.id === 0 ? $event.preventDefault() : null"
          v-reveal
          :style="{ transitionDelay: (i * 90) + 'ms' }"
          class="card-reveal card-link"
        >
          <GameCard
            :game="game"
            :participationStatus="getParticipationStatus(game.title)"
          />
        </RouterLink>
      </div>
    </div>

    
    <!-- 영상 목록 -->
    <div class="container" v-reveal>
      <div>
        <span class="title">영상 목록</span>
        <RouterLink :to="{ name: 'Videos' }" class="wrapper">
          <span class="link">전체 영상 보러가기</span>
          <div class="triangle-right"></div>
        </RouterLink>
      </div>
      <span class="tag"># MZ가 주목하는 챌린지 영상</span>


      <div class="video-list">
        <RouterLink
          v-for="video in randomVideos"
          :key="video.id"
          :to="video.id === 0 ? '#' : { name: 'VideoDetail', params: { id: video.id } }"
          style="text-decoration: none;"
          :class="{ 'blank-video': video.id === 0 }"
          @click="video.id === 0 ? $event.preventDefault() : null"
        >
          <VideoCard :video="video" />
        </RouterLink>
      </div>

    </div>

      <div class="request-container" v-reveal>
        <Request></Request>
      </div>
      </div>
    </div>
    <SiteFooter></SiteFooter>
  </div>
</template>

<style scoped>
  .main-content-wrapper {
    max-width: 1440px;
    margin: 0 auto;
    width: 100%;
    padding: 0 20px;
    box-sizing: border-box;
  }
  .main-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .main-image {
    width: 100%;
    margin-top: 25px;
  }

  .container {
    width: 100%;
    max-width: 1440px;
    margin: 80px auto 0 auto;
    padding: 30px 20px 80px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
  }

  .container > div {
    display: flex;
    align-items: center;
  }

  .title {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 40px;
    font-style: normal;
    font-weight: 700;
    line-height: 1;
    margin-right: 11px;
  }

  .tag {
    color: #A0A0A0;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
    margin-top: 14px;
  }

  .link {
    color: #5A5A5A;
    text-align: center;
    font-family: Inter;
    font-size: 18px;
    font-style: normal;
    font-weight: 400;
    line-height: 1;
    position: relative;
    top: 11px;
  }

  .triangle-right {
    width: 0;
    height: 0;
    border-top: 6.5px solid transparent;
    border-left: 13px solid #5A5A5A;
    border-bottom: 6.5px solid transparent;
    position: relative;
    top: 15px;
    margin-left: 4px;
  }

  .wrapper {
    display: flex;
    flex-direction: row;
    text-decoration: none;
  }


  .game-list {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 21px;
    margin-top: 30px;
  }

  .video-list {
    display: flex;
    flex-wrap: wrap;
    gap: 22px;
    margin-top: 30px;
  }

    /* 빈 상태 컨테이너도 기존 레이아웃과 같은 폭/간격 유지 */
  .video-empty {
    display: flex;
    flex-direction: column;
    gap: 14px;
    margin-top: 30px;
  }

  /* 스켈레톤 카드들: 실제 카드와 동일한 그리드 감 유지 */
  .skeleton-list {
    display: flex;
    flex-wrap: wrap;
    gap: 22px;
    min-height: 220px;       /* 목록이 비어도 섹션이 쪼그라들지 않도록 */
  }

  /* 카드 한 장의 형태를 대략적으로 유지 */
  .skeleton-card {
    width: 452px;
    max-width: calc((1440px - 2*22px - 2*20px) / 3); /* 반응형에서도 자연스럽게 */
    flex: 1 1 400px;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }



  /* 16:9 썸네일 자리 (회색 반짝 효과) */
  .skeleton-thumb {
    aspect-ratio: 16 / 9;
    border-radius: 12px;
    background: linear-gradient(90deg, #f2f2f3 25%, #ececee 37%, #f2f2f3 63%);
    background-size: 400% 100%;
    animation: shimmer 1.2s infinite linear;
  }

  /* 텍스트 줄 자리 */
  .skeleton-line {
    height: 12px;
    border-radius: 8px;
    background: linear-gradient(90deg, #f2f2f3 25%, #ececee 37%, #f2f2f3 63%);
    background-size: 400% 100%;
    animation: shimmer 1.2s infinite linear;
  }
  .skeleton-line.w-80 { width: 80%; }
  .skeleton-line.w-60 { width: 60%; }

  /* 반짝 애니메이션 */
  @keyframes shimmer {
    0%   { background-position: 100% 0; }
    100% { background-position: 0 0; }
  }

  /* 반응형에서도 기존 규칙을 따르도록 간격만 맞춤 */
  @media (max-width: 1024px) {
    .skeleton-list { gap: 16px; }
  }
  @media (max-width: 768px) {
    .skeleton-list { gap: 12px; justify-content: center; }
  }
  @media (max-width: 480px) {
    .skeleton-list { gap: 10px; }
  }


  
  .request-container {
    width: 100%;
    margin-top: 40px;
  }

  /* 반응형 스타일 */
  @media (max-width: 1280px) {
    .game-list { gap: 18px; }
    .video-list { gap: 18px; }
  }

  @media (max-width: 1024px) {
    .container { margin: 60px auto 0 auto; }
    .title { font-size: 32px; }
    .tag { font-size: 18px; }
    .link { font-size: 16px; }
    .game-list { gap: 16px; }
    .video-list { gap: 16px; }
    /* 4개 → 3개로 줄이기 */
    .game-list > *:nth-child(n+4) { display: none; }
    .video-list > *:nth-child(n+4) { display: none; }
  }

  @media (max-width: 768px) {
    .container { margin: 40px auto 0 auto; padding: 0 16px; }
    .container > div { flex-direction: column; align-items: flex-start; gap: 12px; }
    .title { font-size: 28px; margin-right: 0; }
    .tag { font-size: 16px; margin-top: 12px; }
    .link { font-size: 14px; top: 0; }
    .triangle-right { top: 4px; }
    .game-list { gap: 12px; margin-top: 20px; justify-content: center; }
    .video-list { gap: 12px; margin-top: 20px; justify-content: center; }
    .main-image { margin-top: 15px; }
    /* 3개 → 2개로 줄이기 */
    .game-list > *:nth-child(n+3) { display: none; }
    .video-list > *:nth-child(n+3) { display: none; }
  }

  @media (max-width: 480px) {
    .container { margin: 30px auto 0 auto; padding: 0 12px; }
    .title { font-size: 24px; }
    .tag { font-size: 14px; }
    .link { font-size: 13px; }
    .game-list { gap: 10px; margin-top: 16px; justify-content: center; }
    .video-list { gap: 10px; margin-top: 16px; justify-content: center; }
    .main-image { margin-top: 10px; }
    /* 2개 → 1개로 줄이기 */
    .game-list > *:nth-child(n+2) { display: none; }
    .video-list > *:nth-child(n+2) { display: none; }
  }

  /* ─────────────────────────────────────────────
     스크롤 진입 페이드‑인 애니메이션
  ───────────────────────────────────────────── */
  .reveal {
    opacity: 0;
    transform: translateY(16px) scale(0.98);
    transition:
      opacity 600ms ease,
      transform 600ms cubic-bezier(0.22, 1, 0.36, 1);
    will-change: opacity, transform;
  }
  .reveal.is-visible {
    opacity: 1;
    transform: translateY(0) scale(1);
  }

  /* 카드류는 살짝 더 가벼운 모션 */
  .card-reveal.reveal {
    transform: translateY(10px) scale(0.99);
    transition-duration: 520ms;
  }

  /* 호버 미세 상향(포인터 있는 환경만) */
  @media (hover: hover) and (pointer: fine) {
    .game-list > *:hover {
      transform: translateY(-3px);
      transition: transform 200ms ease;
    }
  }

  /* 접근성: 모션 최소화 환경 */
  @media (prefers-reduced-motion: reduce) {
    .reveal,
    .reveal.is-visible,
    .card-reveal.reveal {
      transition: none !important;
      transform: none !important;
      opacity: 1 !important;
    }
  }

  /* 추가 예정 영상 카드는 클릭 방지하되 호버는 허용 */
  .blank-video {
    cursor: default;
  }
  
  .blank-video:hover {
    text-decoration: none !important;
  }

  /* 추가 예정 게임 카드는 클릭 방지하되 호버는 허용 */
  .blank-game {
    cursor: default;
  }
  
  .blank-game:hover {
    text-decoration: none !important;
  }

  /* 링크가 그리드 셀 전체를 차지하도록 */
  .card-link {
    display: block;
    width: 100%;
    text-decoration: none;
  }
</style>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { RouterLink } from 'vue-router'
  import { useGameStore } from '@/store/Games';
  import { useParticipationStore } from '@/store/Participations';
  import { useVideoStore } from '@/store/Videos';
  import { blankGame, blankVideo, type Game, type VideoList } from '@/services/info';

  import AppHeader from '@/common/components/shared/AppHeader.vue';
  import SiteFooter from '@/common/components/shared/SiteFooter.vue';
  import GameCard from '@/common/components/shared/GameCard.vue';
  import VideoCard from '@/common/components/shared/VideoCard.vue';
  import Request from '@/modules/main/Request.vue';

  /* ────────────────────────────────────────────────────
     IntersectionObserver 기반 v-reveal 디렉티브
     스크롤로 뷰포트에 들어오면 페이드‑인
  ──────────────────────────────────────────────────── */
  const observers = new WeakMap<Element, IntersectionObserver>()

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

      // 초기 상태
      el.classList.add('reveal')

      // 모션 최소화 환경에서는 즉시 표시
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
  // ※ <script setup>에서는 const vReveal 로 선언하면 템플릿에서 v-reveal로 바로 사용 가능

  const gamestore = useGameStore()
  const participationstore = useParticipationStore()
  const videoStore = useVideoStore()
  const randomGames = ref<Game[] | null>(null)
  const randomVideos = ref<VideoList[] | null>(null)

  // 참여한 게임인지 확인
  const getParticipationStatus = (title: string): 'COMPLETED' | 'NOT_PARTICIPATED' => {
    const found = (participationstore.participated_game ?? []).find(p => p.gameTitle === title)
    return found ? (found.status as 'COMPLETED' | 'NOT_PARTICIPATED') : 'NOT_PARTICIPATED'
  }

  onMounted(async () => {
    await gamestore.getGameList()
    if (sessionStorage.getItem("accessToken")) {
      await participationstore.getParticipatedGame()
    }

    const allGames = gamestore.game_list ?? []
    if (allGames.length > 0) {
      const shuffled = [...allGames].sort(() => Math.random() - 0.5)
      const selected = shuffled.slice(0, 3)

      while (selected.length < 3) {
        selected.push({ ...blankGame })
      }
      randomGames.value = selected
    } else {
      randomGames.value = Array.from({ length: 3 }, () => ({ ...blankGame }))
    }
  })

  onMounted(async () => {
    await videoStore.getVideoList()
    const allVideos = videoStore.video_list ?? []
    if (allVideos.length > 0) {
      const shuffled = [...allVideos].sort(() => Math.random() - 0.5)
      const selected = shuffled.slice(0, 3)
      
      while (selected.length < 3) {
        selected.push({ ...blankVideo })
      }
      randomVideos.value = selected
    } else {
      randomVideos.value = Array.from({ length: 3 }, () => ({ ...blankVideo }))
    }
  })
</script>

<style scoped>
  .container > div {
    display: flex;
    align-items: center;
  }

  .title {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 40px;
    font-style: normal;
    font-weight: 700;
    line-height: 1;
    margin-right: 11px;
  }

  .tag {
    color: #A0A0A0;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
    margin-top: 14px;
  }

  .link {
    color: #5A5A5A;
    text-align: center;
    font-family: Inter;
    font-size: 18px;
    font-style: normal;
    font-weight: 400;
    line-height: 1;
    position: relative;
    top: 11px;
  }

  .triangle-right {
    width: 0;
    height: 0;
    border-top: 6.5px solid transparent;
    border-left: 13px solid #5A5A5A;
    border-bottom: 6.5px solid transparent;
    position: relative;
    top: 15px;
    margin-left: 4px;
  }

  .wrapper {
    display: flex;
    flex-direction: row;
    text-decoration: none;
  }


  .game-list {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 21px;
    margin-top: 30px;
  }

  .video-list {
    display: flex;
    flex-wrap: wrap;
    gap: 22px;
    margin-top: 30px;
  }

    /* 빈 상태 컨테이너도 기존 레이아웃과 같은 폭/간격 유지 */
  .video-empty {
    display: flex;
    flex-direction: column;
    gap: 14px;
    margin-top: 30px;
  }

  /* 스켈레톤 카드들: 실제 카드와 동일한 그리드 감 유지 */
  .skeleton-list {
    display: flex;
    flex-wrap: wrap;
    gap: 22px;
    min-height: 220px;       /* 목록이 비어도 섹션이 쪼그라들지 않도록 */
  }

  /* 카드 한 장의 형태를 대략적으로 유지 */
  .skeleton-card {
    width: 452px;
    max-width: calc((1440px - 2*22px - 2*20px) / 3); /* 반응형에서도 자연스럽게 */
    flex: 1 1 400px;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }



  /* 16:9 썸네일 자리 (회색 반짝 효과) */
  .skeleton-thumb {
    aspect-ratio: 16 / 9;
    border-radius: 12px;
    background: linear-gradient(90deg, #f2f2f3 25%, #ececee 37%, #f2f2f3 63%);
    background-size: 400% 100%;
    animation: shimmer 1.2s infinite linear;
  }

  /* 텍스트 줄 자리 */
  .skeleton-line {
    height: 12px;
    border-radius: 8px;
    background: linear-gradient(90deg, #f2f2f3 25%, #ececee 37%, #f2f2f3 63%);
    background-size: 400% 100%;
    animation: shimmer 1.2s infinite linear;
  }
  .skeleton-line.w-80 { width: 80%; }
  .skeleton-line.w-60 { width: 60%; }

  /* 반짝 애니메이션 */
  @keyframes shimmer {
    0%   { background-position: 100% 0; }
    100% { background-position: 0 0; }
  }

  /* 반응형에서도 기존 규칙을 따르도록 간격만 맞춤 */
  @media (max-width: 1024px) {
    .skeleton-list { gap: 16px; }
  }
  @media (max-width: 768px) {
    .skeleton-list { gap: 12px; justify-content: center; }
  }
  @media (max-width: 480px) {
    .skeleton-list { gap: 10px; }
  }


  
  .request-container {
    width: 100%;
    margin-top: 40px;
  }

  /* 반응형 스타일 */
  @media (max-width: 1280px) {
    .game-list { gap: 18px; }
    .video-list { gap: 18px; }
  }

  @media (max-width: 1024px) {
    .container { margin: 60px auto 0 auto; }
    .title { font-size: 32px; }
    .tag { font-size: 18px; }
    .link { font-size: 16px; }
    .game-list { gap: 16px; }
    .video-list { gap: 16px; }
    /* 4개 → 3개로 줄이기 */
    .game-list > *:nth-child(n+4) { display: none; }
    .video-list > *:nth-child(n+4) { display: none; }
  }

  @media (max-width: 768px) {
    .container { margin: 40px auto 0 auto; padding: 0 16px; }
    .container > div { flex-direction: column; align-items: flex-start; gap: 12px; }
    .title { font-size: 28px; margin-right: 0; }
    .tag { font-size: 16px; margin-top: 12px; }
    .link { font-size: 14px; top: 0; }
    .triangle-right { top: 4px; }
    .game-list { gap: 12px; margin-top: 20px; justify-content: center; }
    .video-list { gap: 12px; margin-top: 20px; justify-content: center; }
    .main-image { margin-top: 15px; }
    /* 3개 → 2개로 줄이기 */
    .game-list > *:nth-child(n+3) { display: none; }
    .video-list > *:nth-child(n+3) { display: none; }
  }

  @media (max-width: 480px) {
    .container { margin: 30px auto 0 auto; padding: 0 12px; }
    .title { font-size: 24px; }
    .tag { font-size: 14px; }
    .link { font-size: 13px; }
    .game-list { gap: 10px; margin-top: 16px; justify-content: center; }
    .video-list { gap: 10px; margin-top: 16px; justify-content: center; }
    .main-image { margin-top: 10px; }
    /* 2개 → 1개로 줄이기 */
    .game-list > *:nth-child(n+2) { display: none; }
    .video-list > *:nth-child(n+2) { display: none; }
  }

  /* ─────────────────────────────────────────────
     스크롤 진입 페이드‑인 애니메이션
  ───────────────────────────────────────────── */
  .reveal {
    opacity: 0;
    transform: translateY(16px) scale(0.98);
    transition:
      opacity 600ms ease,
      transform 600ms cubic-bezier(0.22, 1, 0.36, 1);
    will-change: opacity, transform;
  }
  .reveal.is-visible {
    opacity: 1;
    transform: translateY(0) scale(1);
  }

  /* 카드류는 살짝 더 가벼운 모션 */
  .card-reveal.reveal {
    transform: translateY(10px) scale(0.99);
    transition-duration: 520ms;
  }

  /* 호버 미세 상향(포인터 있는 환경만) */
  @media (hover: hover) and (pointer: fine) {
    .game-list > *:hover {
      transform: translateY(-3px);
      transition: transform 200ms ease;
    }
  }

  /* 접근성: 모션 최소화 환경 */
  @media (prefers-reduced-motion: reduce) {
    .reveal,
    .reveal.is-visible,
    .card-reveal.reveal {
      transition: none !important;
      transform: none !important;
      opacity: 1 !important;
    }
  }
</style>
