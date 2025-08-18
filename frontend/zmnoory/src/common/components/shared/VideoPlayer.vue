<!-- VideoPlayer.vue -->
<template>
  <div
    class="video-container"
    ref="wrapperRef"
    :class="{ fullscreen: isFullscreen }"
    @mouseenter="playerControlsVisibility(true)"
    @mouseleave="playerControlsVisibility(false)"
  >
    <video
      class="video-container__video"
      ref="videoRef"
      controls
      playsinline
      :src="props.videoPlay"
      @timeupdate="onTimeUpdate"
      @play="isPaused = false"
      @pause="isPaused = true"
      @loadedmetadata="onLoadedMeta"
    >
      <source :src="videoPlay" type="video/mp4" />
    </video>

    <div class="video-container__controls" :class="{ visible: controlsVisible }">
      <div class="progress" ref="progressBarRef" @click="onProgressClick">
        <div class="progress__current" :style="{ width: progressPercent + '%' }"></div>
      </div>

      <button class="control control--backward" @click="seekBy(-10)">
        <i class="fas fa-backward"></i>
      </button>

      <button class="control control--play" :class="{ paused: isPaused }" @click="togglePlay">
        <i class="fas fa-play"></i>
        <i class="fas fa-pause"></i>
      </button>

      <button class="control control--stop" @click="stop">
        <i class="fas fa-stop"></i>
      </button>

      <button class="control control--forward" @click="seekBy(10)">
        <i class="fas fa-forward"></i>
      </button>

      <button class="control control--replay" @click="replay">
        <i class="fas fa-sync"></i>
      </button>

      <button class="control control--volume" :class="{ muted: volume <= 0 }">
        <div class="control--volume__button" @click="toggleMute">
          <i class="fas fa-volume-off"></i>
          <i class="fas fa-volume-up"></i>
        </div>
        <input
          class="control--volume__slider"
          ref="volumeSliderRef"
          type="range"
          min="0"
          max="1"
          step="0.01"
          v-model.number="volume"
          @input="onVolumeInput"
        />
      </button>

      <button class="control control--fullscreen" @click="toggleFullscreen">
        <i class="fas fa-expand"></i>
        <i class="fas fa-compress"></i>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, withDefaults, defineProps } from 'vue'

const props = withDefaults(defineProps<{ videoPlay: string }>(), {})

const wrapperRef = ref<HTMLDivElement | null>(null)
const videoRef = ref<HTMLVideoElement | null>(null)
const progressBarRef = ref<HTMLDivElement | null>(null)
const volumeSliderRef = ref<HTMLInputElement | null>(null)

const controlsVisible = ref(false)
const isPaused = ref(true)
const isFullscreen = ref(false)
const volume = ref(1)
const progressPercent = ref(0)

// mount 시 초기 볼륨/풀스크린 이벤트 연결
onMounted(() => {
  if (videoRef.value) videoRef.value.volume = volume.value

  const syncFs = () => {
    isFullscreen.value = document.fullscreenElement === wrapperRef.value
  }
  document.addEventListener('fullscreenchange', syncFs)
  document.addEventListener('webkitfullscreenchange' as any, syncFs)
  document.addEventListener('mozfullscreenchange' as any, syncFs)

  onBeforeUnmount(() => {
    document.removeEventListener('fullscreenchange', syncFs)
    document.removeEventListener('webkitfullscreenchange' as any, syncFs)
    document.removeEventListener('mozfullscreenchange' as any, syncFs)
  })
})

// volume 반응
watch(volume, (v) => {
  if (videoRef.value) videoRef.value.volume = Math.max(0, Math.min(1, v))
})

function playerControlsVisibility(v: boolean) {
  controlsVisible.value = v
}

function togglePlay() {
  const v = videoRef.value
  if (!v) return
  if (isPaused.value) void v.play()
  else v.pause()
  isPaused.value = !isPaused.value
}

function stop() {
  const v = videoRef.value
  if (!v) return
  v.pause()
  v.currentTime = 0
  isPaused.value = true
}

function replay() {
  const v = videoRef.value
  if (!v) return
  v.currentTime = 0
  isPaused.value = false
  void v.play()
}

function seekBy(sec: number) {
  const v = videoRef.value
  if (!v) return
  const dur = isFinite(v.duration) ? v.duration : Number.POSITIVE_INFINITY
  v.currentTime = Math.max(0, Math.min(dur, v.currentTime + sec))
}

function toggleMute() {
  volume.value = volume.value > 0 ? 0 : 1
}

function onVolumeInput() {
  // class 바인딩은 volume으로 자동 처리됨
}

function onTimeUpdate() {
  const v = videoRef.value
  if (!v || !isFinite(v.duration) || v.duration <= 0) return
  progressPercent.value = (100 / v.duration) * v.currentTime
}

function onProgressClick(e: MouseEvent) {
  const v = videoRef.value
  const bar = progressBarRef.value
  if (!v || !bar || !isFinite(v.duration) || v.duration <= 0) return
  const rect = bar.getBoundingClientRect()
  const ratio = (e.clientX - rect.left) / rect.width
  v.currentTime = Math.max(0, Math.min(v.duration, ratio * v.duration))
}

function toggleFullscreen() {
  const el = wrapperRef.value
  if (!el) return
  const anyEl = el as any
  const anyDoc = document as any
  if (!isFullscreen.value) {
    if (el.requestFullscreen) el.requestFullscreen()
    else if (anyEl.webkitRequestFullscreen) anyEl.webkitRequestFullscreen()
    else if (anyEl.mozRequestFullScreen) anyEl.mozRequestFullScreen()
    else if (anyEl.msRequestFullscreen) anyEl.msRequestFullscreen()
  } else {
    if (document.exitFullscreen) document.exitFullscreen()
    else if (anyDoc.webkitExitFullscreen) anyDoc.webkitExitFullscreen()
    else if (anyDoc.mozCancelFullScreen) anyDoc.mozCancelFullScreen()
    else if (anyDoc.msExitFullscreen) anyDoc.msExitFullscreen()
  }
}

function onLoadedMeta() {
  progressPercent.value = 0
}
</script>

<style scoped>
*{ box-sizing:border-box; }

.video-container{
  position:relative;
  height:500px;
  display:inline-flex;
}

.video-container.fullscreen{
  height:100%;
  width:100%;
  background:black;
  display:flex;
}
.video-container.fullscreen .control--fullscreen .fa-expand{ display:none; }
.video-container.fullscreen .control--fullscreen .fa-compress{ display:inline-flex; }

.video-container__video{
  height:100%;
  position:relative;
  z-index:1;
}
.video-container__video::-webkit-media-controls{ display:none !important; }

.video-container__controls{
  position:absolute;
  z-index:2;
  bottom:0;
  width:100%;
  display:flex;
  align-items:center;
  padding-top:40px;
  padding-left:10px;
  padding-right:10px;
  height:80px;
  background:linear-gradient(0deg,rgba(0,0,0,.8) 0, rgba(0,0,0,.35) 60%, transparent);
  opacity:0;
  transition:all 150ms ease;
}
.video-container__controls.visible{ opacity:1; }

.video-container__controls .progress{
  width:calc(100% - 20px);
  cursor:pointer;
  height:24px;
  position:absolute;
  left:0;
  top:20px;
  margin-left:10px;
  margin-right:10px;
}
.video-container__controls .progress::before{
  content:'';
  position:absolute;
  top:50%;
  left:0;
  transform:translateY(-50%);
  width:100%;
  height:4px;
  background:rgba(255,255,255,.35);
}

.video-container__controls .progress__current{
  position:absolute;
  top:50%;
  left:0;
  height:4px;
  background:rgba(255,152,0,1);
  z-index:2;
  transform:translateY(-50%);
}
.video-container__controls .progress__current::before{
  content:'';
  display:block;
  position:absolute;
  right:0;
  top:50%;
  transform:translate(50%, -50%);
  border-radius:50%;
  border:4px solid #fff;
  width:6px;
  height:6px;
  background:#ff9800;
}

.control{
  border:0;
  padding:0;
  background:transparent;
  outline:none;
  margin-right:15px;
  cursor:pointer;
  width:20px;
  display:flex;
  align-items:center;
  justify-content:center;
}
.control:hover i{ color:rgba(255,152,0,1); }
.control i{ font-size:16px; color:#fff; }

.control--play .fa-pause{ display:inline-flex; }
.control--play .fa-play{ display:none; }
.control--play.paused .fa-play{ display:inline-flex; }
.control--play.paused .fa-pause{ display:none; }

.control--volume{ width:unset; }

.control--volume__button{
  width:20px;
  display:flex;
  justify-content:flex-start;
}
.control--volume__button .fa-volume-up{ display:inline-flex; }
.control--volume__button .fa-volume-off{ display:none; }

.control--volume__slider{
  display:flex;
  opacity:0;
  -webkit-appearance:none;
  width:90px;
  margin-left:10px;
}
.control--volume__slider::-webkit-slider-runnable-track{
  width:100%;
  height:4px;
  cursor:pointer;
  background:rgba(0,0,0,.5);
}
.control--volume__slider::-webkit-slider-thumb{
  -webkit-appearance:none;
  margin-top:-5px;
  height:14px;
  width:4px;
  border-radius:0;
  background-color:rgba(255,152,0,1);
  cursor:pointer;
}
.control--volume__slider:focus{ outline:none; }
.control--volume__slider:hover{ opacity:1; }
.control--volume:hover .control--volume__slider{ opacity:1; }

.control--volume.muted .fa-volume-up{ display:none; }
.control--volume.muted .fa-volume-off{ display:inline-flex; }

.control--fullscreen{
  margin-left:auto;
  margin-right:0;
}
.control--fullscreen .fa-compress{ display:none; }
</style>
