import type Marker from '$lib/data/types'
import { get, post, wait } from './api'

let isSyncing = false
let loadedAudio = $state('')
let volume = $state(100)
let playing = $state(false)
let markers: Marker[] = $state([])

const syncAudio = async () => {
    const data = await get('/audio/info')
    isSyncing = true
    loadedAudio = data.loaded
    volume = data.volume
    playing = data.playing
    markers = data.markers
    isSyncing = false
}

$effect(() => {
    if (!isSyncing) {
        wait(
            post('/audio/volume', {
                volume: volume
            })
        )
    }
})

const syncMarkers = async () => {
    const data = await get('/audio/markers')
    isSyncing = true
    markers = data.markers
    isSyncing = false
}

export default { loadedAudio, volume, playing, markers, syncAudio, syncMarkers }
