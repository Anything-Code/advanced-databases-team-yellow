import { RedisSubscription, WebSocket, WebSocketEvent } from './deps.ts';

export interface ServerPayload {
    socket: WebSocket;
    event: WebSocketEvent;
}

export interface RedisChannelLookup {
    key: string;
    value: RedisSubscription<string>;
}
