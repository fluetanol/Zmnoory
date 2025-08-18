<!-- ZmnnooryHero.vue -->
<template>
  <section class="hero" :class="{ 'no-motion': !animate }" aria-label="즈믄누리 인트로">
    <!-- 우상단 별 궤적 -->
    <div class="swirl" aria-hidden="true">
      <svg viewBox="0 0 300 300">
        <!-- 여러 개의 원호를 얇은 스트로크로 회전 -->
        <g class="swirl-rot">
          <circle class="arc" cx="240" cy="60" r="180" />
          <circle class="arc" cx="240" cy="60" r="150" />
          <circle class="arc" cx="240" cy="60" r="120" />
          <circle class="arc short" cx="240" cy="60" r="90" />
          <circle class="arc short" cx="240" cy="60" r="60" />
        </g>
      </svg>
    </div>

    <!-- 본문 -->
    <div class="content">
      <h1 class="brand">
        <span class="brand-text">즈믄누리</span>
        <span class="brand-dot">•</span>
      </h1>

      <p class="slogan">
        “ 천 개의 <span class="accent">얼굴</span>, 천 개의 <span class="accent">세계</span> ”
      </p>

      <!-- 라인 + 별 포인트 + 캡션 -->
      <div class="constellation">
        <svg class="line" viewBox="0 0 800 120" preserveAspectRatio="none">
          <!-- 기준선 -->
          <line x1="20" y1="90" x2="780" y2="90" class="baseline" />
          <!-- 반짝이는 점(왼쪽 시작점) -->
          <circle cx="20" cy="90" r="7" class="node glow" />
          <!-- 지그재그 라인 -->
          <polyline
            class="zig"
            points="260,88 340,40 420,88 520,60 600,80"
          />
          <!-- 지그재그 노드 -->
          <circle cx="260" cy="88" r="7" class="node glow delay-1" />
          <circle cx="340" cy="40" r="7" class="node glow delay-2" />
          <circle cx="420" cy="88" r="7" class="node glow delay-3" />
          <circle cx="520" cy="60" r="7" class="node glow delay-4" />
          <circle cx="600" cy="80" r="7" class="node glow delay-5" />
        </svg>
      </div>
    </div>

    <!-- 우하단 인장 느낌 -->
    <div class="seal" aria-hidden="true">즈믄</div>

  </section>
</template>

<script setup lang="ts">
/**
 * 애니메이션 On/Off가 필요하면 prop으로 제어하세요.
 * (접근성: 사용자가 'Reduce motion' 설정 시 자동으로 느려짐)
 */
defineProps<{ animate?: boolean }>()
</script>

<style>
:root {
  --bg: #2a2a2a;           /* 배경 */
  --fg: #e9e9e9;           /* 일반 텍스트 */
  --muted: #a0a0a0;        /* 보조 텍스트 */
  --teal: #00c8bd;         /* 포인트 (얼굴/세계) */
  --line: #cfd2d4;         /* 라인 */
  --glow: #ffffff;         /* 별 글로우 */
  --seal: #c92020;         /* 인장 */
}

/* 레이아웃 */
.hero {
  position: relative;
  width: 100%;
  min-height: clamp(800px, 55vw, 720px);
  background: var(--bg);
  color: var(--fg);
  overflow: hidden;
  display: grid;
  place-items: center;
  padding: clamp(24px, 4vw, 48px);
}

/* 본문 컨테이너 */
.content {
  width: min(1200px, 92%);
  margin: 0 auto;
}

/* 브랜드 타이틀 */
.brand {
  display: flex;
  align-items: baseline;
  gap: 12px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: 0.02em;
  margin: 0 0 18px;
  /* 슬라이드 인 */
  animation: slideIn 700ms ease-out both;
}

.brand-text {
  font-size: clamp(48px, 9vw, 128px);
  font-family: "Pretendard Variable", "Inter", system-ui, -apple-system, Segoe UI, Roboto, "Noto Sans KR", sans-serif;
  /* 둥근 느낌 */
  text-rendering: optimizeLegibility;
}

.brand-dot {
  translate: 0 0.25em;
  font-size: clamp(24px, 4vw, 40px);
  opacity: 0.85;
}

/* 슬로건 */
.slogan {
  font-size: clamp(16px, 2.4vw, 28px);
  color: var(--muted);
  margin: 6px 0 28px;
  animation: fadeIn 900ms 120ms ease-out both;
}
.accent { color: var(--teal); font-weight: 700; }

/* 라인 + 별자리 */
.constellation {
  position: relative;
  display: inline-block;
  animation: fadeIn 900ms 240ms ease-out both;
}
.caption {
  position: absolute;
  left: 0;
  top: 100%;
  margin-top: 12px;
  font-size: clamp(14px, 1.6vw, 20px);
  color: var(--muted);
}

/* SVG 라인 */
.line {
  width: min(920px, 96vw);
  height: clamp(72px, 12vw, 120px);
}

.baseline {
  stroke: var(--line);
  stroke-width: 2;
  opacity: 0.7;
}
.zig {
  fill: none;
  stroke: #fff;
  stroke-width: 2.5;
  stroke-linecap: round;
  stroke-linejoin: round;
  /* 스트로크 그려지는 애니메이션 */
  stroke-dasharray: 500;
  stroke-dashoffset: 500;
  animation: draw 1.8s 300ms ease forwards;
}

/* 별 노드 + 글로우 */
.node {
  fill: var(--glow);
  filter: drop-shadow(0 0 8px rgba(255,255,255,0.65));
}
.glow {
  animation: twinkle 2.6s ease-in-out infinite;
}
.delay-1 { animation-delay: 200ms; }
.delay-2 { animation-delay: 400ms; }
.delay-3 { animation-delay: 600ms; }
.delay-4 { animation-delay: 800ms; }
.delay-5 { animation-delay: 1000ms; }

/* 우상단 별 궤적 */
.swirl {
  position: absolute;
  right: -10%;
  top: -20%;
  width: min(70vmin, 720px);
  opacity: 0.8;
  pointer-events: none;
}
.swirl svg { width: 100%; height: auto; }
.arc {
  fill: none;
  stroke: #e6e6e6;
  stroke-width: 1.2;
  opacity: 0.25;
}
.arc.short {
  stroke-dasharray: 30 14;
  stroke-linecap: round;
}
.swirl-rot {
  transform-origin: 80% 20%;
  animation: rotate 36s linear infinite;
}

/* 우하단 인장 느낌 */
.seal {
  position: absolute;
  right: clamp(12px, 2vw, 20px);
  bottom: clamp(12px, 2vw, 20px);
  width: clamp(36px, 5vw, 48px);
  height: clamp(36px, 5vw, 48px);
  border: 2px solid var(--seal);
  color: var(--seal);
  font-weight: 800;
  display: grid;
  place-items: center;
  font-size: clamp(12px, 2vw, 14px);
  letter-spacing: 0.05em;
}

/* 카피라이트 */
.copyright {
  position: absolute;
  left: clamp(12px, 2vw, 24px);
  bottom: clamp(12px, 2vw, 20px);
  color: var(--muted);
  font-size: 12px;
  opacity: 0.9;
}

/* 애니메이션 키프레임 */
@keyframes rotate {
  to { transform: rotate(360deg); }
}
@keyframes twinkle {
  0%, 100% { opacity: 0.65; filter: drop-shadow(0 0 2px rgba(255,255,255,0.2)); }
  50% { opacity: 1;   filter: drop-shadow(0 0 10px rgba(255,255,255,0.9)); }
}
@keyframes draw {
  to { stroke-dashoffset: 0; }
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to   { opacity: 1; transform: translateY(0); }
}
@keyframes slideIn {
  from { opacity: 0; transform: translateY(10px) scale(0.985); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* 사용자 ‘Reduce motion’ 고려 + prop으로 제어 */
@media (prefers-reduced-motion: reduce) {
  .swirl-rot { animation-duration: 120s; }
  .glow { animation-duration: 6s; }
  .zig { animation-duration: 1s; }
}
.no-motion .swirl-rot,
.no-motion .glow,
.no-motion .zig {
  animation: none !important;
}
</style>
