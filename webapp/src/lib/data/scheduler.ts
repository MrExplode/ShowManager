import { writable } from 'svelte/store'
import type { ScheduledEvent } from '$lib/data/types'
import { get, post, wait } from '$lib/data/api'

let isSyncing = true
export const events = writable<ScheduledEvent[]>([])
export const recording = writable(false)

recording.subscribe((v) => {
    if (!isSyncing) {
        wait(
            post('/scheduler/record', {
                enabled: v
            })
        )
    }
})

export const syncEvents = async () => {
    const data = await get('/scheduler/events')
    isSyncing = true
    events.set(data.events)
    isSyncing = false
}

export const syncRecording = async () => {
    const data = await get('/scheduler/record')
    isSyncing = true
    recording.set(data.recording)
    isSyncing = false
}

export const addEvent = async (e: ScheduledEvent) => {
    await post('/scheduler/events/add', {
        event: e
    })
}

export const deleteEvents = async (events: ScheduledEvent[]) => {
    await post('/scheduler/events/delete', {
        events: events.map((e) => e.id)
    })
}
