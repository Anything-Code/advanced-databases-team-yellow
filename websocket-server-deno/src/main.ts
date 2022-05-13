import { connect, listenAndServe, WebSocket, WebSocketEvent } from './deps.ts';
import { sleep } from './util.ts';

const redis = await connect({ hostname: '127.0.0.1' });

const subToAll = await redis.subscribe('All');
const subToPolice = await redis.subscribe('Police');
const subToAmbulance = await redis.subscribe('Ambulance');
const subToFiretruck = await redis.subscribe('Firetruck');

async function streamCoordinatesFromAllCars(socket: WebSocket) {
    for await (const { channel, message } of subToAll.receive()) {
        socket.send(`${channel}: ${message}`);
    }
    for await (const { channel, message } of subToPolice.receive()) {
        socket.send(`${channel}: ${message}`);
    }
    for await (const { channel, message } of subToAmbulance.receive()) {
        socket.send(`${channel}: ${message}`);
    }
    for await (const { channel, message } of subToFiretruck.receive()) {
        socket.send(`${channel}: ${message}`);
    }
}

listenAndServe(':8080', async ({ socket, event }) => {
    // await streamCoordinatesFromAllCars(socket);

    if (typeof event === 'string') {
        if (event === 'Join 1') {
            console.log('Join stabbing BWL_A_1 8.657238054886221 49.3784348');
        }
        if (event === 'Join 2') {
            console.log('Join stabbing BWL_A_2 8.657238054886221 49.3784348');
        }
        if (event === 'Join 3') {
            console.log('Join stabbing BWL_A_3 8.657238054886221 49.3784348');
        }
    }
});

try {
    const socket = new WebSocket('ws://localhost:8080/ayyy');
    // Connection opened
    socket.addEventListener('open', (event) => {
        socket.send('Join 1');
    });

    socket.addEventListener('message', (event) => {
        console.log('Client: ', event.data);
    });

    // await sleep(5000);
} catch (err) {
    console.error(`Could not connect to WebSocket: '${err}'`);
}
