<!-- [FILEPATH] src/common/components/shared/CheckInput.vue -->
<!-- 상위 폴더에서 type="원하는거" placeholder='원하는거' 하면됨 -->
<!-- 상위 폴더에서 @click-event 라고 쓰면 사용 가능 -->
<template>
  <div>
    <input :type="type" :placeholder="placeholder" class="base-input">
    <button class="check-button" @click="$emit('clickEvent')">중복확인</button>
  </div>
</template>

<script setup>
  defineProps (
    {
      type: String,
      placeholder: String
    }
  )
</script>

<style scoped>
  .base-input {
    width: 441px;
    height: 65px;
    border: 1px solid #A0A0A0;
    color: #A0A0A0;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    padding-left: 18px;
  }

  .check-button {
    width: 116px;
    height: 65px;
    background: #F5E7DA;
    border: 1px solid #A0A0A0;
    color: #2E2E2E;
    text-align: center;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 700;
    line-height: normal;
    margin-left: 23px;
  }
</style>