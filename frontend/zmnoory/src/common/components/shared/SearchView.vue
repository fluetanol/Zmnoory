<!-- [FILEPATH] src/common/components/shared/SearchView.vue -->
<template>
  <div class="search-bar" :class="{ choice: isChoice }">

    <div
      ref="keywordRef"
      class="filter-item"
      :class="{ focused: isKeywordFocused }"
      style="width: 393px; padding-left: 35px;"
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

    <div class="divider" :class="{ hidden: activeIndex === 0 || activeIndex === 1 || hoverIndex === 0 || hoverIndex === 1}"></div>

    <div
      ref="firstFilterRef"
      class="filter-item" style="width: 213px;" tabindex="0"
      @focus="activeIndex = 1"
      @blur="activeIndex = null"
      @mouseenter="hoverIndex = 1"
      @mouseleave="hoverIndex = null"
    >
      <label>{{ props.filters[0] }}</label>
      <span class="placeholder">{{ firstLabel || props.filters[0] + ' 선택' }}</span>
    </div>

    <div class="divider" :class="{ hidden: activeIndex === 2 || activeIndex === 1 || hoverIndex === 2 || hoverIndex === 1}"></div>

    <div
      ref="secondFilterRef"
      class="filter-item" style="width: 213px;" tabindex="0"
      @focus="activeIndex = 2"
      @blur="activeIndex = null"
      @mouseenter="hoverIndex = 2"
      @mouseleave="hoverIndex = null"
    >
      <label>{{ props.filters[1] }}</label>
      <span v-if="isRange === true" class="placeholder">{{ (minRange !== 0 || maxRange !== 10000) ? `${minRange}~${maxRange}` : props.filters[1] + ' 선택' }}</span>
      <span v-if="isRange === false" class="placeholder">{{ secondLabel || props.filters[1] + ' 선택' }}</span>
    </div>

    <button class="search-btn" @click="emitSearch">
      <img src="@/assets/search-icon.png" alt="search">
    </button>

    <Dropdown
      v-if="activeIndex === 1"
      class="dropdown-1"
      :style="getDropdownStyle(1)"
      :options="first_options"
      @select="onSelectFirst"
    ></Dropdown>

    <Dropdown
      v-if="activeIndex === 2 && isRange === false"
      class="dropdown-2"
      :style="getDropdownStyle(2)"
      :options="second_options"
      @select="onSelectSecond"
      ></Dropdown>

    <RangeDropDown
      v-if="activeIndex === 2 && isRange === true"
      class="dropdown-2"
      :style="getDropdownStyle(2)"
      :option="props.filters[1]"
      :initialMin="minRange"
      :initialMax="maxRange"
      @update:min="minRange = $event"
      @update:max="maxRange = $event"
    ></RangeDropDown>
  </div>
</template>


<script setup lang="ts">
  import { ref, computed } from 'vue'
  import Dropdown from './Dropdown.vue'
  import RangeDropDown from './RangeDropdown.vue'

  const keyword = ref('')
  const firstLabel = ref('')
  const secondLabel = ref('') 
  const minRange = ref(0)
  const maxRange = ref(10000)
  const isKeywordFocused = ref(false)
  const isChoice = computed(() => activeIndex.value !== null)

  // divider 를 없애기 위한 작업
  const hoverIndex = ref<number | null>(null)
  const activeIndex = ref<number | null>(null)

  // 드롭다운 위치 계산을 위한 ref
  const keywordRef = ref<HTMLElement>()
  const firstFilterRef = ref<HTMLElement>()
  const secondFilterRef = ref<HTMLElement>()

  interface Props {
    filters: string[]
    first_options: string[]
    second_options: string[]
    isRange: boolean
  }

  const props = defineProps<Props>()

  const onSelectFirst = function (value: string) {
    firstLabel.value = value
    activeIndex.value = null
  }

  const onSelectSecond = function (value: string) {
    secondLabel.value = value
    activeIndex.value = null
  }

  function getDropdownStyle(index: number) {
    const element = index === 1 ? firstFilterRef.value : secondFilterRef.value
    if (!element) return {}
    
    const rect = element.getBoundingClientRect()
    const searchBarRect = element.closest('.search-bar')?.getBoundingClientRect()
    
    if (!searchBarRect) return {}
    
    return {
      position: 'absolute',
      top: `${rect.height + 10}px`,
      left: `${rect.left - searchBarRect.left}px`,
      zIndex: 10
    }
  }

  const emit = defineEmits<{
    (e: 'search', payload: {
      keyword: string
      first: string
      second: string
      min: number
      max: number
    }): void
  }>()

  function emitSearch() {
    emit('search', {
      keyword: keyword.value,
      first: firstLabel.value,
      second: secondLabel.value,
      min: minRange.value,
      max: maxRange.value,
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

  .divider {
    width: 1px;
    height: 53px;
    background-color: #2E2E2E;
  }

  .divider.hidden {
    opacity: 0;
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

  /* 드롭다운 스타일 수정 */
  .dropdown-1,
  .dropdown-2 {
    position: absolute;
    z-index: 10;
  }
</style>
