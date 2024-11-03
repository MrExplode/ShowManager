import { writable } from 'svelte/store'
import { get } from './api'

export const playing = writable(false)
export const paused = writable(false)
export const currentTime = writable('00 : 00 : 00 / 00')
export const logs = writable<string[]>([])
export const connected = writable(false)
export const retryCountdown = writable(5)

export const syncPlaying = async () => {
    const data = await get('/control/play')
    playing.set(data.playing)
}
