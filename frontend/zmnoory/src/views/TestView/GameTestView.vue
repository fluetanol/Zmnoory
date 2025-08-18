<!-- [FILEPATH] src/views/emoji/EmojiGameView.vue -->
<!-- 이모지 게임이 띄워질 간단한 예시 뷰입니다. (실제 서비스에선 이거 쓰면 안됨) -->
<template>
  <main class="emoji-game-view">

          <GameComponent
          :is-test="true"/>
  </main>
</template>

<script setup lang="ts">
import { defineAsyncComponent } from 'vue'

const GameComponent = defineAsyncComponent(() =>
  import('@/modules/games/emoji/EmojiGameComponent.vue')
)

console.log("loaded EmojiGameView.vue")

</script>

<style scoped>
.emoji-game-view {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: flex-start;
  gap: 2rem;
  padding: 2rem;
  height: 100%;
  box-sizing: border-box;
}

.video-section {
  flex-shrink: 0;
}


</style>
