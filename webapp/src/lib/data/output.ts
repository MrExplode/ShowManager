import { writable, get as getValue } from 'svelte/store'
import { get, post, wait } from './api'

let isSyncing = true
export const artNetOutput = writable(false)
export const audioOutput = writable(false)
export const ltcOutput = writable(false)
export const schedulerActive = writable(false)

artNetOutput.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/output/artnet', {
                enabled: getValue(artNetOutput)
            })
        )
    }
})

audioOutput.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/output/audio', {
                enabled: getValue(audioOutput)
            })
        )
    }
})

ltcOutput.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/output/ltc', {
                enabled: getValue(ltcOutput)
            })
        )
    }
})

schedulerActive.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/output/scheduler', {
                enabled: getValue(schedulerActive)
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

export const setSyncing = (value: boolean) => {
    isSyncing = value
}
