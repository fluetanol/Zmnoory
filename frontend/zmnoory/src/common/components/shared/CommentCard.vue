<template>
  <div class="comment-container">
    <img class="comment-user-thumbnail" :src="comment.memberProfileImageUrl" alt="user">
    <div class="comment-info">
      <div>
        <span class="comment-nickname">{{ comment.memberNickname }}</span>
        <span class="comment-createdat">{{ comment.createdAt.slice(0, 10) }}</span>
      </div>
      <span class="comment-context">{{ comment.content }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
  import type { Comment } from '@/services/info';

  defineProps<{
    comment: Comment
  }>()
</script>

<style scoped>
  .comment-container {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  .comment-info {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin-left: 20px;
  }

  .comment-nickname {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 15px;
    font-style: normal;
    font-weight: 600;
    line-height: normal;
  }

  .comment-createdat {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 8px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    margin-left: 7px;
  }

  .comment-context {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 12px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
  }

  .comment-user-thumbnail {
    width: 45px;
    height: 45px;
    border-radius: 50%;
  }
</style>