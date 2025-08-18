import type { Component } from 'vue'

// 모든 Game.vue를 지연 로딩으로 수집 (번들에 자동 포함)
const modules = import.meta.glob('./**/*GameComponent.vue') as Record<
  string,
  () => Promise<{ default: Component }>
>
console.log("게임 컴포넌트 모듈 파일들 : ", modules);

/**
 * 서버의 id(0,1,2,...) → 로컬 Game.vue 파일 경로
 * - id는 "관리자 등록 순서"라고 하셨으니, 여기만 맞춰주면 됩니다.
 * - 경로는 이 파일 기준 상대경로(★ 앞에 './' 꼭 유지)
 */
export const ID_TO_PATH = {
  102: './emoji/EmojiGameComponent.vue',
  152: './famousline/FamouslineGameComponent.vue',
  153: './guess-expression/GuessExpressionGameComponent.vue',
  // ...
} as const

export function loadGameById(id: number) {
  const path = ID_TO_PATH[id as keyof typeof ID_TO_PATH]
  return path ? modules[path] : null
}

/** 안전한 존재 여부 체크 (리스트/관리도구에서 사용) */
export function hasGameForId(id: number) {
  const path = ID_TO_PATH[id as keyof typeof ID_TO_PATH]
  return !!path && !!modules[path]
}
