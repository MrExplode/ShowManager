import type { EventEmitterForPlayerEvents, PlayerAdapter } from 'peaks.js'

let emitter: EventEmitterForPlayerEvents | null = null

export const player: PlayerAdapter = {
    init: async (eventEmitter: EventEmitterForPlayerEvents) => {
        emitter = eventEmitter
    },
    destroy: () => {},
    play: async () => {},
    pause: () => {},
    seek: (time: number) => {},
    isPlaying: () => {
        return false
    },
    isSeeking: () => {
        return false
    },
    getCurrentTime: () => {
        return 0
    },
    getDuration: () => {
        return 0
    }
}
