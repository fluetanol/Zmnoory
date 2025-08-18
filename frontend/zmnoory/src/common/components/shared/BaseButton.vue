<!-- [FILEPATH] src/common/components/shared/BaseButton.vue -->
<!-- 상위 폴더에서 @click-event 라고 쓰면 사용 가능 -->
<template>
  <button class="base-button" @click="$emit('clickEvent')">
    <slot></slot>
  </button>
</template>

<style scoped>
  .base-button {
    width: 441px;
    height: 73px;
    background: #F5E7DA;
    color: #2E2E2E;
    text-align: center;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 700;
    line-height: normal;
  }
  /* 마우스 올렸을 때 */
  .base-button:hover {
    background: #EED5C0; /* 살짝 진한 톤 */
    transform: translateY(-2px); /* 살짝 떠오르는 느낌 */
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  
/* 클릭 중일 때 */
.base-button:active {
  background: #E6C8AE; /* 더 진한 톤 */
  transform: translateY(0); /* 눌린 느낌 */
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}
</style>
