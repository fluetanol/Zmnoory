<!-- [FILEPATH] src/common/components/shared/Dropdown.vue -->
<template>
  <div class="catalog-container">
    <p
      class="dropdown"
      v-for="item in options"
      :key="item"
      @mousedown="$emit('select', item)"
    >
      {{item}}
    </p>
  </div>
</template>

<script setup>
  defineProps (
    {
      options: Array,
    }
  )

  defineEmits(['select'])
</script>

<style scoped>
  .catalog-container {
    width: 213px;
    flex-shrink: 0;
    border-radius: 30px;
    border: 1px solid #2E2E2E;
    background: #F5E7DA;
    box-shadow: 0px 2px 10px 0px rgba(0, 0, 0, 0.25);
  }

  .dropdown {
    width: 145px;
    margin: 0 auto;
    color: #2E2E2E;
    text-align: center;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
    padding: 15px;
    border-top: 1px solid #A0A0A0;
  }

  /* 첫 번째 항목은 선 제거 */
  .dropdown:first-child {
    border-top: none;
  }
</style>