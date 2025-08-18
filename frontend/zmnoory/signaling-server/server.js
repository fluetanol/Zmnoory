// [FILEPATH] signaling-server/server.js
const WebSocket = require('ws');

// Simulated room code validation (in production, use a database)
const validRoomCodes = new Map([
  ['ROOM123', true], // Example room code
  ['TEST456', true], // Another example room code
]);

const wss = new WebSocket.Server({ port: 3000 });
const rooms = new Map();

wss.on('connection', (ws) => {
  let clientId = null;
  let currentRoom = null;

  ws.on('message', (message) => {
    const data = JSON.parse(message);

    switch (data.type) {
      case 'join':
        clientId = data.id;
        currentRoom = data.roomId;
        
        if (validRoomCodes.has(currentRoom)) {
          if (!rooms.has(currentRoom)) {
            rooms.set(currentRoom, new Map());
          }
          
          rooms.get(currentRoom).set(clientId, ws);
          
          // Notify other clients in the room
          rooms.get(currentRoom).forEach((client, id) => {
            if (id !== clientId && client.readyState === WebSocket.OPEN) {
              client.send(JSON.stringify({
                type: 'user-joined',
                id: clientId,
                roomId: currentRoom,
              }));
            }
          });
          
          ws.send(JSON.stringify({
            type: 'join-success',
            roomId: currentRoom,
          }));
        } else {
          ws.send(JSON.stringify({
            type: 'join-failure',
            roomId: currentRoom,
          }));
        }
        break;

      case 'leave':
        if (currentRoom && rooms.has(currentRoom)) {
          rooms.get(currentRoom).delete(clientId);
          rooms.get(currentRoom).forEach((client, id) => {
            if (id !== clientId && client.readyState === WebSocket.OPEN) {
              client.send(JSON.stringify({
                type: 'user-left',
                id: clientId,
                roomId: currentRoom,
              }));
            }
          });
          if (rooms.get(currentRoom).size === 0) {
            rooms.delete(currentRoom);
          }
        }
        break;

      case 'offer':
      case 'answer':
      case 'ice-candidate':
        const targetClient = rooms.get(currentRoom)?.get(data.targetId);
        if (targetClient && targetClient.readyState === WebSocket.OPEN) {
          targetClient.send(JSON.stringify({
            ...data,
            id: clientId,
          }));
        }
        break;
    }
  });

  ws.on('close', () => {
    if (currentRoom && rooms.has(currentRoom) && clientId) {
      rooms.get(currentRoom).delete(clientId);
      rooms.get(currentRoom).forEach((client, id) => {
        if (id !== clientId && client.readyState === WebSocket.OPEN) {
          client.send(JSON.stringify({
            type: 'user-left',
            id: clientId,
            roomId: currentRoom,
          }));
        }
      });
      if (rooms.get(currentRoom).size === 0) {
        rooms.delete(currentRoom);
      }
    }
  });
});

console.log(`WebSocket server running on ws://0.0.0.0:3000`);