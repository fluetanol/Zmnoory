<template>
  <div class="main-content-wrapper">
    <AppHeader></AppHeader>
  </div>
  <div v-if="video" class="video-wrapper">
    <div class="main-content">
      <video :src="video.videoUrl" controls playsinline></video>
      <span class="video-title">{{ video.title }}</span>
      <div class="uploader-info">
        <img :src="video.memberProfileImageUrl ? video.memberProfileImageUrl : baseProfile" alt="작성자 프로필 이미지">
        <div style="display: flex; flex-direction: column; gap: 2px;">
          <span class="uploader-nickname">{{ video.memberNickname }}</span>
          <span class="uploader-id">{{ video.memberEmail }}</span>
        </div>

        <div
          class="like-wrap"
          @click="onToggleLike"
          role="button"
          tabindex="0"
          :aria-pressed="liked"
          @keydown.enter.prevent="onToggleLike"
          @keydown.space.prevent="onToggleLike"
        >
          <span class="like-count">{{ likeCount }}</span>
          <img class="like-icon" :src="liked ? likeIcon : nonLikeIcon" alt="좋아요" />
        </div>
      </div>
      <div
        class="video-desc-wrapper"
        :class="{ expanded: isExpanded }"
        ref="descBox"
        @click="toggleDesc"
        role="button"
        tabindex="0"
        :aria-expanded="isExpanded"
      >
        <div class="first-row">
          <span class="video-createdat">{{ video.createdAt.slice(0, 10) }}</span>
          <span class="video-hashtag">#{{ video.gameTitle }}</span>
        </div>
        
        <span class="video-description">{{ video?.description }}</span>
        
        <div class="desc-fade" v-if="!isExpanded && isClamped"></div>
      </div>
      <hr style="width: 800px; height: 1px; margin: 20px 0;">
      <span class="video-title" style="margin: 0 0 15px 0;">댓글</span>
      <div class="comment-plus">
        <img :src="me?.profileImageUrl ? me.profileImageUrl : baseProfile" alt="thumbnail">
        <input type="text" v-model="newComment" placeholder="댓글 작성 ...">
        <button @click="addComment">댓글</button>
      </div>
      <div class="comment-list">
        <CommentCard
          v-for="comment in commentList"
          :comment="comment"
        ></CommentCard>
      </div>
    </div>
    <div class="side-content">
      <RouterLink
        v-for="sidevideo in sideVideos"
        :key="sidevideo.id"
        :to="{ name: 'VideoDetail', params: { id: sidevideo.id } }"
        style="text-decoration: none;"
      >
        <VideoCard :video="sidevideo" />
      </RouterLink>
    </div>
  </div>
  <SiteFooter></SiteFooter>
</template>

<script setup lang="ts">
  import AppHeader from '@/common/components/shared/AppHeader.vue';
  import SiteFooter from '@/common/components/shared/SiteFooter.vue';
  import { type MyMember, type Video } from '@/services/info';
  import { ref, onMounted, computed, nextTick } from 'vue'
  import { useRoute } from 'vue-router';
  import { useCommentStore } from '@/store/Comments';
  import { useVideoStore } from '@/store/Videos';
  import { useAccountStore } from '@/store/Accounts';
  import { useLikeStore } from '@/store/Likes';
  import CommentCard from '@/common/components/shared/CommentCard.vue';
  import VideoCard from '@/common/components/shared/VideoCard.vue';
  import likeIcon from '@/assets/like.png'
  import nonLikeIcon from '@/assets/non_like.png'
  import baseProfile from '@/assets/test_profile.png'

  const commentStore = useCommentStore()
  const videoStore = useVideoStore()
  const accountStore = useAccountStore()
  const likeStore = useLikeStore()

  const route = useRoute()
  const video = ref<Video | null>(null)
  const sideVideos = ref<Video[] | null>(null)
  const id = ref<number>(0)
  const newComment = ref<string>('')
  const me = ref<MyMember | null>(null)

  const isExpanded = ref(false)
  const isClamped = ref(false)
  const descBox = ref<HTMLElement | null>(null)

  const measureClamp = () => {
    const el = descBox.value
    if (!el) return
    if (!isExpanded.value) {
      isClamped.value = el.scrollHeight - el.clientHeight > 1
    }
  }

  const toggleDesc = () => {
    isExpanded.value = !isExpanded.value
    nextTick(measureClamp)
  }
  
  const liked = computed(() => !!likeStore.like_status)
  const likeCount = computed(() => likeStore.like_count ?? 0)
  
  const onToggleLike = async () => {
    if (!id.value) return
    try {
      await likeStore.toggleLike(id.value)
      
      await Promise.all([
        likeStore.isLike(id.value),
        likeStore.countLike(id.value),
      ])
    } catch (e) {
      console.log(e)
    }
  }

  onMounted(async () => {
    id.value = Number(route.params.id)

    await commentStore.getCommentList(id.value)
    await videoStore.getVideoDetail(id.value)
    await videoStore.getVideoList()
    await accountStore.getMyDetail()
    await likeStore.isLike(id.value)
    await likeStore.countLike(id.value)

    video.value = videoStore.video_detail
    sideVideos.value = videoStore.video_list
    me.value = accountStore.member_me

    await nextTick()
    measureClamp()
  })

  const commentList = computed(() => commentStore.comment_list ?? [])

  async function addComment() {
    if (!newComment.value?.trim()) return // 빈 댓글 방지

    commentStore.createComment(id.value, newComment.value)
    newComment.value = '' // 입력창 초기화
  }
</script>

<style scoped>
  .main-content-wrapper {
    max-width: 1440px;
    margin: 0 auto;
    width: 100%;
    padding: 0 20px;
    box-sizing: border-box;
  }
  
  .video-wrapper {
    display: flex;
    gap: 60px;
    padding: 40px 0;
    max-width: 1280px;
    margin: 0 auto;
    box-sizing: border-box;
  }

  .main-content {
    flex: 0 0 800px;
    display: flex;
    flex-direction: column;
  }

  .main-content video {
    width: 800px;
    height: 430px;
  }

  .video-title {
    color: #2E2E2E;
    font-family: Inter;
    font-size: 25px;
    font-style: normal;
    font-weight: 700;
    line-height: normal;
    margin-top: 22px;
  }

  .uploader-info {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-top: 10px;
    gap: 10px;
    width: 100%;
  }

  .uploader-info > img {
    width: 45px;
    height: 45px;
    border-radius: 50%;
  }

  .uploader-nickname {
    color: #2E2E2E;
    font-family: Inter, sans-serif;
    font-size: 15px;
    font-weight: 600;
  }

  .uploader-id {
    color: #a0a0a0;
    font-family: Inter, sans-serif;
    font-size: 13px;
    font-weight: 400;
  }

  .like-wrap {
    margin-left: auto;
    display: flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    user-select: none;
  }

  .like-icon {
    width: 20px;
  }

  .like-count {
    color: #2E2E2E;
    font-family: Inter, sans-serif;
    font-size: 14px;
    font-weight: 600;
  }

  .video-desc-wrapper {
    position: relative;
    line-height: 1.4;
    background-color: #FCF8F4;
    margin-top: 10px;
    line-height: 1.4;
    max-height: calc(1.4em * 4);
    overflow: hidden;
    overflow: hidden;
    white-space: pre-wrap;
    cursor: pointer;
    padding: 10px 15px;
    border-radius: 10px;
    display: flex;
    flex-direction: column;
  }

  .video-desc-wrapper.expanded {
    max-height: none;
    overflow: visible;
  }

  .desc-fade {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 1.5rem;                /* 페이드 높이 */
    /* 위쪽은 투명, 아래쪽은 배경색(흰색)에 가깝게 */
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0), #F9F1E9);
    pointer-events: none;
  }

  .first-row {
    display: flex;
    flex-direction: row;
    gap: 15px;
    margin-bottom: 5px;
  }

  .video-createdat {
    color: #2E2E2E;
    font-family: Inter, sans-serif;
    font-size: 15px;
    font-weight: 600;
  }

  .video-hashtag {
    color: #a0a0a0;
    font-family: Inter, sans-serif;
    font-size: 15px;
    font-weight: 400;
  }

  .video-description {
    color: #2E2E2E;
    font-family: Inter, sans-serif;
    font-size: 15px;
    font-weight: 400;
  }

  .comment-list {
    display: flex;
    flex-direction: column;
    gap: 25px;
  }

  .comment-plus {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-bottom: 32px;
    gap: 13px;
  }

  .comment-plus img {
    width: 45px;
    height: 45px;
    border-radius: 50%;
  }
  
  .comment-plus input {
    width: 642px;
    height: 27px;
    border: none;
    border-bottom: 1px solid #D9D9D9;
    outline: none;
    font-size: 20px;
    font-family: 'Inter', sans-serif;
    font-weight: 400;
    line-height: normal;
  }

  .comment-plus input::placeholder {
    color: #D9D9D9;
    font-family: 'Inter', sans-serif;
    font-size: 20px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
  }

  .comment-plus button {
    width: 70px;
    height: 27px;
    background-color: #F5E7DA;
    border: none;
    border-radius: 15px;
    color: #2E2E2E;
    font-family: Inter;
    font-size: 15px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
  }

  .side-content {
    flex: 0 0 392px;        /* 카드 폭에 맞춰 고정 */
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
  }
</style>