<!-- [FILEPATH] src/common/components/shared/BaseInput.vue -->
<!-- 상위 폴더에서 type="원하는거" placeholder='원하는거' 하면됨 -->
<template>
  <textarea
    :placeholder="placeholder"
    class="base-input base-textarea"
    :value="modelValue"
    @input="$emit('update:modelValue', ($event.target as HTMLTextAreaElement).value)"
  ></textarea>
</template>

<script setup lang="ts">
  defineProps<{
    modelValue: string
    placeholder: string
  }>()

  defineEmits<{
    (e: 'update:modelValue', value: string): void
  }>()
</script>

<style scoped>
.base-input {
  width: 441px;
  border: 1px solid #A0A0A0;
  color: #A0A0A0;
  font-family: Inter;
  font-size: 20px;
  font-weight: 400;
  padding-left: 18px;
}

/* textarea 전용 스타일 */
.base-textarea {
  min-height: 160px;
  padding-top: 12px;
  padding-right: 12px;
  resize: vertical; /* 크기 조절 가능 */
  line-height: 1.4;
}

.base-input::placeholder {
  color: #A0A0A0;
}
</style>