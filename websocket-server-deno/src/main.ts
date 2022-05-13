import { connect, listenAndServe } from './deps.ts';
import { ServerPayload } from './types.ts';
import { sleep, Streamer } from './util.ts';

const sub = await connect({ hostname: '127.0.0.1' });
const pub = await connect({ hostname: '127.0.0.1' });

// await sub.subscribe('All');
await sub.subscribe('Police');
await sub.subscribe('Ambulance');
await sub.subscribe('Firetruck');

listenAndServe(':8080', async ({ socket, event }: ServerPayload) => {
    const streamer = new Streamer(socket);

    if (typeof event === 'string') {
        if (event === 'Client connected!') {
            streamer.append({
                key: 'All',
                value: await sub.subscribe('All'),
            });
        }
        if (event === 'Join 1') {
            const message = 'Join #Manhunt-1 BWL_A_1 8.6508929 49.4141772';
            await pub.publish('Emergencies', message);
            // console.log(message);
            const newChannel = {
                key: '#Manhunt-1',
                value: await sub.subscribe('#Manhunt-1'),
            };
            streamer.append(newChannel);
        }
        if (event === 'Join 2') {
            const message = 'Join #Stabbing-1 BWL_A_2 8.657238054886221 49.3784348';
            await pub.publish('Emergencies', message);
            // console.log(message);
            const newChannel = {
                key: '#Stabbing-1',
                value: await sub.subscribe('#Stabbing-1'),
            };
            streamer.append(newChannel);
        }
        if (event === 'Join 3') {
            const message = 'Join #Firehazard-1 BWL_A_3 8.6839833 49.41273485';
            await pub.publish('Emergencies', message);
            // console.log(message);
            const newChannel = {
                key: '#Firehazard-1',
                value: await sub.subscribe('#Firehazard-1'),
            };
            streamer.append(newChannel);
        }
        if (event === 'Leave 1') {
            const message = 'Leave BWL_A_1';
            await pub.publish('Emergencies', message);
            // console.log(message);
            const channel = streamer.find('#Manhunt-1');

            if (channel) {
                await channel.value.unsubscribe();
                await channel.value.close();
            }
            streamer.remove('#Manhunt-1');
        }
        if (event === 'Leave 2') {
            const message = 'Leave BWL_A_2';
            await pub.publish('Emergencies', message);
            // console.log(message);
            const channel = streamer.find('#Stabbing-1');

            if (channel) {
                await channel.value.unsubscribe();
                await channel.value.close();
            }
            streamer.remove('#Stabbing-1');
        }
        if (event === 'Leave 3') {
            const message = 'Leave BWL_A_3';
            await pub.publish('Emergencies', message);
            // console.log(message);
            const channel = streamer.find('#Firehazard-1');

            if (channel) {
                await channel.value.unsubscribe();
                await channel.value.close();
            }
            streamer.remove('#Firehazard-1');
        }
    }
});

// Same-app testing using browser-api for fast prototyping
try {
    const socket = new WebSocket('ws://localhost:8080');

    socket.addEventListener('open', async (_event) => {
        socket.send('Client connected!');
        await sleep(5000);
        socket.send('Join 1');
        await sleep(5000);
        socket.send('Leave 1');
        socket.send('Join 2');
        await sleep(5000);
        socket.send('Leave 2');
        socket.send('Join 3');
        await sleep(5000);
        socket.send('Leave 3');
        // Deno.exit();
    });

    socket.addEventListener('message', (event) => {
        console.log('Client: ', event.data);
    });
} catch (err) {
    console.error(`Could not connect to WebSocket: '${err}'`);
}
