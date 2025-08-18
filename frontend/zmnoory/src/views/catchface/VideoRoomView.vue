<!-- [FILEPATH] src/views/catchface/VideoRoomView.vue -->
<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import type { Ref } from 'vue';

interface Peer {
  id: string;
  pc: RTCPeerConnection;
  stream: MediaStream | null;
}

const localVideo: Ref<HTMLVideoElement | null> = ref(null);
const remoteVideos: Ref<Array<{ id: string; ref: HTMLVideoElement | null }>> = ref([]);
const peers: Ref<Peer[]> = ref([]);
const ws = ref<WebSocket | null>(null);
const localId = ref<string>('');
const roomCode = ref<string>(''); // Room code input
const isJoined = ref<boolean>(false); // Track join status
const localStream = ref<MediaStream | null>(null); // Store local stream
const delayedOffers: Ref<Array<any>> = ref([]); // Queue for delayed offers

const iceServers = [
  { urls: 'stun:stun.l.google.com:19302' },
];

async function initializeWebSocket() {
  try {
    ws.value = new WebSocket("ws://3.35.175.44:3000");
    //"ws://3.35.175.44:3000"
    //`ws://${location.hostname}:3000`
    //"ws://localhost:3000"
    ws.value.onmessage = async (event) => {
      const message = JSON.parse(event.data);
      
      switch (message.type) {
        case 'user-joined':
          if (message.id !== localId.value) {
            if (localStream.value) {
              await createPeerConnection(message.id, localStream.value);
            } else {
              console.warn('User joined before local stream is ready, delaying peer connection');
            }
          }
          break;
          
        case 'user-left':
          handleUserLeft(message.id);
          break;
          
        case 'offer':
          if (message.targetId === localId.value) {
            if (localStream.value) {
              await handleOffer(message);
            } else {
              console.warn('Offer received before local stream is ready, queuing offer');
              delayedOffers.value.push(message);
            }
          }
          break;
          
        case 'answer':
          if (message.targetId === localId.value) {
            await handleAnswer(message);
          }
          break;
          
        case 'ice-candidate':
          if (message.targetId === localId.value) {
            await handleIceCandidate(message);
          }
          break;
          
        case 'join-success':
          isJoined.value = true;
          await initializeLocalStream();
          // Process any queued offers after stream is ready
          while (delayedOffers.value.length > 0 && localStream.value) {
            await handleOffer(delayedOffers.value.shift());
          }
          break;
          
        case 'join-failure':
          alert('Invalid room code. Please try again.');
          ws.value?.close();
          break;
      }
    };

    ws.value.onopen = () => {
      localId.value = getLocalId();
    };

  } catch (error) {
    console.error('WebSocket initialization error:', error);
  }
}

async function initializeLocalStream() {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: true,
      audio: true,
    });
    localStream.value = stream;

    await nextTick();
    if (localVideo.value) {
      localVideo.value.srcObject = stream;
      localVideo.value.play().catch((error) => {
        console.error('Failed to play local video:', error);
      });
    } else {
      console.error('localVideo ref is not available after join');
    }
  } catch (error) {
    console.error('Local stream initialization error:', error);
  }
}

async function joinRoom() {
  if (!roomCode.value || ws.value?.readyState !== WebSocket.OPEN) return;

  ws.value?.send(JSON.stringify({
    type: 'join',
    roomId: roomCode.value,
    id: localId.value,
  }));
}

async function createPeerConnection(peerId: string, localStream: MediaStream) {
  const pc = new RTCPeerConnection({ iceServers });
  
  localStream.getTracks().forEach(track => {
    pc.addTrack(track, localStream);
  });

  pc.ontrack = (event) => {
    if (event.streams[0]) {
      if (!remoteVideos.value.find(v => v.id === peerId)) {
        remoteVideos.value.push({ id: peerId, ref: null });
      }
      
      const videoElement = document.getElementById(`remote-${peerId}`) as HTMLVideoElement;
      if (videoElement) {
        videoElement.srcObject = event.streams[0];
        videoElement.play().catch((error) => {
          console.error('Failed to play remote video:', error);
        });
      }
    }
  };

  pc.onicecandidate = (event) => {
    if (event.candidate && ws.value) {
      ws.value.send(JSON.stringify({
        type: 'ice-candidate',
        candidate: event.candidate,
        targetId: peerId,
        roomId: roomCode.value,
      }));
    }
  };

  pc.onconnectionstatechange = () => {
    if (pc.connectionState === 'disconnected' || pc.connectionState === 'closed') {
      handleUserLeft(peerId);
    }
  };

  peers.value.push({ id: peerId, pc, stream: null });

  const offer = await pc.createOffer();
  await pc.setLocalDescription(offer);
  
  ws.value?.send(JSON.stringify({
    type: 'offer',
    offer,
    targetId: peerId,
    roomId: roomCode.value,
  }));
}

async function handleOffer(message: any) {
  const pc = new RTCPeerConnection({ iceServers });
  if (localStream.value) {
    localStream.value.getTracks().forEach(track => {
      pc.addTrack(track, localStream.value!); // Non-null assertion since checked above
    });
  } else {
    throw new Error('Local stream not available for handleOffer');
  }

  pc.ontrack = (event) => {
    if (event.streams[0]) {
      if (!remoteVideos.value.find(v => v.id === message.id)) {
        remoteVideos.value.push({ id: message.id, ref: null });
      }
      
      const videoElement = document.getElementById(`remote-${message.id}`) as HTMLVideoElement;
      if (videoElement) {
        videoElement.srcObject = event.streams[0];
        videoElement.play().catch((error) => {
          console.error('Failed to play remote video:', error);
        });
      }
    }
  };

  pc.onicecandidate = (event) => {
    if (event.candidate && ws.value) {
      ws.value.send(JSON.stringify({
        type: 'ice-candidate',
        candidate: event.candidate,
        targetId: message.id,
        roomId: message.roomId,
      }));
    }
  };

  pc.onconnectionstatechange = () => {
    if (pc.connectionState === 'disconnected' || pc.connectionState === 'closed') {
      handleUserLeft(message.id);
    }
  };

  peers.value.push({ id: message.id, pc, stream: null });

  await pc.setRemoteDescription(new RTCSessionDescription(message.offer));
  const answer = await pc.createAnswer();
  await pc.setLocalDescription(answer);

  ws.value?.send(JSON.stringify({
    type: 'answer',
    answer,
    targetId: message.id,
    roomId: message.roomId,
  }));
}

async function handleAnswer(message: any) {
  const peer = peers.value.find(p => p.id === message.id);
  if (peer && peer.pc.signalingState !== 'stable') {
    await peer.pc.setRemoteDescription(new RTCSessionDescription(message.answer));
  } else {
    console.warn('Ignoring setRemoteDescription: Peer connection is in stable state');
  }
}

async function handleIceCandidate(message: any) {
  const peer = peers.value.find(p => p.id === message.id);
  if (peer && message.candidate) {
    try {
      await peer.pc.addIceCandidate(new RTCIceCandidate(message.candidate));
    } catch (e) {
      console.error('Error adding ICE candidate:', e);
    }
  }
}

function handleUserLeft(peerId: string) {
  const peerIndex = peers.value.findIndex(p => p.id === peerId);
  if (peerIndex !== -1) {
    peers.value[peerIndex].pc.close();
    peers.value.splice(peerIndex, 1);
  }

  const videoIndex = remoteVideos.value.findIndex(v => v.id === peerId);
  if (videoIndex !== -1) {
    const videoElement = document.getElementById(`remote-${peerId}`) as HTMLVideoElement;
    if (videoElement && videoElement.srcObject) {
      (videoElement.srcObject as MediaStream).getTracks().forEach(track => track.stop());
      videoElement.srcObject = null;
    }
    remoteVideos.value.splice(videoIndex, 1);
  }
}

function getLocalId() {
  return Math.random().toString(36).substring(2);
}

onMounted(() => {
  initializeWebSocket();
});

onUnmounted(() => {
  peers.value.forEach(peer => {
    peer.pc.close();
    if (peer.stream) {
      peer.stream.getTracks().forEach(track => track.stop());
    }
  });
  ws.value?.send(JSON.stringify({
    type: 'leave',
    roomId: roomCode.value,
    id: localId.value,
  }));
  ws.value?.close();
  if (localStream.value) {
    localStream.value.getTracks().forEach(track => track.stop());
  }
});
</script>

<template>
  <div class="video-room">
    <div v-if="!isJoined" class="join-container">
      <input
        v-model="roomCode"
        type="text"
        placeholder="Enter room code"
        class="room-code-input"
      />
      <button @click="joinRoom" class="join-button">Join Room</button>
    </div>
    <div v-else class="video-grid">
      <div class="video-container">
        <video ref="localVideo" autoplay muted playsinline></video>
        <span class="label">Local</span>
      </div>
      <div
        v-for="remote in remoteVideos"
        :key="remote.id"
        class="video-container"
      >
        <video
          :id="'remote-' + remote.id"
          autoplay
          playsinline
          ref="remote.ref"
        ></video>
        <span class="label">Participant {{ remote.id.slice(0, 4) }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.video-room {
  padding: 20px;
  height: 100vh;
  background-color: #f0f2f5;
}

.join-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
}

.room-code-input {
  padding: 8px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  width: 200px;
}

.join-button {
  padding: 8px 16px;
  font-size: 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.join-button:hover {
  background-color: #0056b3;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.video-container {
  position: relative;
  background-color: #000;
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16 / 9;
}

video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.label {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
}
</style>