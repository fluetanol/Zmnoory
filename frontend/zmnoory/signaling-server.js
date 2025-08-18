const WebSocket = require('ws');
const http = require('http');

const server = http.createServer();
const wss = new WebSocket.Server({ server });

// 방 관리
const rooms = new Map();

wss.on('connection', (ws) => {
  console.log('새로운 클라이언트 연결됨');
  
  let currentRoom = null;
  let currentParticipant = null;

  ws.on('message', (message) => {
    try {
      const data = JSON.parse(message);
      console.log('받은 메시지:', data);

      switch (data.type) {
        case 'join':
          handleJoin(ws, data, currentRoom, currentParticipant);
          break;
        case 'offer':
          handleOffer(ws, data, currentRoom);
          break;
        case 'answer':
          handleAnswer(ws, data, currentRoom);
          break;
        case 'ice_candidate':
          handleIceCandidate(ws, data, currentRoom);
          break;
        case 'data':
          handleData(ws, data, currentRoom);
          break;
        default:
          console.log('알 수 없는 메시지 타입:', data.type);
      }
    } catch (error) {
      console.error('메시지 처리 오류:', error);
    }
  });

  ws.on('close', () => {
    console.log('클라이언트 연결 해제됨');
    if (currentRoom && currentParticipant) {
      handleLeave(ws, currentRoom, currentParticipant);
    }
  });

  function handleJoin(ws, data, room, participant) {
    const { roomCode, participantName, role } = data;
    
    if (!rooms.has(roomCode)) {
      rooms.set(roomCode, new Map());
    }
    
    const roomParticipants = rooms.get(roomCode);
    const participantId = `${participantName}_${Date.now()}`;
    
    roomParticipants.set(participantId, {
      ws,
      name: participantName,
      role,
      id: participantId
    });
    
    currentRoom = roomCode;
    currentParticipant = participantId;
    
    console.log(`${participantName}이(가) 방 ${roomCode}에 참가함`);
    
    // 방의 다른 참가자들에게 새 참가자 알림
    roomParticipants.forEach((p, id) => {
      if (id !== participantId) {
        p.ws.send(JSON.stringify({
          type: 'participant_joined',
          participantId,
          participantName,
          roomCode
        }));
      }
    });
    
    // 새 참가자에게 기존 참가자들 목록 전송
    const existingParticipants = Array.from(roomParticipants.entries())
      .filter(([id]) => id !== participantId)
      .map(([id, p]) => ({ id, name: p.name }));
    
    ws.send(JSON.stringify({
      type: 'room_info',
      participants: existingParticipants,
      roomCode
    }));
  }

  function handleOffer(ws, data, room) {
    const { participantId, offer, roomCode } = data;
    const roomParticipants = rooms.get(roomCode);
    
    if (roomParticipants && roomParticipants.has(participantId)) {
      const targetParticipant = roomParticipants.get(participantId);
      targetParticipant.ws.send(JSON.stringify({
        type: 'offer',
        participantId: currentParticipant,
        offer,
        roomCode
      }));
    }
  }

  function handleAnswer(ws, data, room) {
    const { participantId, answer, roomCode } = data;
    const roomParticipants = rooms.get(roomCode);
    
    if (roomParticipants && roomParticipants.has(participantId)) {
      const targetParticipant = roomParticipants.get(participantId);
      targetParticipant.ws.send(JSON.stringify({
        type: 'answer',
        participantId: currentParticipant,
        answer,
        roomCode
      }));
    }
  }

  function handleIceCandidate(ws, data, room) {
    const { participantId, candidate, roomCode } = data;
    const roomParticipants = rooms.get(roomCode);
    
    if (roomParticipants && roomParticipants.has(participantId)) {
      const targetParticipant = roomParticipants.get(participantId);
      targetParticipant.ws.send(JSON.stringify({
        type: 'ice_candidate',
        participantId: currentParticipant,
        candidate,
        roomCode
      }));
    }
  }

  function handleData(ws, data, room) {
    const { roomCode, sender } = data;
    const roomParticipants = rooms.get(roomCode);
    
    if (roomParticipants) {
      // 방의 모든 참가자에게 데이터 전송 (송신자 제외)
      roomParticipants.forEach((p, id) => {
        if (id !== currentParticipant) {
          p.ws.send(JSON.stringify({
            type: 'data',
            data: data.data,
            sender,
            roomCode,
            timestamp: data.timestamp
          }));
        }
      });
    }
  }

  function handleLeave(ws, room, participant) {
    const roomParticipants = rooms.get(room);
    if (roomParticipants && roomParticipants.has(participant)) {
      roomParticipants.delete(participant);
      
      // 방이 비어있으면 방 삭제
      if (roomParticipants.size === 0) {
        rooms.delete(room);
        console.log(`방 ${room} 삭제됨`);
      } else {
        // 다른 참가자들에게 퇴장 알림
        roomParticipants.forEach((p) => {
          p.ws.send(JSON.stringify({
            type: 'participant_left',
            participantId: participant,
            roomCode: room
          }));
        });
      }
    }
  }
});

const PORT = process.env.PORT || 3001;
server.listen(PORT, () => {
  console.log(`시그널링 서버가 포트 ${PORT}에서 실행 중입니다.`);
  console.log(`WebSocket URL: ws://localhost:${PORT}`);
});
