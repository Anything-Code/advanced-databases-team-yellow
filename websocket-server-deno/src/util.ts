import { RedisChannelLookup } from './types.ts';
import { WebSocket } from './deps.ts';

export function sleep(ms: number) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

export class Streamer {
    socket: WebSocket;
    emergencyChannels: Array<RedisChannelLookup> = [];

    constructor(socket: WebSocket) {
        this.socket = socket;
    }

    public async append(newChannel: RedisChannelLookup) {
        try {
            this.emergencyChannels = [...this.emergencyChannels, newChannel];
            await this.stream();
        } catch (error) {
            console.log(error);
        }
    }
    public remove(key: string) {
        this.emergencyChannels = this.emergencyChannels.filter((item) => item.key !== key);
    }
    public find(key: string) {
        return this.emergencyChannels.find((item) => item.key === key);
    }
    public channels() {
        return this.emergencyChannels;
    }

    async stream() {
        for (const emergencyChannel of this.emergencyChannels) {
            try {
                for await (const { channel, message } of emergencyChannel.value.receive()) {
                    this.socket.send(`${channel}: ${message}`);
                }
            } catch (error) {
                console.log(error);
            }
        }
    }
}
