/* eslint-disable prefer-const */
import { get } from './api'

let connected = $state(false)
let playing = $state(false)
let paused = $state(false)
let currentTime = $state('00 : 00 : 00 / 00')
let logs: string[] = $state([])

const syncPlaying = async () => {
    const data = await get('/control/play')
    playing = data.playing
}

export default { connecting: connected, playing, paused, currentTime, logs, syncPlaying }
