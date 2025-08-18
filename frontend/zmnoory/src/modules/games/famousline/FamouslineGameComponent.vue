<template>
  <div class="webrtc-container">
    <!-- 오버레이 UI 영역 -->
    <div class="ui-overlay">
      <div class="top-bar">
        <span class="info-text">🎬 명대사 따라하기</span>
        <span class="timer">{{ ref_formattedTime }}</span>
      </div>
      <div class="button-bar">
        <button v-if="ref_isGameStart" @click="onStart">게임 시작</button>
        <button v-if="ref_isStopRecord" @click="onStop">중지</button> 
      </div>
      
      <!-- 음량 레벨바 컨테이너 -->
      <div v-if ="ref_isVolumebar" class="bars-container">
        <div
          v-for="(bar, i) in bars"
          :key="i"
          class="bar"
          :style="{ height: bar + '%' }"
        ></div>
      </div>

      <div v-if="ref_isVolumebar"  class="level-meter">
        <div class="level-fill" :style="{ width: (levelPct * 100).toFixed(0) + '%' }"></div>
      </div>
      <div class="speaking-indicator" :class="{ on: isSpeaking }">
        {{ isSpeaking ? '🎤 Speaking' : '…' }}
      </div>

    <div v-show="ref_isVolumebar">
      <img
        v-show="!ref_isSpeaking"
        src="/anim/popcat_pause.png"
        class="talk-gif"
      />
      <img
        v-show="ref_isSpeaking"
        :src="ref_gifSrc"
        class="talk-gif"
      />
    </div>
    </div>

    
    <!-- WebRTC 비디오 -->
    <video ref="ref_video" autoplay playsinline muted class="webrtc-video"></video>
    <!-- 렌더링 캔버스 -->
    <canvas ref="ref_maincanvas" class="overlay-canvas"></canvas>


     <!-- 전체 파형 미니맵 -->
    <canvas v-show = "ref_isShowOverview" ref="ref_overviewWave" class="ref-overview" :class="{ 'fade-in': ref_overviewVisible }"></canvas>
    <!-- 원본 대사 음성 실시간 파형 캔버스 -->
    <canvas  v-show = "!ref_isShowOverview" ref="ref_refWave" class="ref-wave"   :class="{ 'is-morphing': ref_isMorphing }"></canvas>


      <!-- 원본 대사 영상 오버레이 -->
    <div v-if="ref_isShowClip" class="refclip-overlay">
      <img
        src = "/clips/thumbnail_takeEverything.png"
        class = "refclip-img"
        ></img>


      <video
        ref="ref_refClip"
        :src="Info_VideoClips[0].src"
        class="refclip-video"
        playsinline
        controls
      ></video>
    </div>

    <!-- (선택) 따라하기 카운트다운 표시 -->
    <div v-if="ref_countdown" class="countdown-overlay">
      {{ ref_countdown }}
    </div>

    <!-- 플레이가 녹화된 영상 -->
    <video v-if="ref_recordedVideoURL" :src="ref_recordedVideoURL" class="recorded-preview" 
    controls autoplay loop></video>
  </div>


    <audio v-if="ref_recordedAudioURL" :src="ref_recordedAudioURL" class = "recorded-preview" 
     preload="auto"></audio>
     
    <!-- 필요 시 다운로드 -->
    <a :href="ref_recordedAudioURL!" class="download-link"  download="player.webm">파일 저장</a>

</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick  } from 'vue'
import { MediaUtils } from './class/MediaUtils'
import type { VideoClipInfo } from './types/FamouslineTypes'
import type { ImagePayload } from '@/services/info';
// import type { ImagePayload } from '@/services/info';

// emit 객체들
const emit_Result = defineEmits<{
  (e:'emit_gameResult' , value : string) : void
  (e:'emit_s3url', value:string) : void
  (e:'emit_video', value:string):void
  (e:'emit_cropImages', value:ImagePayload[]):void
  (e:'emit_videoBlob', value:Blob):void
}>()

const props = defineProps<{
  isTest: boolean
}>()


const gifBase = '/anim/popcat.gif'
const ref_formattedTime = ref('00:00')

let stream : MediaStream | null = null;
let mediaRecorder : MediaRecorder | null = null;


/** 파형 저장소: 화면 폭(픽셀)과 1:1로 매핑된 진폭값(0..1) */
let waveStore: Float32Array | null = null


/** 개요 파형 렌더링용 컨텍스트/치수 */
let octx: CanvasRenderingContext2D | null = null
let O_W = 0, O_H = 0, O_cols = 0

/** 메타정보 */
let clipDuration = 0

// ✅ 플레이어(사용자) 오버레이 파형 저장/타이밍
let playerStore: Float32Array | null = null
let playerStartMs = 0
let playerTotalSec = 0


let ac: AudioContext | null = null
let micStream: MediaStream | null = null
let dest :MediaStreamAudioDestinationNode | null = null
let micRecorder : MediaRecorder | null = null
let micChunks: BlobPart[] = []

let src: MediaElementAudioSourceNode | null = null
let an: AnalyserNode | null = null
let rafId2: number | null = null

let audioCtx: AudioContext | null = null
let analyser: AnalyserNode | null = null
let sourceNode: MediaStreamAudioSourceNode | null = null
let rafId: number | null = null

let playerBlob: Blob[] | null = []

const Info_VideoClips : VideoClipInfo[] = [
  {
      src: '/clips/clip_takeEverything.mp4',
      line: '꼭 그렇게 다 가져가야만 속이 후련했냐!'
  }
]

// Hysteresis 기반 VAD(말하기 감지) 파라미터
const THRESHOLD_ON  = 4  // 말하기 시작 임계치 (정규화)


//ref 객체들
const ref_video = ref<HTMLVideoElement | null>(null)
const ref_maincanvas = ref<HTMLCanvasElement | null>(null)
const ref_recordedVideoURL = ref<string | null>(null)
const ref_recordVideoBlob = ref<Blob | null>(null)
const ref_refClip = ref<HTMLVideoElement | null>(null)
const ref_isShowClip = ref<boolean>(false)
const ref_countdown = ref<number | null>(0)

/**
 * 게임 중 음량바를 표시할 지 여부
 */
const ref_isVolumebar = ref<boolean>(false) 

/**
 * 게임 중 녹음을 중단할지에 대한 여부
 */
const ref_isStopRecord = ref<boolean>(false)

/** 
게임 시작 여부를 판단하는 변수
*/
const ref_isGameStart = ref<boolean>(true)


const ref_overviewWave = ref<HTMLCanvasElement|null>(null)

const ref_isShowOverview = ref<boolean>(false)

const ref_overviewVisible = ref(false) // 전체 파형이 화면에 자연스럽게 드러나는 타이밍 제어용

const ref_isMorphing = ref(false)

const ref_gifSrc = ref(gifBase)

const ref_isSpeaking = ref(false);

const ref_showPlayerOverlay = ref(false)   // ✅ 플레이어 파형 

const ref_recordedAudioURL = ref<string | null>(null);

// 레벨/말하기 상태
const levelPct = ref(0)     // 0.0 ~ 1.0

const isSpeaking = ref(false)

const bars = ref<number[]>(Array(10).fill(10)) // 초기 높이 10%

const ref_refWave  = ref<HTMLCanvasElement|null>(null)

onMounted(async () => {
  console.log(props.isTest ? "테스트 모드" : "실제 모드");
  try {
    stream = await MediaUtils.getUserMediastream({
      video: true,
      audio: true, // 이후 음성 녹음용
    }, ref_video.value);

    console.log('사용자 카메라 활성화 완료');
  } catch (err) {
    console.error('❌ 카메라 접근 실패:', err);
    alert('카메라 또는 마이크 권한이 필요합니다.');
  }
});
;

async function onStart(){
    ref_isShowClip.value = true;
    ref_isGameStart.value = false;
    await nextTick();

    //Clip재생
    await startReferenceWave();
    await MediaUtils.playVideoStream(ref_refClip.value, false);
    MediaUtils.stopVideoStream(ref_refClip, async () => {
      console.log("클립 재생 중지");
      ref_isShowClip.value = false;

      await nextTick();
      morphWaveToOverview(700) // 원하는 속도로 (ms)
      
      // ref_isShowOverview.value = true;
      //stopReferenceWave();
      startCountdown();
      
    });

    console.log("녹화 시작")
}

async function onStop(){
  if (RecordInterval) {
    clearInterval(RecordInterval);
    RecordInterval = null;
  }
  stopBarsMeter();
  await MediaUtils.finishRecord(mediaRecorder!);
  MediaUtils.connectBase64URL(ref_recordedVideoURL, ref_recordVideoBlob);

  drawOverviewAll(playerTotalSec)
}

//리셋함수
// function resetWaveComparison() {
//   playerStore = null
//   playerTotalSec = 0
//   ref_showPlayerOverlay.value = false
//   drawOverviewAll(0) // 원본만 표시
// }

function startCountdown(second : number = 3){
  ref_countdown.value = second;
    console.log("카운트다운 시작:", ref_countdown.value);
  const interval = setInterval(() => {
    // console.log("카운트다운:", ref_countdown.value);
    ref_countdown.value! -=1;
    if(ref_countdown.value! <= 0){
      clearInterval(interval);
      RecordingPlayerLine();
    }
  }, 1000);

}

// 버튼 클릭 시(=사용자 제스처) 오디오 컨텍스트 resume 필요
async function ensureAudioCtx() {
  if (!audioCtx) {
    audioCtx = new AudioContext()
  }
  if (audioCtx.state === 'suspended') {
    try { await audioCtx.resume() } catch {}
  }
}


async function startReferenceWave() {
  await nextTick()        
  const clip = ref_refClip.value
  const cvs  = ref_refWave.value
  const ocvs = ref_overviewWave.value
  if (!clip || !cvs || !ocvs) { console.warn('clip/canvas 없음'); return }

    // 메타(총 길이) 준비
  if (isNaN(clip.duration) || !clip.duration) {
    console.log("엥 왜 듀레이션이 안찍혀")
    await new Promise<void>(res => {
      const h = () => { clip.removeEventListener('loadedmetadata', h); res() }
      clip.addEventListener('loadedmetadata', h, { once: true })
    })
  }
  clipDuration = clip.duration || 0
  console.log("이제 구햇냐 ? ",clipDuration)
  setupOverviewCanvas();

  if (!ac) ac = new AudioContext()
  if (ac.state === 'suspended') await ac.resume()

  if (!src) src = ac.createMediaElementSource(clip)
  an = ac.createAnalyser()
  an.fftSize = 2048                 // 시간해상도↑
  an.smoothingTimeConstant = 0.7

  // 비디오 자체 소리는 음소거하고, 오디오컨텍스트로 재생(중복 방지)
  // clip.muted = true
  clip.volume = 1
  src.connect(an)
  src.connect(ac.destination)

  const dpr = window.devicePixelRatio || 1
  const W = (cvs.clientWidth  || 600) * dpr
  const H = (cvs.clientHeight || 96)  * dpr
  cvs.width = W; cvs.height = H
  const g = cvs.getContext('2d')!

  const midY = Math.floor(H/2)
  const td = new Uint8Array(an.fftSize) // time-domain

  // 색상(분홍 채우기 + 바닥 쪽 붉은 경계 느낌)
  const fillGrad = g.createLinearGradient(0, 0, 0, H)
  fillGrad.addColorStop(0.00, '#ff33cc')
  fillGrad.addColorStop(0.60, '#ff00aa')
  fillGrad.addColorStop(1.00, '#7a005a')

  const edgeColor = '#ff2747' // 아래쪽 얇은 붉은 라인 느낌

  // 칼럼(세로줄) 폭과 스크롤 속도
  const COL = Math.max(1, Math.round(2*dpr)) // 세로 2px 정도
  const SPEED = COL                           // 프레임당 오른쪽에 1칼럼 추가

  const draw = () => {
    if (!an) return
    an.getByteTimeDomainData(td)

    // 기존 그림을 왼쪽으로 스크롤
    const snap = g.getImageData(SPEED, 0, W - SPEED, H)
    g.putImageData(snap, 0, 0)
    g.clearRect(W - SPEED, 0, SPEED, H)

    // 한 프레임의 피크 진폭 계산(=overview 축적에도 사용)
    const samplesPerCol = Math.floor(td.length / 64) || 1
    let minV =  1, maxV = -1
    for (let i=0; i<samplesPerCol; i++) {
      const v = (td[i] - 128) / 128 // -1..1
      if (v < minV) minV = v
      if (v > maxV) maxV = v
    }
    // 진폭을 캔버스 높이로 매핑(살짝 과장)
    const amp = (Math.max(Math.abs(minV), Math.abs(maxV)) * 0.9)

    // 실시간 칼럼 그리기    
    const h   = Math.max(2*dpr, amp * (H/2))
    g.fillStyle = fillGrad
    g.fillRect(W - COL, midY - h, COL, h*2)

    // 아래쪽 가장자리 붉은 얇은 라인(원하시면 주석 해제)
    g.fillStyle = edgeColor
    g.fillRect(W - COL, midY + h - Math.max(1, Math.round(1*dpr)), COL, Math.max(1, Math.round(1*dpr)))

    accumulateOverview(amp, clip)
      // drawOverview(clip.currentTime)
      drawOverviewAll(clip.currentTime)

    rafId2 = requestAnimationFrame(draw)
  }
  draw()
}


async function startMicRecord() {
  if (!ac) ac = new AudioContext()
  micStream = await navigator.mediaDevices.getUserMedia({
    audio: { echoCancellation: true, noiseSuppression: true, autoGainControl: false }
  })
  const src = ac.createMediaStreamSource(micStream)
  const gain = ac.createGain()
  gain.gain.value = 1.0 // UI로 조절 가능

  dest = ac.createMediaStreamDestination()
  src.connect(gain)
  gain.connect(dest)

  micChunks = []
  // 브라우저 지원 코덱: audio/webm;codecs=opus 가 가장 호환 좋음
  micRecorder = new MediaRecorder(dest.stream, { mimeType: 'audio/webm;codecs=opus' })
  micRecorder.ondataavailable = (e) => { if (e.data?.size) micChunks.push(e.data) }
  micRecorder.start()
}


function stopReferenceWave() {
  if (rafId2) cancelAnimationFrame(rafId2), rafId2=null
  try { src && src.disconnect() } catch {}
  try { an  && an.disconnect() } catch {}
  an = null
  // ac는 다른 분석에 재사용 가능. 필요하면 ac?.close()
}


async function stopMicRecord(): Promise<Blob> {
  if (!micRecorder) throw new Error('not recording')
  const done = new Promise<Blob>((resolve) => {
    micRecorder!.onstop = () => resolve(new Blob(micChunks, { type: 'audio/webm' }))
  })
  micRecorder.stop()

  
  return await done
}


async function startBarsMeter(stream: MediaStream) {
  ref_isVolumebar.value =true;
  sourceNode = audioCtx!.createMediaStreamSource(stream)
  analyser = audioCtx!.createAnalyser()
  analyser.fftSize = 256
  sourceNode.connect(analyser)

  const bufferLength = analyser.frequencyBinCount
  const dataArray = new Uint8Array(bufferLength)

  const draw = () => {
    if (!analyser) return

    analyser.getByteFrequencyData(dataArray)

    // 전체 주파수대역을 막대 개수로 분할
    const chunkSize = Math.floor(bufferLength / bars.value.length)
    bars.value = bars.value.map((_, idx) => {
      const start = idx * chunkSize
      const end = start + chunkSize
      const slice = dataArray.slice(start, end)
      const avg = slice.reduce((a, b) => a + b, 0) / slice.length
      return Math.min(100, (avg / 255) * 100) // % 값
    })

    analyser.smoothingTimeConstant = 0.8
    analyser.fftSize = 2048
    const timeData = new Uint8Array(analyser.fftSize);
    analyser.getByteTimeDomainData(timeData);
    let sum = 0
    for (let i = 0; i < timeData.length; i++) {
      const v = (timeData[i] - 128) / 128     // -1 ~ +1 로 정규화
      sum += v * v
    }
    const rms = Math.sqrt(sum / timeData.length) // 0~1
    const levelPct = Math.min(1, rms * 1.4)      // 보기 좋게 스케일 조정
    const level100 = Math.round(levelPct * 100)  // 0~100 범위 정수

    if(level100 >= THRESHOLD_ON){
      // console.log("speak!", level100);
      ref_isSpeaking.value = true;
    }
    else{
      ref_isSpeaking.value =false
    }
    
    accumulatePlayerOverlay(rms);
    rafId = requestAnimationFrame(draw)
  }
  draw()
}

function stopBarsMeter() {
  if (rafId) cancelAnimationFrame(rafId)
  try { sourceNode?.disconnect() } catch {}
  try { analyser?.disconnect() } catch {}
  sourceNode = null
  analyser = null
  audioCtx?.close()
  audioCtx = null
  ref_isVolumebar.value = false
}

let RecordInterval : NodeJS.Timeout | null = null

async function RecordingPlayerLine(){
  console.log("녹화 시작")
  mediaRecorder = MediaUtils.startRecord(stream!, ref_recordVideoBlob);
  ref_isStopRecord.value = true;

  console.log("녹화 시작 :",ref_refClip.value)
  let totaltime= Math.round(clipDuration+0.5);
  
  // ✅ 오버레이 초기화
  initPlayerOverlay(totaltime)

  await ensureAudioCtx();
  await startMicRecord();
  await startBarsMeter(stream!);
  RecordInterval = setInterval(async () => {
    ref_formattedTime.value = "00:" + String(totaltime).padStart(2, "0");
    totaltime--;

    if (totaltime < 0 && mediaRecorder && mediaRecorder.state !== "inactive") {
      // stopLevelMeter()
      stopBarsMeter();
      await MediaUtils.finishRecord(mediaRecorder!);
      MediaUtils.connectBase64URL(ref_recordedVideoURL, ref_recordVideoBlob);
      clearInterval(RecordInterval!);
      console.log(" 녹화 종료");

      // 녹음 끝난 직후
      const recordBlob = await stopMicRecord();
      playerBlob?.push(recordBlob);

      const url = URL.createObjectURL(recordBlob);
      console.log("url : ,", url)
      ref_recordedAudioURL.value = url;
      emit_Result('emit_video', ref_recordedVideoURL.value!);
      emit_Result('emit_s3url', "");
      emit_Result('emit_gameResult', 'success');
      emit_Result('emit_cropImages', []); // 이미지 크롭은 현재 사용 안함
      emit_Result('emit_videoBlob', ref_recordVideoBlob.value!);
      
    }
  },1000);


}

function setupOverviewCanvas() {
  const cvs = ref_overviewWave.value
  if (!cvs) return
  const dpr = window.devicePixelRatio || 1
  const cssW = Math.max(300, cvs.clientWidth || 600)
  const cssH = Math.max(40,  cvs.clientHeight || 72)

  O_W = Math.round(cssW * dpr)
  O_H = Math.round(cssH * dpr)
  O_cols = O_W  // “한 픽셀 = 한 칼럼” 전략

  cvs.width  = O_W
  cvs.height = O_H
  octx = cvs.getContext('2d')

  // 기존 데이터를 새 폭으로 리샘플(없으면 신규)
  if (!waveStore) {
    waveStore = new Float32Array(O_cols)
  } 
  else if (waveStore.length !== O_cols) {
    const old = waveStore
    const next = new Float32Array(O_cols)
    for (let x = 0; x < O_cols; x++) {
      const t = x / (O_cols - 1)
      const ox = t * (old.length - 1)
      const i0 = Math.floor(ox)
      const i1 = Math.min(old.length - 1, i0 + 1)
      const a = ox - i0
      next[x] = old[i0] * (1 - a) + old[i1] * a
    }
    waveStore = next
  }
  // drawOverview(0) // 초기화
  drawOverviewAll(0);
}

// function resampleArray(src: Float32Array, newLen: number) {
//   const out = new Float32Array(newLen)
//   for (let x=0; x<newLen; x++){
//     const t = x/(newLen-1)
//     const ox = t*(src.length-1)
//     const i0 = Math.floor(ox), i1 = Math.min(src.length-1, i0+1)
//     const a = ox - i0
//     out[x] = src[i0]*(1-a) + src[i1]*a
//   }
//   return out
// }


function accumulateOverview(amp: number, clip: HTMLVideoElement) {
  if (!waveStore || !O_cols || !clipDuration) return
  const t = clip.currentTime / clipDuration
  const x = Math.max(0, Math.min(O_cols - 1, Math.floor(t * (O_cols - 1))))
  // 과거에 기록된 값보다 크면 갱신(피크 홀드)
  waveStore[x] = Math.max(waveStore[x], Math.max(0, Math.min(1, amp)))
}

// function drawOverview(currentTimeSec: number) {
//   if (!octx || !waveStore) return
//   const g = octx
//   g.clearRect(0, 0, O_W, O_H)

//   // 배경
//   g.fillStyle = '#0c0c0c'
//   g.fillRect(0, 0, O_W, O_H)

//   // 파형(위아래 대칭 채움)
//   g.beginPath()
//   const midY = Math.floor(O_H / 2)
//   g.moveTo(0, midY)
//   for (let x = 0; x < O_cols; x++) {
//     const v = waveStore[x] || 0
//     const h = Math.max(1, Math.round(v * (O_H / 2)))
//     g.lineTo(x, midY - h)
//   }
//   for (let x = O_cols - 1; x >= 0; x--) {
//     const v = waveStore[x] || 0
//     const h = Math.max(1, Math.round(v * (O_H / 2)))
//     g.lineTo(x, midY + h)
//   }
//   g.closePath()
//   g.fillStyle = '#b84cff' // 채움(원하시면 그라데이션으로 교체 가능)
//   g.fill()

//   // 진행 위치 커서
//   if (clipDuration > 0) {
//     const t = currentTimeSec / clipDuration
//     const cx = Math.max(0, Math.min(O_W - 1, Math.floor(t * (O_W - 1))))
//     g.fillStyle = 'rgba(255,255,255,0.8)'
//     g.fillRect(cx, 0, 2, O_H)
//   }
// }


onBeforeUnmount(() => {
  MediaUtils.disposeStream(ref_video.value?.srcObject as MediaStream);
  stopReferenceWave();
  console.log('스트림 정리 완료');
});


function getRectEvenIfHidden(el: HTMLElement): DOMRect {
  const prevDisplay = el.style.display
  const wasHidden = getComputedStyle(el).display === 'none'
  if (wasHidden) { el.style.visibility = 'hidden'; el.style.display = 'block' }
  const rect = el.getBoundingClientRect()
  if (wasHidden) { el.style.display = prevDisplay; el.style.visibility = '' }
  return rect
}

/** 녹음 시작 시 호출: 플레이어 오버레이 초기화 */
function initPlayerOverlay(totalSec: number) {
  // if (!ref_overviewWave.value) return
  if (!O_cols) setupOverviewCanvas()         // 폭 계산이 아직이면 세팅
  playerTotalSec = Math.max(0.01, totalSec)
  playerStartMs = performance.now()
  playerStore = new Float32Array(O_cols)     // 0으로 초기화
  ref_showPlayerOverlay.value = true
}

/** 실시간 RMS(0..1)를 현재 진행도에 매핑해서 누적(피크-홀드) */
function accumulatePlayerOverlay(amp01: number) {
  if (!playerStore || !O_cols || playerTotalSec <= 0) return

  const elapsed = (performance.now() - playerStartMs) / 1000

  const t = Math.min(1, elapsed / playerTotalSec)

  const x = Math.max(0, Math.min(O_cols - 1, Math.floor(t * (O_cols - 1))))

  playerStore[x] = Math.max(playerStore[x], Math.max(0, Math.min(1, amp01)))

  drawOverviewAll(elapsed)   // 매 프레임 리렌더
}



function smoothArray(src: Float32Array, passes = 1): Float32Array {
  if (!src) return src
  let out = src
  for (let p=0; p<passes; p++){
    const next = new Float32Array(out.length)
    for (let i=0;i<out.length;i++){
      const a = out[Math.max(0, i-1)]
      const b = out[i]
      const c = out[Math.min(out.length-1, i+1)]
      next[i] = (a + 2*b + c) / 4  // 간단한 1D 스무딩
    }
    out = next
  }
  return out
}


// 안전 클램프
const clamp01 = (x:number) => Math.max(0, Math.min(1, x))
const PLAYER_DRAW_SCALE = 2.5

/** 개요(원본 + 플레이어 오버레이 + 진행 커서) 그리기 */
function drawOverviewAll(playerElapsedSec: number) {
  if (!octx) return
  const g = octx
  g.clearRect(0, 0, O_W, O_H)

  // 1) 배경
  g.fillStyle = '#0c0c0c'
  g.fillRect(0, 0, O_W, O_H)

  const midY = Math.floor(O_H / 2)

  // 2) 원본(참조) 파형 채움 (waveStore에 이미 축적됨)
  if (waveStore) {
    g.beginPath()
    g.moveTo(0, midY)
    for (let x = 0; x < O_cols; x++) {
      const v = waveStore[x] || 0
      const h = Math.max(1, Math.round(v * (O_H / 2)))
      g.lineTo(x, midY - h)
    }
    for (let x = O_cols - 1; x >= 0; x--) {
      const v = waveStore[x] || 0
      const h = Math.max(1, Math.round(v * (O_H / 2)))
      g.lineTo(x, midY + h)
    }
    g.closePath()
    g.fillStyle = '#a14cff'                // 원본 영역 컬러
    g.globalAlpha = 0.85
    g.fill()
    g.globalAlpha = 1
  }

  // 3) 플레이어 오버레이(라인)
  if (ref_showPlayerOverlay.value && playerStore) {
     const smoothed = smoothArray(playerStore, 1)
      // 대칭 폴리곤 경로 구성
      g.beginPath()
      g.moveTo(0, midY)
      for (let x = 0; x < O_cols; x++) {
          const v = smoothed[x] || 0                  // 0..1

          const vv = clamp01(v * PLAYER_DRAW_SCALE)   // 배율 시키기 (일부러 보정하는 거)

          const h = Math.max(1, Math.round(vv * (O_H/2)))

          g.lineTo(x, midY - h)                       // 위쪽 라인
      }
      for (let x = O_cols - 1; x >= 0; x--) {
        const v = smoothed[x] || 0

        const vv = clamp01(v * PLAYER_DRAW_SCALE) // 배율 시키기 (일부러 보정하는 거)

        const h = Math.max(1, Math.round(vv * (O_H/2)))
        g.lineTo(x, midY + h)                       // 아래쪽 라인
      }
        g.closePath()
        // 겹침 시 시인성 좋은 색감(원본 위에 반투명 핫핑크)
        g.save()
        g.globalAlpha = 0.75
        g.fillStyle = '#ff2bbf'
        g.fill()
        g.restore()
        // 테두리 얇게 그어주면 윤곽 또렷
        g.lineWidth = Math.max(1, Math.round((window.devicePixelRatio||1)))
        g.strokeStyle = '#ff2bbf'
        g.stroke()
  }

  // 4) 진행 커서 (플레이어 기준)
  if (playerTotalSec > 0 && playerElapsedSec >= 0 && playerElapsedSec <= playerTotalSec) {
      const t = Math.min(1, playerElapsedSec / playerTotalSec)
      const cx = Math.floor(t * (O_W - 1))
      g.fillStyle = 'rgba(255,255,255,0.06)'
    g.fillRect(cx, 0, O_W - cx, O_H)  // 오른쪽 영역 살짝 톤다운
  }
}

//****** */회심의 필살기 모핑 애니메이션*********/

async function morphWaveToOverview(durationMs = 600) {
  const src = ref_refWave.value as HTMLCanvasElement | null
  const dst = ref_overviewWave.value as HTMLCanvasElement | null
  const container = document.querySelector('.webrtc-container') as HTMLElement | null
  if (!src || !dst || !container) return

  // 1) 좌표 계산 (컨테이너 기준)
  const cRect = container.getBoundingClientRect()
  const sRect = src.getBoundingClientRect()
  const dRect = getRectEvenIfHidden(dst)

  const sX = sRect.left - cRect.left
  const sY = sRect.top  - cRect.top
  const dX = dRect.left - cRect.left
  const dY = dRect.top  - cRect.top

  const scaleX = dRect.width  / Math.max(1, sRect.width)
  const scaleY = dRect.height / Math.max(1, sRect.height)

  // 2) “유령 레이어”로 쓰기 위해 실시간 캔버스를 클론 (시각만 복제)
  const ghost = src.cloneNode(false) as HTMLCanvasElement
  // 현재 그려진 픽셀을 그대로 복사(시각적으로 자연스럽게)
  // 캔버스 내용을 복사하려면 drawImage로 찍습니다.
  ghost.width  = src.width
  ghost.height = src.height
  const gctx = ghost.getContext('2d')
  const sctx = src.getContext('2d')

  if (gctx && sctx) gctx.drawImage(src, 0, 0)

  // 시작 위치에 배치
  ghost.classList.add('morph-layer')
  Object.assign(ghost.style, {
    left: `${sX}px`,
    top:  `${sY}px`,
    width:  `${sRect.width}px`,
    height: `${sRect.height}px`,
    transform: 'translate(0px,0px) scale(1,1)',
    borderRadius: getComputedStyle(src).borderRadius || '8px',
    boxShadow: '0 8px 24px rgba(0,0,0,0.35)',
  })

  // 3) DOM에 붙이고 애니메이션 시작
  container.appendChild(ghost)

  // 전체 파형은 일단 숨김 → 모핑 거의 끝날 때 페이드 인
  const oldShow = ref_isShowOverview.value
  ref_isShowOverview.value = false
  ref_overviewVisible.value = false
  ref_isMorphing.value =true
  src.style.visibility = 'hidden'

  // 리플로우 강제 후 트랜스폼 적용
  void ghost.offsetWidth
  ghost.style.transitionDuration = `${durationMs}ms`
  ghost.style.transform = `translate(${dX - sX}px, ${dY - sY}px) scale(${scaleX}, ${scaleY})`
  ghost.style.borderRadius = getComputedStyle(dst).borderRadius || '8px'
  ghost.style.boxShadow = '0 12px 32px rgba(0,0,0,0.5)'

  // 4) 타이밍: 끝나기 살짝 전에 overview를 켜고 페이드인
  const fadeDelay = Math.max(0, durationMs - 200)
  window.setTimeout(() => {
      setupOverviewCanvas();   
    ref_isShowOverview.value = true
    // 다음 틱에 페이드인 클래스 온
    requestAnimationFrame(() => { ref_overviewVisible.value = true })
  }, fadeDelay)

  // 5) 애니메이션 종료 처리
  const onEnd = () => {
    ghost.removeEventListener('transitionend', onEnd)
    ghost.remove()
    ref_isMorphing.value = false
    
    // 원본 실시간 파형은 Vue가 v-show로 이미 숨겨둠(=ref_isShowOverview true)
    // 혹시 모핑 전에 보여지고 있었다면 안전하게 가려주기
    if (!oldShow) {
      // 실시간 파형 숨김 유지
    }
  }
  ghost.addEventListener('transitionend', onEnd)
}







</script>

<style scoped>
.webrtc-container {
  position: relative;
  width: 100%;
  aspect-ratio: 3/2;
  max-width:960px;
  margin:0 auto;
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
    background-color: black;

      --overview-w: clamp(360px, 70%, 820px);  /* 전체 파형 가로폭 */
  --overview-bottom: 12%;                  /* 컨테이너 높이 대비 하단 여백 */
}

/* 비디오는 바닥에 위치 */
.webrtc-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 렌더링 캔버스는 비디오 위에 */
.overlay-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

/* UI 오버레이 */
.ui-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  pointer-events: none; /* UI 버튼은 pointer-events를 켜야 함 */
  z-index: 2;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  color: white;
  font-weight: bold;
  font-size: 20px;
}

.button-bar {
  margin-top: auto;
  display: flex;
  gap: 12px;
  justify-content: center;
  pointer-events: auto;
}

button {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid white;
  border-radius: 8px;
  padding: 8px 16px;
  cursor: pointer;
  backdrop-filter: blur(4px);
  transition: background 0.3s;
}
button:hover {
  background: rgba(255, 255, 255, 0.3);
}

.recorded-preview {
  position: absolute;
  right: 12px;
  bottom: 12px;
  width: 320px;           /* 필요 시 크기 조절 */
  aspect-ratio: 16 / 9;
  background: #000;
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.35);
  z-index: 3;             /* .ui-overlay(2)와 캔버스보다 위 */
  pointer-events: auto;   /* 컨트롤 클릭 가능 */
}


.download-link{
  color: white;
  text-decoration: none;
  border: 1px solid white;
  border-radius: 8px;
  padding: 8px 16px;
  backdrop-filter: blur(4px);
  transition: background 0.3s;
}
.download-link:hover{
  background: rgba(255, 255, 255, 0.3);
}


.countdown-overlay {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  font-size: clamp(48px, 8vw, 120px);
  font-weight: 800;
  color: #fff;
  text-shadow: 0 6px 24px rgba(0,0,0,0.45);
  z-index: 3;
  pointer-events: none;
}

/* 참조 영상 오버레이 */
.refclip-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.75);
  display: grid;
  place-items: center;
  z-index: 2;
}

.refclip-video {
  width: 0;
  height : 0;
  background: #000;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0,0,0,0.5);
}

.refclip-img {
  width: min(90vw, 960px);
  aspect-ratio: 16 / 9;
  background: #000;
  opacity: 0.5;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0,0,0,0.5);
}

.bars-container {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 40px; /* 막대 최대 높이 */
  width: 120px;
}
.bar {
  flex: 1;
  background: linear-gradient(to top, #4ade80, #facc15, #f87171);
  transition: height 0.08s ease-out;
}

.ref-wave{
  position:absolute;
  left:16px; right:16px; bottom:16px;
  height:96px;
  background:#000;
  border-radius:8px;
  z-index: 3;          /* <- 추가 */
  pointer-events: none;/* UI 클릭 방해 방지(선택) */
}

.ref-overview{
  position:absolute;
  left:50%;
  transform: translateX(-50%);
  bottom: var(--overview-bottom);
  width: var(--overview-w);
  height:72px;

  background:#0c0c0c;
  border-radius:8px;
  z-index: 5;
  pointer-events: none;
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.06);
  opacity: 0;
  transition: opacity 320ms ease;
}

/* 모핑에 쓸 공통 레이어 스타일 */
.morph-layer {
  position: absolute;        /* 컨테이너 기준으로 이동 */
  transform-origin: top left;
  will-change: transform, border-radius, box-shadow, opacity;
  z-index: 7;                /* 맨 위로 띄워서 자연스럽게 */
  /* 아래 트랜지션은 JS에서 duration만 바꿔도 됨 */
  transition:
    transform 600ms cubic-bezier(.22,.61,.36,1),
    border-radius 600ms cubic-bezier(.22,.61,.36,1),
    box-shadow 600ms cubic-bezier(.22,.61,.36,1),
    opacity 300ms ease;
}


/* 전체 파형 페이드 인 */
.ref-overview {
  opacity: 0;
  transition: opacity 320ms ease;
}
.ref-overview.fade-in {
  opacity: 1;
}

/* 모핑 동안 원본 파형은 안 보이게 */
.ref-wave.is-morphing {
  opacity: 0;                 /* 시각적으로 완전 숨김 */
  visibility: hidden;         /* Safari 잔상 방지 */
  pointer-events: none;
}


</style>
