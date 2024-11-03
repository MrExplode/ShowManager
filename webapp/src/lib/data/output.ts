import { writable } from 'svelte/store'
import { get, post, wait } from './api'

let isSyncing = false
export const artNetOutput = writable(false)
export const audioOutput = writable(false)
export const ltcOutput = writable(false)
export const schedulerActive = writable(false)

artNetOutput.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/output/artnet', {
                enabled: artNetOutput
            })
        )
    }
})

audioOutput.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/output/audio', {
                enabled: audioOutput
            })
        )
    }
})

ltcOutput.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/output/ltc', {
                enabled: ltcOutput
            })
        )
    }
})

schedulerActive.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/output/scheduler', {
                enabled: schedulerActive
            })
        )
    }
})

export const syncOutputs = async () => {
    const data = await get('/output/all')
    isSyncing = true
    artNetOutput.set(data.artnet)
    audioOutput.set(data.audio)
    ltcOutput.set(data.ltc)
    schedulerActive.set(data.scheduler)
    isSyncing = false
}
