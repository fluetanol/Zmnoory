<!-- [FILEPATH] src/common/modals/GameModal.vue -->
<template>
  <div class="modal">
    <div class = "input-field">
    <BaseInput class="modal-input" v-model="title" :type="'text'" :placeholder="'제목'"></BaseInput>
    <!-- <BaseInput class="modal-input2" v-model="description" :type="'text'" :placeholder="'영상 설명'"></BaseInput> -->
    <BaseTextArea class="modal-input2" v-model="description" :placeholder="'영상 설명'"></BaseTextArea>
    </div>
    <div class="file-input-wrapper">
      <span class="file-name">{{ fileName || '썸네일 이미지' }}</span>
      <label class="upload-button">
        업로드
        <input type="file" accept=".png, .jpg" class="file-input-hidden" @change="handleFileChange" />
      </label>
    </div>

    <BaseButton class="base-button" @click="submitVideo">영상 추가</BaseButton>
  </div>
</template>

<script setup lang="ts">
  import BaseInput from '../components/shared/BaseInput.vue'
  import { ref } from 'vue'
  import type { ImagePayload, ParticipateReward} from '@/services/info'
  import { useParticipationStore } from '@/store/Participations'
  import { useVideoStore } from '@/store/Videos'
  import { useAccountStore } from '@/store/Accounts'
import BaseButton from '../components/shared/BaseButton.vue'
import BaseTextArea from '../components/shared/BaseTextArea.vue'
import axios from 'axios'


  const props = defineProps<{
    email: string
    gameTitle: string
    videoUrl: string
    thumbnailUrl : string
    videoObjectKey: string
    thumbnailObjectKey: string
    videoBlob : Blob,
    imageCrops :ImagePayload[]
  }>()

const emit = defineEmits<{
  (e: 'emit_uploadfinish', value: number): void
  (e: 'close'): void
  (e: 'refresh'): void
}>()

  const participationStore = useParticipationStore()
  const videoStore = useVideoStore()
  const accountStore = useAccountStore()

  const fileName = ref<string>('')

  const title = ref('')
  const description = ref('')
  const thumbnailFile = ref<File | null>(null)


  const handleFileChange = (e: Event) => {
    const target = e.target as HTMLInputElement
    const file = target.files?.[0]
    if (file) {
      fileName.value = file.name
      thumbnailFile.value = file
    }
  }


async function uploadToS3(blob:Blob, presigned_url:string, contentType:string) : Promise<boolean>{
  try{
    const res = await axios.put(presigned_url, blob, {
      headers:{
        'Content-Type' : contentType,
      }
    })
    console.log("s3 업로드 성공", res)
  }catch(error)
  {
    console.error('S3 업로드 실패', error);
    return false;
  }
  return true;
}

  const submitVideo = async () => {
      if (!thumbnailFile.value) {
        alert('썸네일 이미지를 업로드해주세요')
        return
      }

      console.log("videoUrl ",props.videoUrl, " thumbnailUrl " , props.thumbnailUrl, ' blob ',props.videoBlob);
      console.log("videoObjectKey:", props.videoObjectKey);
      console.log("thumbnailObjectKey:", props.thumbnailObjectKey);
      

      //커뮤니티용 영상 S3업로드
      await uploadToS3(props.videoBlob, props.videoUrl, 'video/mp4');
      //썸네일 파일 S3업로드 (File타입은 blob을 상속받아서 동작합니다)
      await uploadToS3(thumbnailFile.value, props.thumbnailUrl, 'image/jpeg');

      // const base64Thumbnail = reader.result as string
      const payload: ParticipateReward = {
        email: props.email,
        gameTitle: props.gameTitle,
        videoObjectKey: props.videoObjectKey,
        thumbnailObjectKey: props.thumbnailObjectKey,
        title: title.value,
        description: description.value,
        isPublic : true
      }
      console.log("gameEnd payload:", payload);
      await participationStore.gameEnd(payload)

      // 리워드 지급 후 사용자 정보 업데이트
      await accountStore.getMyDetail()

      const videoId = await participationStore.rewardResponse?.videoId
      console.log(participationStore.rewardResponse)

      videoStore.uploadImages({
        videoId : videoId!,
        images : props.imageCrops
      })
      
      emit("emit_uploadfinish", videoId!);
    
  }
</script>

<style scoped>
  .modal {
    width: 36.5625rem;
    height: 30.6875rem;
    flex-shrink: 0;
    background: #FFF;
    border: 2px solid black;
    border-radius: 5px;
    padding: 24px 48px;

    justify-content:  center;
    align-items: center;
  }


  .modal-input {
    margin-bottom: 15px;
    width: 100%;
  }
  
  .modal-input2 {
    margin-bottom: 15px;
    height: 190px;
    width: 100%;
  }
  
  .drop-input {
    width: 215px;
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
    width: 100%;
    height: 55px;
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
    width: 100%;
    height: 55px;
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

</style>