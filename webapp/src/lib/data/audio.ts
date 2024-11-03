import type Marker from '$lib/data/types'
import { writable, get as getValue } from 'svelte/store'
import { get, post, wait } from '$lib/data/api'

let isSyncing = true
export const loadedAudio = writable('')
export const volume = writable(100)
export const playing = writable(false)
export const markers = writable<Marker[]>([])

export const syncAudio = async () => {
    const data = await get('/audio/info')
    isSyncing = true
    loadedAudio.set(data.loaded)
    volume.set(data.volume)
    playing.set(data.playing)
    markers.set(data.markers)
    isSyncing = false
}

volume.subscribe(() => {
    if (!isSyncing) {
        console.log('updating')
        wait(
            post('/audio/volume', {
                volume: getValue(volume)
            })
        )
    }
})

export const syncMarkers = async () => {
    const data = await get('/audio/markers')
    markers.set(data.markers)
}

export const setSyncing = (value: boolean) => {
    isSyncing = value
}
