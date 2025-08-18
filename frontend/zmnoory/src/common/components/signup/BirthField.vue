<!-- [FILEPATH] src/common/components/signup/BirthField.vue -->
<template>
  <div class="bf-root" :style="{width: props.width ? props.width + 'px' : undefined, flex: '0 0 auto'}">
    <Combobox>
      <div class="bf-box" :class="{ 'bf-invalid': invalid }">
        <ComboboxInput
          class="bf-input"
          :placeholder="placeholder"
          :value="innerValue"
          @input="onInput"
          @blur="onBlur"
          @keydown.tab.stop
        />
        <ComboboxButton class="bf-button" aria-label="열기">▾</ComboboxButton>
      </div>

      <Transition
        enter-active-class="bf-enter"
        enter-from-class="bf-enter-from"
        enter-to-class="bf-enter-to"
        leave-active-class="bf-leave"
        leave-from-class="bf-leave-from"
        leave-to-class="bf-leave-to"
      >
        <ComboboxOptions class="bf-options">
          <ComboboxOption
            v-for="opt in options"
            :key="opt"
            :value="opt"
            v-slot="{ active, selected }"
            as="template"
          >
            <li
              class="bf-option"
              :class="{ 'bf-active': active, 'bf-selected': selected }"
              @click="selectOption(opt)"
            >
              {{ opt }}
            </li>
          </ComboboxOption>
        </ComboboxOptions>
      </Transition>
    </Combobox>
  </div>
</template>



<script setup lang="ts">
import { ref, watch } from 'vue';
import {
  Combobox,
  ComboboxInput,
  ComboboxOptions,
  ComboboxOption,
  ComboboxButton,
} from '@headlessui/vue';

const props = withDefaults(defineProps<{
  modelValue: string
  options: string[]
  width?: number
  placeholder?: string
  invalid?: boolean
  numericOnly?: boolean
  maxLength?: number
}>(), {
  modelValue: '',
  options: () => [],
});

const emit = defineEmits<{
  (e: 'update:modelValue', v: string): void
  (e: 'touched'): void
}>();

const innerValue = ref(props.modelValue);

watch(() => props.modelValue, (v) => {
  innerValue.value = v;
});

// 사용자가 직접 타이핑할 때
function onInput(event: Event) {
  let v = (event.target as HTMLInputElement).value;

  if (props.numericOnly) v = v.replace(/\D/g, '');
  if (props.maxLength) v = v.slice(0, props.maxLength);

  innerValue.value = v;
  emit('update:modelValue', v); // 실시간으로 부모에게 전달
}

// 드롭다운에서 옵션을 '클릭'할 때
function selectOption(option: string) {
  innerValue.value = option;
  emit('update:modelValue', option);
}

// 포커스를 잃었을 때는 touched 상태만 전달
function onBlur() {
  emit('touched');
}
</script>

<style scoped>
/* 컨테이너 */
.bf-root { position: relative; min-width: 0; }

/* ────────── 박스 ────────── */
.bf-box {
  position: relative;                 /* ▼ absolute 위치용 기준점 */
  display: flex; align-items: center;
  height: 60px;
  border: 1px solid #D0D0D0;
  border-radius: 8px;
  background: #fff;
  padding: 0 40px 0 16px;             /* 오른쪽 40px = 화살표 자리 */
}
.bf-box.bf-invalid { border-color: #E53935; }

/* ────────── 입력창 ────────── */
.bf-input {
  flex: 1 1 auto;                     /* 남은 공간만 차지, shrink 가능 */
  min-width: 0;                       /* 텍스트 길어도 줄바꿈 대신 축소 */
  height: 100%;
  font-size: 18px; line-height: 1.2;
  border: none; outline: none; background: transparent;
}
.bf-input::placeholder { color: #A0A0A0; }

/* ────────── 화살표 버튼 ────────── */
.bf-button {
  position: absolute;                 /* 부모 .bf-box 기준 고정 */
  top: 50%; right: 12px;
  transform: translateY(-50%);
  width: 20px; height: 20px;          /* 절대 0으로 안 줄어듦 */
  border: none; background: transparent; cursor: pointer;
  font-size: 18px; line-height: 1; color: #888;
}

/* ────────── 옵션 레이어 ────────── */
.bf-options {
  position: absolute; left: 0; right: 0; margin-top: 6px;
  max-height: 240px; overflow: auto;
  background: #fff; border: 1px solid #DADADA; border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0,0,0,.08);
  z-index: 50; padding: 6px;
}

/* 옵션 아이템 */
.bf-option {
  list-style: none; padding: 10px 12px; border-radius: 8px;
  font-size: 16px; cursor: pointer;
}
.bf-option + .bf-option { margin-top: 4px; }
.bf-active   { background: #F5E7DA; }
.bf-selected { font-weight: 600; }

/* 트랜지션 */
.bf-enter      { transition: all .12s ease-out; }
.bf-enter-from { opacity: 0; transform: translateY(-4px) scale(.98); }
.bf-enter-to   { opacity: 1; transform: translateY(0)    scale(1); }
.bf-leave      { transition: all .10s ease-in; }
.bf-leave-from { opacity: 1; transform: translateY(0)    scale(1); }
.bf-leave-to   { opacity: 0; transform: translateY(-4px) scale(.98); }
</style>