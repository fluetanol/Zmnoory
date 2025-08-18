<template>
  <div
    class="picker"
    tabindex="0"
    @keydown.up.prevent="step(-1)"
    @keydown.down.prevent="step(1)"
  >
    <div
      class="viewport"
      @wheel.prevent="onWheel"
      :style="{ height: `${itemHeight * visibleCount}px` }"
    >
      <ul
        class="track"
        :style="{ transform: `translateY(${offsetY}px)` }"
      >
        <li
          v-for="(opt, i) in options"
          :key="opt"
          class="item"
          :class="{ active: i === selectedIndex }"
          @click="select(i)"
          :style="{ height: `${itemHeight}px` }"
        >
          {{ opt }}
        </li>
      </ul>

      <!-- 위/아래 페이드 -->
      <div class="fade top"></div>
      <div class="fade bottom"></div>

      <!-- 오른쪽 내부 작은 화살표 버튼 -->
      <button class="arrow-btn up" type="button" @click="step(-1)" aria-label="이전">▲</button>
      <button class="arrow-btn down" type="button" @click="step(1)" aria-label="다음">▼</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = withDefaults(defineProps<{
  modelValue: string
  options: string[]
  itemHeight?: number
  visibleCount?: number
}>(), {
  itemHeight: 65,
  visibleCount: 1,
})

const emit = defineEmits<{ (e: 'update:modelValue', v: string): void }>()

const selectedIndex = ref(
  Math.max(0, props.options.findIndex(o => o === props.modelValue))
)

watch(() => props.modelValue, v => {
  const idx = props.options.findIndex(o => o === v)
  if (idx !== -1) selectedIndex.value = idx
})

const offsetY = computed(() => {
  const center = Math.floor(props.visibleCount / 2)
  return -(selectedIndex.value - center) * props.itemHeight
})

function clamp(i: number) {
  return Math.min(props.options.length - 1, Math.max(0, i))
}
function select(i: number) {
  i = clamp(i)
  selectedIndex.value = i
  emit('update:modelValue', props.options[i])
}
function step(dir: -1 | 1) {
  select(selectedIndex.value + dir)
}
function onWheel(e: WheelEvent) {
  step(e.deltaY > 0 ? 1 : -1)
}
</script>

<style scoped>
  .picker {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
    width: 441px;
    outline: none;
  }

  .viewport {
    position: relative;
    border: 1px solid #A0A0A0;
    overflow: hidden;
    background: #fff;
  }

  .track {
    list-style: none;
    padding: 0;
    margin: 0;
    transition: transform 160ms ease-out;
  }

  .item {
    display: flex;
    align-items: center;
    padding-left: 18px;
    font-family: Inter, system-ui, sans-serif;
    font-size: 20px;
    color: #7b7b7b;
    user-select: none;
    cursor: pointer;
  }

  .item.active {
    color: #2E2E2E;
    font-weight: 400;
  }

  /* 페이드(이웃 항목 살짝 보이기) */
  .fade {
    position: absolute;
    left: 0; right: 0;
    height: 28px;
    pointer-events: none;
  }
  .fade.top {
    top: 0;
    background: linear-gradient(#fff, rgba(255,255,255,0));
  }
  .fade.bottom {
    bottom: 0;
    background: linear-gradient(rgba(255,255,255,0), #fff);
  }

  /* 오른쪽 내부 작은 화살표 버튼 */
  .arrow-btn {
    position: absolute;
    right: 8px;
    width: 28px;
    height: 28px;
    border: none;
    background: none;
    font-weight: 700;
    font-size: 12px;
    line-height: 1;
    cursor: pointer;
    border-radius: 4px;
    display: grid;
    place-items: center;
  }
  .arrow-btn.up { top: 8px; }
  .arrow-btn.down { bottom: 8px; }
</style>
