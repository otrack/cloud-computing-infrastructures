import * as Y from 'yjs';
import { WebsocketProvider } from 'y-websocket';

const ydoc = new Y.Doc();
const wsProvider = new WebsocketProvider(
    'ws://localhost:1234', // WebSocket server URL
    'my-roomname',         // Room name
    ydoc                   // Yjs document
);

const todos = ydoc.getMap("todos");
const votes = ydoc.getMap("votes");

const proposalList = document.getElementById('proposal-list');
votes.observe(() => {
    // TODO
});

const addProposalButton = document.getElementById('add-proposal-btn');
addProposalButton.addEventListener('click',
				   () => {
				       // TODO
				   });
