import { writable } from 'svelte/store'
import { get, post } from '$lib/data/api'
import type { LogEntry, Timecode } from '$lib/data/types'

export const playing = writable(false)
export const paused = writable(false)
export const currentTime = writable<Timecode>({
    hour: 0,
    min: 0,
    sec: 0,
    frame: 0,
    millisecLength: 0
})
export const logs = writable<LogEntry[]>([])
export const connected = writable(false)
export const retryCountdown = writable(5)

const LOG_LIMIT = 1000

export const syncPlaying = async () => {
    const data = await get('/control/play')
    playing.set(data.playing)
}

export const loadLogs = (entries: LogEntry[]) => {
    logs.set(entries.slice(-LOG_LIMIT))
}

export const addLog = (entry: LogEntry) => {
    logs.update((current) => {
        const next = [...current, entry]
        return next.length > LOG_LIMIT ? next.slice(next.length - LOG_LIMIT) : next
    })
}

export const clearLogs = () => logs.set([])

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

/** amount is in whole seconds; negative jumps back. */
export const quickJump = async (amount: number) => {
    await post('/control/quickjump', { amount })
}
