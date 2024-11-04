import type Marker from '$lib/data/types'
import { writable, get as getValue } from 'svelte/store'
import { get, post, wait } from '$lib/data/api'
import type { AudioTrack } from '$lib/data/types'

let isSyncing = true
export const loadedAudio = writable('')
export const volume = writable<number[]>([100])
export const playing = writable(false)
export const markers = writable<Marker[]>([])
export const availableTracks = writable<AudioTrack[]>([])

export const syncAudio = async () => {
    const data = await get('/audio/info')
    isSyncing = true
    loadedAudio.set(data.loaded)
    volume.set([data.volume])
    playing.set(data.playing)
    markers.set(data.markers)
    availableTracks.set(data.availableTracks)
    isSyncing = false
}

volume.subscribe(() => {
    if (!isSyncing) {
        wait(
            post('/audio/volume', {
                volume: getValue(volume)[0]
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
