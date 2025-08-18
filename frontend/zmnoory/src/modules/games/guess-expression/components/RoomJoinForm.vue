<template>
  <div class="center-card">
    <div class="card card-center-content pregame-card">
      <div class="card-header">
        <h2>방 참가</h2>
        <p class="subtitle">방 코드를 입력하고 닉네임을 정해 입장하세요</p>
      </div>
      <form @submit.prevent="handleSubmit" class="room-form">
        <div class="input-row">
          <label for="roomCode">방 코드</label>
          <div class="input-group">
            <span class="input-icon">#</span>
            <input
              id="roomCode"
              type="text"
              v-model="roomCode"
              maxlength="10"
              placeholder="예) ABC123"
              autocomplete="off"
            />
          </div>
        </div>
        <div class="input-row">
          <label for="nickname">닉네임</label>
          <div class="input-group">
            <input
              id="nickname"
              type="text"
              v-model="nickname"
              placeholder="표시할 이름"
              autocomplete="off"
            />
            <button
              type="button"
              class="random-button"
              @click="generateRandomNickname"
            >
              랜덤
            </button>
          </div>
        </div>
        <button type="button" class="enter-button" @click="handleJoin">
          입장하기
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ADJECTIVES, NOUNS } from "@/constants/nicknameData";

// Props
const props = defineProps({
  initialRoomCode: {
    type: String,
    default: "",
  },
  initialNickname: {
    type: String,
    default: "",
  },
});

// Emits
const emit = defineEmits(["join-room"]);

// 반응형 데이터
const roomCode = ref(props.initialRoomCode);
const nickname = ref(props.initialNickname);

// 랜덤 닉네임 생성
const generateRandomNickname = () => {
  const adj = ADJECTIVES[Math.floor(Math.random() * ADJECTIVES.length)];
  const noun = NOUNS[Math.floor(Math.random() * NOUNS.length)];
  nickname.value = `${adj} ${noun}`;
};

// 폼 제출 처리
const handleSubmit = () => {
  if (!roomCode.value || !nickname.value) return;
  handleJoin();
};

// 방 입장 처리
const handleJoin = () => {
  if (!roomCode.value || !nickname.value) {
    alert("방 코드와 닉네임을 입력해주세요.");
    return;
  }

  emit("join-room", {
    roomCode: roomCode.value,
    nickname: nickname.value,
  });
};
</script>

<style scoped>
@import "../styles/RoomJoinForm.css";
</style>
