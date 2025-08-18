<!-- [FILEPATH] src/modules/emojigame/components/WEBRTCVideo3D.vue -->
<template>
  <div class="webrtc-container">
    <div class = "UIoverlay">
      <div id = "time-display">{{ ref_formatTime }}</div>
      <button @click="resetParticipate"> participate reset </button>
    </div>

    <video ref="ref_video" autoplay muted playsinline></video>
    <canvas ref="ref_threeCanvas" class="three-canvas"></canvas>
    <canvas ref="ref_detectCanvas" class= "three-canvas debug-border"></canvas>
    <div v-if="!ref_isFaceDetected" class="game-over-overlay">
    <p>😃 {{ ref_recognizeText }}</p>
    </div>
    <div v-if="ref_countdown !== null" class="countdown-overlay">
      <p style="font-size: 48px; font-weight: bold;">{{ ref_countdown }}</p>
    </div>
    
      <!-- 종료 UI 오버레이 -->
    <div v-if="ref_gameOver" class="game-over-overlay">
      <h1>🎉 게임 종료 🎉</h1>
      <p>도착을 축하합니다!</p>
      <div  v-show ="ref_isSuccess">
        <!-- <BaseButton @click="restart">다시 시작</BaseButton> -->
        <BaseButton @click="exit">나가기</BaseButton>
      </div>
    </div>

          <!-- 종료 UI 오버레이 -->
    <div v-if="ref_timeOver" class="game-over-overlay">
      <h1> 실패 ㅠㅠ </h1>
      <p>시간초과 되셨습니다! </p>
        <div v-show ="ref_isSuccess">
          <BaseButton @click="restart">다시 시작</BaseButton>
          <BaseButton @click="exit">나가기</BaseButton>
       </div>
    </div>

      <div v-if="ref_capturedImage" class="captured-preview">
        <h3>캡처된 이미지:</h3>
        <img :src="ref_capturedImage" alt="Captured face" />
      </div>
  </div>

</template>

<script lang="ts" setup>
import { onMounted, ref, onBeforeUnmount } from 'vue'
import {
  Box3,
} from 'three'
import type {  EmojiImageInfo, State_Current } from "./types/emojiTypes";
import type { FaceExpressions} from 'face-api.js'
import { CameraManager } from './class/CameraManager'
import { GameUtils } from './class/GameUtils'
import { PlayerManager } from './class/PlayerManager'
import { GameManager } from './class/GameManager'
import { SceneManager } from './class/SceneManager'
import { EmojiManager} from './class/EmojiManager'
import { MediaManager } from './class/MediaManager'
import { useAccountStore } from '@/store/Accounts';
import { useParticipationStore } from '@/store/Participations';
import type { ImagePayload, MyMember } from '@/services/info';
import { nextTick } from 'process';
import axios from 'axios';
import { useGameStore } from '@/store/Games';
import BaseButton from '@/common/components/shared/BaseButton.vue';



let faceapi: typeof import('face-api.js')

/* ----------------------------------- */
// 🎥 비디오/캔버스
const ref_video = ref<HTMLVideoElement | null>(null)
const ref_threeCanvas = ref<HTMLCanvasElement | null>(null)
const ref_detectCanvas = ref<HTMLCanvasElement | null>(null)

// ⏱ 시간/텍스트 관련
const ref_formatTime = ref("00:00")
const ref_countdown = ref<number | null>(null)

// 📷 이미지 / 영상 기록
const ref_capturedImage = ref<string | null>(null)
const ref_capturedImages = ref<ImagePayload[]>([])
const ref_recordedVideoURL = ref<string | null>(null)
const ref_normalRecordedVideoURL = ref<string | null>(null)

// 🎮 게임 상태
const ref_gameOver = ref(false)
const ref_timeOver = ref(false)
const ref_isFaceDetected = ref(false)

// 🧠 UX 메시지
const ref_recognizeText = ref("얼굴을 인식 중입니다... 시작을 위해 화면에 얼굴을 보여주세요")
const ref_isSuccess = ref(false);
//none
//update
//success
//fail
const emit_Result = defineEmits<{
  (e:'emit_gameResult' , value : string) : void
  (e:'emit_cropImages', value:ImagePayload[]):void
  (e:'emit_video', value:string):void
  (e: 'emit_videoBlob', value:Blob) : void
}>()


const props = defineProps<{
  isTest : boolean
}>()

// 🎮 Three.js 기본 구성
let canvas: HTMLCanvasElement
let detectcanvas : HTMLCanvasElement

// 🎮 감정 상태 관련 변수
let expressdelta = 0
let expressions: FaceExpressions
let dirtycheck = false

let temp_myInfo:MyMember | null = null;
let temp_myEmail:string
let game_name = "이모지 게임"

// 🧍‍♂️ 에이전트 상태
let updateId : number;
let detectId :number;
let start_interval : NodeJS.Timeout;

let cameraManager : CameraManager
let playerManager : PlayerManager
let sceneManager :SceneManager;
let emojiManager : EmojiManager;
let gameManager : GameManager;
let mediaManager : MediaManager
/* -----------------------------------*/

const accountStore = useAccountStore();
const participationStore  = useParticipationStore();
const gameStore = useGameStore();

/*DOM CALLBACK FUNCTION*/
async function resetParticipate(){
  await participationStore.gameDeleteAll();
  restart();
}

function restart() {
  location.reload()  // 간단하게 새로고침
}

function exit(){
  emit_Result("emit_gameResult", "success");
}

function showCropImage(info_image: EmojiImageInfo){
      ref_capturedImage.value = info_image.image
      ref_capturedImages.value.push({
        fileName : info_image.imageName+".png",
        data : info_image.image
      })
}

//화면 크기 바뀔 때 마다 게임 화면 크기 조정함
function resizeRendererToDisplaySize (){
  console.log("resize");
  const container = ref_threeCanvas.value?.parentElement
  if (!container || !ref_threeCanvas.value || !sceneManager.renderer) return;

  const width = container.clientWidth;
  const height = container.clientHeight;

  // renderer 크기 재설정
  sceneManager.renderer.setSize(width, height, false);
  // camera 비율도 맞추기
  cameraManager._camera.aspect = width / height;
  cameraManager._camera.updateProjectionMatrix();
}

function resizeCanvasToDisplaySize() {
  const dpr = window.devicePixelRatio || 1
  const width = detectcanvas.clientWidth
  const height = detectcanvas.clientHeight

  // 💡 픽셀 해상도 보정
  detectcanvas.width = width * dpr
  detectcanvas.height = height * dpr

  const ctx = detectcanvas.getContext('2d')
  ctx?.scale(dpr, dpr)
  dims.height = detectcanvas.height
  dims.width = detectcanvas.width
}


/* Vue LifeCycle*/
onMounted(async () => {
    emit_Result("emit_gameResult", "none");
    await importFaceModel();
    await loginCheck();
    await awake();

    console.log("isTest ",props.isTest);

    window.addEventListener('resize', resizeRendererToDisplaySize);

    nextTick(() =>{
        resizeRendererToDisplaySize();
        resizeCanvasToDisplaySize();
    } ); // 최초 1회 보정

    if (ref_video.value) {
      ref_video.value.srcObject = mediaManager.stream
      ref_video.value.onloadeddata = () => {
      detect()
    }
  }

  console.log("test ver? : ",props.isTest);
  }
)

onBeforeUnmount(() => {
  // 스트림 정리
  sceneManager.renderer?.dispose()
  mediaManager.stopRecording();
  mediaManager.dispose();
  sceneManager.dispose();
  cancelAnimationFrame(updateId)
  cancelAnimationFrame(detectId)
  clearInterval(start_interval) 
  console.log("onbefore amount ",sceneManager.renderer.info.memory);
})

async function importFaceModel(){
    [faceapi] = await Promise.all([
      import('face-api.js')
    ])
    await Promise.all([
      faceapi.nets.tinyFaceDetector.load('/models/'),
      faceapi.nets.faceExpressionNet.load('/models/')
    ])
}


async function loginCheck(){
  console.log(" flag ",accountStore.member_me?.optionalConsent);

  if(accountStore.member_me == null){
    console.warn("계정 로그인 실패, 잘못된 동작")
    temp_myInfo = null;
  }
  else if(!props.isTest){
    temp_myInfo = accountStore.member_me!;
    temp_myEmail = temp_myInfo.email!
    gameStore.getGameDetail(1);
    console.log(".타이틀!!!!!! " ,gameStore.game_detail?.title);
    game_name = gameStore.game_detail?.title!;
    
    await participationStore.gameStart({
      email: temp_myEmail,
      gameTitle: game_name
    })
  }
  else{
    console.log("사용자가 정보 제공 동의를 하지 않아 데이터를 수집하지 않음") 
  }
}

let detectctx;
const dims = {
  width : 0,
  height : 0
}


/*Three.js Game LifeCycle*/
async function awake(){

  GameUtils.resetClock();
  //캔버스 셋팅
  canvas = ref_threeCanvas.value!
  detectcanvas = ref_detectCanvas.value!
  detectctx =detectcanvas.getContext('2d');
  dims.width = detectcanvas.width;
  dims.height = detectcanvas.height;
  console.log("detect +", detectctx)

  //각종 서비스 로직 초기화
  sceneManager = new SceneManager(canvas, 2, 600, 400);
  mediaManager = new MediaManager(600,400, sceneManager.renderer, ref_video.value!);
  emojiManager = new EmojiManager(sceneManager.list_obj_emojiface);
  cameraManager =  new CameraManager(sceneManager.camera, 15);
  playerManager = new PlayerManager(sceneManager.obj_agent);

  const list_States :State_Current[] =  sceneManager.makeEmojiStateCurrentList(emojiManager.list_obj_emoji_state);
  await mediaManager.initializeMedia();
  gameManager = new GameManager(sceneManager.obj_land, list_States, sceneManager, ref_timeOver);
  gameManager.callbackGameOver = GameOver;
  gameManager.callbackTimeOver = TimeOver;
  cameraManager.initializeCamera(sceneManager.obj_agent);
  playerManager.initializePlayer();
}

// 애니메이션 루프
function update() {
    const current_state : State_Current =  gameManager.state_current;
    GameUtils.timeDirtyChcek = true;

    cameraManager.update();
    if(!ref_gameOver.value && !ref_timeOver.value) updateTime();

    //player emoji change
    let flag = emojiManager.getCurrentEmotion(expressions,dirtycheck);
    if(flag != -1){
      let idx_atlas = EmojiManager.map_emoji_atlas_state.get(flag)!
      GameUtils.changeTexture(playerManager._obj_player,EmojiManager.list_tex_emoji[idx_atlas]);
      playerManager.state_emoji = flag;
    }

    let isCollision =  playerManager.isCollision(new Box3().setFromObject(current_state.current_wall));
    if(isCollision && playerManager.state_emoji == current_state.current_state_emoji.emojistate){
        let state = playerManager.state_emoji;
        emojiManager.list_emoji_count[state]++;
        //성공 후 통과
          //image crop
          if(ref_video.value){
            let ImageInfo : EmojiImageInfo = mediaManager.cropImage(current_state.current_state_emoji, gameManager.repeatCount);
              showCropImage(ImageInfo);
             // console.log("캡처 완료:", ref_capturedImage.value) // ✅ 출력 확인
          }

          //wall 이동 및 텍스처
          if(gameManager.repeatCount < gameManager.gameData.mapRepeat-1){
            const offset = 20;
            GameManager.offsetWall(current_state.current_group, sceneManager.obj_land, offset)
            const state = EmojiManager.randomizeEmojiTexture(current_state.current_state_emoji.emojiObj)
            EmojiManager.changeEmojiTexture(current_state, state);
            gameManager.repeatCount++;
          }
          //종료조건에 가까워 지는 경우 오브젝트를 안보이게 만들어서 최적화시키기
          else{
              current_state.current_group.visible = false;
              current_state.current_info.isActive = false;
              gameManager.repeatCount++;
          }
          gameManager.changeCurrentState();
    }
    else if(isCollision){
        playerManager.isKnockback = true;
    }

    playerManager.update();
    gameManager.update();
    sceneManager.update();
    mediaManager.update();
    updateId = requestAnimationFrame(update)
}


async function uploadVideoToS3(blob:Blob, presigned_url:string) : Promise<boolean>{
  try{
    const res = await axios.put(presigned_url, blob, {
      headers:{
        'Content-Type' :'video/webm',
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


const detect = async () => {
  if(expressdelta < gameManager.gameData.detectTick){
     expressdelta ++
  } 
  else{
    if (!ref_video.value) return
      const result = await faceapi.detectSingleFace(
      ref_video.value,
      new faceapi.TinyFaceDetectorOptions()
    ).withFaceExpressions()


    if (result) {
      // console.log('감지됨:', result.detection)
      const resizeResult = faceapi.resizeResults(result, dims)
       let box = resizeResult!.detection.box;
       detectctx!.clearRect(0,0, detectcanvas.width, detectcanvas.height)

      detectctx!.strokeStyle = 'red'
      detectctx!.lineWidth = 3
      detectctx!.strokeRect(box.x, box.y, box.width, box.height)
    // // 예: 텍스트도 표시 가능
      detectctx!.font = '20px'
     detectctx!.fillStyle = 'red'
     detectctx!.fillText('이 사람!', box.x, box.y - 10)

      expressions = result.expressions
      dirtycheck = true;

      if(!ref_isFaceDetected.value && !gameManager.gameStart){
        ref_isFaceDetected.value = true
        gameManager.gameStart = true
        startCountdown(3)
      }
      else if(!ref_isFaceDetected.value){
        ref_isFaceDetected.value = true
      }
    }
    else{
      ref_isFaceDetected.value = false;
      ref_recognizeText.value = "어디 가셨나요??? 빨리 돌아와~~~!!"
    }
    expressdelta = 0
  }
  detectId = requestAnimationFrame(detect)
}

function startCountdown(count : number){
  ref_countdown.value = count
  start_interval = setInterval(() => {
    if (ref_countdown.value !== null) {
      ref_countdown.value--

      if (ref_countdown.value === 0) {
        clearInterval(start_interval)
        ref_countdown.value = null

          sceneManager.group_finish.position.z = sceneManager.obj_land.position.z - 20
          sceneManager.group_finish.position.y = -4.5
          sceneManager.renderer.render(sceneManager.scene, cameraManager._camera)

          mediaManager.startRecording(50, ref_recordedVideoURL, ref_normalRecordedVideoURL)
          update();  // ⏰ 게임 시작
          console.log("on amount ",sceneManager.renderer.info.memory);
           sceneManager.group_finish.position.z = 100
           sceneManager.group_finish.position.y = 100
          console.log("게임 시작!")
      }
    }
  }, 1000)
}

function updateTime(){
  var typeTime = GameUtils.updateTime(gameManager.overTime);
  ref_formatTime.value = `${String(typeTime.minute).padStart(2, '0')}:${String(typeTime.second).padStart(2, '0')}`
}

// import { useS3UploadStore } from '@/store/UploadS3';
// const S3UploadStore = useS3UploadStore();

//정상 종료
async function GameOver(){
  ref_gameOver.value = gameManager.gameOver;
  playerManager.stop();
  gameManager.overtick++;
  cancelAnimationFrame(updateId)
  cancelAnimationFrame(detectId);
  detectctx!.clearRect(0,0, detectcanvas.width, detectcanvas.height)
  ref_isFaceDetected.value = false;

  if(gameManager.overtick > gameManager.overTickFrame){
    cancelAnimationFrame(gameManager.overId);
    mediaManager.stop();
    sceneManager.stop();

    if(temp_myInfo == null){
      console.warn("계정 정보가 없습니다. 게임 결과를 업로드할 수 없습니다.")
      emit_Result("emit_gameResult", "fail")
    }
    else if(props.isTest){
      console.warn("사용자가 정보 제공을 동의하지 않았습니다. 게임을 바로 종요합니다.")
      emit_Result("emit_gameResult", "fail")
    }
    else{
      await uploadS3();
    }
    return;
  }
  gameManager.overId = requestAnimationFrame(GameOver)
}

function TimeOver(){
    cancelAnimationFrame(updateId)
    mediaManager.stop();
    sceneManager.stop
    playerManager.stop();
    ref_timeOver.value = true;
}


async function uploadS3() : Promise<boolean>{
  await participationStore.createUrl({
        email:temp_myEmail,
        gameTitle:game_name,
        fileName:"emoji_video",
        contentType:"video/webm"
      })
    const s3_url = participationStore.presigned_url?.uploadUrl

    //원본 영상 업로드
    const blob = new Blob(mediaManager.normalRecordedChunks, {type:'video/webm'});
    const blob2 = new Blob(mediaManager.canvasRecordedChunks, {type:'video/webm'});

    // await S3UploadStore.uploadVideoToS3(blob, s3_url!);
    // let flag = S3UploadStore.uploadVideoResult;
    // console.log("flag ", flag);
    console.log("S3_url",s3_url)
    let flag = await uploadVideoToS3(blob, s3_url!);

      console.log("record  : ",ref_recordedVideoURL.value);
    if(flag == true){
      emit_Result("emit_cropImages", ref_capturedImages.value);
      emit_Result("emit_video", ref_recordedVideoURL.value!);
      emit_Result("emit_videoBlob", blob2);
      ref_isSuccess.value = true;
    }    
    else{
      emit_Result("emit_gameResult", "fail")
      console.log("잘 업로드 못 함")
    }

    return flag;
}



</script>

<style scoped>
.webrtc-container {
  position: relative;
  width: 100%;
  aspect-ratio: 3/2;
  max-width:960px;
  margin:0 auto;
  /* width: 600px;
  height: 400px; */
  overflow: hidden;
  border-radius: 8px;
  background-color: black;
}
video, .three-canvas {
  position: absolute;
  top :0;
  left : 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

video {
  z-index: 1;
}

.three-canvas{
  z-index : 2;
  pointer-events: none;
}



.game-over-overlay {
  position: absolute;
  top: 10%;
  left: 10%;
  width: 80%;
  height: 80%;
  background-color: rgba(0, 0, 0, 0.75);
  color: white;
  font-size: 1.5rem;
  line-height: 1.6; /* 👈 행간 여유 */
  text-align: center;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 10;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.2);
}

.game-over-overlay h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.game-over-overlay p {
  font-size: 1.25rem;
  margin-bottom: 2rem;
}

.game-over-overlay button {
  font-size: 1rem;
  padding: 0.8rem 1.6rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin:10px;
  transition: background-color 0.2s ease;
}


.captured-preview {
  position: absolute;
  bottom: 20px;
  left: 20px;
  background: rgba(255, 255, 255, 0.95);
  padding: 16px;
  z-index: 150;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  animation: fadeIn 0.5s ease-out;
  transition: all 0.3s ease;
  max-width: 500px;
}


.captured-preview h3 {
  margin: 0 0 10px 0;
  font-size: 1.2rem;
  color: #333;
  font-weight: bold;
}

.captured-preview img {
  width: 200px; /* 🔽 적당한 크기로 고정 */
  max-width: 100%;
  height: auto;
  display: block;
  border-radius: 8px;
  border: 2px solid #ccc;
  object-fit: contain;
  margin-top: 8px;
}

.captured-preview video {
  width: 250px; /* 🔽 적당한 크기로 고정 */
  max-width: 200%;
  height: auto;
  display: block;
  border-radius: 8px;
  border: 2px solid #ccc;
  object-fit: contain;
  margin-top: 8px;
}



.UIoverlay {
  position: absolute;
  top: 20px;
  right: 20px;
  color: white;
  font-size: 20px;
  font-family: 'Arial', sans-serif;
  z-index: 10;
  background: rgba(0, 0, 0, 0.4);
  padding: 8px 12px;
  border-radius: 8px;
}

.countdown-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 5rem;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 2px 2px 8px rgba(0,0,0,0.5);
  z-index: 999;
}

canvas.debug-border {
  border: 2px dashed limegreen; /* 또는 red, blue 등 */
  box-sizing: border-box;
  z-index: 9999; /* 맨 위로 올리고 싶을 때 */
}


@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

</style>
