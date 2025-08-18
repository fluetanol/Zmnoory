<!-- [FILEPATH] src/common/modals/GameModal.vue -->
<template>
  <div class="modal">
    <button class="close-btn" @click="emit('close')" aria-label="닫기">×</button>

    <BaseInput class="modal-input" v-model="title" :type="'text'" :placeholder="'게임 제목'"></BaseInput>
    <div class="drop-input-box">
      <VerticalPicker
        style="width: 215px;"
        v-model="difficulty"
        :options="['EASY', 'MEDIUM', 'HARD', 'VERY_HARD']"
      />
      <input class="drop-input" v-model="requireDataType" type="text" placeholder="데이터"></input>
    </div>
    <BaseInput class="modal-input" v-model="point" :type="'text'" :placeholder="'획득 포인트'"></BaseInput>
    <BaseInput class="modal-input" v-model="desc" :type="'text'" :placeholder="'게임 설명'" maxlength="50"></BaseInput>
    <BaseInput class="modal-input" v-model="explain" :type="'text'" :placeholder="'플레이 방법'" maxlength="50"></BaseInput>
    <div class="file-input-wrapper">
      <span class="file-name">{{ fileName || '썸네일 이미지' }}</span>
      <label class="upload-button">
        업로드
        <input type="file" accept=".png, .jpg" class="file-input-hidden" @change="handleFileChange" />
      </label>
    </div>

    <BaseButton class="base-button" @click="submitGame">{{ props.game ? '게임 수정' : '게임 추가' }}</BaseButton>
  </div>
</template>

<script setup lang="ts">
  import BaseInput from '../components/shared/BaseInput.vue'
  import VerticalPicker from '../components/shared/VerticalPicker.vue'
  import { ref, watch } from 'vue'
  import type { GameRegisterPayload, Game } from '@/services/info'
  import { useGameStore } from '@/store/Games'
import BaseButton from '../components/shared/BaseButton.vue'

  const props = defineProps<{
    game?: Game | null
  }>()

  const emit = defineEmits<{
    (e: 'close'): void
    (e: 'success'): void
  }>()

  const gameStore = useGameStore()

  const fileName = ref<string>('')

  const title = ref('')
  const desc = ref('')
  const explain = ref('')
  const difficulty = ref('EASY')
  const point = ref('')
  const thumbnail = ref<File | null>(null)
  const requireDataType = ref('')

  // 한글 난이도를 영어로 변환하는 함수
  const convertDifficultyToEnglish = (koreanDifficulty: string): string => {
    const difficultyMap: Record<string, string> = {
      '쉬움': 'EASY',
      '보통': 'MEDIUM', 
      '어려움': 'HARD',
      '매우 어려움': 'VERY_HARD',
      // 이미 영어인 경우 그대로 반환
      'EASY': 'EASY',
      'MEDIUM': 'MEDIUM',
      'HARD': 'HARD',
      'VERY_HARD': 'VERY_HARD'
    }
    return difficultyMap[koreanDifficulty] || 'EASY'
  }

  // 폼 데이터 초기화 함수
  const initializeFormData = () => {
    if (props.game) {
      title.value = props.game.title
      desc.value = props.game.description
      explain.value = props.game.explanation || ''
      // 난이도는 영어로 변환해서 저장
      difficulty.value = convertDifficultyToEnglish(props.game.difficulty)
      point.value = props.game.point.toString()
      requireDataType.value = props.game.requireDataType || ''
      fileName.value = '기존 썸네일'
    } else {
      // 등록 모드일 때는 모든 필드 초기화
      title.value = ''
      desc.value = ''
      explain.value = ''
      difficulty.value = 'EASY'
      point.value = ''
      requireDataType.value = ''
      fileName.value = ''
      thumbnail.value = null
    }
  }

  // props.game이 변경될 때마다 폼 데이터 업데이트 (즉시 실행 포함)
  watch(() => props.game, () => {
    initializeFormData()
  }, { immediate: true })

  const handleFileChange = (e: Event) => {
    const target = e.target as HTMLInputElement
    const file = target.files?.[0]
    if (file) {
      fileName.value = file.name
      thumbnail.value = file
    }
  }

  const submitGame = async () => {
    // 수정 모드일 때는 새 썸네일 없어도 됨
    if (!props.game && !thumbnail.value) {
      alert('썸네일 이미지를 업로드해주세요')
      return
    }

    const processGameData = async (base64Thumbnail?: string) => {
      let payload: GameRegisterPayload

      if (props.game && !base64Thumbnail) {
        // 수정 모드이고 새 썸네일이 없는 경우: 기존 썸네일 유지
        payload = {
          title: title.value,
          description: desc.value,
          explanation: explain.value,
          difficulty: difficulty.value,
          point: Number(point.value) ?? 0,
          thumbnail: props.game.thumbnail,
          requiredDataType: requireDataType.value,
        }
      } else {
        // 등록 모드이거나 새 썸네일이 있는 경우
        payload = {
          title: title.value,
          description: desc.value,
          explanation: explain.value,
          difficulty: difficulty.value,
          point: Number(point.value) ?? 0,
          thumbnail: base64Thumbnail || '',
          requiredDataType: requireDataType.value,
        }
      }

      try {
        const isLocalDev = window.location.hostname === 'localhost'
        if (props.game) {
          // 수정 모드
          await gameStore.gameUpdate(props.game.id, payload, isLocalDev)
          alert('게임이 성공적으로 수정되었습니다!')
        } else {
          // 등록 모드
          await gameStore.gameRegister(payload, isLocalDev)
          alert('게임이 성공적으로 등록되었습니다!')
        }
        emit('success')
        emit('close')
      } catch (err: any) {
        console.error(err)
        const errorMessage = err?.response?.data?.body?.data?.thumbnail || 
                           err?.response?.data?.message || 
                           err?.message || 
                           '처리 중 오류가 발생했습니다.'
        alert(`게임 ${props.game ? '수정' : '등록'} 실패: ${errorMessage}`)
      }
    }

    if (thumbnail.value) {
      const reader = new FileReader()
      reader.onload = async () => {
        const base64Thumbnail = reader.result as string
        await processGameData(base64Thumbnail)
      }
      reader.readAsDataURL(thumbnail.value)
    } else {
      await processGameData()
    }
  }
</script>

<style scoped>
  .modal {
    position: relative;
    flex-shrink: 0;
    background: #FFF;
    border: 1px solid black;
    padding: 40px;
  }

  .modal-input {
    margin-bottom: 20px;
  }

  .drop-input {
    width: 215px;
    height: 65px;
    border: 1px solid #A0A0A0;
    color: #2E2E2E;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    padding-left: 18px;
    margin-bottom: 20px;
  }

  .drop-input-box {
    display: flex;
    gap: 11px;
  }

  .file-input {
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
    margin-bottom: 20px;
  }

  .base-button {
    width: 441px;
    height: 73px;
    background: #F5E7DA;
    color: #2E2E2E;
    border: 1px solid #A0A0A0;
    text-align: center;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 700;
    line-height: normal;
    cursor: pointer;
  }

  .file-input-wrapper {
    width: 441px;
    height: 65px;
    border: 1px solid #A0A0A0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 18px;
    font-family: Inter;
    font-size: 20px;
    color: #A0A0A0;
    margin-bottom: 20px;
    box-sizing: border-box;
  }

  .file-name {
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    color: #A0A0A0;
    font-family: Inter;
    font-size: 20px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
  }

  .upload-button {
    background: #F5E7DA;
    border: none;
    padding: 8px 12px;
    cursor: pointer;
    font-weight: bold;
    font-size: 16px;
    color: #333;
    white-space: nowrap;
    border: 1px solid #a0a0a0;
  }

  .file-input-hidden {
    display: none;
  }

  .close-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    border: none;
    background: none;
    font-size: 20px;
    font-weight: bold;
    cursor: pointer;
    color: #2E2E2E;
  }
</style>