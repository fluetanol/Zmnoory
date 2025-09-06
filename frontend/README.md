<!-- [FILEPATH] frontend/README.md -->
# 즈믄누리(ZMNNOORY) Frontend

> Vue 3 + TypeScript 기반의 Web/3D 인터랙티브 게임 프론트엔드. Three.js와 face‑api.js, WebRTC를 활용해 표정/음성/카메라 인터랙션을 실험합니다.

<p align="left">
  <img alt="Vue.js" src="https://img.shields.io/badge/Vue.js-35495E?logo=vuedotjs&logoColor=4FC08D" />
  <img alt="Three.js" src="https://img.shields.io/badge/three.js-000000?logo=threedotjs&logoColor=FFFFFF" />
  <img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=FFFFFF" />
  <img alt="face-api.js" src="https://img.shields.io/badge/face--api.js-FF6A00?logo=javascript&logoColor=FFFFFF" />
</p>

---

## 1) 서비스 목적 (Why)

* **감정·표정 기반 인터랙션 연구**: 웹캠/마이크/브라우저만으로도 표정 인식/음성/멀티플레이가 결합된 **가벼운 실험 게임**들을 빠르게 만들고 검증합니다.
* **실험 → 데이터 → 개선의 선순환**: 초기에 **MVP**를 빠르게 출시하고 사용자 피드백과 플레이 로그를 바탕으로 반복 개선합니다.
* **프론트엔드 공학 실험장**: 성능 최적화(번들 청크 분리, 텍스처 아틀라스, GPU 워밍업, 리소스 디스포즈 등)와 **원자적 컴포넌트 설계**를 적용해 **확장성**과 **재사용성**을 극대화합니다.
* **고풍스러움 × 미래지향**: “즈믄누리”의 세계관 톤앤매너(고풍스러운 타이포/레이아웃)와 기술적 모던함(3D/AI/실시간)을 **절충**한 디자인을 지향합니다.

---

## 2) 기술 스택 (Tech Stack)

* **Framework**: Vue 3 (Vite, Pinia)
* **Language**: TypeScript
* **3D / 렌더링**: Three.js (카메라 제어, 파티클/이펙트, 오브젝트 풀링)
* **비전/표정 인식**: face‑api.js (커스텀 파인튜닝 모델 스위칭 지원)
* **실시간 통신**: WebRTC (멀티 참가자 화상/음성)
* **유틸/기타**: Node 18+, ESLint/Prettier, Vitest(선택)

> **Node**: v18 이상 권장. **패키지 매니저**: pnpm 또는 npm.

---

## 3) 개발한 게임 (Mini‑Games)

1. **EmojiGame**

   * 웹캠으로 인식한 **실제 얼굴의 표정**과 화면의 **이모지 표정**을 **실시간 매칭**하여 점수를 획득합니다.
   * 포인트: 표정 인식 파이프라인(전처리 → 감정 추론 → 매칭 로직), 텍스처 아틀라스 기반 이모지 UI.

2. **FamousLine**

   * 주어진 **영화/드라마 명대사**를 **특정 감정**으로 읊습니다. 음성/표정 피드로 **유사도/정확도** 점수화.
   * 포인트: 감정 프롬프트, 타이밍/톤 가이드, 멀티 모달 점수 산출(텍스트/오디오/비전 추정치 결합 가능 구조).

3. **Guess‑Expressions**

   * **WebRTC**로 여러 명이 화상으로 참여. 출제자가 지은 표정을 참가자들이 **실시간으로 맞추는** 파티 게임.
   * 포인트: P2P 시그널링/방 관리, 플레이어 상태 동기화, 라운드/타이머 UI.

---

## 4) 성과 (Highlights)

* **컴포넌트 설계**

  * 원자적(Atomic) 컴포넌트 설계로 **UI 재사용성**을 높이고 계층 구조를 정립. 공통 HUD/패널/컨트롤을 모듈화.

* **MVP 출시**

  * **생성형 AI**를 활용해 프로토타입 → MVP까지 속도전 전개. 얼리 유저 피드백으로 빠른 개선 사이클 구축.

* **독창적인 디자인 컨셉**

  * AI 의존 템플릿을 지양하고, **컬러 팔레트/타이포/레이아웃**을 직접 설계. “고풍스러움 × 미래지향” 톤앤매너를 확립.

* **Three.js로 직접 게임 동작 구현**

  * 단순 3D UI를 넘어 **순수 게임 로직**(카메라 워크, 충돌, 넉백 등)을 Three.js 객체로 구성/제어.

* **TypeScript 활용**

  * 타입으로 상태/이벤트/리소스 수명 관리의 **모호성 제거**. 디버깅 효율과 빌드 안정성 향상.

---

## 5) 이슈 & 트러블슈팅 (Issues & Troubleshooting)

### 5‑1. 빌드 청크 파일 크기 개선

**상황**: 초기 번들에 3D/비전 라이브러리가 단일 청크로 몰려 **용량 과대** 및 초기 로드 지연.

**접근**:

* `vite.config.ts`의 `rollupOptions.output.manualChunks`를 통해 **벤더 청크 분리**

  * 예) `three`, `face-api`, `webrtc-adapter` 등을 개별 청크로 분리
* **라우트 단위 코드 스플리팅**: 각 게임 뷰를 `() => import('...')`로 **지연 로딩**
* **게임별 리소스 지연 로드**: 진입 시점에 텍스처/사운드/모델을 **프리페치 → 필요 시 로드**
* **텍스처 아틀라스** 활용: 이모지 스프라이트를 아틀라스로 묶어 **텍스처 바인딩/드로우콜 감소**

**효과**:

* 실제 프로젝트에서 **번들 사이즈를 대폭 축소**(예: 메인 청크 1205 kB → 516 kB 수준)하고 **첫 로드 체감 시간** 개선.

> 참고 스니펫

```ts
// vite.config.ts
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          three: ['three'],
          faceapi: ['face-api.js'],
          webrtc: ['webrtc-adapter']
        }
      }
    }
  }
});
```

```ts
// router
const EmojiGame = () => import('@/modules/emojigame/views/EmojiGameView.vue');
```

---

### 5‑2. 급격한 프레임 드랍 (카메라에 새 오브젝트가 잡힐 때)

**상황**: 카메라에 비추지 않던 대형 메시/텍스처가 갑자기 뷰에 들어올 때 **첫 프레임 드랍** 발생.

**접근**:

* **GPU 워밍업**: 씬 로드 직후, 오브스크린 렌더 타깃에 **N 프레임 프리패스 렌더**(머티리얼/셰이더 캐시 워밍)
* **프리로드 & 프리컴파일**: 텍스처/셔더/GL 프로그램을 미리 요청하고, 첫 노출 전 더미 드로우로 준비
* **오브젝트 풀링**: 재사용 가능한 Mesh/Geometry를 **풀**로 관리해 생성/파괴 비용 제거
* **가시성 힌트**: LOD/FrustumCulling 튜닝, `frustumCulled=false`가 필요한 특수 오브젝트 최소화
* **디스포즈 철저**: 더 이상 쓰지 않는 `geometry/material/texture`는 `dispose()`로 메모리 누수 방지

**효과**:

* 첫 노출 프레임의 **스파이크 감소**, 시야 전환 시 **프레임 안정성** 확보.

---

### 5‑3. face‑api 표정 인식 정확도 문제

**상황**: 기본 가중치만으로는 한국어권 사용자 표정에서 **정확도가 낮아** EmojiGame 체감 품질 저하.

**접근**:

* \*\*AI‑Hub “한국인 감정인식을 위한 복합 영상”\*\*을 활용해 **파인튜닝**한 가중치 적용
* 인퍼런스 파이프라인에서 **전처리**(정규화/얼굴 정렬) 강화 및 **감정 클래스 재매핑**

**결과/참고**:

* 실제 플레이 품질 향상. 모델과 툴킷은 공개 리포지토리 참고 → [https://github.com/psiudo/asia-face-emotion-jsapi](https://github.com/psiudo/asia-face-emotion-jsapi)

---

## 6) 프로젝트 구조 (예시)

```
src/
├─ assets/                # 정적 자산 (이미지/폰트/사운드)
├─ common/                # 공통 컴포넌트, 헬퍼, 상수 등
├─ layouts/               # 페이지 레이아웃
├─ middlewares/           # 라우터 가드 등
├─ modules/               # 도메인/게임별 모듈 (emojigame, famousline, guess-expressions)
├─ plugins/               # 외부 라이브러리 등록 (e.g., face-api init)
├─ router/                # vue-router 설정 (lazy import)
├─ services/              # API/RTC/스토리지 등 서비스 레이어
├─ static/                # 정적 파일 (Vite public 유사)
├─ store/                 # 전역 상태 (Pinia)
├─ views/                 # 페이지 컴포넌트
├─ App.vue                # 루트 컴포넌트
└─ main.ts                # 앱 엔트리
```

---

## 7) 빠른 시작 (Getting Started)

```bash
# 1) 의존성 설치
pnpm i    # 또는 npm i

# 2) 개발 서버
pnpm dev  # 또는 npm run dev

# 3) 프로덕션 빌드
pnpm build

# 4) 프리뷰
pnpm preview
```

**권장 브라우저**: 최신 Chrome/Edge. **HTTPS** 환경에서만 카메라/마이크 접근 가능.

### 환경 변수 (예시)

```
VITE_APP_TITLE=ZMNNOORY
VITE_RTC_STUN=stun:stun.l.google.com:19302
VITE_FACEAPI_MODEL_URL=/models   # 모델 호스트 경로
```

---

## 8) 개발 노트 (Perf/UX 팁)

* **텍스처 아틀라스**로 드로우콜/텍스처 스위치 감소
* **LOD/Instancing** 적용(가능 시)으로 대규모 객체 처리
* **requestAnimationFrame** 루프에서 **비전/오디오 인퍼런스는 워커**로 분리 고려
* 뷰 전환 시 **리소스 정리**: 이벤트/타이머 해제, `dispose()` 호출, `cancelAnimationFrame`
* 모바일/저사양 모드 옵션 제공: 해상도 스케일/이펙트 오프 등

---

## 9) 라이선스 & 크레딧

* 코드 라이선스: TBD
* 데이터/모델: AI‑Hub 한국인 감정인식 복합 영상(참조/파생), face‑api.js
* 아이콘/폰트/이미지: 각 저작권/라이선스에 따름

---

## 10) 기여 (Contributing)

PR/Issue 환영합니다. 버그 리포트 시 **재현 단계/환경/콘솔 로그**를 함께 남겨주세요. 성능 이슈는 **하드웨어 스펙/프레임 타이밍**(스파이크 위치) 정보를 부탁드립니다.


