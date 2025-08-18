<!-- [FILEPATH] src/common/components/shared/BaseInput.vue -->
<!-- 상위 폴더에서 type="원하는거" placeholder='원하는거' 하면됨 -->
<template>
  <input
    :type="type"
    :placeholder="placeholder"
    class="base-input"
    :value="modelValue"
    @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
  />
</template>

<script setup lang="ts">
  defineProps<{
    modelValue: string
    type: string
    placeholder: string
  }>()

  defineEmits<{
    (e: 'update:modelValue', value: string): void
  }>()
</script>

<style scoped>
  .base-input {
    display: block;
    width: 441px;
    height: 65px;
    border: 1px solid #A0A0A0;
    color: #2E2E2E;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    padding-left: 18px;
  }

  .base-input input::placeholder {
    color: #A0A0A0;
  }
</style>