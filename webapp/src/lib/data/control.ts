import { writable } from 'svelte/store'
import { get, post } from '$lib/data/api'
import type { Timecode } from '$lib/data/types'

export const playing = writable(false)
export const paused = writable(false)
export const currentTime = writable<Timecode>({
    hour: 0,
    min: 0,
    sec: 0,
    frame: 0,
    millisecLength: 0
})
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

export const play = async () => {
    await post('/control/play')
}

export const pause = async () => {
    await post('/control/pause')
}

export const stop = async () => {
    await post('/control/stop')
}

export const setTime = async (t: Timecode) => {
    await post('/control/set', t)
}
