import { loadedAudio, playing } from '$lib/data/audio'
import { currentTime } from '$lib/data/control'
import type { EventEmitterForPlayerEvents, PlayerAdapter } from 'peaks.js'
import { get } from 'svelte/store'

let emitter: EventEmitterForPlayerEvents | null = null
let lastUpdate = 0

export const player: PlayerAdapter = {
    init: async (eventEmitter: EventEmitterForPlayerEvents) => {
        emitter = eventEmitter
    },
    destroy: () => {},
    play: async () => {},
    pause: () => {},
    seek: (time: number) => {},
    isPlaying: () => {
        return get(playing)
    },
    isSeeking: () => {
        return false
    },
    getCurrentTime: () => {
        const audio = get(loadedAudio)
        if (audio == null || !get(playing)) return 0
        const globalTime = get(currentTime)
        return (globalTime.millisecLength - audio.start.millisecLength) / 1000.0
    },
    getDuration: () => {
        const audio = get(loadedAudio)
        if (audio == null || !audio.end) return 0
        return (audio?.end!.millisecLength - audio?.start.millisecLength) / 1000.0
    }
}

playing.subscribe((p) => {
    if (p) {
        emitter?.emit('player.playing', player.getCurrentTime())
    } else {
        emitter?.emit('player.pause', player.getCurrentTime())
    }
})

currentTime.subscribe((t) => {
    if (!get(playing) || t.millisecLength - lastUpdate < 250) return
    lastUpdate = t.millisecLength
    emitter?.emit('player.timeupdate', player.getCurrentTime())
})
