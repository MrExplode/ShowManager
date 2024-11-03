import type Marker from '$lib/data/types'
import { readable, writable } from 'svelte/store'
import { get, post, wait } from './api'

let isSyncing = false
const _loadedAudio = writable('')
export const volume = writable(100)
const _playing = writable(false)
const _markers = writable<Marker[]>([])

export const loadedAudio = readable(_loadedAudio)
export const playing = readable(_playing)
export const markers = readable(_markers)

export const syncAudio = async () => {
    const data = await get('/audio/info')
    isSyncing = true
    _loadedAudio.set(data.loaded)
    volume.set(data.volume)
    _playing.set(data.playing)
    _markers.set(data.markers)
    isSyncing = false
}

volume.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/audio/volume', {
                volume: volume
            })
        )
    }
})

export const syncMarkers = async () => {
    const data = await get('/audio/markers')
    _markers.set(data.markers)
}
