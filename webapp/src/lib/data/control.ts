import { writable } from 'svelte/store'
import { get } from '$lib/data/api'

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

export const loadLogs = (log: string[]) => {
    logs.set(log.slice(0, 100))
}

export const addLog = (log: string) => {
    // todo add & trunc
    console.log(log)
}
