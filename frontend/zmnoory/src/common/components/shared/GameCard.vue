<!-- [FILEPATH] src/common/components/shared/GameCard.vue -->
<template>
  <div class="game-card-wrapper">
    <div class="game-header">
      <span class="title">{{ game.title }}</span>
      <span class="reward">{{ game.point }}P</span>
    </div>
    <div class="game-card">
      <img :src="game.thumbnail" alt="game thumbnail">
    </div>
    <div class="game-info">
      <div class="game-details">
        <span v-if="game.difficulty" class="difficulty">{{ game.difficulty }}</span>
        <span class="datatype">플레이어님의 "{{ game.requireDataType }}" 데이터를 수집합니다</span>
      </div>
      <div class="participate" :class="{ done: participationStatus === 'COMPLETED', undone: participationStatus === 'NOT_PARTICIPATED' }">
        <span>{{ participationStatus === 'COMPLETED' ? '참여완료' : '미참여' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import type { Game } from '@/services/info'

  defineProps<{
    game: Game
    participationStatus: 'COMPLETED' | 'NOT_PARTICIPATED'
  }>()
</script>

<style scoped>
  .game-card-wrapper {
    width: 100%;
    height: 340px;
    display: flex;
    flex-direction: column;
    /* 부드러운 애니메이션 */
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }

  @media (hover: hover) and (pointer: fine) {
    .game-card-wrapper:hover {
      transform: translateY(-4px) scale(1.03);
    }

    .game-card-wrapper:hover .game-card {
      box-shadow: 0 8px 15px rgba(0, 0, 0, 0.25);
    }
  }

  .game-header {
    width: 100%;
    margin-bottom: 10px;
    padding: 0 5px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .game-card {
    width: 100%;
    height: 260px;
    flex-shrink: 0;
    border-radius: 15px;
    background: #F5F5F5;
    display: flex;
    justify-content: center;
    align-items: center;
    filter: drop-shadow(0 5px 5px rgba(0, 0, 0, 0.25));
    overflow: hidden;
    position: relative;
  }

  .game-card img {
    height: 100%;
    width: auto;
    object-fit: cover;
    display: block;
  }

  .participate {
    width: 62px;
    height: 20px;
    flex-shrink: 0;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .participate.done {
    background: #00D722;
  }

  .participate.undone {
    background: #E81313;
  }

  .participate span {
    color: #FFF;
    text-align: center;
    font-family: Inter;
    font-size: 12px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
  }

  .game-info {
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    width: 100%;
    margin-top: 14px;
    padding: 0 5px;
  }

  .game-details {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .title {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
  }

  .reward {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 18px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
  }

  .difficulty {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    margin-bottom: 4px;
  }

  .datatype {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 14px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
  }
</style>