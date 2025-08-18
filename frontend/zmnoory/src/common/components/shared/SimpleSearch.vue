<template>
  <div class="search-bar" :class="{ choice: isChoice }">

    <div
      ref="keywordRef"
      class="filter-item"
      :class="{ focused: isKeywordFocused }"
      style="width: 820px; padding-left: 35px;"
      tabindex="0"
      @focus="activeIndex = 0"
      @blur="activeIndex = null"
      @mouseenter="hoverIndex = 0"
      @mouseleave="hoverIndex = null"
    >
      <label>검색어</label>
      <input
        type="text"
        placeholder="검색어 입력"
        v-model="keyword"
        @focus="isKeywordFocused = true"
        @blur="isKeywordFocused = false"
      >
    </div>

    <button class="search-btn" @click="emitSearch">
      <img src="@/assets/search-icon.png" alt="search">
    </button>
  </div>
</template>


<script setup lang="ts">
  import { ref, computed } from 'vue'

  const keyword = ref('')
  const isKeywordFocused = ref(false)
  const isChoice = computed(() => activeIndex.value !== null)

  // divider 를 없애기 위한 작업
  const hoverIndex = ref<number | null>(null)
  const activeIndex = ref<number | null>(null)

  // 드롭다운 위치 계산을 위한 ref
  const keywordRef = ref<HTMLElement>()

  const emit = defineEmits<{
    (e: 'search', payload: {
      keyword: string
    }): void
  }>()

  function emitSearch() {
    emit('search', {
      keyword: keyword.value,
    })
  }
</script>

<style scoped>
  /* 검색 바 관련 */
  .search-bar {
    position: relative;
    display: flex;
    align-items: center;
    width: 898px;
    height: 75px;
    background: #FFFFFF;
    border: 1px solid #2E2E2E;
    box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.25);
    border-radius: 45px;
    box-sizing: border-box;
  }

  .search-bar.choice {
    display: flex;
    align-items: center;
    width: 898px;
    height: 75px;
    background: #F5F5F5;
    border: 1px solid #2E2E2E;
    box-shadow: 2px 2px 10px 0 rgba(0, 0, 0, 0.25) inset, 0 2px 10px 0 rgba(0, 0, 0, 0.25);
    border-radius: 45px;
    box-sizing: border-box;
  }

  .filter-item {
    height: 73px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 0 24px;
    border-radius: 45px;
  }

  .filter-item label {
    font-family: 'Inter';
    font-size: 20px;
    font-weight: 400;
    color: #2E2E2E;
    margin-bottom: 2px;
  }
  
  .filter-item input {
    height: 29.26px;
    border: none;
    background: transparent;
    font-size: 20px;
    outline: none;
    color: #A0A0A0;
  }

  .filter-item input::placeholder {
    color: #A0A0A0; /* 원하는 색상으로 변경 */
  }

  .filter-item:hover {
    background-color: #FDFAF8;
  }

  .filter-item:focus {
    border: 1px solid #2E2E2E;
    background: #F5E7DA;
    box-shadow: 0 2px 10px 0 rgba(0, 0, 0, 0.25);
  }

  .filter-item.focused {
    border: 1px solid #2E2E2E;
    background: #F5E7DA;
    box-shadow: 0 2px 10px 0 rgba(0, 0, 0, 0.25);
  }

  .placeholder {
    font-family: 'Inter';
    font-size: 20px;
    font-weight: 400;
    color: #A0A0A0;
  }

  .search-btn {
    margin-left: auto;
    width: 43px;
    height: 43px;
    background: #F5E7DA;
    border: none;
    border-radius: 50%;
    font-size: 18px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20px;
  }
</style>
