<!-- [FILEPATH] src/common/components/shared/RangeDropdown.vue -->
<template>
  <div class="catalog-container" @mousedown.stop>
    <!-- 최소 보상 -->
    <div class="dropdown">
      <span class="label">최소 {{ option }}</span>
      <div class="controls">
        <button @mousedown.prevent @click="decrease('min')">-</button>
        <span class="value">{{ minValue }}</span>
        <button @mousedown.prevent @click="increase('min')">+</button>
      </div>
    </div>

    <div class="divider"></div>

    <!-- 최대 보상 -->
    <div class="dropdown">
      <span class="label">최대 {{ option }}</span>
      <div class="controls">
        <button @mousedown.prevent @click="decrease('max')">-</button>
        <span class="value">{{ maxValue }}</span>
        <button @mousedown.prevent @click="increase('max')">+</button>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, watch, onMounted } from 'vue'

  const props = defineProps (
    {
      option: String,
      initialMin: Number,
      initialMax: Number,
    }
  )

  const emit = defineEmits(['update:min', 'update:max'])

  const minValue = ref(props.initialMin)
  const maxValue = ref(props.initialMax)

  onMounted(() => {
    emit('update:min', minValue.value)
    emit('update:max', maxValue.value)
  })

  watch(minValue, (val) => emit('update:min', val))
  watch(maxValue, (val) => emit('update:max', val))

  const increase = (type) => {
    if (type === 'min') minValue.value += 1000
    else maxValue.value += 1000
  }

  const decrease = (type) => {
    if (type === 'min' && minValue.value > 0) minValue.value -= 1000
    if (type === 'max' && maxValue.value > 0) maxValue.value -= 1000
  }
</script>

<style scoped>
  .catalog-container {
    width: 353px;
    height: 156px;
    border-radius: 30px;
    border: 1px solid #2E2E2E;
    background: #F5E7DA;
    box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.25);
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    padding: 16px;
    box-sizing: border-box;
  }

  .dropdown {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 260px;         /* ← divider와 동일한 너비 */
    margin: 0 auto;       /* ← 중앙 정렬 */
  }

  .label {
    font-family: 'Inter', sans-serif;
    font-weight: 600;
    font-size: 20px;
    color: #2E2E2E;
  }

  .controls {
    display: flex;
    align-items: center;
    gap: 15px;
  }

  .controls button {
    width: 31px;
    height: 31px;
    border-radius: 50%;
    border: 1px solid #A0A0A0;
    background: #FFF;
    font-size: 20px;
    color: #A0A0A0;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0;
    cursor: pointer;
  }

  .value {
    font-family: 'Inter', sans-serif;
    font-size: 15px;
    font-weight: 400;
    color: #2E2E2E;
    min-width: 40px;
    text-align: center;
  }

  .divider {
    height: 1px;
    background-color: #A0A0A0;
    width: 260px;
    margin: 0 auto;
  }
</style>

