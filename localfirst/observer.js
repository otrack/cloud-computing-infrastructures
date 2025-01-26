import * as Y from 'yjs'
import { WebsocketProvider } from 'y-websocket'
import ws from 'ws'

global.WebSocket = ws;

const ydoc = new Y.Doc();

const wsProvider = new WebsocketProvider(
  'ws://localhost:1234', // WebSocket server URL
  'my-roomname',         // Room name
  ydoc                   // Yjs document
);

wsProvider.on('status', event => {
    console.log(event.status); // logs "connected" or "disconnected"
})

